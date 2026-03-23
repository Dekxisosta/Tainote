package com.dekxi.tainote.handler.ui;

import com.dekxi.tainote.config.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.util.*;

import java.util.*;
import java.util.stream.*;

public class StyleHandler {
    private ConfigManager config;
    private TextArea textArea;
    private ComboBox<String> fontComboBox;
    private Spinner<Integer> fontSizeSpinner;
    private ColorPicker colorPicker;
    public StyleHandler(
            ConfigManager config,
            TextArea textArea,
            ComboBox<String> fontComboBox,
            Spinner<Integer> fontSizeSpinner,
            ColorPicker colorPicker
    ){
        this.config = config;
        this.textArea = textArea;
        this.fontComboBox = fontComboBox;
        this.fontSizeSpinner = fontSizeSpinner;
        this.colorPicker = colorPicker;

        setUpColorPickerHandler();
        setUpFontSizeHandler();
        setUpFontComboBoxHandler();
        setUpTextArea();
    }
    private void setUpTextArea(){
        textArea.setStyle(config.getEditorStyleSheet());
        textArea.setWrapText(true);
    }
    private void setUpFontSizeHandler(){
        fontSizeSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,1024, config.getFontSizeInPx()));
        fontSizeSpinner.getValueFactory().setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                if (value == null) return "";
                return value + "px";
            }

            @Override
            public Integer fromString(String string) {
                if (string == null || string.isEmpty()) return 0;
                String numeric = string.replaceAll("[^0-9]", "");
                return numeric.isEmpty() ? 0 : Integer.parseInt(numeric);
            }
        });
        fontSizeSpinner.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    int newSize = Integer.parseInt(fontSizeSpinner.getEditor().getText());
                    fontSizeSpinner.getValueFactory().setValue(newSize);
                    fontSizeSpinner.getEditor().positionCaret(fontSizeSpinner.getEditor().getText().length());
                    config.setFontSizeInPx(newSize);
                } catch (NumberFormatException e) {
                    if(fontSizeSpinner.getEditor().getText().length() == 0) {
                        fontSizeSpinner.getValueFactory().setValue(config.getFontSizeInPx());
                    } else fontSizeSpinner.getEditor().setText(fontSizeSpinner.getValue().toString());
                }
            }
        });
        fontSizeSpinner.valueProperty().addListener((_, _, _) -> {
            updateEditorStyle();
        });
    }
    private void setUpColorPickerHandler(){
        colorPicker.setValue(config.getFontColor());
        colorPicker.setOnAction(_ -> {
            updateEditorStyle();
        });
    }
    private void setUpFontComboBoxHandler(){
        fontComboBox.setValue(config.getFontStyle().replaceAll("'", ""));
        fontComboBox.getItems().addAll(Font.getFamilies());
        fontComboBox.setOnAction(_ -> {
            updateEditorStyle();
        });
    }

    private void updateEditorStyle(){
        config.setFontColor(colorPicker.getValue());
        config.setFontStyle(fontComboBox.getValue());
        config.setFontSizeInPx(fontSizeSpinner.getValue());
        textArea.setStyle(getToolbarProperties());
    }
    private String getToolbarProperties(){
        Map<String, String> map = parseStyle(textArea.getStyle());
        map.put("-fx-font-family", "'" + fontComboBox.getValue() + "'");
        map.put("-fx-font-size", fontSizeSpinner.getValue() + "px");
        map.put("-fx-text-fill", toCssColor(colorPicker.getValue()));
        return toCss(map);
    }
    private String toCss(Map<String, String> style) {
        System.out.println(style.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue() + ";")
                .collect(Collectors.joining(" ")));
        return style.entrySet().stream()
                .map(e -> e.getKey() + ": " + e.getValue() + ";")
                .collect(Collectors.joining(" "));
    }
    private String toCssColor(Color c) {
        return String.format(
                "rgba(%d,%d,%d,%f)",
                (int)(c.getRed() * 255),
                (int)(c.getGreen() * 255),
                (int)(c.getBlue() * 255),
                c.getOpacity()
        );
    }
    private Map<String, String> parseStyle(String css) {
        Map<String, String> map = new HashMap<>();
        if (css == null || css.isBlank()) return map;

        for (String rule : css.split(";")) {
            if (rule.isBlank()) continue;

            String[] parts = rule.split(":", 2);
            if (parts.length == 2) {
                map.put(parts[0].trim(), parts[1].trim());
            }
        }
        return map;
    }
}
