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
        String sql = "SELECT post_id, author_nickname, content, likes_count FROM posts";

        try (Connection conn = DatabaseHelper.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Post post = new Post(rs.getString("author_nickname"), rs.getString("content"));
                post.setId(rs.getInt("post_id"));
                post.setLikes(rs.getInt("likes_count"));
                posts.add(post);
            }
        } catch (SQLException e) {
            System.err.println("Error: Could not retrieve posts - " + e.getMessage());
        }
        return posts;
    }

    public List<Post> getPostsByAuthor(String authorNickname) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT author_nickname, content, likes_count FROM posts WHERE author_nickname = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, authorNickname);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                posts.add(new Post(rs.getString("author_nickname"), rs.getString("content")));
            }
        } catch (SQLException e) {
            System.err.println("Error: Fetching posts by author failed - " + e.getMessage());
        }
        return posts;
    }

    public void addLike(int postId) {
        String sql = "UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?";
        executeSimpleUpdate(sql, postId, "Post liked!");
    }

    public void removeLike(int postId) {
        String sql = "UPDATE posts SET likes_count = likes_count - 1 WHERE post_id = ? AND likes_count > 0";
        executeSimpleUpdate(sql, postId, "Like removed.");
    }

    private void executeSimpleUpdate(String sql, int postId, String successMsg) {
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.println("Success: " + successMsg);
        } catch (SQLException e) {
            System.err.println("Error: Operation failed - " + e.getMessage());
        }
    }

    public void updatePostContent(int postId, String newContent) {
        String sql = "UPDATE posts SET content = ? WHERE post_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setInt(2, postId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Success: Post #" + postId + " content updated.");
            }
        } catch (SQLException e) {
            System.err.println("Error: Update failed - " + e.getMessage());
        }
    }

    public void deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, postId);

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Success: Post #" + postId + " deleted.");
            }
        } catch (SQLException e) {
            System.err.println("Error: Delete failed - " + e.getMessage());
        }
    }
}

