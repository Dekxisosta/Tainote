package com.dekxi.tainote.util;

import javafx.scene.control.*;
import javafx.scene.image.*;
import java.util.*;

public class DialogBuilder {
    private DialogBuilder(){}

    public static void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(DialogConfig.APP_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setGraphic(buildIcon(DialogConfig.ICON_SIZE_LARGE));
        alert.showAndWait();
    }

    public static void showWarning(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(DialogConfig.WARNING_TITLE);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.setGraphic(buildIcon(DialogConfig.ICON_SIZE_LARGE));
        alert.showAndWait();
    }

    public static boolean continuePromptFromUnsavedChanges() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(DialogConfig.CONFIRM_TITLE);
        dialog.setHeaderText(DialogConfig.UNSAVED_HEADER);
        dialog.setContentText(DialogConfig.UNSAVED_CONTENT);
        dialog.setGraphic(buildIcon(DialogConfig.ICON_SIZE_SMALL));

        ButtonType continueBtn = new ButtonType(DialogConfig.UNSAVED_CONTINUE_BTN);
        ButtonType cancelBtn = new ButtonType(DialogConfig.CANCEL_BTN, ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getButtonTypes().setAll(continueBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();
        return result.isPresent() && result.get() == continueBtn;
    }

    public static boolean confirmImportOverwrite() {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle(DialogConfig.CONFIRM_TITLE);
        dialog.setHeaderText(DialogConfig.IMPORT_OVERWRITE_HEADER);
        dialog.setContentText(DialogConfig.IMPORT_OVERWRITE_CONTENT);
        dialog.setGraphic(buildIcon(DialogConfig.ICON_SIZE_SMALL));

        ButtonType updateBtn = new ButtonType(DialogConfig.IMPORT_OVERWRITE_UPDATE_BTN);
        ButtonType cancelBtn = new ButtonType(DialogConfig.CANCEL_BTN, ButtonBar.ButtonData.CANCEL_CLOSE);
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
        Image image = new Image(DialogBuilder.class.getResource(DialogConfig.WARNING_ICON).toExternalForm());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(size);
        imageView.setFitHeight(size);
        imageView.setPreserveRatio(true);
        return imageView;
    }
}