package com.dekxi.tainote.handler.main;

import com.dekxi.tainote.db.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javafx.util.*;

import java.util.*;
import java.util.stream.*;

import static com.dekxi.tainote.util.NodeBuilder.buildLoadingStage;

public class SyncHandler {
    private final TainoteManager tainoteManager;
    private final DatabaseManager databaseManager;
    private final ListView<TainotePreview> listView;
    private final Stage stage;
    private final MenuItem syncMenuItem;

    public SyncHandler(
            MenuItem syncMenuItem,
            TainoteManager tainoteManager,
            DatabaseManager databaseManager,
            ListView<TainotePreview> listView,
            Stage stage
    ) {
        this.syncMenuItem = syncMenuItem;
        this.tainoteManager = tainoteManager;
        this.databaseManager = databaseManager;
        this.listView = listView;
        this.stage = stage;

        syncManagers();
        syncMenuItem.setOnAction(_ -> syncManagersAndShow());
    }

    public void syncManagersAndShow() {
        syncMenuItem.setDisable(true);
        Stage syncStage = buildLoadingStage(stage, "Syncing Tainotes...");
        syncStage.show();

        Task<Void> syncTask = new Task<>() {
            @Override
            protected Void call() {
                syncManagers();
                return null;
            }
        };

        syncTask.setOnSucceeded(_ -> Platform.runLater(() -> {
            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                syncStage.close();
                listView.getItems().setAll(databaseManager.getAllNotes());
                syncMenuItem.setDisable(false);
            });
            pause.play();
        }));

        new Thread(syncTask).start();
    }
    private Void syncManagers(){
        Set<String> dbIds = databaseManager.getAllNoteIds();
        List<Tainote> allNotes = tainoteManager.getAllTainotes();

        Set<String> fileIds = allNotes.stream()
                .map(Tainote::id)
                .collect(Collectors.toSet());

        for (String id : dbIds) {
            if (!fileIds.contains(id)) databaseManager.deleteNote(id);
        }

        for (Tainote note : allNotes) {
            if (!dbIds.contains(note.id())) {
                databaseManager.insertNote(
                        note.id(), note.title(), note.content(),
                        note.author(), note.status(), note.created(), note.modified()
                );
            }
        }
        return null;
    }
}