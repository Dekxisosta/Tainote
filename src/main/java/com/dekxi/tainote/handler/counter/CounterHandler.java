package com.dekxi.tainote.handler;

import javafx.animation.*;
import javafx.fxml.*;
import javafx.scene.control.*;
import javafx.util.Duration;

import java.time.*;
import java.time.format.*;
import java.util.*;

public class CounterHandler {
    private TextArea textArea;
    private Label charCountLabel;
    private Label wordCountLabel;
    private Label uniqueWordCountLabel;
    private Label timeStartedLabel;
    private Label timeElapsedLabel;
    private Label zoomLabel;
    private Label wpmLabel;

    public CounterHandler(
        TextArea textArea,
        Label charCountLabel,
        Label wordCountLabel,
        Label uniqueWordCountLabel,
        Label timeStartedLabel,
        Label timeElapsedLabel,
        Label zoomLabel,
        Label wpmLabel
    ){
        this.textArea = textArea;
        this.charCountLabel = charCountLabel;
        this.wordCountLabel = wordCountLabel;
        this.uniqueWordCountLabel = uniqueWordCountLabel;
        this.timeStartedLabel = timeStartedLabel;
        this.timeElapsedLabel = timeElapsedLabel;
        this.zoomLabel = zoomLabel;
        this.wpmLabel = wpmLabel;

        setUpElapsedTimer();
        setUpStartTime();
        setUpCounters();
    }
    private void setUpCounters(){
        charCountLabel.setText(String.valueOf(0));
        wordCountLabel.setText(String.valueOf(0));
        uniqueWordCountLabel.setText(String.valueOf(0));
        textArea.textProperty().addListener((_, _, newVal) -> {
            charCountLabel.setText(String.valueOf(newVal.length()));

            String trimmed = newVal.trim();
            int wordCount = trimmed.isEmpty() ? 0 : trimmed.split("\\s+").length;
            wordCountLabel.setText(String.valueOf(wordCount));

            long uniqueCount = trimmed.isEmpty() ? 0 :
                    Arrays.stream(trimmed.split("\\s+"))
                            .map(String::toLowerCase)
                            .distinct()
                            .count();
            uniqueWordCountLabel.setText(String.valueOf(uniqueCount));
        });
    }
    private void setUpElapsedTimer(){
        final long[] secondsElapsed = {0};
        Timeline timer = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            secondsElapsed[0]++;
            long hrs = secondsElapsed[0] / 3600;
            long mins = (secondsElapsed[0] % 3600) / 60;
            long secs = secondsElapsed[0] % 60;
            timeElapsedLabel.setText(String.format("%02d:%02d:%02d", hrs, mins, secs));
        }));
        timer.setCycleCount(Animation.INDEFINITE);
        timer.play();
    }
    private void setUpStartTime(){
        LocalTime now = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        timeStartedLabel.setText(now.format(formatter));
    }
}
