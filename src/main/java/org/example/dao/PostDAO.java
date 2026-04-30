package org.example.dao;

import org.example.model.Post;
import org.example.util.DatabaseHelper;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Post entities.
 * Handles CRUD operations and interaction with post_likes table.
 */
public class PostDAO {
    /**
     * Saves a new post to the database.
     *
     * @param content The text content of the post.
     * @param author  The nickname of the post creator.
     */
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
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves all posts including an indicator if the current user has liked them.
     *
     * @param currentNickname The nickname of the logged-in user.
     * @return List of posts sorted by ID descending.
     */
    public List<Post> getAllPosts(String currentNickname) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT p.*, l.user_nickname AS liked_indicator " +
                "FROM posts p " +
                "LEFT JOIN post_likes l ON p.post_id = l.post_id AND l.user_nickname = ? " +
                "ORDER BY p.post_id DESC";

        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, currentNickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapRowToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    /**
     * Searches for posts by author's nickname using a partial match.
     */
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

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    results.add(mapRowToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    /**
     * Updates an existing post's content and sets the updated_at timestamp.
     */
    public void updatePost(int postId, String newContent) {
        String sql = "UPDATE posts SET content = ?, updated_at = ? WHERE post_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newContent);
            pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(java.time.LocalDateTime.now()));
            pstmt.setInt(3, postId);

            int rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes a post from the database by ID.
     */
    public void deletePost(int postId) {
        String sql = "DELETE FROM posts WHERE post_id = ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, postId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds or removes a like and updates likes_count in a single transaction.
     */
    public void toggleLike(int postId, String nickname) {
        String checkSql = "SELECT 1 FROM post_likes WHERE post_id = ? AND user_nickname = ?";

        try (Connection conn = DatabaseHelper.getConnection()) {
            conn.setAutoCommit(false);
            try {
                boolean alreadyLiked;
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, postId);
                    checkStmt.setString(2, nickname);
                    alreadyLiked = checkStmt.executeQuery().next();
                }

                if (alreadyLiked) {
                    executeLikeUpdate(conn,
                            "DELETE FROM post_likes WHERE post_id = ? AND user_nickname = ?",
                            "UPDATE posts SET likes_count = likes_count - 1 WHERE post_id = ?",
                            postId, nickname);
                } else {
                    executeLikeUpdate(conn,
                            "INSERT INTO post_likes (post_id, user_nickname) VALUES (?, ?)",
                            "UPDATE posts SET likes_count = likes_count + 1 WHERE post_id = ?",
                            postId, nickname);
                }
                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Retrieves the most liked posts.
     */
    public List<Post> getTopPosts(int limit) {
        List<Post> posts = new ArrayList<>();
        String sql = "SELECT * FROM posts ORDER BY likes_count DESC LIMIT ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    posts.add(mapRowToPost(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return posts;
    }

    /**
     * Calculates posts count per author.
     */
    public List<AuthorStats> getTopAuthors(int limit) {
        List<AuthorStats> stats = new ArrayList<>();
        String sql = "SELECT author_nickname, COUNT(*) as cnt FROM posts GROUP BY author_nickname ORDER BY cnt DESC LIMIT ?";
        try (Connection conn = DatabaseHelper.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    stats.add(new AuthorStats(rs.getString("author_nickname"), rs.getInt("cnt")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return stats;
    }

    /**
     * Helper to map a ResultSet row to a Post object to avoid code duplication.
     */
    private Post mapRowToPost(ResultSet rs) throws SQLException {
        Post post = new Post(rs.getString("author_nickname"), rs.getString("content"));
        post.setId(rs.getInt("post_id"));
        post.setLikes(rs.getInt("likes_count"));

        try {
            post.setLikedByMe(rs.getString("liked_indicator") != null);
        } catch (SQLException ignored) {
        }

        post.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        post.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return post;
    }

    private void executeLikeUpdate(Connection conn, String sql1, String sql2, int postId, String nickname) throws SQLException {
        try (PreparedStatement ps1 = conn.prepareStatement(sql1);
             PreparedStatement ps2 = conn.prepareStatement(sql2)) {
            ps1.setInt(1, postId);
            if (sql1.contains("post_likes")) ps1.setString(2, nickname);
            ps1.executeUpdate();
            ps2.setInt(1, postId);
            ps2.executeUpdate();
        }
    }

    public static class AuthorStats {
        private final String nickname;
        private final int count;
        public AuthorStats(String nickname, int count) { this.nickname = nickname; this.count = count; }
        public String getNickname() { return nickname; }
        public int getCount() { return count; }
    }
}

