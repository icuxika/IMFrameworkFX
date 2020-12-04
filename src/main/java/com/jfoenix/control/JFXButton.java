package com.jfoenix.control;

import com.jfoenix.JFoenixResource;
import com.jfoenix.converter.ButtonTypeConverter;
import com.jfoenix.skin.JFXButtonSkin;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFXButton extends Button {

    public JFXButton() {
        initialize();
    }

    public JFXButton(String text) {
        super(text);
        initialize();
    }

    public JFXButton(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    public JFXButton(StringBinding text) {
        textProperty().bind(text);
        initialize();
    }

    public JFXButton(StringBinding text, Node graphic) {
        textProperty().bind(text);
        setGraphic(graphic);
        initialize();
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXButtonSkin(this);
    }

    @Override
    public String getUserAgentStylesheet() {
        return DEFAULT_STYLESHEET;
    }

    private ObjectProperty<Paint> ripplerFill = new SimpleObjectProperty<>(null);

    public final ObjectProperty<Paint> ripplerFillProperty() {
        return this.ripplerFill;
    }

    public final Paint getRipplerFill() {
        return this.ripplerFillProperty().get();
    }

    public final void setRipplerFill(final Paint ripplerFill) {
        this.ripplerFillProperty().set(ripplerFill);
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-button";
    private static final String DEFAULT_STYLESHEET = JFoenixResource.load("css/control/jfx-button.css").toExternalForm();

    public enum ButtonType {FLAT, RAISED}

    private StyleableObjectProperty<ButtonType> buttonType = new SimpleStyleableObjectProperty<>(StyleableProperties.BUTTON_TYPE, JFXButton.this, "buttonType", ButtonType.FLAT);

    public ButtonType getButtonType() {
        return buttonType == null ? ButtonType.FLAT : buttonType.get();
    }

    public StyleableObjectProperty<ButtonType> buttonTypeProperty() {
        return this.buttonType;
    }

    public void setButtonType(ButtonType type) {
        this.buttonType.set(type);
    }

    private StyleableBooleanProperty disableVisualFocus = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_VISUAL_FOCUS, JFXButton.this, "disableVisualFocus", false);

    public final StyleableBooleanProperty disableVisualFocusProperty() {
        return this.disableVisualFocus;
    }

    public final Boolean isDisableVisualFocus() {
        return disableVisualFocus != null && this.disableVisualFocusProperty().get();
    }

    public final void setDisableVisualFocus(final Boolean disabled) {
        this.disableVisualFocusProperty().set(disabled);
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXButton, ButtonType> BUTTON_TYPE = new CssMetaData<JFXButton, ButtonType>("-jfx-button-type", ButtonTypeConverter.getInstance(), ButtonType.FLAT) {
            @Override
            public boolean isSettable(JFXButton styleable) {
                return styleable.buttonType == null || !styleable.buttonType.isBound();
            }

            @Override
            public StyleableProperty<ButtonType> getStyleableProperty(JFXButton styleable) {
                return styleable.buttonTypeProperty();
            }
        };

        private static final CssMetaData<JFXButton, Boolean> DISABLE_VISUAL_FOCUS = new CssMetaData<JFXButton, Boolean>("-jfx-disable-visual-focus", BooleanConverter.getInstance(), false) {
            @Override
            public boolean isSettable(JFXButton styleable) {
                return styleable.disableVisualFocus == null || !styleable.disableVisualFocus.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(JFXButton styleable) {
                return styleable.disableVisualFocusProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Button.getClassCssMetaData());
            Collections.addAll(styleables, BUTTON_TYPE, DISABLE_VISUAL_FOCUS);
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
