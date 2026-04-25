package org.example.dao;

import org.example.model.Post;
import org.example.util.DatabaseHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    public void savePost(String content, String author) {
        String sql = "INSERT INTO posts (author_nickname, content, likes_count) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, author);
            pstmt.setString(2, content);
            pstmt.setInt(3, 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Post> getAllPosts(String currentNickname) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, l.user_nickname AS liked_indicator " +
                "FROM posts p " +
                "LEFT JOIN post_likes l ON p.post_id = l.post_id AND l.user_nickname = ? " +
                "ORDER BY p.post_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentNickname);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Post post = new Post(
                        rs.getString("author_nickname"),
                        rs.getString("content")
                );
                post.setId(rs.getInt("post_id"));
                post.setLikes(rs.getInt("likes_count"));

                post.setLikedByMe(rs.getString("liked_indicator") != null);

                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

    public void deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void toggleLike(int postId, String nickname) {
        String checkSql = "SELECT 1 FROM post_likes WHERE post_id = ? AND user_nickname = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            boolean alreadyLiked;
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                checkStmt.setInt(1, postId);
                checkStmt.setString(2, nickname);
                alreadyLiked = checkStmt.executeQuery().next();
            }

            if (alreadyLiked) {
                String deleteLike = "DELETE FROM post_likes WHERE post_id = ? AND user_nickname = ?";
                String decreaseCount = "UPDATE posts SET likes_count = likes_count - 1 WHERE post_id = ?";

                try (PreparedStatement ps1 = conn.prepareStatement(deleteLike);
                     PreparedStatement ps2 = conn.prepareStatement(decreaseCount)) {
                    ps1.setInt(1, postId);
                    ps1.setString(2, nickname);
                    ps1.executeUpdate();

                    ps2.setInt(1, postId);
                    ps2.executeUpdate();
                }
            } else {
                String insertLike = "INSERT INTO post_likes (post_id, user_nickname) VALUES (?, ?)";
                String increaseCount = "UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?";

                try (PreparedStatement ps1 = conn.prepareStatement(insertLike);
                     PreparedStatement ps2 = conn.prepareStatement(increaseCount)) {
                    ps1.setInt(1, postId);
                    ps1.setString(2, nickname);
                    ps1.executeUpdate();

                    ps2.setInt(1, postId);
                    ps2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

