package com.juzicool.gather;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;

public class SimpleDB {

    public static void main(String[] args) {
        File sqlFile = new File("connect.db");

        SimpleDB db = new SimpleDB();
        db.openFile(sqlFile);
        db.KV().put("name","sser");
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

        db.Queue().push("dww",5,"xcfdsfd");
        db.Queue().push("3442",9,"11111");
        db.Queue().push("3444",6,"333333");

        System.out.println("queue size : " + db.Queue().size());

        DB.QueueData data = db.Queue().poll();
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


        data = db.Queue().poll();
        System.out.println("queue size : " + db.Queue().size());
        System.out.println("data is null: " + (data == null));

        db.close();
    }

    private  File mFile;
    private  String jdbcUrl;
    private KV mKv;
    private Queue mQueue;

    private ArrayList<Queue> extraQueues = new ArrayList();

    public SimpleDB(){

    }

    public void openFile(File file){
        mFile = file;
        jdbcUrl = "jdbc:sqlite:" +file.getAbsolutePath();
        try {
            mQueue =new Queue("simple_db_queue",createConnection());
            mKv = new KV(createConnection());
           // mKv.prepare();
        } catch (SQLException e) {
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
    }

    private Connection createConnection()throws SQLException{

        // db parameters
        Connection con = DriverManager.getConnection(jdbcUrl);

        return con;
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

        public boolean has(String key){
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

        public void put(String key, Serializable value){
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

        public <T extends Serializable> T get(String key,T defaultValue){
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

        public int size(){
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

        public void push(DB.QueueData data){
            push(data.key,data.priority,data.data);
        }

        public void push(String key, int priority, Serializable data){
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

        public boolean has(String key){
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

        public DB.QueueData peek(){
            return getPeekOrRemove(false);
        }

        public DB.QueueData poll(){

            return getPeekOrRemove(true);
        }

        private DB.QueueData getPeekOrRemove(boolean removePeek){
            String sql = "SELECT *  FROM " + QUEUE_TABLE +" ORDER BY "+PRIORITY+" desc limit 1";
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                // loop through the result set
                if (rs.next()) {
                    byte[] byteData = rs.getBytes(DATA);
                    int p = rs.getInt(PRIORITY);
                    String key = rs.getString(QUEUE_KEY);
                    Serializable obj = byteToObject(byteData);
                    DB.QueueData data =  new DB.QueueData();
                    data.data = obj;
                    data.priority = p;
                    data.key = key;

                    if(removePeek){
                        stmt.execute("delete  from " + QUEUE_TABLE +" where " + QUEUE_KEY +" ='"+ data.key +"'");
                    }

                    return data;
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
            return  null;
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

}
