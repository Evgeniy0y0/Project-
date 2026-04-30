package org.example.model;

import java.time.LocalDateTime;

/**
 * Represents a social network post entity.
 * Contains information about the author, content, likes, and timestamps.
 */
public class Post {
    private int id;
    private String author;
    private String content;
    private int likes;
    private boolean likedByMe;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Default constructor for frameworks or manual initialization.
     */
    public Post() {}

    /**
     * Constructor for creating a new post with minimal required data.
     * @param author Nickname of the creator.
     * @param content Text content of the post.
     */
    public Post(String author, String content) {
        this.author = author;
        this.content = content;
        this.likes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    /**
     * Helper method to check if the post was edited.
     * @return true if updated_at is different from created_at.
     */
    public boolean isEdited() {
        if (createdAt == null || updatedAt == null) return false;
        return !createdAt.equals(updatedAt);
    }
}