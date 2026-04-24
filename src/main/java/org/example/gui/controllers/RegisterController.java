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
import org.example.util.exceptions.UserAlreadyExistsException;

import java.io.IOException;

public class RegisterController {
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        hideError();
        String nick = nicknameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        if (nick.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            showError("All fields are required!");
            return;
        }

        try {
            userDAO.registerUser(nick, pass, email);

            System.out.println("Registration successful for: " + nick);
            handleBackToLogin();

        } catch (UserAlreadyExistsException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("Connection error. Please try again later.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/views/login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) nicknameField.getScene().getWindow();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("NetworkApp - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private Label errorLabel;
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