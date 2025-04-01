package org.example.db;

import org.example.User;  // If you have a User class

import java.sql.*;

public class MyJDBC {
    // Database configuration
    private static final String DB_URL = "YourSQLServerURL";
    private static final String DB_USER = "youruser";
    private static final String DB_PASSWORD = "yourpassword";

    public static User validateLogin(String username, String password) {
        User user = null;

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public static boolean registerUser(String fullName, String email, String username,
                                       String password, String phone) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Check if username exists first
            if (isUsernameTaken(conn, username)) {
                return false;
            }

            String sql = "INSERT INTO users (full_name, email, username, password, phone) " +
                    "VALUES (?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, fullName);
            pstmt.setString(2, email);
            pstmt.setString(3, username);
            pstmt.setString(4, password);
            pstmt.setString(5, phone);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isUsernameTaken(Connection conn, String username) throws SQLException {
        String sql = "SELECT username FROM users WHERE username = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }

    public static boolean updateBalance(int userId, double amount) {
        String sql = "UPDATE users SET balance = balance + ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDouble(1, amount);
            pstmt.setInt(2, userId);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.err.println("Update balance error: " + e.getMessage());
            return false;
        }
    }

    public static double getUserBalance(int userId) {
        String sql = "SELECT balance FROM users WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getDouble("balance");
            }
            return 0.00;

        } catch (SQLException e) {
            System.err.println("Get balance error: " + e.getMessage());
            return 0.00;
        }
    }

    public static boolean transferFunds(int senderId, String recipientUsername, double amount) {
        String sqlDebit = "UPDATE users SET balance = balance - ? WHERE id = ?";
        String sqlCredit = "UPDATE users SET balance = balance + ? WHERE username = ?";
     try(Connection conn = DriverManager.getConnection(DB_URL,DB_USER,DB_PASSWORD)) {
     conn.setAutoCommit(false);
         try (PreparedStatement debitStmt = conn.prepareStatement(sqlDebit);
              PreparedStatement creditStmt = conn.prepareStatement(sqlCredit)) {

             debitStmt.setDouble(1, amount);
             debitStmt.setInt(2, senderId);
             int rowsDebited = debitStmt.executeUpdate();

             creditStmt.setDouble(1, amount);
             creditStmt.setString(2, recipientUsername);
             int rowsCredited = creditStmt.executeUpdate();

             if(rowsDebited>0 && rowsCredited>0) {
             conn.commit();
             return true;
             } else {
             conn.rollback();
             return false;
             }
         }
         } catch (SQLException e) {
         System.err.println("Transfer error: " + e.getMessage());
         return false;
     }
    }

public static boolean userExists(String username) {
    String sql = "SELECT id FROM users WHERE username = ?";

    try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, username);
        ResultSet rs = pstmt.executeQuery();
        return rs.next();
    }   catch (SQLException e){
        System.err.println("User does not exist: " + e.getMessage());
        return false;
    }
}
}