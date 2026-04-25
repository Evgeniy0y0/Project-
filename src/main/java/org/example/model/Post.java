package org.example.model;

public class Post {
    private int id;
    private String author;
    private String content;
    private int likes;
    private boolean likedByMe;

    public Post(String author, String content) {
        this.author = author;
        this.content = content;
        this.likes = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getAuthor() { return author; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getLikes() { return likes; }
    public void setLikes(int likes) { this.likes = likes; }

    public boolean isLikedByMe() { return likedByMe; }
    public void setLikedByMe(boolean likedByMe) { this.likedByMe = likedByMe; }

}
