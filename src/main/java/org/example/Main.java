package org.example;

import org.example.dao.PostDAO;
import org.example.dao.UserDAO;
import org.example.model.Post;
import org.example.util.DatabaseHelper;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DatabaseHelper.initializeDatabase();
        UserDAO userDAO = new UserDAO();
        PostDAO postDAO = new PostDAO();

        userDAO.saveUser("Jenek", "jenek@tech.com", "pass123");

        System.out.println("\nStep 1: Creating a post...");
        postDAO.save(new Post("Jenek", "Original content from laptop"));

        System.out.println("\nStep 2: Fetching posts to get ID...");
        List<Post> posts = postDAO.getAllPosts();
        if (posts.isEmpty()) {
            System.out.println("Error: No posts found!");
            return;
        }

        Post myPost = posts.get(posts.size() - 1);
        int targetId = myPost.getId();
        System.out.println("Target Post ID: " + targetId + " | Current Likes: " + myPost.getLikes());

        System.out.println("\nStep 3: Updating post (Like + Edit)...");
        postDAO.addLike(targetId);
        postDAO.updatePostContent(targetId, "Updated content: Everything works perfectly!");

        System.out.println("\nStep 4: Verifying updates...");
        List<Post> updatedPosts = postDAO.getAllPosts();
        updatedPosts.stream()
                .filter(p -> p.getId() == targetId)
                .forEach(p -> System.out.println("Verified -> ID: " + p.getId() + " | Content: " + p.getContent() + " | Likes: " + p.getLikes()));

        System.out.println("\nStep 5: Testing search by author 'Jenek'...");
        List<Post> jeneksPosts = postDAO.getPostsByAuthor("Jenek");
        System.out.println("Found " + jeneksPosts.size() + " posts for Jenek.");

        System.out.println("\nStep 6: Deleting post...");
        postDAO.deletePost(targetId);
    }
}