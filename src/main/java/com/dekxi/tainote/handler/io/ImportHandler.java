package com.dekxi.tainote.handler.io;

import com.dekxi.tainote.manager.TainoteManager;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;

import static com.dekxi.tainote.util.NodeBuilder.*;

public class ImportHandler {
    private MenuItem importMenuItem;
    private final TainoteManager tainoteManager;
    private final Stage stage;

    public ImportHandler(
            MenuItem importMenuItem,
            TainoteManager tainoteManager,
            Stage stage) {
        this.importMenuItem = importMenuItem;
        this.tainoteManager = tainoteManager;
        this.stage = stage;
        importMenuItem.setOnAction((_)->importTainote());
    }

    private void importTainote() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(IOConfig.IMPORT_TITLE);
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(IOConfig.FILE_FILTER_DESC, IOConfig.FILE_FILTER_EXT)
        );
        fileChooser.setInitialDirectory(new File(IOConfig.USER_HOME));

        File selected = fileChooser.showOpenDialog(stage);
        if (selected == null) return;

        TainoteManager.ImportResult result = tainoteManager.importTainote(selected);

        switch (result) {
            case SUCCESS -> showInfo("Import successful.", "Tainote has been added to your library.");
            case ALREADY_EXISTS -> {
                if (confirmImportOverwrite())
                    tainoteManager.forceImportTainote(selected);
            }
            case INVALID -> showWarning("Invalid file.", "Selected file is not a valid Tainote.");
            case FAILED -> showWarning("Import failed.", "Something went wrong. Please try again.");
        }
    }
}