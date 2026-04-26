package org.example.dao;

import org.example.model.Post;
import org.example.util.DatabaseHelper;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PostDAO {
    public void savePost(String content, String author) {
        String sql = "INSERT INTO posts (content, author_nickname, created_at, updated_at) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            java.sql.Timestamp now = java.sql.Timestamp.valueOf(java.time.LocalDateTime.now());

            pstmt.setString(1, content);
            pstmt.setString(2, author);
            pstmt.setTimestamp(3, now);
            pstmt.setTimestamp(4, now);

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

                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return posts;
    }

    public List<Post> searchByAuthor(String authorQuery, String currentNickname) {
        List<Post> results = new ArrayList<>();
        String sql = "SELECT p.*, l.user_nickname AS liked_indicator " +
                "FROM posts p " +
                "LEFT JOIN post_likes l ON p.post_id = l.post_id AND l.user_nickname = ? " +
                "WHERE p.author_nickname LIKE ? " +
                "ORDER BY p.post_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentNickname);
            pstmt.setString(2, "%" + authorQuery + "%");

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post post = new Post(rs.getString("author_nickname"), rs.getString("content"));
                post.setId(rs.getInt("post_id"));
                post.setLikes(rs.getInt("likes_count"));
                post.setLikedByMe(rs.getString("liked_indicator") != null);
                post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                results.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    public void updatePost(int postId, String newContent) {
        String sql = "UPDATE posts SET content = ?, updated_at = ? WHERE post_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setInt(3, postId);

            int rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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

    public List<Post> getTopPosts(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY likes_count DESC LIMIT ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Post p = new Post(rs.getString("author_nickname"), rs.getString("content"));
                p.setId(rs.getInt("post_id"));
                p.setLikes(rs.getInt("likes_count"));
                p.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                p.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                posts.add(p);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return posts;
    }

    public static class AuthorStats {
        private final String nickname;
        private final int count;
        public AuthorStats(String nickname, int count) { this.nickname = nickname; this.count = count; }
        public String getNickname() { return nickname; }
        public int getCount() { return count; }
    }

    public List<AuthorStats> getTopAuthors(int limit) {
        List<AuthorStats> stats = new ArrayList<>();
        String sql = "SELECT author_nickname, COUNT(*) as cnt FROM posts GROUP BY author_nickname ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                stats.add(new AuthorStats(rs.getString("author_nickname"), rs.getInt("cnt")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return stats;
    }
}

