package com.dekxi.tainote.controller;

import com.dekxi.tainote.manager.*;
import com.dekxi.tainote.config.ConfigManager;
import javafx.application.*;
import javafx.fxml.*;

import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;
import org.fxmisc.richtext.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class Controller {
    private HostServices hostServices;
    private ConfigManager config;
    private TainoteManager manager;
    private Stage stage;

    private Runnable onClose;

    @FXML @SuppressWarnings("unused")
    private ColorPicker colorPicker;

    @FXML @SuppressWarnings("unused")
    private ComboBox<String> fontComboBox;

    @FXML @SuppressWarnings("unused")
    private Spinner<Integer> fontSizeSpinner;

    @FXML @SuppressWarnings("unused")
    private StackPane imageContainer;

    @FXML @SuppressWarnings("unused")
    private ImageView imageView;

    @FXML @SuppressWarnings("unused")
    private MenuItem newNoteMenuItem;

    @FXML @SuppressWarnings("unused")
    private MenuItem openNoteMenuItem;

    @FXML @SuppressWarnings("unused")
    private MenuItem aboutMenuItem;

    @FXML @SuppressWarnings("unused")
    private InlineCssTextArea editor;

    @FXML @SuppressWarnings("unused")
    public void initialize(){}
    public void onClose(){
        onClose.run();
    }
    public void setOnClose(Runnable onClose){
        this.onClose = onClose;
    }
    public void setStage(Stage stage){
        this.stage = stage;
    }
    public void setConfigManager(ConfigManager config){this.config = config;}
    public void setTainoteManager(TainoteManager manager){this.manager = manager;}
    public void setHostServices(HostServices hostServices) {this.hostServices = hostServices;}
    /*
     * ==========================================
     * Tainote Note-taking CRUD Operations
     * ==========================================
     */
    @FXML @SuppressWarnings("unused")
    private void createNewTainote(){
    }
    @FXML @SuppressWarnings("unused")
    private void openTainote(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");

        File file = fileChooser.showOpenDialog(stage);
        if(file != null) {
            try{
                UUID id = manager.checkFileIfTainote(file);
                Map<String, String> parsed = manager.readTainote(id);
            }catch(IllegalArgumentException e){
                showWarning("Tainote Check Error", e.getMessage());
            }
        }
    }
    @FXML @SuppressWarnings("unused")
    private void saveTainote(){

    }
    @FXML @SuppressWarnings("unused")
    private void deleteTainote(){

    }


    /*
     * ==========================================
     * Tainote Set Up Injected FX Fields
     * ==========================================
     */
    public void setDefaults(){
        setUpImageView();
        setUpFontSizeSpinner();
        setUpFontComboBox();
        setUpAboutMenuItem();
        setUpColorPicker();
        setUpEditor();
    }
    private void setUpEditor(){
        editor.setStyle(String.format("""
                -fx-background-color: #191a1f;
                -fx-padding: 10;"
                -fx-border-color: #444;
                -fx-border-width: 1;
                -fx-text-fill: %s;
                """));
    }
    private void setUpAboutMenuItem(){
        aboutMenuItem.setOnAction(_ -> hostServices.showDocument("https://github.com/Dekxisosta"));
    }
    private void setUpImageView(){
        Image image = new Image(getClass().getResourceAsStream("/assets/tai_dancing.gif"));
        imageView.setImage(image);
    }
    private void setUpFontSizeSpinner(){
        setUpFontSizeSpinnerValues();
        setUpFontSizeSpinnerEditor();
        setUpFontSizeSpinnerArrowListener();
    }
    private void setUpFontSizeSpinnerValues(){
        fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1024, config.getFontSize()));
    }
    private void setUpFontSizeSpinnerEditor(){

        fontSizeSpinner.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int newSize = Integer.parseInt(fontSizeSpinner.getEditor().getText());
                    fontSizeSpinner.getValueFactory().setValue(newSize);
                    fontSizeSpinner.getEditor().positionCaret(fontSizeSpinner.getEditor().getText().length());
                    config.setFontSize(newSize);

                } catch (NumberFormatException e) {
                    if(fontSizeSpinner.getEditor().getText().length() == 0) fontSizeSpinner.getValueFactory().setValue(config.getFontSize());
                    else fontSizeSpinner.getEditor().setText(fontSizeSpinner.getValue().toString());
                }
            }
        });
    }
    private void setUpFontSizeSpinnerArrowListener(){
        fontSizeSpinner.valueProperty().addListener((_, _, newValue) -> {
            config.setFontSize(newValue);
        });
    }
    private void setUpColorPicker(){
        setUpColorPickerValues();
        setUpColorPickerHandler();
    }
    private void setUpColorPickerValues(){
        colorPicker.setValue(config.getFontColor());
    }
    private void setUpColorPickerHandler(){
        colorPicker.setOnAction(_ -> {
            Color selected = colorPicker.getValue();
            String rgb = String.format("rgb(%d, %d, %d)",
                    (int)(selected.getRed() * 255),
                    (int)(selected.getGreen() * 255),
                    (int)(selected.getBlue() * 255));
            config.setFontColor(selected);
        });
    }
    private void setUpFontComboBox(){
        setUpFontComboBoxValues();
        setUpFontComboBoxHandler();
    }
    private void setUpFontComboBoxValues(){
        fontComboBox.setValue(config.getFontStyle());
        fontComboBox.getItems().addAll(Font.getFamilies());
    }
    private void setUpFontComboBoxHandler(){
        fontComboBox.setOnAction(_ -> {
            config.setFontStyle(fontComboBox.getValue());
        });
    }

    /*
     * ==========================================
     * Tainote FX Utilities
     * ==========================================
     */
    private void showWarning(String header, String content){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    private boolean continuePromptFromUnsavedChanges(){
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Warning");
        dialog.setHeaderText("Unsaved changes");
        dialog.setContentText("You have unsaved changes. Do you want to continue?");

        ButtonType continueBtn = new ButtonType("Continue");
        ButtonType cancelBtn = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getButtonTypes().setAll(continueBtn, cancelBtn);

        Optional<ButtonType> result = dialog.showAndWait();

        if (result.isPresent() && result.get() == continueBtn) return true;
        else return false;
    }


}
