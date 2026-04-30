package org.example.util;

/**
 * Global session manager to track the currently authenticated user.
 * Implemented as a thread-safe singleton-like utility.
 */
public class UserSession {

    private static volatile String currentUserNickname;

    private UserSession() {}

    /**
     * Initializes the session with a user's nickname.
     * @param nickname The nickname of the logged-in user.
     */
    public static void setInstance(String nickname) {
        currentUserNickname = nickname;
    }

    /**
     * @return The nickname of the current user, or null if no one is logged in.
     */
    public static String getCurrentUserNickname() {
        return currentUserNickname;
    }

    /**
     * Checks if a user is currently authenticated.
     * @return true if session is active.
     */
    public static boolean isLoggedIn() {
        return currentUserNickname != null;
    }

    /**
     * Clears the current session (logout).
     */
    public static void cleanUserSession() {
        currentUserNickname = null;
    }
}