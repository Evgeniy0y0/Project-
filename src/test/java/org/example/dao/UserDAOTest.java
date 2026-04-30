package org.example.dao;

import org.example.util.DatabaseHelper;
import org.example.util.exceptions.InvalidCredentialsException;
import org.example.util.exceptions.UserAlreadyExistsException;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        DatabaseHelper.initializeDatabase();
        userDAO = new UserDAO();
    }

    @Test
    @DisplayName("Should successfully register and validate user")
    void testRegisterAndValidate() throws UserAlreadyExistsException, InvalidCredentialsException {
        userDAO.registerUser("tester", "pass123", "test@mail.com");

        String nickname = userDAO.validateUser("test@mail.com", "pass123");
        assertEquals("tester", nickname);
    }

    @Test
    @DisplayName("Should throw exception for wrong password")
    void testInvalidPassword() throws UserAlreadyExistsException {
        userDAO.registerUser("tester", "pass123", "test@mail.com");

        assertThrows(InvalidCredentialsException.class, () -> {
            userDAO.validateUser("test@mail.com", "wrong_pass");
        });
    }

    @Test
    @DisplayName("Should detect duplicate user")
    void testDuplicateUser() throws UserAlreadyExistsException {
        userDAO.registerUser("tester", "pass", "test@mail.com");

        assertThrows(UserAlreadyExistsException.class, () -> {
            userDAO.registerUser("tester", "other_pass", "other@mail.com");
        });
    }
}