package com.juzicoo.ipservcie;


import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

/**
 * KV 键值
 */
public class ProxyIpDB {


    public static void main(String[] args) {
        File sqlFile = new File("proxyIp.db");

        ProxyIpDB db = new ProxyIpDB(sqlFile);
        db.prepare();


        db.close();
    }

    private final String jdbcUrl;
    private Connection mConnection = null;
    private final File mFile;
    private static final String TABLE_NAME = "proxyip";

    public ProxyIpDB(File file){
        mFile = file;
        jdbcUrl = "jdbc:sqlite:" +file.getAbsolutePath();
    }

    public void prepare(){
        // db parameters
        try {
            Class.forName("org.sqlite.JDBC");
            mConnection = DriverManager.getConnection(jdbcUrl);
            String createSql = "CREATE TABLE IF NOT EXISTS "+TABLE_NAME+" (\n" +
                    "   host text  PRIMARY KEY,\n" +
                    "   port integer,\n" +
                    "   rate10 float,\n" +
                    "   token long,\n" +
                    "   extra blob\n" +
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

    public void delateAll(){
        String sql = "delete  FROM " + TABLE_NAME;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = mConnection;
            stmt= conn.createStatement();
            stmt.execute(sql);
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
    }

    public void deletes(String[] hosts){

        if(hosts == null || hosts.length == 0){
            return;
        }

        String sql = "delete from "+TABLE_NAME+" where host in (";

        StringBuffer sb = new StringBuffer(sql);
        sb.append( "'"+ hosts[0] +"'");
        for(int i=1;i<hosts.length;i++) {
           sb.append(","+ "'"+ hosts[i] +"'");
        }
        sb.append(")");

        sql = sb.toString();

        PreparedStatement pstmt = null;
        try {
            Connection conn = mConnection;
           // conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
          /*  for(Juzi juzi :juzis){
                pstmt.setInt(1,(int)juzi.id);
                pstmt.addBatch();
            }*/
            pstmt.executeUpdate();
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


    public List<ProxyIp> get(List<String> hosts) {
        String sql = "SELECT *  FROM " + TABLE_NAME +" WHERE host in (";
        StringBuffer sb = new StringBuffer(sql);
        sb.append( "'"+ hosts.get(0) +"'");
        for(int i=1;i<hosts.size();i++) {
            sb.append(",'"+ hosts.get(i) +"'");
        }
        sb.append(")");
        sql = sb.toString();
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = mConnection;
            stmt= conn.createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<ProxyIp> list = new ArrayList<>();
            // List<String> hostsList = new ArrayList<>();
            while (rs.next()) {
                ProxyIp ip = new ProxyIp();
                ip.setHost(rs.getString("host"));
                ip.setRate10(rs.getFloat("rate10"));
                ip.setPort(rs.getInt("port"));

                Serializable data = byteToObject(rs.getBytes("extra"));
                if(data instanceof HashMap){
                    ip.setExtra((HashMap)data);
                }
                // hostsList.add(ip.getHost());
                list.add(ip);
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
    }

    /***
         * 轮询获取IP。
         * @param size
         * @return
         */
    public List<ProxyIp> next(int size){
        String sql = "SELECT *  FROM " + TABLE_NAME +"  order by token asc LIMIT  " +size+ "   OFFSET " + 0;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Connection conn = mConnection;
            stmt= conn.createStatement();
            rs = stmt.executeQuery(sql);
            ArrayList<ProxyIp> list = new ArrayList<>();
            long maxToken = 0;
           // List<String> hostsList = new ArrayList<>();
            while (rs.next()) {
                ProxyIp ip = new ProxyIp();
                ip.setHost(rs.getString("host"));
                ip.setRate10(rs.getFloat("rate10"));
                ip.setPort(rs.getInt("port"));

                Serializable data = byteToObject(rs.getBytes("extra"));
                if(data instanceof HashMap){
                    ip.setExtra((HashMap)data);
                }
                long token = rs.getLong("token");
                if(token > maxToken){
                    maxToken = token;
                }

               // hostsList.add(ip.getHost());
                list.add(ip);
            }


            if(list.isEmpty()){
                return null;
            }


            //更新token+1


            StringBuffer sb = new StringBuffer("update  "+TABLE_NAME+"  SET token = " + (maxToken+1) + " WHERE host  in (");
            sb.append( "'"+ list.get(0).getHost() +"'");
            for(int i=1;i<list.size();i++) {
                sb.append(","+ "'"+list.get(i).getHost() +"'");
            }
            sb.append(")");

            String updateSql = sb.toString();
            stmt.execute(updateSql);

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



    public void putIfNotExist(Collection<ProxyIp> ipList) {
        //INSERT OR IGNORE
        String sql = "INSERT OR IGNORE INTO "+TABLE_NAME+"(" +
                "host, port,rate10,token, extra) VALUES(?,?,?,?,?)";
        PreparedStatement pstmt = null;
        Connection conn = mConnection;
        try {
            conn.setAutoCommit(false);
            pstmt = conn.prepareStatement(sql);
            for(ProxyIp ip : ipList) {
                pstmt.setString(1, ip.getHost());
                pstmt.setInt(2, ip.getPort());
                pstmt.setFloat(3, ip.getRate10());
                pstmt.setLong(4, 0);
                pstmt.setBytes(5, objectToByte(ip.getExtra()));
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


    public int size(){
        String sql = "SELECT COUNT(host) FROM "+ TABLE_NAME;
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
