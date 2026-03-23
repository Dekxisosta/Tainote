package com.dekxi.tainote.handler.ui;

import javafx.scene.*;
import javafx.scene.control.*;

import javax.print.*;
import java.io.*;
import java.net.*;
import java.nio.file.*;

import static com.dekxi.tainote.util.TextFormatter.toTitleCase;

public class ThemeHandler {
    private Menu themeMenu;
    private Scene scene;

    public ThemeHandler(
            Menu themeMenu,
            Scene scene
    ){
        this.themeMenu = themeMenu;
        this.scene = scene;
        installThemes();
    }

    public void installThemes(){
        File themesDir;
        try{
            themesDir = new File(getClass().getResource("/themes").toURI());
        }catch(URISyntaxException e){
            e.printStackTrace();
            return;
        }
        File[] cssFiles = themesDir.listFiles((_, name) -> name.endsWith(".css"));
        for (File cssFile : cssFiles) {
            MenuItem cssMenuItem = new MenuItem(toTitleCase(cssFile.getName()).replace(".css", ""));
            cssMenuItem.setOnAction(_ -> {
                scene.getStylesheets().clear();
                scene.getStylesheets().add(cssFile.toURI().toString());
            });
            themeMenu.getItems().add(cssMenuItem);
        }
    }
}
