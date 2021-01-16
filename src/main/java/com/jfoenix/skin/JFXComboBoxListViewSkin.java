package com.jfoenix.skin;

import com.jfoenix.control.JFXComboBox;
import javafx.beans.property.ObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.Styleable;
import javafx.css.StyleableObjectProperty;
import javafx.css.StyleableProperty;
import javafx.css.converter.PaintConverter;
import javafx.scene.Node;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JFXComboBoxListViewSkin<T> extends ComboBoxListViewSkin<T> {

    /***************************************************************************
     *                                                                         *
     * Private fields                                                          *
     *                                                                         *
     **************************************************************************/

    private boolean invalid = true;

    private Text promptText;
    private ValidationPane<JFXComboBox> errorContainer;
    private PromptLinesWrapper<JFXComboBox> linesWrapper;

    protected final ObjectProperty<Paint> promptTextFill = new StyleableObjectProperty<Paint>(Color.GRAY) {
        @Override
        public Object getBean() {
            return JFXComboBoxListViewSkin.this;
        }

        @Override
        public String getName() {
            return "promptTextFill";
        }

        @Override
        public CssMetaData<JFXComboBox, Paint> getCssMetaData() {
            return StyleableProperties.PROMPT_TEXT_FILL;
        }
    };

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JFXComboBoxListViewSkin(final JFXComboBox<T> comboBox) {
        super(comboBox);


        linesWrapper = new PromptLinesWrapper<>(
                comboBox,
                promptTextFill,
                comboBox.valueProperty(),
                comboBox.promptTextProperty(),
                () -> promptText);

        linesWrapper.init(() -> createPromptNode());
        Pane arrowButton = null;
        for (Node node : getChildren()) {
            if (node.getId().equals("arrow-button")) {
                arrowButton = (Pane) node;
                break;
            }
        }
        if (arrowButton != null) {
            linesWrapper.clip.widthProperty().bind(linesWrapper.promptContainer.widthProperty().subtract(arrowButton.widthProperty()));
        }

        errorContainer = new ValidationPane<>(comboBox);

        getChildren().addAll(linesWrapper.line, linesWrapper.focusedLine, linesWrapper.promptContainer, errorContainer);

        if (comboBox.isEditable()) {
            comboBox.getEditor().setStyle("-fx-background-color:TRANSPARENT;-fx-padding: 0.333333em 0em;");
            comboBox.getEditor().promptTextProperty().unbind();
            comboBox.getEditor().setPromptText(null);
            comboBox.getEditor().textProperty().addListener((o, oldVal, newVal) -> linesWrapper.usePromptText.invalidate());
        }

        registerChangeListener(comboBox.disableProperty(), obs -> linesWrapper.updateDisabled());
        registerChangeListener(comboBox.focusColorProperty(), obs -> linesWrapper.updateFocusColor());
        registerChangeListener(comboBox.unFocusColorProperty(), obs -> linesWrapper.updateUnfocusColor());
        registerChangeListener(comboBox.disableAnimationProperty(), obs -> errorContainer.updateClip());
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
    protected void layoutChildren(final double x, final double y,
                                  final double w, final double h) {
        super.layoutChildren(x, y, w, h);
        final double height = getSkinnable().getHeight();
        linesWrapper.layoutLines(x, y, w, h, height,
                promptText == null ? 0 : snapPositionX(promptText.getBaselineOffset() + promptText.getLayoutBounds().getHeight() * .36));
        errorContainer.layoutPane(x, height + linesWrapper.focusedLine.getHeight(), w, h);

        linesWrapper.updateLabelFloatLayout();

        if (invalid) {
            invalid = false;
            // update validation container
            errorContainer.invalid(w);
            // focus
            linesWrapper.invalid();
        }
    }

    private void createPromptNode() {
        if (promptText != null || !linesWrapper.usePromptText.get()) {
            return;
        }
        promptText = new Text();
        // create my custom pane for the prompt node
        promptText.textProperty().bind(getSkinnable().promptTextProperty());
        promptText.fillProperty().bind(linesWrapper.animatedPromptTextFill);
        promptText.getStyleClass().addAll("text");
        promptText.getTransforms().add(linesWrapper.promptTextScale);
        promptText.visibleProperty().bind(linesWrapper.usePromptText);
        promptText.setTranslateX(1);
        linesWrapper.promptContainer.getChildren().add(promptText);

        if (getSkinnable().isFocused() && ((JFXComboBox<T>) getSkinnable()).isLabelFloat()) {
            promptText.setTranslateY(-snapPositionY(promptText.getBaselineOffset() + promptText.getLayoutBounds().getHeight() * .36));
            linesWrapper.promptTextScale.setX(0.85);
            linesWrapper.promptTextScale.setY(0.85);
        }
    }

    private static class StyleableProperties {
        private static final CssMetaData<JFXComboBox, Paint> PROMPT_TEXT_FILL =
                new CssMetaData<JFXComboBox, Paint>("-fx-prompt-text-fill",
                        PaintConverter.getInstance(), Color.GRAY) {

                    @Override
                    public boolean isSettable(JFXComboBox n) {
                        final JFXComboBoxListViewSkin<?> skin = (JFXComboBoxListViewSkin<?>) n.getSkin();
                        return skin.promptTextFill == null || !skin.promptTextFill.isBound();
                    }

                    @Override
                    @SuppressWarnings("unchecked")
                    public StyleableProperty<Paint> getStyleableProperty(JFXComboBox n) {
                        final JFXComboBoxListViewSkin<?> skin = (JFXComboBoxListViewSkin<?>) n.getSkin();
                        return (StyleableProperty<Paint>) skin.promptTextFill;
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(ComboBoxListViewSkin.getClassCssMetaData());
            styleables.add(PROMPT_TEXT_FILL);
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }


    /**
     * @return The CssMetaData associated with this class, which may include the
     * CssMetaData of its super classes.
     */
    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }
}
