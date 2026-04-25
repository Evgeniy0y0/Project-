package org.example.util;

public class UserSession {
    private static String currentUserNickname;

    public static void setInstance(String nickname) {
        currentUserNickname = nickname;
    }

    public static String getCurrentUserNickname() {
        return currentUserNickname;
    }

    public static void cleanUserSession() {
        currentUserNickname = null;
    }
}
