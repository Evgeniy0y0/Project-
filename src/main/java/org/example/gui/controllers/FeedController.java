package org.example.gui.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.PostDAO;
import org.example.model.Post;
import org.example.util.UserSession;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FeedController {
    @FXML private Label welcomeLabel;
    @FXML private TextField searchField;
    @FXML private TextArea postTextArea;
    @FXML private VBox postsContainer;
    @FXML private ComboBox<String> leaderboardTypeCombo;

    @FXML private TableView<Object> leaderboardTable;
    @FXML private TableColumn<Object, String> rankColumn;
    @FXML private TableColumn<Object, String> userColumn;
    @FXML private TableColumn<Object, String> likesColumn;

    private final PostDAO postDAO = new PostDAO();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");

    private void loadPosts() {
        postsContainer.getChildren().clear();

        String currentNick = UserSession.getCurrentUserNickname();
        List<Post> allPosts = postDAO.getAllPosts(currentNick);

        for (Post post : allPosts) {
            postsContainer.getChildren().add(createPostCard(post, currentNick));
        }
    }

    private HBox createPostCard(Post post, String currentNick) {
        HBox postBox = new HBox(10);
        postBox.setAlignment(Pos.CENTER_LEFT);
        postBox.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 5; -fx-background-color: white;");

        VBox textContainer = new VBox(3);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        Label contentLabel = new Label(post.getAuthor() + ": " + post.getContent());
        contentLabel.setWrapText(true);
        contentLabel.setMaxWidth(Double.MAX_VALUE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM HH:mm:ss");

        String createdStr = post.getCreatedAt().format(DATE_FORMATTER);
        String updatedStr = post.getUpdatedAt().format(DATE_FORMATTER);

        String timestamp = !createdStr.equals(updatedStr)
                ? createdStr + " (updated " + updatedStr + ")"
                : createdStr;

        Label dateLabel = new Label(timestamp);
        dateLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: gray;");

        textContainer.getChildren().addAll(contentLabel, dateLabel);

        Button likeBtn = new Button((post.isLikedByMe() ? "♥ " : "♡ ") + post.getLikes());
        likeBtn.setMinWidth(Region.USE_PREF_SIZE);
        likeBtn.setOnAction(e -> {
            postDAO.toggleLike(post.getId(), currentNick);
            loadPosts();
        });

        postBox.getChildren().addAll(textContainer, likeBtn);

        if (post.getAuthor().equals(currentNick)) {
            Button editBtn = new Button("✎");
            editBtn.setMinWidth(Region.USE_PREF_SIZE);

            Button deleteBtn = new Button("🗑");
            deleteBtn.setMinWidth(Region.USE_PREF_SIZE);
            deleteBtn.setStyle("-fx-text-fill: red;");

            editBtn.setOnAction(e -> {
                TextArea editArea = new TextArea(post.getContent());
                editArea.setWrapText(true);
                editArea.setPrefHeight(60);

                Button cancelBtn = new Button("✕");
                cancelBtn.setMinWidth(Region.USE_PREF_SIZE);

                int index = postBox.getChildren().indexOf(textContainer);
                postBox.getChildren().set(index, editArea);

                editBtn.setText("✔");

                int delIndex = postBox.getChildren().indexOf(deleteBtn);
                postBox.getChildren().set(delIndex, cancelBtn);

                likeBtn.setVisible(false);

                editBtn.setOnAction(saveEvent -> {
                    String updatedText = editArea.getText().trim();
                    if (!updatedText.isEmpty()) {
                        postDAO.updatePost(post.getId(), updatedText);
                        loadPosts();
                    }
                });

                cancelBtn.setOnAction(cancelEvt -> loadPosts());
            });

            deleteBtn.setOnAction(e -> {
                postDAO.deletePost(post.getId());
                loadPosts();
            });

            postBox.getChildren().addAll(editBtn, deleteBtn);
        }

        return postBox;
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
        String query = searchField.getText().trim();
        String currentNick = UserSession.getCurrentUserNickname();

        postsContainer.getChildren().clear();
        List<Post> results;

        if (query.isEmpty()) {
            results = postDAO.getAllPosts(currentNick);
        } else {
            results = postDAO.searchByAuthor(query, currentNick);
        }

        for (Post post : results) {
            postsContainer.getChildren().add(createPostCard(post, currentNick));
        }
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

    @FXML
    public void initialize() {
        String nick = UserSession.getCurrentUserNickname();
        welcomeLabel.setText("Welcome, " + (nick != null ? nick : "Guest") + "!");
        loadPosts();

        if (leaderboardTypeCombo != null) {
            leaderboardTypeCombo.getItems().setAll("Top Users", "Top Posts");
            leaderboardTypeCombo.setValue("Top Users");

            rankColumn.setCellFactory(col -> new TableCell<Object, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            });

            refreshLeaderboard();
        }
        }

    @FXML
    private void handleLeaderboardTypeChange() {
        refreshLeaderboard();
    }

    private void refreshLeaderboard() {
        if (leaderboardTable == null) return;

        leaderboardTable.getItems().clear();
        String type = leaderboardTypeCombo.getValue();

        if ("Top Posts".equals(type)) {
            userColumn.setText("Post Content");
            likesColumn.setText("Likes");

            userColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(((Post) data.getValue()).getContent()));
            likesColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(String.valueOf(((Post) data.getValue()).getLikes())));

            leaderboardTable.getItems().addAll(postDAO.getTopPosts(10));
        } else {
            userColumn.setText("User Nickname");
            likesColumn.setText("Posts Count");

            userColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(((PostDAO.AuthorStats) data.getValue()).getNickname()));
            likesColumn.setCellValueFactory(data ->
                    new SimpleStringProperty(String.valueOf(((PostDAO.AuthorStats) data.getValue()).getCount())));

            leaderboardTable.getItems().addAll(postDAO.getTopAuthors(10));
        }
    }
}