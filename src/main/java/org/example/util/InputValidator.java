package org.example.util;

public class InputValidator {

    public static void validateRegistration(String nick, String email, String pass) throws Exception {
        if (nick == null || nick.length() < 3) {
            throw new Exception("Nickname must be at least 3 characters long.");
        }

        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new Exception("Please enter a valid email address (e.g., user@example.com).");
        }

        if (pass == null || pass.length() < 6) {
            throw new Exception("Password must be at least 6 characters long.");
        }
    }
}