package org.example.model;

public class Post {
    private String author;
    private String content;
    private int likes;

    public Post(String author, String content) {
        this.author = author;
        this.content = content;
        this.likes = 0;
    }

    public String getAuthor() { return author; }
    public String getContent() { return content; }
    public int getLikes() { return likes; }
}