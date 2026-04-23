package org.example;

import org.example.dao.PostDAO;
import org.example.dao.UserDAO;
import org.example.model.Post;
import org.example.util.DatabaseHelper;

import java.util.List;

/**
 * The Main class serves as the entry point for the NetworkApp application.
 * It handles the initialization of the database and demonstrates CRUD operations.
 *
 * @author Eugene
 * @version 1.0
 */
public class Main {

    /**
     * Default constructor for the Main class.
     */
    public Main() {
    }

    /**
     * The main entry point of the program.
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        try {
            DatabaseHelper.initializeDatabase();
            System.out.println("Status: Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Status: Critical error during database initialization!");
            return;
        }

        UserDAO userDAO = new UserDAO();

        PostDAO postDAO = new PostDAO();


        List<Post> allPosts = postDAO.getAllPosts();

        for (org.example.model.Post p : allPosts) {
            System.out.println("---------------------------------");
            System.out.println("Author: " + p.getAuthor());
            System.out.println("Content: " + p.getContent());
            System.out.println("Likes: " + p.getLikes());
        }
    }
}