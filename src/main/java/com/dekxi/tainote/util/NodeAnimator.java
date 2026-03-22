package com.dekxi.tainote.util;

import javafx.animation.*;
import javafx.scene.*;
import javafx.util.*;

public class NodeAnimator {
    public static void shakeNode(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setFromX(0);
        shake.setByX(10);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.setOnFinished(_ -> node.setTranslateX(0));
        shake.play();
    }
    public static void addHoverLift(Node node) {
        TranslateTransition up = new TranslateTransition(Duration.millis(150), node);
        up.setToY(-5);

        TranslateTransition down = new TranslateTransition(Duration.millis(150), node);
        down.setToY(0);

        node.setOnMouseEntered(_ -> up.play());
        node.setOnMouseExited(_ -> down.play());
    }
}
