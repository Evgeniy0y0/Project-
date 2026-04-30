package org.example.dao;

import org.example.util.DatabaseHelper;
import org.example.util.exceptions.InvalidCredentialsException;
import org.example.util.exceptions.UserAlreadyExistsException;

import java.sql.*;

/**
 * Data Access Object for User entities.
 * Manages user authentication and registration.
 */
public class UserDAO {

    /**
     * Validates user credentials.
     * @param email User's email.
     * @param password User's plain text password.
     * @return User's nickname if credentials are valid.
     * @throws InvalidCredentialsException if email or password does not match.
     */
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Registers a new user in the database.
     * @param nickname Unique username.
     * @param password Plain text password.
     * @param email Unique email address.
     * @throws UserAlreadyExistsException if nickname or email is already taken.
     */
    public void registerUser(String nickname, String password, String email) throws UserAlreadyExistsException {
        String sql = "INSERT INTO users (nickname, password, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nickname);
            pstmt.setString(2, password);
            pstmt.setString(3, email);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                throw new UserAlreadyExistsException("A user with this nickname or email already exists.");
            }
            throw new RuntimeException(e);
        }
    }
}
