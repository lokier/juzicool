package com.juzicool.gather;

import java.io.*;
import java.sql.*;
import java.util.HashMap;

/**
 * KV 键值
 */
public class DB {
    private static HashMap<File,DB> gMap = new HashMap<>();

    public static DB request(File file){
        DB db = gMap.get(file);
        if(db == null) {
            db = new DB(file);
            try {
                db.initConnection();
                gMap.put(file, db);
            } catch (SQLException e) {
                db.close();
                throw new RuntimeException(e);
            }


        }
        return db;
    }

    public static void release(DB db) {
        if(db == null){
            return;
        }
        db.close();
        gMap.remove(db.mFile);
    }

    private final String jdbcUrl;
    private Connection mConnection = null;
    private final File mFile;

    private DB(File file){
        mFile = file;
        jdbcUrl = "jdbc:sqlite:" +file.getAbsolutePath();
    }

    private Queue mQueue = null;

    public Queue getQueue(){
        if(mQueue == null){
            mQueue = new Queue(mConnection);
        }
        return mQueue;
    }


    public void putKv(String key, Serializable value){
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

    public <T extends Serializable> T getKv(String key,T defaultValue){
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

    private void close(){
        try {
            if (mConnection != null) {
                mConnection.close();
            }
            mConnection = null;
        } catch (SQLException ex) {

        }
    }

    private static final String KEY = "key";
    private static final String VALUE = "value";
    private static final String TABLE_NAME = "kv_db";

    private void initConnection()throws SQLException{

            // db parameters
            mConnection = DriverManager.getConnection(jdbcUrl);
            String sql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\n" + KEY + "  text PRIMARY KEY,\n"
                    + VALUE + " BLOB);";
            Statement stmt = mConnection.createStatement();
            stmt.execute(sql);
            stmt.closeOnCompletion();

    }

    public static class Queue {
        private final String DATA = "data";
        private final String PRIORITY = "priority";
        private final String QUEUE_KEY = "key";

        private final String QUEUE_TABLE = "queue_db";
        private Connection mConnection = null;

        private Queue(Connection connection){

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

        public void push(QueueData data){
            push(data.key,data.priority,data.data);
        }

        public void push(String key,int priority,Serializable data){
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

        public  QueueData poll(){
            String sql = "SELECT *  FROM " + QUEUE_TABLE +"ORDER BY "+PRIORITY+" desc limit 1";
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
                    QueueData data =  new QueueData();
                    data.data = obj;
                    data.priority = p;
                    data.key = key;
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
    }

    public static class QueueData {

        public String key;
        public int priority;
        public Serializable data;
    }


    public static void main(String[] args) {
        File sqlFile = new File("connect.db");

        DB db = DB.request(sqlFile);
        db.putKv("name","sser");
        db.putKv("name2",234);

        String name = db.getKv("name",null);
        Integer integr = db.getKv("name2",null);

        System.out.println(String.format("name:%s,name2:%d",name,integr));
        System.out.println("null: " + db.getKv("www","shout null"));

        DB.release(db);
    }

}
