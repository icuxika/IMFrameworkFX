package com.jfoenix.control;

import com.jfoenix.JFoenixResource;
import com.jfoenix.skin.JFXTabPaneSkin;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableBooleanProperty;
import javafx.css.Styleable;
import javafx.css.StyleableBooleanProperty;
import javafx.css.converter.BooleanConverter;
import javafx.scene.control.Skin;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFXTabPane extends TabPane {
    /**
     * Initialize the style class to 'jfx-tab-pane'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-tab-pane";
    private static final String USER_AGENT_STYLESHEET = JFoenixResource.load("css/control/jfx-tab-pane.css").toExternalForm();

    /**
     * {@inheritDoc}
     */
    public JFXTabPane() {
        initialize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new JFXTabPaneSkin(this);
    }

    private void initialize() {
        this.getStyleClass().setAll(DEFAULT_STYLE_CLASS);
        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return USER_AGENT_STYLESHEET;
    }

    /**
     * propagate any mouse events on the tab pane to its parent
     */
    public void propagateMouseEventsToParent() {
        this.addEventHandler(MouseEvent.ANY, e -> {
            e.consume();
            this.getParent().fireEvent(e);
        });
    }

    /**
     * disable animation on validation
     */
    private StyleableBooleanProperty disableAnimation = new SimpleStyleableBooleanProperty(StyleableProperties.DISABLE_ANIMATION,
            JFXTabPane.this,
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
        private static final CssMetaData<JFXTabPane, Boolean> DISABLE_ANIMATION =
                new CssMetaData<JFXTabPane, Boolean>("-jfx-disable-animation",
                        BooleanConverter.getInstance(), false) {
                    @Override
                    public boolean isSettable(JFXTabPane control) {
                        return control.disableAnimation == null || !control.disableAnimation.isBound();
                    }

                    @Override
                    public StyleableBooleanProperty getStyleableProperty(JFXTabPane control) {
                        return control.disableAnimationProperty();
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(
                    TabPane.getClassCssMetaData());
            Collections.addAll(styleables, DISABLE_ANIMATION);
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getControlCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }

}
