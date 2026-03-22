package com.dekxi.tainote.handler.ui;

import javafx.scene.control.*;
import javafx.stage.*;

import static com.dekxi.tainote.util.NodeBuilder.buildAboutStage;

public class AboutHandler {
    private final MenuItem aboutMenuItem;
    private final Stage stage;

    public AboutHandler(MenuItem aboutMenuItem, Stage stage) {
        this.aboutMenuItem = aboutMenuItem;
        this.stage = stage;

        aboutMenuItem.setOnAction(_ -> buildAboutStage(stage).show());
    }
}
