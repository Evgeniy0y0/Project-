package org.example.dao;

import org.example.util.DatabaseHelper;
import java.sql.*;

public class UserDAO {
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