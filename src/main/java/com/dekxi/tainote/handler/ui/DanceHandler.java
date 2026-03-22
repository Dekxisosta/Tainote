package com.dekxi.tainote.handler;

import javafx.animation.*;
import javafx.scene.control.*;
import javafx.scene.image.*;
import javafx.scene.input.*;
import javafx.util.*;

import java.util.*;

final class DanceConfig {
    static final String SLEEP_GIF = "/assets/sleep.gif";
    static final String DANCING_FRAME_FORMAT = "/assets/tai_dancing-%d.png";
    static final int FRAME_COUNT = 15;
    static final long FAST_TYPING_THRESHOLD = 200;
    static final long SLOW_TYPING_THRESHOLD = 1000;
    static final int IDLE_SECONDS = 3;

    private DanceConfig() {}
}

public class DanceHandler {
    private long lastKeystrokeTime = -1;
    private int taiDancingCounter = 0;
    private Timeline idleTimeline;

    private Image[] images;


    private ImageView taiDancingImageView;
    private TextArea textArea;

    public DanceHandler(
            ImageView taiDancingImageView,
            TextArea textArea
    ) {
        this.taiDancingImageView = taiDancingImageView;
        this.textArea = textArea;

        setUpTairitsuDancing();
    }
    private void setUpTairitsuDancing() {
        List<Image> list = new ArrayList<>();
        for (int i = 0; i < DanceConfig.FRAME_COUNT; i++) {
            list.add(new Image(getClass().getResource(
                    String.format(DanceConfig.DANCING_FRAME_FORMAT, i)).toExternalForm()));
        }
        Image sleep = new Image(getClass().getResource(DanceConfig.SLEEP_GIF).toExternalForm());
        images = list.toArray(new Image[0]);

        idleTimeline = new Timeline(new KeyFrame(Duration.seconds(DanceConfig.IDLE_SECONDS), _ -> {
            taiDancingImageView.setImage(sleep);
        }));
        idleTimeline.setCycleCount(1);
        taiDancingImageView.setImage(sleep);

        textArea.addEventHandler(KeyEvent.KEY_TYPED, _ -> {
            long now = System.currentTimeMillis();
            if (lastKeystrokeTime != -1) {
                long gap = now - lastKeystrokeTime;
                if (gap < DanceConfig.FAST_TYPING_THRESHOLD) {
                    taiDancingCounter++;
                } else if (gap > DanceConfig.SLOW_TYPING_THRESHOLD) {
                    taiDancingCounter--;
                    if (taiDancingCounter < 0) taiDancingCounter = 0;
                }
            }
            lastKeystrokeTime = now;
            idleTimeline.stop();
            idleTimeline.playFromStart();
            taiDancingCounter %= images.length;
            taiDancingImageView.setImage(images[taiDancingCounter++]);
        });
    }
}
