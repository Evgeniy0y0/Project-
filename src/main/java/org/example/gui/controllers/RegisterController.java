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

public class RegisterController {
    @FXML private TextField nicknameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;

    private UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String nick = nicknameField.getText();
        String email = emailField.getText();
        String pass = passwordField.getText();

        if (userDAO.registerUser(nick, pass, email)) {
            System.out.println("Registration successful for: " + nick);
            handleBackToLogin();
        } else {
            System.out.println("Registration failed. Check if user already exists.");
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
}