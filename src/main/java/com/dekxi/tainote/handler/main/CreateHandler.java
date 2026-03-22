package com.dekxi.tainote.handler.main;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.model.*;
import javafx.scene.control.*;

import static com.dekxi.tainote.util.NodeBuilder.continuePromptFromUnsavedChanges;

public class CreateHandler {
    private final MenuItem newMenuItem;
    private final AppState appState;
    private final TextField titleField;
    private final TextArea textArea;
    private final ListView<TainotePreview> listView;

    public CreateHandler(
            MenuItem newMenuItem,
            AppState appState,
            TextField titleField,
            TextArea textArea,
            ListView listView
    ) {
        this.newMenuItem = newMenuItem;
        this.appState = appState;
        this.titleField = titleField;
        this.textArea = textArea;
        this.listView = listView;

        newMenuItem.setOnAction(_->createNewTainote());
    }

    public void createNewTainote() {
        if (appState.hasUnsavedChanges() && !continuePromptFromUnsavedChanges()) return;
        textArea.clear();
        titleField.clear();
        appState.setCurrentNoteId(null);
        appState.setHasUnsavedChanges(false);
        appState.setLastSavedTainote(null);
        listView.getSelectionModel().clearSelection();
    }
}