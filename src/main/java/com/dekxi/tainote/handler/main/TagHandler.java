package com.dekxi.tainote.handler;

import javafx.geometry.*;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.*;

import java.util.*;

import static com.dekxi.tainote.util.NodeAnimator.addHoverLift;
import static com.dekxi.tainote.util.NodeAnimator.shakeNode;

final class TagConfig {
    static final String ADD_CHIP_STYLE = """
            -fx-background-color: transparent;
            -fx-background-radius: 12;
            -fx-padding: 4 10 4 10;
            -fx-border-color: #7B00FF;
            -fx-border-radius: 12;
            -fx-border-width: 1.5;
            -fx-cursor: hand;
            """;
    static final String ADD_CHIP_LABEL_STYLE = "-fx-text-fill: #7B00FF; -fx-font-size: 11px;";
    static final String ADD_CHIP_LABEL = "+ Add";

    static final String CHIP_STYLE = """
            -fx-background-color: #7B00FF;
            -fx-background-radius: 12;
            -fx-padding: 4 10 4 10;
            """;
    static final String CHIP_LABEL_STYLE = "-fx-text-fill: white; -fx-font-size: 11px;";
    static final String CHIP_REMOVE_BTN_STYLE = """
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 11px;
            -fx-padding: 0;
            -fx-cursor: hand;
            """;
    static final String CHIP_REMOVE_BTN_TEXT = "×";
    static final double CHIP_MAX_HEIGHT = 28;

    static final String TAG_EMPTY_PROMPT = "Enter a tag..!";

    private TagConfig() {}
}

public class TagHandler {
    private final FlowPane tagChipPane;
    private final TextField tagTextField;
    private final List<String> currentTags;

    public TagHandler(FlowPane tagChipPane, TextField tagTextField) {
        this.tagChipPane = tagChipPane;
        this.tagTextField = tagTextField;
        this.currentTags = new ArrayList<>();
        setUpTagInput();
    }

    private void setUpTagInput() {
        HBox.setMargin(tagChipPane, new Insets(10));
        tagChipPane.getChildren().add(createAddChip());
        tagTextField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) addTag();
        });
    }

    private void addTag() {
        String tag = tagTextField.getText().trim().toLowerCase();
        if (tag.isEmpty()) {
            tagTextField.setPromptText(TagConfig.TAG_EMPTY_PROMPT);
            shakeNode(tagTextField);
            return;
        }
        if (currentTags.contains(tag)) return;

        currentTags.add(tag);
        tagChipPane.getChildren().add(
                tagChipPane.getChildren().size() - 1,
                createChip(tag)
        );
        tagTextField.clear();
    }

    private HBox createAddChip() {
        HBox chip = new HBox(5);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setStyle(TagConfig.ADD_CHIP_STYLE);

        Label label = new Label(TagConfig.ADD_CHIP_LABEL);
        label.setStyle(TagConfig.ADD_CHIP_LABEL_STYLE);

        chip.getChildren().add(label);
        chip.setOnMouseClicked(_ -> addTag());
        return chip;
    }

    private HBox createChip(String tag) {
        HBox chip = new HBox(5);
        chip.setAlignment(Pos.CENTER_LEFT);
        chip.setMaxHeight(TagConfig.CHIP_MAX_HEIGHT);
        chip.setStyle(TagConfig.CHIP_STYLE);

        Label label = new Label(tag);
        label.setStyle(TagConfig.CHIP_LABEL_STYLE);

        Button remove = new Button(TagConfig.CHIP_REMOVE_BTN_TEXT);
        remove.setStyle(TagConfig.CHIP_REMOVE_BTN_STYLE);
        remove.setOnAction(e -> {
            currentTags.remove(tag);
            tagChipPane.getChildren().remove(chip);
        });

        addHoverLift(label);
        chip.getChildren().addAll(label, remove);
        return chip;
    }

    public String[] getCurrentTags() {
        return currentTags.toArray(new String[0]);
    }
}