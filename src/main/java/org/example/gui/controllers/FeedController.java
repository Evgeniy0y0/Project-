package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.PostDAO;
import org.example.model.Post;
import org.example.util.UserSession;

import java.io.IOException;
import java.util.List;

public class FeedController {
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private TextArea postTextArea;
    @FXML private VBox postsContainer;
    @FXML private ComboBox<String> leaderboardTypeCombo;
    private final PostDAO postDAO = new PostDAO();

    @FXML
    public void initialize() {
        String nick = UserSession.getCurrentUserNickname();
        welcomeLabel.setText("Welcome, " + (nick != null ? nick : "Guest") + "!");
        loadPosts();

        if (leaderboardTypeCombo != null) {
            leaderboardTypeCombo.getItems().addAll("Top Users", "Top Posts");
            leaderboardTypeCombo.setValue("Top Users");
        }
    }

    private void loadPosts() {
        postsContainer.getChildren().clear();
        List<Post> allPosts = postDAO.getAllPosts();

        for (Post post : allPosts) {
            Label postLabel = new Label(post.getAuthor() + ": " + post.getContent() + " (❤️ " + post.getLikes() + ")");
            postLabel.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5;");
            postLabel.setMaxWidth(Double.MAX_VALUE);

            postsContainer.getChildren().add(postLabel);
        }
    }

    @FXML
    private void handleLeaderboardTypeChange() {
        String selected = leaderboardTypeCombo.getValue();
        if ("Top Users".equals(selected)) {
            System.out.println("Switching to Top Users...");
        } else {
            System.out.println("Switching to Top Posts...");
        }
    }

    @FXML
    private void handleCreatePost() {
        String text = postTextArea.getText().trim();
        if (text.isEmpty()) return;

        String author = UserSession.getCurrentUserNickname();
        postDAO.savePost(text, author);

        postTextArea.clear();
        loadPosts();
    }

    @FXML
    private void handleSearch() {
        System.out.println("Searching for: " + searchField.getText());
    }

    @FXML
    private void handleLogout() {
        UserSession.cleanUserSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/gui/views/login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 400, 300));
            stage.setTitle("NetworkApp - Login");

            Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();
            currentStage.close();

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}