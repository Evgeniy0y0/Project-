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
    }
}
