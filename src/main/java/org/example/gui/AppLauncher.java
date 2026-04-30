package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.util.DatabaseHelper;

import java.util.Objects;

/**
 * Main entry point for the JavaFX application.
 * Responsibility: Initialize the primary stage and load the initial login scene.
 */
public class AppLauncher extends Application {

    @Override
    public void init() {
        try {
            DatabaseHelper.initializeDatabase();
        } catch (Exception e) {
            System.err.println("Database initialization failed!");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(
                    getClass().getResource("/org/example/gui/views/login.fxml")
            ));

            Scene scene = new Scene(root, 400, 300);

            String cssPath = "/style.css";
            if (getClass().getResource(cssPath) != null) {
                scene.getStylesheets().add(Objects.requireNonNull(
                        getClass().getResource(cssPath)
                ).toExternalForm());
            }

            primaryStage.setTitle("NetworkApp - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

        } catch (Exception e) {
            throw new RuntimeException("Failed to launch the application", e);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}