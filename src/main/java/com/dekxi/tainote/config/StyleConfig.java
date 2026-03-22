package com.dekxi.tainote.app;

// Will put in a .properties file later on, or a .txt
public final class StyleConfig {
    static final String CELL_SELECTED = "-fx-border-color: black; -fx-border-width: 1.5; -fx-background-color: transparent;";
    static final String CELL_DEFAULT = "-fx-border-color: transparent; -fx-background-color: transparent;";

    static final String TITLE_SELECTED = "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #7B00FF;";
    static final String TITLE_DEFAULT = "-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: black;";

    static final String SUBTITLE_SELECTED = "-fx-font-size: 11px; -fx-text-fill: #B06EFF;";
    static final String SUBTITLE_DEFAULT_AUTHOR = "-fx-font-size: 11px; -fx-text-fill: #888888;";
    static final String SUBTITLE_DEFAULT_MODIFIED = "-fx-font-size: 11px; -fx-text-fill: gray;";

    private StyleConfig() {}
}
