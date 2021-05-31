package com.icuxika.framework;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class Framework {

    /**
     * 是否启用暗黑模式
     */
    private static final BooleanProperty darkMode = new SimpleBooleanProperty(false);

    public static BooleanProperty darkModeProperty() {
        return darkMode;
    }

    public static void setDarkMode(boolean value) {
        darkModeProperty().set(value);
    }


    /**
     * 是否启用离线模式
     */
    private static final BooleanProperty offlineMode = new SimpleBooleanProperty(false);

    public static BooleanProperty offlineModeProperty() {
        return offlineMode;
    }

    public static void setOfflineMode(boolean value) {
        offlineModeProperty().set(value);
    }
}
