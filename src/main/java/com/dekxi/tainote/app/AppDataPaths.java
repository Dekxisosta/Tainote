package com.dekxi.tainote.config;

import java.nio.file.*;

public final class AppDataPaths {

    private static final String APP_NAME = "Tainote";

    public static Path getAppDataDir() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            String appData = System.getenv("APPDATA");
            if (appData == null) {
                throw new IllegalStateException("APPDATA not set");
            }
            return Paths.get(appData, APP_NAME);

        } else if (os.contains("mac")) {
            return Paths.get(
                    System.getProperty("user.home"),
                    "Library", "Application Support", APP_NAME
            );

        } else {
            String xdg = System.getenv("XDG_CONFIG_HOME");
            if (xdg != null && !xdg.isBlank()) {
                return Paths.get(xdg, APP_NAME.toLowerCase());
            }
            return Paths.get(
                    System.getProperty("user.home"),
                    ".config", APP_NAME.toLowerCase()
            );
        }
    }
}