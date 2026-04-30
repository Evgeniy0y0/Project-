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

    @Test
    @DisplayName("Should delete all associated likes when post is deleted")
    void testCascadeDelete() {
        postDAO.savePost("Post to be deleted", "author");
        int postId = postDAO.getAllPosts("author").get(0).getId();
        postDAO.toggleLike(postId, "author");

        postDAO.deletePost(postId);

        List<Post> posts = postDAO.getAllPosts("author");
        assertTrue(posts.isEmpty(), "Post list should be empty");
    }

    @Test
    @DisplayName("Should correctly handle top authors ranking")
    void testTopAuthorsRanking() throws Exception {
        userDAO.registerUser("user1", "p", "u1@m.com");
        userDAO.registerUser("user2", "p", "u2@m.com");

        postDAO.savePost("Post 1", "user1");
        postDAO.savePost("Post 2", "user1");
        postDAO.savePost("Post 3", "user2");

        List<PostDAO.AuthorStats> top = postDAO.getTopAuthors(10);

        assertEquals(2, top.size());
        assertEquals("user1", top.get(0).getNickname());
        assertEquals(2, top.get(0).getCount());
    }

    @Test
    @DisplayName("Multiple like toggles should maintain correct count")
    void testMultipleToggles() {
        postDAO.savePost("Stress test", "author");
        int postId = postDAO.getAllPosts("author").get(0).getId();

        postDAO.toggleLike(postId, "author");
        postDAO.toggleLike(postId, "author");
        postDAO.toggleLike(postId, "author");

        Post post = postDAO.getAllPosts("author").get(0);
        assertEquals(1, post.getLikes(), "After 3 toggles, likes should be 1");
        assertTrue(post.isLikedByMe());
    }
}