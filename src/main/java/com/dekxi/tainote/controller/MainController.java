package com.dekxi.tainote.controller;

import com.dekxi.tainote.app.*;
import com.dekxi.tainote.config.*;
import com.dekxi.tainote.db.*;
import com.dekxi.tainote.handler.counter.*;
import com.dekxi.tainote.handler.io.*;
import com.dekxi.tainote.handler.main.*;
import com.dekxi.tainote.handler.state.*;
import com.dekxi.tainote.handler.ui.*;
import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.model.*;
import javafx.animation.*;
import javafx.application.*;
import javafx.concurrent.*;
import javafx.fxml.*;

import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.control.MenuItem;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.*;
import javafx.util.*;

import java.util.*;
import java.util.stream.*;

public class MainController {
    private HostServices hostServices;

    // STATE HOLDER
    private AppState appState;

    // MANAGERS
    private ConfigManager config;
    private TainoteManager tainoteManager;
    private DatabaseManager databaseManager;

    // HANDLERS
    private StyleHandler styleHandler;
    private CounterHandler counterHandler;
    private ListHandler listHandler;
    private DanceHandler danceHandler;
    private WPMHandler wpmHandler;
    private SearchHandler searchHandler;
    private TagHandler tagHandler;
    private AboutHandler aboutHandler;
    private ThemeHandler themeHandler;
    private TainoteWindowHandler tainoteWindowHandler;

    private CreateHandler createHandler;
    private DeleteHandler deleteHandler;
    private SaveHandler saveHandler;
    private ImportHandler importHandler;
    private ExportHandler exportHandler;
    private SyncHandler syncHandler;

    private EditorStateHandler editorStateHandler;

    private Scene scene;
    private Stage stage;
    private Runnable onClose;



    @FXML @SuppressWarnings("unused") private VBox root;

    @FXML @SuppressWarnings("unused") private HBox titleBar;
    @FXML @SuppressWarnings("unused") private Button minimizeButton;
    @FXML @SuppressWarnings("unused") private Button maximizeButton;
    @FXML @SuppressWarnings("unused") private Button closeButton;

    // ── Editor ────────────────────────────────────────
    @FXML @SuppressWarnings("unused") private TextArea textArea;
    @FXML @SuppressWarnings("unused") private HBox editorContainer;
    @FXML @SuppressWarnings("unused") private ColorPicker colorPicker;
    @FXML @SuppressWarnings("unused") private ComboBox<String> fontComboBox;
    @FXML @SuppressWarnings("unused") private Spinner<Integer> fontSizeSpinner;

    // ── Note Metadata ──────────────────────────────────
    @FXML @SuppressWarnings("unused") private TextField titleField;
    @FXML @SuppressWarnings("unused") private TextField authorNameField;
    @FXML @SuppressWarnings("unused") private TextField statusField;

    // ── Tags ───────────────────────────────────────────
    @FXML @SuppressWarnings("unused") private FlowPane tagChipPane;
    @FXML @SuppressWarnings("unused") private TextField tagTextField;

    // ── Sidebar ────────────────────────────────────────
    @FXML @SuppressWarnings("unused") private ListView<TainotePreview> listView;
    @FXML @SuppressWarnings("unused") private ListView<String> wordFrequencyListView;
    @FXML @SuppressWarnings("unused") private TextField searchField;
    @FXML @SuppressWarnings("unused") private ComboBox<String> filterComboBox;
    @FXML @SuppressWarnings("unused") private ImageView taiDancingImageView;
    @FXML @SuppressWarnings("unused") private StackPane imageContainer;

    // ── Status Bar ─────────────────────────────────────
    @FXML @SuppressWarnings("unused") private Label charCountLabel;
    @FXML @SuppressWarnings("unused") private Label wordCountLabel;
    @FXML @SuppressWarnings("unused") private Label uniqueWordCountLabel;
    @FXML @SuppressWarnings("unused") private Label timeStartedLabel;
    @FXML @SuppressWarnings("unused") private Label timeElapsedLabel;
    @FXML @SuppressWarnings("unused") private Label zoomLabel;
    @FXML @SuppressWarnings("unused") private Label wpmLabel;
    @FXML @SuppressWarnings("unused") private Label minuteLabel;

