package com.juzicool.data.db;


import com.juzicool.data.Juzi;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * KV 键值
 */
public class JuziDB {


    public static void main(String[] args) {
        File sqlFile = new File("juzid.db");

        JuziDB db = new JuziDB(sqlFile);
        db.prepare();

        for(int i = 0; i< 10;i++){
            Juzi juzi = new Juzi();
            juzi.content = i+"_content";
            juzi.from = i+"_from";

            db.put(juzi);
            System.out.println("put juzi: " + juzi.toString());
        }

        System.out.println("juzi size: " + db.size());

        JuziDB.Iterator it = db.createIterator();
        List<Juzi> rets = it.next(3);

        for(Juzi juzi:rets){
            System.out.println("get juzi: " + juzi.toString());
        }
        System.out.println("juzi size: " + db.size());

        rets = it.next(1);

        for(Juzi juzi:rets){
            System.out.println("get juzi: " + juzi.toString());
        }
        System.out.println("juzi size: " + db.size());

        rets = it.next(12);

        for(Juzi juzi:rets){
            System.out.println("get juzi: " + juzi.toString());
        }
        System.out.println("juzi size: " + db.size());

        rets = it.next(1);
        System.out.println("rets == null : " + (rets == null));

        db.close();
    }

    private final String jdbcUrl;
    private Connection mConnection = null;
    private final File mFile;
    private static final String TABLE_NAME = "juzi";

    public JuziDB(File file){
        mFile = file;
        jdbcUrl = "jdbc:sqlite:" +file.getAbsolutePath();
    }

    public void prepare(){
        // db parameters
        try {
            mConnection = DriverManager.getConnection(jdbcUrl);
            String createSql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\n" +
                    "   id integer  PRIMARY KEY AUTOINCREMENT,\n" +
                    "   content text,\n" +
                    "   author text,\n" +
                    "   _from text,\n" +
                    "   category text,\n" +
                    "   remark text,\n" +
                    "   tags text,\n" +
                    "   applyTags text\n" +
                    ");";

            Statement stmt = mConnection.createStatement();
            stmt.execute(createSql);
            stmt.closeOnCompletion();
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }

    }

    public void close(){
        try {
            if (mConnection != null) {
                mConnection.close();
            }
            mConnection = null;
        } catch (SQLException ex) {

        }
    }


    public void put(Juzi juzi){
        String sql = "INSERT or replace INTO "+TABLE_NAME+"(" +
                "id, content,author,_from,category, remark,tags, applyTags) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement pstmt = null;
        try {
            Connection conn = mConnection;
            pstmt = conn.prepareStatement(sql);
           // pstmt.setLong(1, juzi.id);
            pstmt.setString(2, juzi.content);
            pstmt.setString(3, juzi.author);
            pstmt.setString(4, juzi.from);
            pstmt.setString(5, juzi.category);
            pstmt.setString(6, juzi.remark);
            pstmt.setString(7, juzi.tags);
            pstmt.setString(8, juzi.applyTags);
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

    public int size(){
        String sql = "SELECT COUNT(id) FROM "+ TABLE_NAME;
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

    public Iterator createIterator(){
        return new Iterator(mConnection);
    }

    private static class Iterator{

        private Connection mConnection = null;
        private int offset = 0;

        private  Iterator(Connection con){
            this.mConnection = con;
        }

        public List<Juzi> next(int size){
            String sql = "SELECT *  FROM " + TABLE_NAME +" LIMIT  " +size+ "   OFFSET " + offset;
            Statement stmt = null;
            ResultSet rs = null;
            try {
                Connection conn = mConnection;
                stmt= conn.createStatement();
                rs = stmt.executeQuery(sql);
                ArrayList<Juzi> list = new ArrayList<>();
                while (rs.next()) {
                    Juzi juzi = new Juzi();
                    juzi.id = rs.getLong("id");
                    juzi.content = rs.getString("content");
                    juzi.author = rs.getString("author");
                    juzi.from = rs.getString("_from");
                    juzi.category = rs.getString("category");
                    juzi.tags = rs.getString("tags");
                    juzi.remark = rs.getString("remark");
                    juzi.applyTags = rs.getString("applyTags");
                    list.add(juzi);
                }
                if(list.isEmpty()){
                    return null;
                }
                offset += list.size();
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
            //return ;
        }
    }




}
