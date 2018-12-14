package sqlite;

import java.io.File;
import java.sql.*;

public class DemoDB {

    public static void main(String[] args) {
        createNewDatabase("D:/software/sqlite/create-db.db");
    }

    public static void createNewDatabase(String fileName) {
        File sqlFile = new File("connect.db");
        String url = "jdbc:sqlite:" + sqlFile.getAbsolutePath();

        try {
            Connection conn = DriverManager.getConnection(url);
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(String name, double capacity) {
        String sql = "INSERT INTO employees(name, capacity) VALUES(?,?)";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, capacity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void selectAll() {
        String sql = "SELECT * FROM employees";

        try {
            Connection conn = connect();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            // loop through the result set
            while (rs.next()) {
                System.out.println(rs.getInt("id") + "\t" + rs.getString("name") + "\t" + rs.getDouble("capacity"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static Connection connect() {
        Connection conn = null;
        File sqlFile = new File("connect.db");
        try {
            // db parameters
            String url = "jdbc:sqlite:" +sqlFile.getAbsolutePath();
            // create a connection to the database
            conn = DriverManager.getConnection(url);

            return conn;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return null;
    }

    public static void createNewTable() {
        // SQLite connection string
        File sqlFile = new File("connect.db");
        String url = "jdbc:sqlite:" + sqlFile.getAbsolutePath();

        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS employees (\n" + " id integer PRIMARY KEY,\n"
                + " name text NOT NULL,\n" + " capacity real\n" + ");";

        try {
            Connection conn = DriverManager.getConnection(url);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            System.out.println("Create table finished.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


}