package com.dekxi.tainote.handler.counter;

import com.dekxi.tainote.app.*;
import javafx.scene.control.*;

public class WPMHandler {
    private long lastKeystrokeTime = -1;
    private long activeMillis = 0;
    private long totalChars;
    private static final long IDLE_THRESHOLD = 1500;

    private AppState appState;
    private Label wpmLabel;
    private TextArea textArea;

    public WPMHandler(
            AppState appState,
            Label wpmLabel,
            TextArea textArea
    ){
        this.appState = appState;
        this.wpmLabel = wpmLabel;
        this.textArea = textArea;

        setUpWpmLabel();
    }

    private void setUpWpmLabel(){
        wpmLabel.setText(String.valueOf(0));

        textArea.textProperty().addListener((_, _, newVal) -> {
            if(appState.isLoadingNote()) return;
            long now = System.currentTimeMillis();
            if (lastKeystrokeTime != -1) {
                long gap = now - lastKeystrokeTime;
                if (gap < IDLE_THRESHOLD) activeMillis += gap;
            }
            appState.setHasTyped(true);
            lastKeystrokeTime = now;
            totalChars++;
            double activeMinutes = activeMillis / 60000.0;
            int wpm = activeMinutes > 0 ? (int) ((totalChars / 5.0) / activeMinutes) : 0;
            wpmLabel.setText(String.valueOf(wpm));
        });
    }
}
