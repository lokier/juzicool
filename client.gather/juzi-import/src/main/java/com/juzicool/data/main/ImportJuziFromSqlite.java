package com.juzicool.data.main;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.juzicool.data.Juzi;
import com.juzicool.data.db.JuziDB;
import com.juzicool.data.main.es.ElasticSearch;
import com.juzicool.data.main.util.MySql;
import com.juzicool.data.main.util.Prop;
import com.juzicool.data.main.util.PropKit;
import com.juzicool.data.simhash.SimHash;
import com.juzicool.data.utils.JuziUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class ImportJuziFromSqlite {

    public static void main(String[] args) {

      /*  if(args== null) {
            args = new String[]{"juzi-import\\bin\\import-sqlite-file.properties"};

        }*/

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
            System.err.println("sqlite 文件不存在:" +sqlFile.getAbsolutePath());
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

            List<Juzi> batchList = null;
            int count = 0;
            int succesCount = 0;  //成功导入的内容。
            long batchFirstId = -1L;
            boolean hasCheckData = false;
            int batchSize = 200;
            do {
                batchList = juziDB.getFirsPage(batchSize);

                if(batchList == null || batchList.size() == 0){
                    break;
                }

                count += batchList.size();
                final long[] ids = getIds(batchList);

                //检查下：过滤重复的内容。
                long startfitlerRepeatContext = System.currentTimeMillis();
                List<Juzi> toInsertList = fitlerRepeatContext(client,batchList);
                System.out.println("   ==> check repeat content spend time: " + (System.currentTimeMillis() - startfitlerRepeatContext));
                int repeatCount = batchList.size() - toInsertList.size();
                if(toInsertList != null && toInsertList.size() > 0){
                    long newBatchFirstId = toInsertList.get(0).id;
                    if(newBatchFirstId == batchFirstId){
                        //简单判断下导入的内容有没有重复。
                        throw  new RuntimeException("import repeat data!:" + newBatchFirstId);
                    }
                    batchFirstId = newBatchFirstId;

                    //插入数据库
                    juziPs.clearBatch();
                    juziExPs.clearBatch();
                    insertJuziToDB(toInsertList, juziPs);


                    if (es_index_version > 0) {
                        //构建索引
                        updateSearchIndex(client, toInsertList, es_index_name, es_index_name_type);
                    }
                    insertJuziExToDB(toInsertList, juziExPs, es_index_version);

                    conn.commit();//执行

                    succesCount += toInsertList.size();

                }

                juziDB.deletes(ids);


                System.out.println(String.format("handle: %d/%d,重复句子:%d", count, juziTotalSize,repeatCount));
                //System.out.println(String.format("new size %d", juziDB.size()));

                //第一次简单的检查下导入源的数据有没有删除
                if(!hasCheckData){
                    int newjuziTotalSize = juziDB.size();
                    if(newjuziTotalSize + ids.length != juziTotalSize){
                        throw  new RuntimeException("JuziDB 应该删除已导入的数据！");
                    }
                    hasCheckData = true;
                }
            } while (batchList != null);
            System.out.println(String.format("导入完成，共完成%d个数据，成功导入个数：%d！", count,succesCount));

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


    private static List<Juzi> fitlerRepeatContext(RestClient client,List<Juzi> batchList)throws Exception {
        //
        HashMap<SimHash,Juzi> juziMap = new HashMap<>();
        for(int i = 0;i < batchList.size();i++){
            Juzi juzi = batchList.get(i);
            SimHash simHash = SimHash.simHash(juzi.content);
            //simHashes[i] = simHash;

            //过滤本身含有重复的内容
            boolean contain = false;
            for(SimHash sh : juziMap.keySet()){
                int dis = SimHash.getDistance(sh.getSimHash(),simHash.getSimHash());
                if(dis <= 3){
                    contain = true;
                    break;
                }
            }
            if(!contain){
                juziMap.put(simHash,juzi);
            }
        }
        if(juziMap.isEmpty()){
            return new ArrayList<>();
        }

        SimHash[] simHashes = juziMap.keySet().toArray(new SimHash[juziMap.size()]);
        long[] ids = findRepeatContent(client,simHashes);
        if(ids.length != simHashes.length){
            throw new RuntimeException("length 一定要相等");
        }
        ArrayList<Juzi> ret = new ArrayList<>();
        for(int i = 0;i < ids.length;i++){
            if(ids[i]==-1){
                //不重复
                SimHash sh = simHashes[i];
                Juzi juzi = juziMap.get(sh);
                if(juzi == null){
                    throw  new NullPointerException();
                }
                ret.add(juzi);
            }
        }

        return ret;
    }


    private static String FIND_REPEAT_ES ="{ " +
            "\"query\": { " +
            "  \"bool\": { " +
            "      \"should\": [ " +
            "         { \"term\": { \"simhashA\": \"%s\"}}, " +
            "         { \"term\": { \"simhashB\": \"%s\"}}, " +
            "         { \"term\": { \"simhashC\": \"%s\"}}, " +
            "         { \"term\": { \"simhashD\": \"%s\"}} " +
            "        ] " +
            "      ,\"minimum_should_match\": 3 " +
            "      ,\"must\": [ " +
            "          {\"fuzzy\" : { " +
            "              \"simhash\" : { " +
            "               \"value\": \"%s\", " +
            "               \"boost\": 1.0, " +
            "               \"fuzziness\": 2, " +
            "                \"prefix_length\": 0, " +
            "               \"max_expansions\": 128 " +
            "                } " +
            "              } " +
            "             " +
            "          } " +
            "      ]     " +
            "    } " +
            "  } " +
            "  ,\"from\":0 " +
            "  ,\"size\":3 " +
            "}";

    /**
     * 查找重复的句子内容；
     * @param client
     * @param simHashList
     * @return 返回该句子的ID值,如果为-1
     */
    public static long[] findRepeatContent(RestClient client,SimHash[] simHashList )throws  Exception{


        StringBuffer sb = new StringBuffer();

        for (int i =0 ;i < simHashList.length;i++) {
            SimHash simHash =simHashList[i];
            String[] simABCD = simHash.get4SimHash();

            //添加simHash信息
            sb.append("{}\n");
            sb.append(String.format(FIND_REPEAT_ES,simABCD[0],simABCD[1],simABCD[2],simABCD[3],simHash.getSimHash())+"\n");
        }
        // Request request = new Request("put", "/" + index + "/" + index_name_type + "/" + juzi.id);

        Request request = new Request("post", "/juzicool/juzi/_msearch");

        request.setJsonEntity(sb.toString());
        UpdateIndexResponseListener listener = new UpdateIndexResponseListener(1);
        client.performRequestAsync(request, listener);

        listener.latch.await();

        if(listener.hasError){
            throw listener.ex;
        }

        HttpEntity entity = listener.response.getEntity();

        String bodyJason =  EntityUtils.toString(entity, "utf8");

        JSONObject obj = JSON.parseObject(bodyJason);

        JSONArray array = obj.getJSONArray("responses");
        if(array.size() != simHashList.length){
            throw new RuntimeException("长度不一致");
        }


        long[] ret = new long[simHashList.length];
        for(int i = 0;i < ret.length;i++){
            ret[i] = -1;
        }

        for(int i = 0;i < simHashList.length;i++){
            JSONObject object = array.getJSONObject(i);
            JSONObject item =   object.getJSONObject("hits");
            int hitTotal = item.getInteger("total");
            if(hitTotal > 0){
                //可能有重复，需要判重
                JSONObject itemObj = item.getJSONArray("hits").getJSONObject(0).getJSONObject("_source");

                String simhash = itemObj.getString("simhash");

                int dis = SimHash.getDistance(simhash,simHashList[i].getSimHash());

                if(dis <= 3){
                    ret[i] = itemObj.getLong("id");
                   // System.err.println("重复句子：id = " +  + ret[i]+", txt = " + texts[i]);
                }

               // continue;

            }
           // System.out.println(texts[i]);
           // System.out.println(object.toJSONString());
        }

        return ret;
    }

    /**
     * 更新句子的索引
     */
    public static void updateSearchIndex(RestClient client,List<Juzi> juziList,String index,String index_name_type)throws Exception {

        StringBuffer sb = new StringBuffer();

        for (Juzi juzi : juziList) {

            String jsonString = JSON.toJSONString(juzi);
            JSONObject json = JSONObject.parseObject(jsonString);

            String text = juzi.content;

            SimHash simHash = SimHash.simHash(text);
            String[] simABCD = simHash.get4SimHash();
            json.put("simhash",simHash.getSimHash());
            json.put("simhashA",simABCD[0]);
            json.put("simhashB",simABCD[1]);
            json.put("simhashC",simABCD[2]);
            json.put("simhashD",simABCD[3]);

            //添加simHash信息

            //System.out.println(jsonString);
            sb.append("{ \"index\":  { \"_index\": \""+index+"\", \"_type\": \""+index_name_type+"\", \"_id\": \""+juzi.id+"\" }}\n");
            sb.append(json.toJSONString()+"\n");
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
        private Response response;

        public UpdateIndexResponseListener(int countDown){
            latch = new CountDownLatch(countDown);
        }

        @Override
        public void onSuccess(Response response) {
            this.response = response;
            latch.countDown();
        }

        @Override
        public void onFailure(Exception exception) {
            hasError = true;
            ex = exception;
            latch.countDown();

        }


    }




}
