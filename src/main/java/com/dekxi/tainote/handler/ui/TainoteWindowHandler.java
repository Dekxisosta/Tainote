package com.dekxi.tainote.handler.ui;

import javafx.event.*;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

import static com.dekxi.tainote.util.NodeBuilder.currentCursor;
import static com.dekxi.tainote.util.NodeBuilder.makeResizable;

public class TainoteWindowHandler {
    private Stage stage;
    private Scene scene;
    private HBox titleBar;
    private Button minimizeButton;
    private Button maximizeButton;
    private Button closeButton;

    public TainoteWindowHandler(
            Stage stage,
            Scene scene,
            HBox titleBar,
            Button minimizeButton,
            Button maximizeButton,
            Button closeButton
    ){
        this.stage = stage;
        this.scene = scene;
        this.titleBar = titleBar;
        this.minimizeButton = minimizeButton;
        this.maximizeButton = maximizeButton;
        this.closeButton = closeButton;

        makeResizable(stage, scene);
        setUpTitleBar();
    }
    private void setUpTitleBar(){
        minimizeButton.setOnAction(_ -> stage.setIconified(true));
        maximizeButton.setOnAction(_ -> stage.setMaximized(!stage.isMaximized()));
        closeButton.setOnAction(_ -> Event.fireEvent(stage, new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST)));

        final double[] offset = new double[2];
        titleBar.setOnMousePressed(e -> {
            offset[0] = e.getSceneX();
            offset[1] = e.getSceneY();
        });
        titleBar.setOnMouseDragged(e -> {
            if (currentCursor[0] != Cursor.DEFAULT) return;
            stage.setX(e.getScreenX() - offset[0]);
            stage.setY(e.getScreenY() - offset[1]);
        });
    }
}
