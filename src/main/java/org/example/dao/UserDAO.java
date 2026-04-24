package org.example.dao;

import org.example.util.DatabaseHelper;
import java.sql.*;

public class UserDAO {
    public boolean validateUser(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            System.err.println("Database error during validation: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(String nickname, String password, String email) {
        String sql = "INSERT INTO users (nickname, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, password);
            pstmt.setString(3, email);

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Registration error: " + e.getMessage());
            return false;
        }
    }

    public void saveUser(String nickname, String email, String password) {
        String sql = "INSERT INTO users (nickname, email, password) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, email);
            pstmt.setString(3, password);

            pstmt.executeUpdate();
            System.out.println("Success: User '" + nickname + "' created.");

        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                System.out.println("Info: User '" + nickname + "' already exists.");
            } else {
                System.err.println("Error creating user: " + e.getMessage());
            }
        }
    }
}