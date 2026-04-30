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
import org.example.util.UserSession;
import org.example.util.exceptions.InvalidCredentialsException;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for the login screen.
 * Handles user authentication and navigation to registration or feed screens.
 */
public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private UserDAO userDAO = new UserDAO();

    /**
     * Attempts to log in the user with provided credentials.
     */
    @FXML
    private void handleLogin() {
        hideError();

        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        if (email.isEmpty() || pass.isEmpty()) {
            showError("Please fill in all fields.");
            return;
        }

        try {
            String nickname = userDAO.validateUser(email, pass);
            UserSession.setInstance(nickname);
            navigateToFeed();
        } catch (InvalidCredentialsException e) {
            showError(e.getMessage());
        } catch (RuntimeException e) {
            showError("Database or system error.");
        }
    }

    /**
     * Switches to the feed screen.
     */
    private void navigateToFeed() {
        switchScene("/org/example/gui/views/feed.fxml", "NetworkApp - Feed", 1000, 700, true);
    }

    /**
     * Switches to the registration screen.
     */
    @FXML
    private void handleShowRegistration() {
        switchScene("/org/example/gui/views/register.fxml", "NetworkApp - Registration", 400, 400, false);
    }

    /**
     * Universal method for scene switching.
     * @param fxmlPath Path to FXML file.
     * @param title Window title.
     * @param width Scene width.
     * @param height Scene height.
     * @param closeCurrent Whether to close the current stage.
     */
    private void switchScene(String fxmlPath, String title, int width, int height, boolean closeCurrent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            if (closeCurrent) {
                Stage newStage = new Stage();
                newStage.setScene(new Scene(root, width, height));
                newStage.setTitle(title);
                currentStage.close();
                newStage.show();
            } else {
                currentStage.setScene(new Scene(root, width, height));
                currentStage.setTitle(title);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load screen: " + fxmlPath, e);
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