package org.example.dao;

import org.example.util.DatabaseHelper;
import org.example.util.exceptions.InvalidCredentialsException;
import org.example.util.exceptions.UserAlreadyExistsException;

import java.sql.*;

public class UserDAO {
    public String validateUser(String email, String password) throws InvalidCredentialsException {
        String sql = "SELECT nickname FROM users WHERE email = ? AND password = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nickname");
                } else {
                    throw new InvalidCredentialsException("Invalid email or password.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Database error");
        }
    }

    public void registerUser(String nickname, String password, String email) throws UserAlreadyExistsException {
        String sql = "INSERT INTO users (nickname, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, password);
            pstmt.setString(3, email);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 23505) {
                throw new UserAlreadyExistsException("A user with this nickname or email already exists.");
            }
            e.printStackTrace();
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
