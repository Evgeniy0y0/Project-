package org.example.util;

import java.util.regex.Pattern;

/**
 * Utility class for user input validation.
 * Ensures that data meets business requirements before hitting the database.
 */
public class InputValidator {

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

    /**
     * Validates user registration data.
     * @param nick User's nickname
     * @param email User's email address
     * @param pass User's password
     * @throws IllegalArgumentException if any field fails validation
     */
    public static void validateRegistration(String nick, String email, String pass) {
        if (nick == null || nick.trim().length() < 3) {
            throw new IllegalArgumentException("Nickname must be at least 3 characters long.");
        }

        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Please enter a valid email address (e.g., user@example.com).");
        }

        if (pass == null || pass.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
    }
}