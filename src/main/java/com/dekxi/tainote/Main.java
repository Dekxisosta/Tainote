package com.dekxi.tainote;

import com.dekxi.tainote.app.App;
import com.dekxi.tainote.controller.*;
import javafx.application.Application;
import javafx.fxml.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.stage.Stage;

import java.io.*;

public class Main extends Application{
    public static void main(String[] args){
        try{
            launch(args);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage stage) throws Exception {
        new App().start(stage, getHostServices());
    }
}
