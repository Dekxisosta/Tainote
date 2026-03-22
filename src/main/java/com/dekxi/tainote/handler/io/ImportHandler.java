package com.dekxi.tainote.handler.io;

import javafx.scene.control.*;

public class FileMenuItemHandler {
    private MenuItem newMenuItem;
    private MenuItem openMenuItem;
    private MenuItem saveMenuItem;

    public FileMenuItemHandler(
        MenuItem newMenuItem,
        MenuItem openMenuItem,
        MenuItem saveMenuItem
    ){
        this.newMenuItem = newMenuItem;
        this.openMenuItem = openMenuItem;
        this.saveMenuItem = saveMenuItem;
    }

//    private void setNewMenuItem(){
//        newMenuItem.disableProperty().bind(
//                Bindings.createBooleanBinding(
//                        () -> titleField.getText().isEmpty() && textArea.getText().isEmpty(),
//                        titleField.textProperty(),
//                        textArea.textProperty()
//                )
//        );
//    }
//    private void setUpAboutMenuItem(){
//        aboutMenuItem.setOnAction(_ -> hostServices.showDocument("https://github.com/Dekxisosta"));
//    }
}
