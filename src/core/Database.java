package core;

import java.sql.*;
import java.time.LocalDate;

public class Database {
    // singleton class
    private static Database db = null;
    private static final String dbname = "assistant.db";
    private Connection conn;

    private Database() {
        connect();
        createWeatherTable();
        createTodoTable();
        createEventsTable();
    }

    public static Database getdb() {
        if (db == null)
            db = new Database();
        return db;
    }

    private void connect() {
        String connection_url = "jdbc:sqlite:" + dbname;
        try {
            conn = DriverManager.getConnection(connection_url);
            System.out.println("Connection with database established");

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
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

    private void createEventsTable() {
        String sql = "CREATE TABLE IF NOT EXISTS events (" +
                "description text," +
                "event_date text" +
                ");";
        executeStatement(sql);
    }

    public ResultSet getEventsList() {
        LocalDate now = LocalDate.now();
        String today = now.toString();
        String sql = "SELECT * FROM events WHERE event_date >= ? ORDER BY event_date";
        ResultSet rs = null;

        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, today);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return rs;
    }

    public boolean insertEvent(String desc, String date) {
        String sql = "INSERT INTO events VALUES(?, ?)";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, desc);
            stmt.setString(2, date);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteEvent(String desc, String date) {
        String sql = "DELETE FROM events WHERE description = ? AND event_date = ?";
        try {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, desc);
            stmt.setString(2, date);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
