package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.example.dao.UserDAO;

import java.io.IOException;

public class LoginController {
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (userDAO.validateUser(email, pass)) {
            System.out.println("Login successful for: " + email);
        } else {
            System.out.println("Login failed: Invalid credentials.");
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
}