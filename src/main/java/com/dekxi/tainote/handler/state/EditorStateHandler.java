package com.dekxi.tainote.handler.state;

import com.dekxi.tainote.app.*;
import javafx.scene.control.*;

public class EditorStateHandler {
    private final AppState appState;
    private final TextArea textArea;
    private final TextField titleField;
    public EditorStateHandler(
            AppState appState,
            TextArea textArea,
            TextField titleField
    ){
        this.appState = appState;
        this.textArea = textArea;
        this.titleField = titleField;
    }

    private void configureHasUnsavedChanges(){
        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            
        });
    }
}
