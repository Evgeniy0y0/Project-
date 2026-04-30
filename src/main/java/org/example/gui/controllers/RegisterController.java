package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dao.UserDAO;
import org.example.util.InputValidator;
import org.example.util.exceptions.UserAlreadyExistsException;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the registration screen.
 * Validates user input and creates new user accounts.
 */
public class RegisterController {
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    /**
     * Handles the registration process.
     * Validates input fields and saves the user to the database.
     */
    @FXML
    private void handleRegister() {
        hideError();

        String nick = nicknameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        try {
            InputValidator.validateRegistration(nick, email, pass);
            userDAO.registerUser(nick, pass, email);
            handleBackToLogin();

        } catch (UserAlreadyExistsException | IllegalArgumentException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("An unexpected error occurred during registration.");
        }
    }

    /**
     * Navigates back to the login screen.
     */
    @FXML
    private void handleBackToLogin() {
        switchScene("/org/example/gui/views/login.fxml", "NetworkApp - Login", 400, 300);
    }

    /**
     * Helper method to switch between scenes.
     */
    private void switchScene(String fxmlPath, String title, int width, int height) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Stage stage = (Stage) nicknameField.getScene().getWindow();
            stage.setScene(new Scene(root, width, height));
            stage.setTitle(title);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load scene: " + fxmlPath, e);
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }
}