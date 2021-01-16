package com.jfoenix.control;

import com.jfoenix.JFoenixResource;
import com.jfoenix.skin.JFXProgressBarSkin;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Skin;

public class JFXProgressBar extends ProgressBar {
    /**
     * Initialize the style class to 'jfx-progress-bar'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-progress-bar";

    /**
     * {@inheritDoc}
     */
    public JFXProgressBar() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    public JFXProgressBar(double progress) {
        super(progress);
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResource.load("css/control/jfx-progress-bar.css").toExternalForm();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXProgressBarSkin(this);
    }

    private void initialize() {
        setPrefWidth(200);
        getStyleClass().add(DEFAULT_STYLE_CLASS);
    }


    private DoubleProperty secondaryProgress = new SimpleDoubleProperty(INDETERMINATE_PROGRESS);

    public double getSecondaryProgress() {
        return secondaryProgress == null ? INDETERMINATE_PROGRESS : secondaryProgress.get();
    }

    public DoubleProperty secondaryProgressProperty() {
        return secondaryProgress;
    }

    public void setSecondaryProgress(double secondaryProgress) {
        secondaryProgressProperty().set(secondaryProgress);
    }
}
