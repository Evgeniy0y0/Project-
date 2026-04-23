package org.example.dao;

import org.example.model.Post;
import org.example.util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    public void save(Post post) {
        String sql = "INSERT INTO posts (author_nickname, content, likes_count) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, post.getAuthor());
            pstmt.setString(2, post.getContent());
            pstmt.setInt(3, post.getLikes());

            pstmt.executeUpdate();
            System.out.println("Success: Post has been saved to H2 database.");

        } catch (SQLException e) {
            System.err.println("Error: Failed to save post - " + e.getMessage());
        }
    }

    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT author_nickname, content, likes_count FROM posts";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String author = rs.getString("author_nickname");
                String content = rs.getString("content");
                int likes = rs.getInt("likes_count");

                posts.add(new Post(author, content));
            }
        } catch (SQLException e) {
            System.err.println("Error: Could not retrieve posts - " + e.getMessage());
        }
        return posts;
    }
}

