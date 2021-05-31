package com.jfoenix.control;

import com.jfoenix.JFoenixResource;
import com.jfoenix.skin.JFXToggleButtonSkin;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.PaintConverter;
import javafx.scene.control.Skin;
import javafx.scene.control.ToggleButton;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFXToggleButton extends ToggleButton {

    public JFXToggleButton() {
        initialize();
        // init in scene builder workaround ( TODO : remove when JFoenix is well integrated in scenebuilder by gluon )
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTraceElements.length && i < 15; i++) {
            if (stackTraceElements[i].getClassName().toLowerCase().contains(".scenebuilder.kit.fxom.")) {
                this.setText("ToggleButton");
                break;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXToggleButtonSkin(this);
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
        // it's up for the user to add this behavior
//        toggleColor.addListener((o, oldVal, newVal) -> {
//            // update line color in case not set by the user
//            if(newVal instanceof Color)
//                toggleLineColor.set(((Color)newVal).desaturate().desaturate().brighter());
//        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResource.load("css/control/jfx-toggle-button.css").toExternalForm();
    }


    /***************************************************************************
     *                                                                         *
     * styleable Properties                                                    *
     *                                                                         *
     **************************************************************************/

    /**
     * Initialize the style class to 'jfx-toggle-button'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-toggle-button";

    /**
     * default color used when the button is toggled
     */
    private StyleableObjectProperty<Paint> toggleColor = new SimpleStyleableObjectProperty<>(StyleableProperties.TOGGLE_COLOR,
            JFXToggleButton.this,
            "toggleColor",
            Color.valueOf(
                    "#009688"));

    public Paint getToggleColor() {
        return toggleColor == null ? Color.valueOf("#009688") : toggleColor.get();
    }

    public StyleableObjectProperty<Paint> toggleColorProperty() {
        return this.toggleColor;
    }

    public void setToggleColor(Paint color) {
        this.toggleColor.set(color);
    }

    /**
     * default color used when the button is not toggled
     */
    private StyleableObjectProperty<Paint> untoggleColor = new SimpleStyleableObjectProperty<>(StyleableProperties.UNTOGGLE_COLOR,
            JFXToggleButton.this,
            "unToggleColor",
            Color.valueOf(
                    "#FAFAFA"));

    public Paint getUnToggleColor() {
        return untoggleColor == null ? Color.valueOf("#FAFAFA") : untoggleColor.get();
    }

    public StyleableObjectProperty<Paint> unToggleColorProperty() {
        return this.untoggleColor;
    }

    public void setUnToggleColor(Paint color) {
        this.untoggleColor.set(color);
    }

    /**
     * default line color used when the button is toggled
     */
    private StyleableObjectProperty<Paint> toggleLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.TOGGLE_LINE_COLOR,
            JFXToggleButton.this,
            "toggleLineColor",
            Color.valueOf("#77C2BB"));

    public Paint getToggleLineColor() {
        return toggleLineColor == null ? Color.valueOf("#77C2BB") : toggleLineColor.get();
    }

    public StyleableObjectProperty<Paint> toggleLineColorProperty() {
        return this.toggleLineColor;
    }

    public void setToggleLineColor(Paint color) {
        this.toggleLineColor.set(color);
    }

    /**
     * default line color used when the button is not toggled
     */
    private StyleableObjectProperty<Paint> untoggleLineColor = new SimpleStyleableObjectProperty<>(
            StyleableProperties.UNTOGGLE_LINE_COLOR,
            JFXToggleButton.this,
            "unToggleLineColor",
            Color.valueOf("#999999"));

    public Paint getUnToggleLineColor() {
        return untoggleLineColor == null ? Color.valueOf("#999999") : untoggleLineColor.get();
    }

    public StyleableObjectProperty<Paint> unToggleLineColorProperty() {
        return this.untoggleLineColor;
    }

    public void setUnToggleLineColor(Paint color) {
        this.untoggleLineColor.set(color);
    }

    /**
     * Default size of the toggle button.
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(
            StyleableProperties.SIZE,
            JFXToggleButton.this,
            "size",
            10.0);

    public double getSize() {
        return size.get();
    }

    public StyleableDoubleProperty sizeProperty() {
        return this.size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    /**
     * Disable the visual indicator for focus
     */
    private StyleableBooleanProperty disableVisualFocus = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_VISUAL_FOCUS,
            JFXToggleButton.this,
            "disableVisualFocus",
            false);

    public final StyleableBooleanProperty disableVisualFocusProperty() {
        return this.disableVisualFocus;
    }

    public final Boolean isDisableVisualFocus() {
        return disableVisualFocus != null && this.disableVisualFocusProperty().get();
    }

    public final void setDisableVisualFocus(final Boolean disabled) {
        this.disableVisualFocusProperty().set(disabled);
    }


    /**
     * disable animation on button action
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_ANIMATION,
            JFXToggleButton.this,
            "disableAnimation",
            false);

    public final StyleableBooleanProperty disableAnimationProperty() {
        return this.disableAnimation;
    }

    public final Boolean isDisableAnimation() {
        return disableAnimation != null && this.disableAnimationProperty().get();
    }

    public final void setDisableAnimation(final Boolean disabled) {
        this.disableAnimationProperty().set(disabled);
    }


    private static class StyleableProperties {
        private static final CssMetaData<JFXToggleButton, Paint> TOGGLE_COLOR =
                new CssMetaData<JFXToggleButton, Paint>("-jfx-toggle-color",
                        PaintConverter.getInstance(), Color.valueOf("#009688")) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.toggleColor == null || !control.toggleColor.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
                        return control.toggleColorProperty();
                    }
                };

        private static final CssMetaData<JFXToggleButton, Paint> UNTOGGLE_COLOR =
                new CssMetaData<JFXToggleButton, Paint>("-jfx-untoggle-color",
                        PaintConverter.getInstance(), Color.valueOf("#FAFAFA")) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.untoggleColor == null || !control.untoggleColor.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
                        return control.unToggleColorProperty();
                    }
                };

        private static final CssMetaData<JFXToggleButton, Paint> TOGGLE_LINE_COLOR =
                new CssMetaData<JFXToggleButton, Paint>("-jfx-toggle-line-color",
                        PaintConverter.getInstance(), Color.valueOf("#77C2BB")) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.toggleLineColor == null || !control.toggleLineColor.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
                        return control.toggleLineColorProperty();
                    }
                };

        private static final CssMetaData<JFXToggleButton, Paint> UNTOGGLE_LINE_COLOR =
                new CssMetaData<JFXToggleButton, Paint>("-jfx-untoggle-line-color",
                        PaintConverter.getInstance(), Color.valueOf("#999999")) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.untoggleLineColor == null || !control.untoggleLineColor.isBound();
                    }

                    @Override
                    public StyleableProperty<Paint> getStyleableProperty(JFXToggleButton control) {
                        return control.unToggleLineColorProperty();
                    }
                };

        private static final CssMetaData<JFXToggleButton, Number> SIZE =
                new CssMetaData<JFXToggleButton, Number>("-jfx-size",
                        StyleConverter.getSizeConverter(), 10.0) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return !control.size.isBound();
                    }

                    @Override
                    public StyleableProperty<Number> getStyleableProperty(JFXToggleButton control) {
                        return control.sizeProperty();
                    }
                };
        private static final CssMetaData<JFXToggleButton, Boolean> DISABLE_VISUAL_FOCUS =
                new CssMetaData<JFXToggleButton, Boolean>("-jfx-disable-visual-focus",
                        BooleanConverter.getInstance(), false) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.disableVisualFocus == null || !control.disableVisualFocus.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(JFXToggleButton control) {
                        return control.disableVisualFocusProperty();
                    }
                };

        private static final CssMetaData<JFXToggleButton, Boolean> DISABLE_ANIMATION =
                new CssMetaData<JFXToggleButton, Boolean>("-jfx-disable-animation",
                        BooleanConverter.getInstance(), false) {
                    @Override
                    public boolean isSettable(JFXToggleButton control) {
                        return control.disableAnimation == null || !control.disableAnimation.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(JFXToggleButton control) {
                        return control.disableAnimationProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(ToggleButton.getClassCssMetaData());
            Collections.addAll(styleables,
                    SIZE,
                    TOGGLE_COLOR,
                    UNTOGGLE_COLOR,
                    TOGGLE_LINE_COLOR,
                    UNTOGGLE_LINE_COLOR,
                    DISABLE_VISUAL_FOCUS,
                    DISABLE_ANIMATION
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }


}
