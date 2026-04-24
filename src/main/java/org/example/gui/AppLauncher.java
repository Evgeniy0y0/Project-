package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class AppLauncher extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/views/login.fxml"));
        Parent root = loader.load();

        stage.setTitle("NetworkApp - User Login");
        stage.setScene(new Scene(root, 400, 300));
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}