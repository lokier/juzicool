package com.juzicool.data.main;

import com.alibaba.fastjson.JSON;
import com.juzicool.data.Juzi;
import com.juzicool.data.db.JuziDB;
import com.juzicool.data.main.es.ElasticSearch;
import com.juzicool.data.main.util.MySql;
import com.juzicool.data.main.util.Prop;
import com.juzicool.data.main.util.PropKit;
import com.juzicool.data.utils.JuziUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import java.io.File;
import java.sql.*;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ImportJuziFromSqlite {

    public static void main(String[] args) {

       // args = new String[]{"juzi-import\\bin\\import-sqlite-file.properties"};

        if(args == null || args.length < 1){
            System.err.println("缺少指定properties文件");
            return ;
        }

        String propFilePath = args[0];

        File file = new File(propFilePath);

        if(!file.exists()){
            System.err.println("指定参数文件不存在：" + file.getAbsolutePath());

        }

        Prop prp = PropKit.use(file);

        final String es_host = prp.get("es_host","localhost");
        final int es_port = prp.getInt("es_port",9200);
        final String es_name =  prp.get("es_name","");
        final String es_password =  prp.get("es_password","");
        final int es_timeout = prp.getInt("es_timeout",30000);
        final String es_index_name = prp.get("es_index_name","");
        final String es_index_name_type = prp.get("es_index_name_type","");

        final String sqliteFilepath =   prp.get("sqliteFilepath",null) ; // "./juzimi_ablum_output.db";

        final String mysql_url = prp.get("mysql_url","");
        final String mysql_name = prp.get("mysql_name","");
        final String mysql_password = prp.get("mysql_password","");


        Integer accountId = prp.getInt("accountId",null);
        Integer sourceType = prp.getInt("sourceType",null);
        Integer es_index_version = prp.getInt("es_index_version",null);
        // 建立索引的版本号，0为不建立索引。

        if(accountId == null){
            System.err.println("accountId should not null");
            return;
        }

        if(sourceType == null){
            System.err.println("sourceType should not null");
            return;
        }

        if(es_index_version == null){
            System.err.println("es_index_version should not null");
            return;
        }

        File sqlFile = new File(sqliteFilepath);

        if(!sqlFile.exists()){
            System.err.println("sqlite 文件不存在");
            return;
        }



        MySql mySql = new MySql(mysql_url,mysql_name,mysql_password);
        mySql.setDeug(false);
        ElasticSearch search = new ElasticSearch(es_host,es_port,es_name,es_password,es_timeout);
        JuziDB juziDB = new JuziDB(sqlFile);
        RestClient client =  search.creatClient();
        try {
            mySql.connect();


            juziDB.prepare();

            int juziTotalSize = juziDB.size();

            System.out.println("juzi total size: " + juziTotalSize);


            Connection conn = mySql.getConnection();

            conn.setAutoCommit(false); //开启事务


            String insertJuzi =  getInsertJuzSql(accountId,sourceType);
            String insertJuziEx = getInsertJuzExSql();

            PreparedStatement juziPs = conn.prepareStatement(insertJuzi,Statement.RETURN_GENERATED_KEYS);
            PreparedStatement juziExPs = conn.prepareStatement(insertJuziEx);

            //JuziDB.Iterator it = juziDB.createIterator();
            List<Juzi> batchList = null;
            int count = 0;
            long batchFirstId = -1L;
            boolean hasCheckData = false;
            int batchSize = 500;
            do {
                batchList = juziDB.getFirsPage(batchSize);

                if(batchList == null || batchList.size() == 0){
                    break;
                }
                final long[] ids = getIds(batchList);

                long newBatchFirstId = batchList.get(0).id;
                if(newBatchFirstId == batchFirstId){
                    //简单判断下导入的内容有没有重复。
                    throw  new RuntimeException("import repeat data!:" + newBatchFirstId);
                }
                batchFirstId = newBatchFirstId;

                count += batchList.size();
                //插入数据库
                juziPs.clearBatch();
                juziExPs.clearBatch();
                insertJuziToDB(batchList, juziPs);


                if (es_index_version > 0) {
                    //构建索引
                    updateSearchIndex(client, batchList, es_index_name, es_index_name_type);
                }
                insertJuziExToDB(batchList, juziExPs, es_index_version);

                conn.commit();//执行

                //删除导入的批量数据
                juziDB.deletes(ids);

                System.out.println(String.format("handle: %d/%d", count, juziTotalSize));
                //System.out.println(String.format("new size %d", juziDB.size()));

                //第一次简单的检查下导入源的数据有没有删除
                if(!hasCheckData){
                    int newjuziTotalSize = juziDB.size();
                    if(newjuziTotalSize + batchList.size() != juziTotalSize){
                        throw  new RuntimeException("JuziDB 应该删除已导入的数据！");
                    }
                    hasCheckData = true;
                }

            } while (batchList != null);
            System.out.println(String.format("导入完成，共完成/%d个数据！", count));

        }catch (Exception ex){
            try{
                mySql.getConnection().rollback();
            }catch (Exception e){
            }
            throw new RuntimeException(ex);
        }finally {
            juziDB.close();

            try {
                client.close();
            }catch (Exception ex){

            }

            mySql.disconnect();
        }

    }

    private static String getInsertJuzSql(int accountId,int sourceType){

        return String.format("INSERT INTO `juzi` (" +
                "`content`, `length`, `author`, `from`, `category`, `tags`, `updateAt`, `createAt`, `remarkValue`, `accountId`, `sourceType`)"
                + "VALUES(?,?,?,?,?,?,?,?,?,%d,%d)",accountId,sourceType);
    }

    private static String getInsertJuzExSql(){
        return "INSERT INTO `juzi_ext` (" +
                "`juzi_id`, `applyDesc`, `remark`, `hash`, `es_index_ver`)"
                + "VALUES(?,?,?,?,?)";
    }

    private static void insertJuziToDB(List<Juzi> juziList, PreparedStatement juziPs)throws SQLException {

        for(Juzi juzi:juziList){
            JuziUtils.adjustJuziLength(juzi);
            int length = juzi.content == null ? 0 : juzi.content.length();
           // System.out.println("juzi: " + juzi.toString());
            Date createAndUpdate = new Date(System.currentTimeMillis());
            juziPs.setString(1,juzi.content);
            juziPs.setShort(2,(short)length);
            juziPs.setString(3,juzi.author);
            juziPs.setString(4,juzi.from);
            juziPs.setString(5,juzi.category);
            juziPs.setString(6,juzi.tags);
            juziPs.setDate(7,createAndUpdate);
            juziPs.setDate(8,createAndUpdate);
            juziPs.setShort(9,(short) JuziUtils.remark(juzi));
            juziPs.addBatch();
        }
        juziPs.executeBatch();

        ResultSet rst = juziPs.getGeneratedKeys();
        for(int i = 0;i < juziList.size();i++){
            if(!rst.next()){
                throw new SQLException("获取自增id错误");
            }
            Juzi juzi = juziList.get(i);
            int juziId = rst.getInt(1);
            juzi.id = juziId;
        }


    }

    private static void insertJuziExToDB(List<Juzi> juziList, PreparedStatement ps,int esIndexVerison)throws SQLException {
        for (Juzi juzi : juziList) {
            ps.setLong(1, juzi.id);
            ps.setString(2, juzi.applyDesc);
            ps.setString(3, juzi.remark);
            ps.setString(4, null);
            ps.setShort(5, (short)esIndexVerison);
            ps.addBatch();
        }
        ps.executeBatch();
    }


    /**
     * 更新句子的索引
     */
    public static void updateSearchIndex(RestClient client,List<Juzi> juziList,String index,String index_name_type)throws Exception {

        StringBuffer sb = new StringBuffer();

        for (Juzi juzi : juziList) {
            String jsonString = JSON.toJSONString(juzi);
            //System.out.println(jsonString);
            sb.append("{ \"index\":  { \"_index\": \""+index+"\", \"_type\": \""+index_name_type+"\", \"_id\": \""+juzi.id+"\" }}\n");
            sb.append(jsonString+"\n");
        }
       // Request request = new Request("put", "/" + index + "/" + index_name_type + "/" + juzi.id);

        Request request = new Request("post", "/_bulk");
        request.setJsonEntity(sb.toString());
        UpdateIndexResponseListener listener = new UpdateIndexResponseListener(1);
        client.performRequestAsync(request, listener);

        listener.latch.await();

        if(listener.hasError){
            throw listener.ex;
        }

    }

    private static long[]  getIds(List<Juzi> juziList) {
        long[] ids = new long[juziList.size()];
        for(int i = 0 ; i < juziList.size();i++){//Juzi juzi : juziList){
            ids[i] = juziList.get(i).id;
        }
        return ids;
    }

    private static class  UpdateIndexResponseListener implements ResponseListener{
        final CountDownLatch latch; // new CountDownLatch(juziList.size());
        boolean hasError = false;
        private Exception ex;

        public UpdateIndexResponseListener(int countDown){
            latch = new CountDownLatch(countDown);
        }

        @Override
        public void onSuccess(Response response) {
            latch.countDown();
        }

        @Override
        public void onFailure(Exception exception) {
            latch.countDown();
            hasError = true;
            ex = exception;
        }


    }


}
