package com.dekxi.tainote.handler.state;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.model.*;
import javafx.scene.control.*;

public class EditorStateHandler {
    private final AppState appState;
    private final TextArea textArea;
    private final TextField titleField;
    private final TextField authorNameField;
    private final TextField statusField;

    public EditorStateHandler(
            AppState appState,
            TextArea textArea,
            TextField titleField,
            TextField authorNameField,
            TextField statusField
    ){
        this.appState = appState;
        this.textArea = textArea;
        this.titleField = titleField;
        this.authorNameField = authorNameField;
        this.statusField = statusField;

        configureHasUnsavedChanges();
    }

    private void configureHasUnsavedChanges(){
        textArea.textProperty().addListener((_, _, _) -> {
            appState.setHasUnsavedChanges(hasUnsavedChanges());
        });
        titleField.textProperty().addListener((_, _, _) -> {
           appState.setHasUnsavedChanges(hasUnsavedChanges());
        });
        statusField.textProperty().addListener((_, _, _) -> {
           appState.setHasUnsavedChanges(hasUnsavedChanges());
        });
        authorNameField.textProperty().addListener((_, _, _) -> {
            appState.setHasUnsavedChanges(hasUnsavedChanges());
        });
    }
    private boolean hasUnsavedChanges(){
        if (appState.isNewNote()) {
            return !textArea.getText().isEmpty()
                    || !titleField.getText().isEmpty()
                    || !authorNameField.getText().isEmpty()
                    || !statusField.getText().isEmpty();
        } else {
            Tainote last = appState.getLastSavedTainote();
            return !textArea.getText().equals(last.content())
                    || !titleField.getText().equals(last.title())
                    || !authorNameField.getText().equals(last.author())
                    || !statusField.getText().equals(last.status());
        }
    }
}
