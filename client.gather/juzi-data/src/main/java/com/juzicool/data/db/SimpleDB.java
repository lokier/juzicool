package com.juzicool.data.db;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SimpleDB {

    public static void main(String[] args) {
        File sqlFile = new File("connect.db");

        SimpleDB db = new SimpleDB();
        db.openFile(sqlFile);

        System.out.println("simple LIST ===========================");

        long timeMillis1 = System.currentTimeMillis();
        db.List().insert(new Serializable[]{
                new Integer(3),
                new Integer(4),
                new Integer(5),
                new Integer(6),
                new Integer(7),
        });
        System.out.println("after batch push , list size : " + db.List().size());

        System.out.println("after batch push , item 4 = (7)  : " + db.list.getPage(0,10,false).get(4));

        try {
            Thread.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long timeMillis2 = System.currentTimeMillis();
        db.List().insert(new Serializable[]{
                new Integer(13),
                new Integer(14),
                new Integer(15),
                new Integer(16),
                new Integer(17),
        });
        System.out.println("after batch push , list size : " + db.List().size());
        System.out.println("after batch push , item 5 = (13)  : " + db.list.getPage(5,10,false).get(0));

        db.List().delete(null,new Date(timeMillis2));
        System.out.println("after batch push , list size (5) : " + db.List().size());
        System.out.println("after batch push , item 4 = (17)  : " + db.list.getPage(0,7,false).get(4));




        System.out.println("simple KV ===========================");

        db.KV().put("name","sser");
        db.KV().put("name2",234);
        db.KV().put("name2",234);

        System.out.println("name = : " +  db.KV().get("name","error!!!"));
        System.out.println("name2 = : " +  db.KV().get("name2",new Integer(-1)));


        String name = db.KV().get("name",null);
        Integer integr =  db.KV().get("name2",null);

        System.out.println(String.format("name:%s,name2:%d",name,integr));
        System.out.println("null: " +  db.KV().get("www","shout null"));
        System.out.println("has name = (true): " +  db.KV().has("name"));
        db.KV().remove("name");
        System.out.println("has name = (false): " +  db.KV().has("name"));

        ArrayList<String> keys = new ArrayList<>();
        for(int i = 0; i < 100;i++){
            String key = "batch_name_"+i;
            db.KV().put( key,23444);
            keys.add(key);
        }

        System.out.println("kv size (>=100):" +db.KV().size());
        db.KV().remove(keys);

        System.out.println("kv size (<10):" +db.KV().size());


        System.out.println("\n");
        System.out.println("simple QUEUE ===========================");


        db.Queue().push("dww",5,"xcfdsfd");
        db.Queue().push("3442",9,"11111");
        db.Queue().push("3444",6,"333333");
        db.Queue().push("xcvxcv",1,"xxxxxx");
        db.Queue().push("sdfxcv",1,"333xxxx333");
        db.Queue().push("34e44",1,"333xxx333");
        db.Queue().push("3d444",1,"xxxx");
        db.Queue().push("344xc4",1,"xxxxx");

        System.out.println("queue size : " + db.Queue().size());

        SimpleDB.QueueData data = db.Queue().poll();
        System.out.println("poll data: " + data.data.toString());

        data = db.Queue().peek();
        System.out.println("peek data: " + data.data.toString());

        data = db.Queue().poll();
        System.out.println("poll data: " + data.data.toString());
        System.out.println("queue size : " + db.Queue().size());

        System.out.println("has dww(true): " + db.Queue().has("dww"));

        data = db.Queue().poll();
        System.out.println("poll data: " + data.data.toString());
        System.out.println("has dww(false): " + db.Queue().has("false"));

        db.Queue().poll(20);

        data = db.Queue().poll();
        System.out.println("queue size : " + db.Queue().size());
        System.out.println("data is null: " + (data == null));

        ArrayList<SimpleDB.QueueData> qDatalist = new ArrayList<>();
        for(int i = 0; i< 100;i++){
            SimpleDB.QueueData qdata = new SimpleDB.QueueData();
            qdata.key ="q_data_q+"+i;
            qdata.priority =1000;
            qDatalist.add(qdata);
        }
        db.Queue().push(qDatalist);
        System.out.println("after batch push , queue size : " + db.Queue().size());
        db.Queue().poll(100);
        System.out.println("after poll(100) , queue size : " + db.Queue().size());





        db.close();
    }

    private  File mFile;
    private  String jdbcUrl;
    private KV mKv;
    private Queue mQueue;
    private List list;
    private Connection mConnection = null;

    private ArrayList<Queue> extraQueues = new ArrayList();

    public SimpleDB(){

    }

    public void openFile(File file){
        mFile = file;
        jdbcUrl = "jdbc:sqlite:" +file.getAbsolutePath();
        try {
            Class.forName("org.sqlite.JDBC");

            mQueue =new Queue("simple_db_queue",createConnection());
            list = new List("simple_db_list",createConnection());
            mKv = new KV(createConnection());
           // mKv.prepare();
        } catch (Exception e) {
           throw new RuntimeException(e);
        }
    }

    public Queue crateQueue(String queueName){
        try {
            Queue  q =new Queue( queueName,createConnection());
            return q;
            // mKv.prepare();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List List(){
        return list;
    }

    public Queue Queue(){
        return mQueue;
    }

    public KV KV(){
        return mKv;
    }

    public void close(){
        if(mKv!= null){
            mKv.close();
        }
        if(mQueue!=null){
            mQueue.close();
        }
        for(Queue queue: extraQueues){
            queue.close();
        }

        try {
            if (mConnection != null) {
                mConnection.close();
            }
        } catch (SQLException ex) {

        }
        mConnection = null;


    }

    private synchronized Connection createConnection()throws SQLException{

        // db parameters
        if(mConnection == null){

            //共用connnection
            mConnection = DriverManager.getConnection(jdbcUrl);
        }

        return mConnection;
    }


    public static class KV {
        private static final String TABLE_NAME = "simple_db_kv";
        private static final String KEY = "key";
        private static final String VALUE = "value";
        private Connection mConnection = null;

        public KV(Connection connection)throws SQLException{
            mConnection = connection;
            String sql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\n" + KEY + "  text PRIMARY KEY,\n"
                    + VALUE + " BLOB);";
           Statement stmt = mConnection.createStatement();
           stmt.execute(sql);
           stmt.closeOnCompletion();
        }

        public synchronized int size(){
            String sql = "SELECT COUNT("+KEY+") FROM "+ TABLE_NAME;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {

                    return rs.getInt(1);
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
            return 0;
        }

        public synchronized boolean has(String key){
            String sql = "SELECT *  FROM " + TABLE_NAME +" where  " + KEY +"='" +key + "'";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {
                    return true;
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
            return false;
        }

        public synchronized void put(String key, Serializable value){
            String sql = "INSERT or replace INTO "+TABLE_NAME+"("+KEY+", "+VALUE+") VALUES(?,?)";
            PreparedStatement pstmt = null;
            try {
                Connection conn = mConnection;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, key);
                pstmt.setBytes(2, objectToByte(value));
                pstmt.executeUpdate();

            } catch (SQLException e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }

            }
        }

        public synchronized void put(java.util.List<String> keys, Serializable value){
            String sql = "INSERT or replace INTO "+TABLE_NAME+"("+KEY+", "+VALUE+") VALUES(?,?)";
            PreparedStatement pstmt = null;
            Connection conn = mConnection;

            try {
                conn.setAutoCommit(false);
                pstmt = conn.prepareStatement(sql);
                for(String key:keys) {
                    pstmt.setString(1, key);
                    pstmt.setBytes(2, objectToByte(value));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();
            } catch (SQLException e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);
                }catch (Exception ex){

                }


            }
        }


        /*     public void remove(String key) {

            String[] keys = new String[1];
            keys[0] = key;
            remove(keys);
        }
*/
        public void remove(String key){

            String sql = "delete  from " + TABLE_NAME +" where " + KEY +" ='"+ key +"'";
            Statement stmt = null;
           // ResultSet rs = null;
            try {
                Connection conn = mConnection;
                 stmt= conn.createStatement();
                 stmt.execute(sql);

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
        }

        public void remove(java.util.List<String> keys){
            String sql = "delete  from " + TABLE_NAME +" where " + KEY +" = ? ";
            PreparedStatement pstmt = null;
            Connection conn = mConnection;
            try {
                conn.setAutoCommit(false);
                pstmt = conn.prepareStatement(sql);
                for(String key: keys){
                    pstmt.setString(1, key);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);

                }catch (Exception ex){

                }

            }
        }


        public synchronized <T extends Serializable> T get(String key,T defaultValue){
            String sql = "SELECT "+VALUE+" FROM " + TABLE_NAME +" where " + KEY +" ='" +key +"'";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                byte[] byteData = null;
                // loop through the result set
                while (rs.next()) {
                    byteData = rs.getBytes(1);
                }
                if(byteData!=null){
                    Serializable obj = byteToObject(byteData);
                    return (T) obj;
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){

                }

            }

            return defaultValue;
        }


        private void close(){
            try {
                if (mConnection != null) {
                    mConnection.close();
                }
                mConnection = null;
            } catch (SQLException ex) {

            }
        }
    }


    public static class Queue {
        private final String DATA = "data";
        private final String PRIORITY = "priority";
        private final String QUEUE_KEY = "key";

        private final String QUEUE_TABLE;
        private Connection mConnection;

        private Queue(String tableName,Connection connection){
            QUEUE_TABLE = tableName;
            mConnection = connection;
            String sql = "CREATE TABLE IF NOT EXISTS "+QUEUE_TABLE+" (\n" + QUEUE_KEY + "  text PRIMARY KEY,\n"
                    + PRIORITY + " integer ,"  + DATA + " BLOB);";
            try {
                Statement stmt = mConnection.createStatement();
                stmt.execute(sql);
                stmt.closeOnCompletion();
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }

        public synchronized int size(){
            String sql = "SELECT COUNT("+QUEUE_KEY+") FROM "+ QUEUE_TABLE;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {

                    return rs.getInt(1);
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
            return 0;
        }

        public void push(SimpleDB.QueueData data){
            push(data.key,data.priority,data.data);
        }

        public void push(java.util.List<SimpleDB.QueueData> list){
            String sql = "INSERT or replace INTO "+QUEUE_TABLE+"("+QUEUE_KEY+", "+PRIORITY+", " +DATA+") VALUES(?,?,?)";
            PreparedStatement pstmt = null;
            Connection conn = mConnection;
            try {
                conn.setAutoCommit(false);
                pstmt = conn.prepareStatement(sql);
                for(SimpleDB.QueueData data : list){
                    pstmt.setString(1, data.key);
                    pstmt.setInt(2, data.priority);
                    pstmt.setBytes(3, objectToByte(data.data));
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);

                }catch (Exception ex){

                }

            }
        }

        public synchronized void push(String key, int priority, Serializable data){
            String sql = "INSERT or replace INTO "+QUEUE_TABLE+"("+QUEUE_KEY+", "+PRIORITY+", " +DATA+") VALUES(?,?,?)";
            PreparedStatement pstmt = null;
            try {
                Connection conn = mConnection;
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, key);
                pstmt.setInt(2, priority);
                pstmt.setBytes(3, objectToByte(data));
                pstmt.executeUpdate();

            } catch (SQLException e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }

            }
        }

        public synchronized boolean has(String key){
            String sql = "SELECT *  FROM " + QUEUE_TABLE +" where  " + QUEUE_KEY +"='" +key + "'";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {

                    return true;
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
            return false;
        }

        public SimpleDB.QueueData peek(){
            return getPeekOrRemove(false);
        }

        public SimpleDB.QueueData poll(){
            return getPeekOrRemove(true);
        }

        public java.util.List<QueueData> poll(int size){
            return getPeekOrRemove(true,size);
        }

        private synchronized SimpleDB.QueueData getPeekOrRemove(boolean removePeek){
            java.util.List<QueueData> ret = getPeekOrRemove(removePeek,1);
            if (ret != null && ret.size() > 0) {
                return ret.get(0);
            }
            return null;
        }

        private synchronized java.util.List<QueueData> getPeekOrRemove(boolean removePeek, int size){
            String sql = "SELECT *  FROM " + QUEUE_TABLE +" ORDER BY "+PRIORITY+" desc limit " +size;
            Statement stmt = null;
            ResultSet rs = null;
            Connection conn = mConnection;
            try {
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                ArrayList<QueueData> list = new ArrayList<>();
                while (rs.next()) {
                    byte[] byteData = rs.getBytes(DATA);
                    int p = rs.getInt(PRIORITY);
                    String key = rs.getString(QUEUE_KEY);
                    Serializable obj = byteToObject(byteData);
                    SimpleDB.QueueData data =  new SimpleDB.QueueData();
                    data.data = obj;
                    data.priority = p;
                    data.key = key;
                    list.add(data);
                }

                if(list.isEmpty()){
                    return null;
                }


                if(removePeek){
                    boolean needBatch = list.size() > 1;
                    if(needBatch){
                        conn.setAutoCommit(false);
                    }
                    for(QueueData data: list){
                        stmt.execute("delete  from " + QUEUE_TABLE +" where " + QUEUE_KEY +" ='"+ data.key +"'");
                    }

                    if(needBatch){
                        conn.commit();
                    }
                }



                return list;

            } catch (Exception e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);
                }catch (Exception ex){

                }
            }
        }

        private void close(){
            try {
                if (mConnection != null) {
                    mConnection.close();
                }
                mConnection = null;
            } catch (SQLException ex) {

            }
        }
    }

    public static class QueueData {

        public String key;
        public int priority;
        public Serializable data;
    }


    public static Serializable byteToObject(byte[] bytes) {
        Serializable obj = null;
        try {
            // bytearray to object
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);

            obj = (Serializable) oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return obj;
    }

    public static byte[] objectToByte(Serializable obj) {
        byte[] bytes = null;
        try {
            // object to bytearray
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);

            bytes = bo.toByteArray();

            bo.close();
            oo.close();
        } catch (Exception e) {
            System.out.println("translation" + e.getMessage());
            e.printStackTrace();
        }
        return bytes;
    }

    public static class ListData<T extends Serializable> {

        public int id;
        public T data;
        public long createDate;
        public long modifyDate;

        @Override
        public String toString() {
            return "ListData{" +
                    "data=" + data +
                    '}';
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }
    }



    public static class List<T extends Serializable> {

        private final String F_DATA = "data";
        private final String F_CREATE_DATE = "createDate";
        private final String F_MODIFY_DATE = "modifyDate";
        private final String F_ID = "id";

        private final String LIST_TABLE;
        private Connection mConnection;

        private List(String tableName, Connection connection) {
            LIST_TABLE = tableName;
            mConnection = connection;
            String sql = "CREATE TABLE IF NOT EXISTS " + LIST_TABLE + " ("
                    + F_ID + "  integer  PRIMARY KEY AUTOINCREMENT,\n"
                    + F_DATA + " BLOB,"
                    + F_MODIFY_DATE + " long ," + F_CREATE_DATE + " long);";
            try {
                Statement stmt = mConnection.createStatement();
                stmt.execute(sql);
                stmt.closeOnCompletion();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }

        public void insert(T[] list){
            String sql = "INSERT or replace INTO "+LIST_TABLE+"("+F_DATA+", " +F_MODIFY_DATE+", " +F_CREATE_DATE+") VALUES(?,?,?)";
            PreparedStatement pstmt = null;
            Connection conn = mConnection;
            try {
                conn.setAutoCommit(false);
                pstmt = conn.prepareStatement(sql);

                //ListData[] retLis = new ListData[list.length];
                for(Serializable data : list){
                    long time =System.currentTimeMillis();
                    pstmt.setBytes(1, objectToByte(data));
                    pstmt.setLong(2, time);
                    pstmt.setLong(3, time);
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);

                }catch (Exception ex){

                }

            }
            return;
        }


        public void update(ListData<T>[] datas){
            String sql = "update  "+LIST_TABLE+" set "+F_DATA+" = ?,"+F_MODIFY_DATE+"=? where "+F_ID+" = ?";
            PreparedStatement pstmt = null;
            Connection conn = mConnection;
            try {
                conn.setAutoCommit(false);
                pstmt = conn.prepareStatement(sql);
                for(ListData data : datas) {
                    pstmt.setBytes(1, objectToByte(data.data));
                    pstmt.setLong(2,System.currentTimeMillis());
                    pstmt.setInt(3, data.id);

                    pstmt.addBatch();
                }

                pstmt.executeBatch();
                conn.commit();

            } catch (SQLException e) {
                try {
                    conn.rollback();
                }catch (Exception ex){

                }
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }
                try {
                    conn.setAutoCommit(true);
                }catch (Exception ex){

                }
            }
        }


        /**
         * 删除之前的记录
         * @param beforeModify
         * @param beforeCreate
         */
        public void delete(Date beforeModify, Date beforeCreate) {

            if(beforeCreate == null && beforeModify == null){
                throw new RuntimeException();
            }

            String condition = "";
            boolean needAnd = false;
            if(beforeCreate!= null){
                condition = F_CREATE_DATE + " < " + beforeCreate.getTime();
                needAnd = true;
            }

            if(beforeModify != null) {
                if(needAnd){
                    condition += " and";
                }

                condition += " " + F_MODIFY_DATE +" < " + beforeModify.getTime();

            }

            String sql = "delete from " + LIST_TABLE + " where " + condition;

            deleteBySql(sql);

        }

        public void delete(int[] ids) {

            if(ids == null || ids.length == 0){
                return;
            }

            String sql = "delete from "+LIST_TABLE+" where "+F_ID+" in (";

            StringBuffer sb = new StringBuffer(sql);
            sb.append(ids[0]);
            for(int i=1;i<ids.length;i++) {
                sb.append(","+ids[i]);
            }
            sb.append(")");

            sql = sb.toString();

            deleteBySql(sql);
        }

        private void deleteBySql(String sql){
            Statement pstmt = null;
            try {
                Connection conn = mConnection;
                pstmt = conn.createStatement();
                pstmt.execute(sql);
                //pstmt = conn.prepareStatement(sql);
                // pstmt.executeUpdate();
                // conn.commit();
            } catch (SQLException e) {
                Connection conn = mConnection;

                try {
                    conn.rollback();
                } catch (SQLException ex) {

                }
                throw  new RuntimeException(e);
            }finally {
                Connection conn = mConnection;
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {

                }
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                }catch (Exception ex){

                }

            }
        }

        public java.util.List<ListData<T>> getPage(int offset,int size,boolean desc){

            String orderCondition = desc ? " desc ": " asc ";
            String sql = "SELECT *  FROM " + LIST_TABLE + " order by " + F_CREATE_DATE+orderCondition +" LIMIT  " +size+ "   OFFSET " + offset;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                ArrayList<ListData<T>> list = new ArrayList<>();
                // List<String> hostsList = new ArrayList<>();
                while (rs.next()) {
                    ListData item = new ListData();
                    Serializable data = byteToObject(rs.getBytes(F_DATA));

                    item.data = data;
                    item.createDate = rs.getLong(F_CREATE_DATE);
                    item.modifyDate = rs.getLong(F_MODIFY_DATE);
                    item.id = rs.getInt(F_ID);

                    list.add(item);
                }

                if(list.isEmpty()){
                    return null;
                }

                return list;
            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){

                }

            }
           // return;
        }

        public synchronized int size(){
            String sql = "SELECT COUNT("+F_ID+") FROM "+ LIST_TABLE;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {

                    return rs.getInt(1);
                }

            } catch (Exception e) {
                throw  new RuntimeException(e);
            }finally {
                try {
                    if (rs != null) {
                        rs.close();
                    }
                }catch (Exception ex){

                }
                try {
                    if (stmt != null) {
                        stmt.close();
                    }
                }catch (Exception ex){
                }

            }
            return 0;
        }

    }

}
