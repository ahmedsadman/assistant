package core;

import java.sql.*;

public class Database {

    private static final String dbname = "assistant.db";
    private Connection conn;

    public Database() {
        connect();
        createWeatherTable();
    }

    private Connection connect() {
        if (conn != null) return conn;
        String connection_url = "jdbc:sqlite:" + dbname;
        try {
            conn = DriverManager.getConnection(connection_url);
            System.out.println("Connection with database established");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    private boolean executeStatement(String sql) {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }

    private void createWeatherTable() {
        String sql = "CREATE TABLE IF NOT EXISTS weather (" +
                "location text" +
                ");";
        executeStatement(sql);
    }

    public void updateWeatherLocation(String loc) {
        String sql = "UPDATE weather SET location = ?;";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, loc);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getWeatherLocation() {
        String sql = "SELECT * FROM weather";
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            return rs.getString("location");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) {
        Database db = new Database();
        System.out.println(db.getWeatherLocation());
    }
}
