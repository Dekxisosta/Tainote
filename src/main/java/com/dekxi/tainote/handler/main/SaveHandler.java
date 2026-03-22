package com.dekxi.tainote.handler.main;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.db.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.Duration;

import java.time.*;
import java.time.format.*;
import java.util.*;

import static com.dekxi.tainote.util.NodeAnimator.shakeNode;
import static com.dekxi.tainote.util.NodeBuilder.buildLoadingStage;

final class SaveConfig {
    static final String DEFAULT_AUTHOR = "Anonymous Author";
    static final String DEFAULT_STATUS = "Undecided";
    static final String DATE_FORMAT = "yyyyMMdd_HHmmss";

    private SaveConfig() {}
}
public class SaveHandler {
    private final MenuItem saveMenuItem;
    private final TainoteManager tainoteManager;
    private final DatabaseManager databaseManager;
    private final AppState appState;
    private final TextField titleField;
    private final TextField authorNameField;
    private final TextField statusField;
    private final TextArea textArea;
    private final ListView<TainotePreview> listView;
    private final Stage stage;

    public SaveHandler(
            MenuItem saveMenuItem,
            TainoteManager tainoteManager,
            DatabaseManager databaseManager,
            AppState appState,
            TextField titleField,
            TextField authorNameField,
            TextField statusField,
            TextArea textArea,
            ListView<TainotePreview> listView,
            Stage stage
    ) {
        this.saveMenuItem = saveMenuItem;
        this.tainoteManager = tainoteManager;
        this.databaseManager = databaseManager;
        this.appState = appState;
        this.titleField = titleField;
        this.authorNameField = authorNameField;
        this.statusField = statusField;
        this.textArea = textArea;
        this.listView = listView;
        this.stage = stage;

        saveMenuItem.setOnAction(_->saveTainote());
    }
    private void showLoading(){
        saveMenuItem.setDisable(true);
        Stage syncStage = buildLoadingStage(stage, "Attempting to save Tainote...");
        syncStage.show();

        Task<Void> syncTask = new Task<>() {
            @Override
            protected Void call() {
                return null;
            }
        };

        syncTask.setOnSucceeded(_ -> Platform.runLater(() -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(e -> {
                syncStage.close();
                saveMenuItem.setDisable(false);
            });
            pause.play();
        }));

        new Thread(syncTask).start();
    }

    private void saveTainote() {
        if (titleField.getText().isEmpty()) {
            shakeNode(titleField);
            return;
        }

        String authorName = authorNameField.getText() == null || authorNameField.getText().isEmpty()
                ? SaveConfig.DEFAULT_AUTHOR : authorNameField.getText();
        String status = statusField.getText() == null || statusField.getText().isEmpty()
                ? SaveConfig.DEFAULT_STATUS : statusField.getText();
        String currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern(SaveConfig.DATE_FORMAT));

        UUID uuid = (appState.isNewNote())? UUID.randomUUID() : appState.getCurrentNoteId();
        showLoading();
        tainoteManager.saveTainote(
                uuid,
                titleField.getText(),
                new String[]{},
                textArea.getText(),
                authorName,
                status,
                currentTime,
                currentTime
        );
        databaseManager.insertNote(
                uuid.toString(),
                titleField.getText(),
                textArea.getText(),
                authorName,
                status,
                currentTime,
                currentTime
        );
        appState.setCurrentNoteId(uuid);
        appState.setLastSavedTainote(tainoteManager.readTainote(uuid));
        databaseManager.printNotesTable();
        listView.getItems().setAll(databaseManager.getAllNotes());
        appState.setHasUnsavedChanges(false);
    }
}
