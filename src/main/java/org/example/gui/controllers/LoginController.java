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
import org.example.util.exceptions.InvalidCredentialsException;

import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        hideError();

        String email = emailField.getText().trim();
        String pass = passwordField.getText();

        try {
            userDAO.validateUser(email, pass);
            System.out.println("Login successful!");

        } catch (InvalidCredentialsException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            showError("System error occurred.");
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShowRegistration() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/views/register.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();

            stage.setScene(new Scene(root, 400, 400));
            stage.setTitle("NetworkApp - Registration");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private Label errorLabel; // Не забудь добавить @FXML

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