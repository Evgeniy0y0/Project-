package org.example.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseHelper {
    private static final Properties properties = new Properties();

    static {
        try (InputStream is = DatabaseHelper.class.getResourceAsStream("/db.properties")) {
            if (is != null) {
                properties.load(is);
                System.out.println("Config loaded");
            } else {
                System.err.println("Error: file db.properties not found in target/classes");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.user"),
                properties.getProperty("db.password")
        );
    }

    public static void initializeDatabase() {
        String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
                "nickname VARCHAR(100) PRIMARY KEY, " +
                "password VARCHAR(100) NOT NULL, " +
                "email VARCHAR(100) UNIQUE NOT NULL);";

        String createPosts = "CREATE TABLE IF NOT EXISTS posts (" +
                "post_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "author_nickname VARCHAR(100) REFERENCES users(nickname), " +
                "content TEXT NOT NULL, " +
                "likes_count INT DEFAULT 0);";

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(createUsers);
            stmt.execute(createPosts);

            var rs = conn.getMetaData().getTables(null, null, "USERS", null);
            if (rs.next()) {
                System.out.println("USERS in base.");
            }

            System.out.println("H2 Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("DB Initialization error: " + e.getMessage());
        }
    }
}