    @FXML @SuppressWarnings("unused") private Menu themeMenu;

    // ── Menu Items ─────────────────────────────────────
    @FXML @SuppressWarnings("unused") private MenuItem newMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem importMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem exportMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem saveMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem syncMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem deleteMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem findReplaceMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem aboutMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem redirectMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem keyConfigMenuItem;
    @FXML @SuppressWarnings("unused") private MenuItem donateMenuItem;

    @FXML @SuppressWarnings("unused") public void initialize(){}
    public void onClose(){
        onClose.run();
    }
    public void setOnClose(Runnable onClose){
        this.onClose = onClose;
    }
    public void setScene(Scene scene){this.scene = scene;}
    public void setStage(Stage stage){
        this.stage = stage;
    }
    public void setConfigManager(ConfigManager config){this.config = config;}
    public void setTainoteManager(TainoteManager tainoteManager){this.tainoteManager = tainoteManager;}
    public void setDatabaseManager(DatabaseManager databaseManager){this.databaseManager = databaseManager;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}

    public void initAppState(){this.appState = new AppState();}
    public void initStyleParser(){this.styleHandler = new StyleHandler(config, textArea, fontComboBox, fontSizeSpinner, colorPicker);}
    public void initCounterHandler(){this.counterHandler = new CounterHandler(textArea, charCountLabel, wordCountLabel, uniqueWordCountLabel, timeStartedLabel, timeElapsedLabel, zoomLabel, wpmLabel, minuteLabel);}
    public void initListHandler(){this.listHandler = new ListHandler(appState, databaseManager, tainoteManager, textArea, titleField, authorNameField, statusField, listView);}
    public void initDanceHandler(){this.danceHandler = new DanceHandler(taiDancingImageView, textArea);}
    public void initWPMHandler(){this.wpmHandler = new WPMHandler(appState, wpmLabel, textArea);}
    public void initSearchHandler(){this.searchHandler = new SearchHandler(filterComboBox, databaseManager, searchField, listView);}
    public void initTagHandler(){this.tagHandler = new TagHandler(tagChipPane, tagTextField);}
    public void initCreateHandler() {this.createHandler = new CreateHandler(newMenuItem, appState, titleField, textArea, listView);}
    public void initDeleteHandler() {this.deleteHandler = new DeleteHandler(deleteMenuItem, tainoteManager, databaseManager, appState, titleField, textArea, listView);}
    public void initSaveHandler() {this.saveHandler = new SaveHandler(saveMenuItem, tainoteManager, databaseManager, appState, titleField, authorNameField, statusField, textArea, listView, stage);}
    public void initImportHandler(){this.importHandler = new ImportHandler(importMenuItem, tainoteManager, stage);}
    public void initExportHandler(){this.exportHandler = new ExportHandler(exportMenuItem, tainoteManager, appState, stage);}
    public void initEditorStateHandler(){this.editorStateHandler = new EditorStateHandler(appState, textArea, titleField, authorNameField, statusField);}
    public void initSyncHandler() {this.syncHandler = new SyncHandler(syncMenuItem, tainoteManager, databaseManager, listView, stage);}
    public void initAboutHandler(){this.aboutHandler = new AboutHandler(aboutMenuItem, stage);}
    public void initApp(){appState.setCurrentNoteId(UUID.randomUUID());}
    public void initThemeHandler(){this.themeHandler = new ThemeHandler(themeMenu, scene);}
    public void initTainoteWindow(){this.tainoteWindowHandler = new TainoteWindowHandler(stage, scene, titleBar, minimizeButton, maximizeButton, closeButton);}
}
