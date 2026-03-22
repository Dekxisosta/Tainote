package com.dekxi.tainote.handler;

import javafx.animation.*;
import javafx.scene.control.*;

public class WPMHandler {
    private long lastKeystrokeTime = -1;
    private long activeMillis = 0;
    private long totalChars;
    private static final long IDLE_THRESHOLD = 1500;

    private Label wpmLabel;
    private TextArea textArea;

    public WPMHandler(
            Label wpmLabel,
            TextArea textArea
    ){
        this.wpmLabel = wpmLabel;
        this.textArea = textArea;

        setUpWpmLabel();
    }

    private void setUpWpmLabel(){
        textArea.textProperty().addListener((_, _, newVal) -> {
            long now = System.currentTimeMillis();
            if (lastKeystrokeTime != -1) {
                long gap = now - lastKeystrokeTime;
                if (gap < IDLE_THRESHOLD) activeMillis += gap;
            }
            lastKeystrokeTime = now;
            totalChars++;
            double activeMinutes = activeMillis / 60000.0;
            int wpm = activeMinutes > 0 ? (int) ((totalChars / 5.0) / activeMinutes) : 0;
            wpmLabel.setText(String.valueOf(wpm));
        });
    }
}
