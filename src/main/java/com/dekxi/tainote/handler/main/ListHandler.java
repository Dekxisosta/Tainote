package com.dekxi.tainote.handler.main;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.db.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;

import java.util.*;

import static com.dekxi.tainote.util.NodeBuilder.continuePromptFromUnsavedChanges;
import static com.dekxi.tainote.util.NodeBuilder.showWarning;

public class ListHandler {
    private AppState appState;

    private DatabaseManager databaseManager;
    private TainoteManager tainoteManager;

    private ListView<TainotePreview> listView;
    private TextArea textArea;
    private TextField titleField;
    private TextField authorNameField;
    private TextField statusField;

    public ListHandler(
            AppState appState,
            DatabaseManager databaseManager,
            TainoteManager tainoteManager,
            TextArea textArea,
            TextField titleField,
            TextField authorNameField,
            TextField statusField,
            ListView<TainotePreview> listView
    ){
        this.appState = appState;
        this.databaseManager = databaseManager;
        this.tainoteManager = tainoteManager;
        this.textArea = textArea;
        this.titleField = titleField;
        this.authorNameField = authorNameField;
        this.statusField = statusField;
        this.listView = listView;
        setUpListView();
    }
    private void setUpListView(){
        listView.setCellFactory(_ -> new ListCell<>() {
            @Override
            protected void updateItem(TainotePreview item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    HBox container = new HBox(10);
                    container.setAlignment(Pos.CENTER_LEFT);
                    container.setCursor(Cursor.HAND);

                    ImageView icon = new ImageView(new Image(
                            getClass().getResource("/assets/tainote_icon.png").toExternalForm()
                    ));
                    icon.setFitWidth(32);
                    icon.setFitHeight(32);
                    icon.setPreserveRatio(true);

                    HBox titleRow = new HBox(8);
                    titleRow.setAlignment(Pos.CENTER_LEFT);
                    Label title = new Label(item.title());
                    Label author = new Label("by " + item.author());
                    titleRow.getChildren().addAll(title, author);

                    Label modified = new Label("Modified: " + item.modified());

                    VBox textBox = new VBox(2);
                    textBox.getChildren().addAll(titleRow, modified);
                    container.getChildren().addAll(icon, textBox);

                    container.setMaxWidth(Double.MAX_VALUE);
                    HBox.setHgrow(textBox, Priority.ALWAYS);

                    applyStyle(title, author, modified, isSelected());

                    selectedProperty().addListener((_, _, isSelected) ->
                            applyStyle(title, author, modified, isSelected));

                    setText(null);
                    setGraphic(container);

                }
            }

            private void applyStyle(Label title, Label author, Label modified, boolean selected) {
                if (selected) {
                    setStyle("-fx-border-color: #7C5CBF; -fx-border-width: 1.5; -fx-background-color: #4A3880;");
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #E8E3F0;");
                    author.setStyle("-fx-font-size: 11px; -fx-text-fill: #9B7FD4;");
                    modified.setStyle("-fx-font-size: 11px; -fx-text-fill: #9B7FD4;");
                } else {
                    setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
                    title.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #E8E3F0;");
                    author.setStyle("-fx-font-size: 11px; -fx-text-fill: #9E99B0;");
                    modified.setStyle("-fx-font-size: 11px; -fx-text-fill: #5C5870;");
                }
            }
        });
        listView.setOnMouseClicked(_ -> {
            TainotePreview selected = listView.getSelectionModel().getSelectedItem();
            if (selected == null) return;

            if (appState.hasUnsavedChanges()) {
                if (!continuePromptFromUnsavedChanges()) return;
            }
            UUID uuid = UUID.fromString(selected.id());

            Tainote note = tainoteManager.readTainote(uuid);
            if (note == null) {
                showWarning("Note not found.", "The selected Tainote could not be loaded. It may have been moved or deleted.");
                return;
            }
            try {
                appState.setCurrentNoteId(uuid);
                appState.setLastSavedTainote(note);
                appState.setHasUnsavedChanges(false);
            } catch (IllegalArgumentException e) {
                showWarning("Invalid Tainote.", "The selected Tainote has a corrupted ID and cannot be opened.");
                return;
            }
            appState.setIsLoadingNote(true);

            titleField.setText(note.title());
            textArea.setText(note.content());
            authorNameField.setText(note.author());
            statusField.setText(note.status());
            appState.setHasUnsavedChanges(false);

            appState.setIsLoadingNote(false);
        });
        listView.getItems().setAll(databaseManager.getAllNotes());
    }
}
