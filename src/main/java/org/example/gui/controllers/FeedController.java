package org.example.gui.controllers;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.dao.PostDAO;
import org.example.model.Post;
import org.example.util.UserSession;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.awt.*;
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

    private ImageView loadIcon(String path) {
        Image image = new Image(getClass().getResourceAsStream("/icons/" + path));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(18);
        imageView.setFitHeight(18);
        return imageView;
    }

    private HBox createPostCard(Post post, String currentNick) {
        HBox postBox = new HBox(15);
        postBox.setAlignment(Pos.CENTER_LEFT);
        postBox.getStyleClass().add("post-card");

        VBox textContainer = new VBox(3);
        HBox.setHgrow(textContainer, Priority.ALWAYS);

        Label authorLabel = new Label(post.getAuthor());
        authorLabel.getStyleClass().add("author-name");

        Label contentLabel = new Label(post.getContent());
        contentLabel.getStyleClass().add("post-content");
        contentLabel.setWrapText(true);
        contentLabel.setMinWidth(50);

        String createdStr = post.getCreatedAt().format(DATE_FORMATTER);
        String updatedStr = post.getUpdatedAt().format(DATE_FORMATTER);
        String timestamp = !createdStr.equals(updatedStr) ? createdStr + " (upd " + updatedStr + ")" : createdStr;

        Label dateLabel = new Label(timestamp);
        dateLabel.getStyleClass().add("date-label");
        textContainer.getChildren().addAll(authorLabel, contentLabel, dateLabel);
        postBox.getChildren().add(textContainer);

        HBox actionButtons = new HBox(8);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);

        Button likeBtn = new Button(String.valueOf(post.getLikes()));
        String likeIcon = post.isLikedByMe() ? "liked.png" : "like.png";
        likeBtn.setGraphic(loadIcon(likeIcon));
        likeBtn.getStyleClass().add("button-action");
        likeBtn.setOnAction(e -> {
            postDAO.toggleLike(post.getId(), currentNick);
            loadPosts();
        });
        actionButtons.getChildren().add(likeBtn);

        if (post.getAuthor().equals(currentNick)) {
            Button editBtn = new Button();
            editBtn.setGraphic(loadIcon("edit.png"));
            editBtn.getStyleClass().add("button-action");

            Button deleteBtn = new Button();
            deleteBtn.setGraphic(loadIcon("delete.png"));
            deleteBtn.getStyleClass().addAll("button-action", "button-delete");
            deleteBtn.setOnAction(e -> {
                postDAO.deletePost(post.getId());
                loadPosts();
            });

            editBtn.setOnAction(e -> {
                editBtn.setGraphic(null);
                TextArea editArea = new TextArea(post.getContent());
                editArea.setWrapText(true);
                editArea.setPrefHeight(60);

                Button cancelBtn = new Button("✕");
                cancelBtn.setMinWidth(Region.USE_PREF_SIZE);
                cancelBtn.getStyleClass().add("button-action");

                int index = postBox.getChildren().indexOf(textContainer);
                postBox.getChildren().set(index, editArea);

                editBtn.setText("✔");

                int delIndex = actionButtons.getChildren().indexOf(deleteBtn);
                actionButtons.getChildren().set(delIndex, cancelBtn);

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
            actionButtons.getChildren().addAll(editBtn, deleteBtn);
        }
        postBox.getChildren().add(actionButtons);
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
        leaderboardTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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