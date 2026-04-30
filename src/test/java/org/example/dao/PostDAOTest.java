package org.example.dao;

import org.example.model.Post;
import org.example.util.DatabaseHelper;
import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PostDAOTest {
    private PostDAO postDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        DatabaseHelper.initializeDatabase();
        postDAO = new PostDAO();
        userDAO = new UserDAO();
        userDAO.registerUser("author", "pass", "auth@mail.com");
    }

    @Test
    void testSaveAndGetPosts() {
        postDAO.savePost("My first post", "author");
        List<Post> posts = postDAO.getAllPosts("author");

        assertEquals(1, posts.size());
        assertEquals("My first post", posts.get(0).getContent());
    }

    @Test
    void testToggleLikeLogic() {
        postDAO.savePost("Likable post", "author");
        int postId = postDAO.getAllPosts("author").get(0).getId();

        postDAO.toggleLike(postId, "author");
        Post postWithLike = postDAO.getAllPosts("author").get(0);
        assertEquals(1, postWithLike.getLikes(), "Count should be 1");
        assertTrue(postWithLike.isLikedByMe());

        postDAO.toggleLike(postId, "author");
        Post postWithoutLike = postDAO.getAllPosts("author").get(0);
        assertEquals(0, postWithoutLike.getLikes(), "Count should return to 0");
        assertFalse(postWithoutLike.isLikedByMe());
    }

    @Test
    void testSearchByAuthor() {
        postDAO.savePost("Content 1", "author");
        postDAO.savePost("Content 2", "other_user");

        List<Post> results = postDAO.searchByAuthor("auth", "author");
        assertEquals(1, results.size());
        assertEquals("author", results.get(0).getAuthor());
    }
}