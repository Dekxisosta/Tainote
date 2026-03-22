package com.dekxi.tainote.handler.main;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.db.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.scene.control.*;

import static com.dekxi.tainote.util.NodeBuilder.*;

public class DeleteHandler {
    private final MenuItem deleteMenuItem;
    private final TainoteManager tainoteManager;
    private final DatabaseManager databaseManager;
    private final AppState appState;
    private final TextField titleField;
    private final TextArea textArea;
    private final ListView<TainotePreview> listView;

    public DeleteHandler(
            MenuItem deleteMenuItem,
            TainoteManager tainoteManager,
            DatabaseManager databaseManager,
            AppState appState,
            TextField titleField,
            TextArea textArea,
            ListView<TainotePreview> listView
    ) {
        this.deleteMenuItem = deleteMenuItem;
        this.tainoteManager = tainoteManager;
        this.databaseManager = databaseManager;
        this.appState = appState;
        this.titleField = titleField;
        this.textArea = textArea;
        this.listView = listView;

        deleteMenuItem.setOnAction(_->deleteTainote());
    }

    public void deleteTainote() {
        if (appState.isNewNote()) {
            showInfo("Nothing to delete.", "Current texts are unsaved");
            return;
        }
        if (!confirmDelete()) return;

        tainoteManager.deleteTainote(appState.getCurrentNoteId());
        System.out.println(appState.getCurrentNoteId().toString());
        databaseManager.deleteNote(appState.getCurrentNoteId().toString());

        titleField.clear();
        textArea.clear();
        appState.setCurrentNoteId(null);
        appState.setLastSavedTainote(null);
        listView.getItems().setAll(databaseManager.getAllNotes());
        appState.setHasUnsavedChanges(false);
    }
}