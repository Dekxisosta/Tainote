package com.dekxi.tainote.app;

import com.dekxi.tainote.config.*;
import com.dekxi.tainote.controller.*;
import com.dekxi.tainote.db.*;
import com.dekxi.tainote.manager.*;
import javafx.application.*;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.*;
import java.io.*;
import java.nio.file.*;

import static com.dekxi.tainote.util.NodeBuilder.buildTitleBar;

final class TainoteSceneConfig {
    public static final String CONFIG_FILE = "tainote_config.properties";
    public static final String EDITOR_FXML = "/scenes/editor.fxml";
    public static final String APP_ICON = "/assets/tairitsu_icon.png";

    private TainoteSceneConfig() {}
}

public class App {
    public void start(Stage stage, HostServices hostServices) throws Exception {
        setupMainStage(stage, hostServices);
        stage.show();
    }
    private Stage setupMainStage(
            Stage stage,
            HostServices hostServices
    ){
        Parent root;
        Path configFile = AppDataPaths.getAppDataDir().resolve(TainoteSceneConfig.CONFIG_FILE);
        Scene scene;

        try{
            ConfigManager config = new ConfigManager(configFile);
            DatabaseManager databaseManager = new DatabaseManager();
            TainoteManager tainoteManager = new TainoteManager();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(TainoteSceneConfig.EDITOR_FXML));
            root = loader.load();
            scene = new Scene(root);
            MainController controller = loader.getController();
            controller.setStage(stage);
            controller.setConfigManager(config);
            controller.setTainoteManager(tainoteManager);
            controller.setDatabaseManager(databaseManager);
            controller.setOnClose(config::saveUserProperties);
            controller.setHostServices(hostServices);

            controller.setScene(scene);
            controller.initAppState();
            controller.initStyleParser();
            controller.initCounterHandler();
            controller.initListHandler();
            controller.initDanceHandler();
            controller.initWPMHandler();
            controller.initSearchHandler();
            controller.initTagHandler();
            controller.initCreateHandler();
            controller.initSaveHandler();
            controller.initDeleteHandler();
            controller.initImportHandler();
            controller.initExportHandler();
            controller.initSyncHandler();
            controller.initAboutHandler();
            controller.initEditorStateHandler();
            controller.initApp();
            controller.addTitleBar();

            Image image = new Image(TainoteSceneConfig.APP_ICON);
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.F11) {
                    if(stage.isFullScreen()) return;
                    stage.setFullScreen(true);
                }
                if(e.getCode()== KeyCode.ESCAPE){
                    if(!stage.isFullScreen()) return;
                    stage.setFullScreen(false);
                }
            });
            stage.setScene(scene);
            stage.setTitle("TaiNote");
            stage.fullScreenExitKeyProperty().setValue(null);
            stage.getIcons().add(image);
            stage.fullScreenExitHintProperty().setValue("");
            stage.setOnCloseRequest((_)->controller.onClose());
            stage.initStyle(StageStyle.UNDECORATED);
            stage.show();
            stage.setMinHeight(875);
            stage.setMinWidth(800);



        }catch(IOException e){
            System.out.println("[ERROR] : " + e.getClass().getSimpleName());
            e.printStackTrace();
        }
        return stage;
    }
}
