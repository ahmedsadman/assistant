package core;

import java.sql.*;

public class Database {
    // singleton class
    private static Database db = null;
    private static final String dbname = "assistant.db";
    private Connection conn;

    private Database() {
        connect();
        createWeatherTable();
        createTodoTable();
    }

    public static Database getdb() {
        if (db == null)
            db = new Database();
        return db;
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

    private void createTodoTable() {
        String sql = "CREATE TABLE IF NOT EXISTS todo (" +
                "description text" +
                ");";
        executeStatement(sql);
    }

    public ResultSet getTodoList() {
        String sql = "SELECT * FROM todo";
        ResultSet rs = null;
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rs;
    }

    public boolean insertTodo(String desc) {
        String sql = "INSERT INTO todo VALUES(?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, desc);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTodo(String desc) {
        String sql = "DELETE FROM todo WHERE description = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, desc);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        // System.out.println(db.getWeatherLocation());
    }
}
