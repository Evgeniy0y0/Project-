package org.example.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.PostDAO;
import org.example.model.Post;
import org.example.util.UserSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        String currentNick = UserSession.getCurrentUserNickname();

        List<Post> allPosts = postDAO.getAllPosts(currentNick);

        for (Post post : allPosts) {
            HBox postCard = createPostCard(post, currentNick);
            postsContainer.getChildren().add(postCard);
        }
    }

    private HBox createPostCard(Post post, String currentNick) {
        HBox postBox = new HBox(10);
        postBox.setAlignment(Pos.CENTER_LEFT);
        postBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: white;");

        Label content = new Label(post.getAuthor() + ": " + post.getContent());
        content.setWrapText(true);
        content.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(content, Priority.ALWAYS);

        Button likeBtn = new Button(post.isLikedByMe() ? "♥ " + post.getLikes() : "♡ " + post.getLikes());
        likeBtn.setMinWidth(Button.USE_PREF_SIZE);
        likeBtn.setOnAction(e -> {
            postDAO.toggleLike(post.getId(), currentNick);
            loadPosts();
        });

        postBox.getChildren().addAll(content, likeBtn);

        if (post.getAuthor().equals(currentNick)) {
            Button deleteBtn = new Button("🗑");
            deleteBtn.setMinWidth(Button.USE_PREF_SIZE);
            deleteBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent;");
            deleteBtn.setOnAction(e -> {
                postDAO.deletePost(post.getId());
                loadPosts();
            });
            postBox.getChildren().add(deleteBtn);
        }

        return postBox;
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