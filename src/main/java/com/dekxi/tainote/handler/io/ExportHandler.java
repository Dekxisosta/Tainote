package com.dekxi.tainote.handler.io;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.scene.control.*;
import javafx.stage.*;

import java.io.*;

import static com.dekxi.tainote.util.NodeBuilder.showInfo;
import static com.dekxi.tainote.util.NodeBuilder.showWarning;
final class IOConfig {
    static final String EXPORT_TITLE = "Export Tainote";
    static final String EXPORT_FILENAME_FORMAT = "%s_%s.tainote";
    static final String IMPORT_TITLE = "Import Tainote";
    static final String FILE_FILTER_DESC = "Tainote Files (*.tainote)";
    static final String FILE_FILTER_EXT = "*.tainote";
    static final String USER_HOME = System.getProperty("user.home");

    private IOConfig() {}
}
public class ExportHandler {
    private final MenuItem exportMenuItem;
    private final TainoteManager tainoteManager;
    private final AppState appState;
    private final Stage stage;

    public ExportHandler(MenuItem exportMenuItem, TainoteManager tainoteManager, AppState appState, Stage stage) {
        this.exportMenuItem = exportMenuItem;
        this.tainoteManager = tainoteManager;
        this.appState = appState;
        this.stage = stage;

        exportMenuItem.setOnAction(_->exportTainote());
    }
    private void exportTainote() {
        if (!appState.hasOpenNote()) {
            showWarning("No note open.", "Please create or open a tainote first.");
            return;
        }
        if (appState.isNewNote()) {
            showWarning("Unsaved Tainote", "Consider saving first before exporting the current tainote.");
            return;
        }
        Tainote tainote = tainoteManager.readTainote(appState.getCurrentNoteId());
        String title = tainote.title();
        String created = tainote.created();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export Tainote");
        fileChooser.setInitialFileName(title + "_" + created + ".tainote");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Tainote Files (*.tainote)", "*.tainote")
        );
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

        File dest = fileChooser.showSaveDialog(stage);
        if (dest == null) return;

        TainoteManager.ExportResult result = tainoteManager.exportTainote(appState.getCurrentNoteId(), dest);

        switch (result) {
            case SUCCESS -> showInfo("Export successful.", "Your tainote has been exported.");
            case NOT_FOUND -> showWarning("File not found.", "The tainote file could not be located.");
            case FAILED -> showWarning("Export failed.", "Something went wrong. Please try again.");
        }
    }
}
