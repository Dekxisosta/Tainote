package com.dekxi.tainote.util;

import javafx.animation.*;
import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.stage.*;
import javafx.util.*;

import java.util.*;

public class NodeBuilder {
    public final static Cursor[] currentCursor = { Cursor.DEFAULT };

    private NodeBuilder(){}
    public static void makeResizable(Stage stage, Scene scene) {
        final double BORDER = 6;
        final double[] dragStart = new double[2];
        final double[] stageStart = new double[4]; // x, y, w, h

        scene.setOnMouseMoved(e -> {
            double x = e.getSceneX();
            double y = e.getSceneY();
            double w = stage.getWidth();
            double h = stage.getHeight();

            if (x < BORDER && y < BORDER) currentCursor[0] = Cursor.NW_RESIZE;
            else if (x > w - BORDER && y < BORDER) currentCursor[0] = Cursor.NE_RESIZE;
            else if (x < BORDER && y > h - BORDER) currentCursor[0] = Cursor.SW_RESIZE;
            else if (x > w - BORDER && y > h - BORDER) currentCursor[0] = Cursor.SE_RESIZE;
            else if (x < BORDER) currentCursor[0] = Cursor.W_RESIZE;
            else if (x > w - BORDER) currentCursor[0] = Cursor.E_RESIZE;
            else if (y < BORDER) currentCursor[0] = Cursor.N_RESIZE;
            else if (y > h - BORDER) currentCursor[0] = Cursor.S_RESIZE;
            else currentCursor[0] = Cursor.DEFAULT;

            scene.setCursor(currentCursor[0]);
        });

        scene.setOnMousePressed(e -> {
            dragStart[0] = e.getScreenX();
            dragStart[1] = e.getScreenY();
            stageStart[0] = stage.getX();
            stageStart[1] = stage.getY();
            stageStart[2] = stage.getWidth();
            stageStart[3] = stage.getHeight();
        });

        scene.setOnMouseDragged(e -> {
            double dx = e.getScreenX() - dragStart[0];
            double dy = e.getScreenY() - dragStart[1];
            Cursor cursor = scene.getCursor();

            if (cursor == Cursor.SE_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] + dx));
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] + dy));
            } else if (cursor == Cursor.SW_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] - dx));
                stage.setX(stageStart[0] + stageStart[2] - stage.getWidth());
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] + dy));
            } else if (cursor == Cursor.NE_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] + dx));
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] - dy));
                stage.setY(stageStart[1] + stageStart[3] - stage.getHeight());
            } else if (cursor == Cursor.NW_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] - dx));
                stage.setX(stageStart[0] + stageStart[2] - stage.getWidth());
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] - dy));
                stage.setY(stageStart[1] + stageStart[3] - stage.getHeight());
            } else if (cursor == Cursor.E_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] + dx));
            } else if (cursor == Cursor.W_RESIZE) {
                stage.setWidth(Math.max(stage.getMinWidth(), stageStart[2] - dx));
                stage.setX(stageStart[0] + stageStart[2] - stage.getWidth());
            } else if (cursor == Cursor.S_RESIZE) {
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] + dy));
            } else if (cursor == Cursor.N_RESIZE) {
                stage.setHeight(Math.max(stage.getMinHeight(), stageStart[3] - dy));
                stage.setY(stageStart[1] + stageStart[3] - stage.getHeight());
            }
        });
    }
    public static Stage buildLoadingStage(Stage owner, String message) {
        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.NONE);
        stage.initStyle(StageStyle.UNDECORATED);

        ImageView icon = new ImageView(new Image("/assets/tairitsu_icon.png"));
        icon.setFitWidth(48);
        icon.setFitHeight(48);
        icon.setPreserveRatio(true);

        Label label = new Label(message);
        label.setStyle("-fx-font-size: 13px;");

        Circle c1 = createDot();
        Circle c2 = createDot();
        Circle c3 = createDot();
        HBox dots = new HBox(6, c1, c2, c3);
        dots.setAlignment(Pos.CENTER);

        animateDot(c1, 0);
        animateDot(c2, 150);
        animateDot(c3, 300);

        VBox content = new VBox(10, icon, label, dots);
        content.setAlignment(Pos.CENTER);
        content.setStyle("-fx-padding: 24; -fx-background-color: transparent;");

        stage.setScene(new Scene(content));
        return stage;
    }

    private static Circle createDot() {
        Circle dot = new Circle(6);
        dot.setFill(Color.web("#7B00FF"));
        return dot;
    }

    private static void animateDot(Circle dot, int delayMs) {
        TranslateTransition bounce = new TranslateTransition(Duration.millis(400), dot);
        bounce.setByY(-10);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(Timeline.INDEFINITE);
        bounce.setDelay(Duration.millis(delayMs));
        bounce.play();
    }
    public static void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(NodeConfig.APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setGraphic(buildIcon(NodeConfig.ICON_SIZE_LARGE));
        alert.showAndWait();
    }
    public static Stage buildAboutStage(Stage owner) {
        String[] features = {
                "WPM tracking",
                "Note syncing",
                "Clean distraction-free writing",
                "Author and status tagging",
                "Persistent local database"
        };

        VBox featureList = new VBox(4);
        featureList.setAlignment(Pos.CENTER_LEFT);
        for (String feature : features) {
            Label item = new Label("• " + feature);
            item.setStyle("-fx-font-size: 12px;");
            featureList.getChildren().add(item);
        }


        Stage aboutStage = new Stage();
        aboutStage.initOwner(owner);
        aboutStage.initModality(Modality.APPLICATION_MODAL);
        aboutStage.initStyle(StageStyle.UNDECORATED);

        ImageView icon = new ImageView(new Image("/assets/tairitsu_icon.png"));
        icon.setFitWidth(64);
        icon.setFitHeight(64);
        icon.setPreserveRatio(true);

        Label appName = new Label("Tainote");
        appName.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Label version = new Label("Version 1.0.0");
        version.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Label description = new Label("""
        Tainote is a lightweight note-taking app built for writers
        who want to track their progress. Features WPM tracking,
        note syncing, and a clean distraction-free writing experience.
        """);
        description.setStyle("-fx-font-size: 13px;");

        Label author = new Label("Made by dekxi");
        author.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

        Separator separator = new Separator();

        Button closeBtn = new Button("Close");
        closeBtn.setOnAction(_ -> aboutStage.close());
        VBox content = new VBox(10, icon, appName, version, description, separator, author, closeBtn, featureList);
        content.setAlignment(Pos.CENTER);
        content.setStyle("""
            -fx-padding: 28;
            -fx-background-color: white;
            -fx-background-radius: 8;
            -fx-border-color: #cccccc;
            -fx-border-width: 1;
            -fx-border-radius: 8;
        """);

        VBox bordered = new VBox(content);
        bordered.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-background-color: white;");

        aboutStage.setScene(new Scene(bordered));
        return aboutStage;
    }
    public static void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(NodeConfig.WARNING_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setGraphic(buildIcon(NodeConfig.ICON_SIZE_LARGE));
        alert.showAndWait();
    }

    public static boolean continuePromptFromUnsavedChanges() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(NodeConfig.CONFIRM_TITLE);
        dialog.setHeaderText(NodeConfig.UNSAVED_HEADER);
        dialog.setContentText(NodeConfig.UNSAVED_CONTENT);
        dialog.setGraphic(buildIcon(NodeConfig.ICON_SIZE_SMALL));

        ButtonType continueBtn = new ButtonType(NodeConfig.UNSAVED_CONTINUE_BTN);
        ButtonType cancelBtn = new ButtonType(NodeConfig.CANCEL_BTN, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(continueBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == continueBtn;
    }

    public static boolean confirmImportOverwrite() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(NodeConfig.CONFIRM_TITLE);
        dialog.setHeaderText(NodeConfig.IMPORT_OVERWRITE_HEADER);
        dialog.setContentText(NodeConfig.IMPORT_OVERWRITE_CONTENT);
        dialog.setGraphic(buildIcon(NodeConfig.ICON_SIZE_SMALL));

        ButtonType updateBtn = new ButtonType(NodeConfig.IMPORT_OVERWRITE_UPDATE_BTN);
        ButtonType cancelBtn = new ButtonType(NodeConfig.CANCEL_BTN, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(updateBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == updateBtn;
    }
    public static boolean confirmDelete() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Warning");
        dialog.setHeaderText("Delete Tainote");
        dialog.setContentText("This action is irreversible. Are you sure?");

        ButtonType deleteBtn = new ButtonType("Delete");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(deleteBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == deleteBtn;
    }
    private static ImageView buildIcon(int size) {
        Image image = new Image(NodeBuilder.class.getResource(NodeConfig.WARNING_ICON).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}