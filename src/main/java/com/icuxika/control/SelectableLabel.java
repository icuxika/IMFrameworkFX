package com.icuxika.control;

import com.icuxika.converter.SelectedBackgroundFillConverter;
import com.icuxika.skin.SelectableLabelSkin;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.css.*;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 可选择Label
 */
public class SelectableLabel extends Labeled {

    public SelectableLabel() {
        initialize();
    }

    public SelectableLabel(String text) {
        super(text);
        initialize();
    }

    public SelectableLabel(String text, Node graphic) {
        super(text, graphic);
        initialize();
    }

    public SelectableLabel(StringBinding text) {
        textProperty().bind(text);
        initialize();
    }

    public SelectableLabel(StringBinding text, Node graphics) {
        textProperty().bind(text);
        setGraphic(graphics);
        initialize();
    }

    public void initialize() {
//        setSelectedTextFill(Paint.valueOf("#FFFFFF"));
//        setSelectedBackgroundFill(Paint.valueOf("#308DFC"));
        setStyle("""
                            -ifx-selected-text-fill: #FFFFFF;
                            -ifx-selected-background-fill: #333333;
                """);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SelectableLabelSkin(this);
    }

    /**
     * 选中的文本
     */
    private final StringProperty selectedText = new SimpleStringProperty();

    public StringProperty selectedTextProperty() {
        return selectedText;
    }

    public String getSelectedText() {
        return selectedTextProperty().get();
    }

    public void setSelectedText(String text) {
        selectedTextProperty().set(text);
    }

    /**
     * 选中的文本的颜色
     */
    private final StyleableObjectProperty<Paint> selectedTextFill = new SimpleStyleableObjectProperty<>(StyleableProperties.SELECTED_TEXT_FILL, SelectableLabel.this, "selectedTextFill");

    public StyleableObjectProperty<Paint> selectedTextFillProperty() {
        return selectedTextFill;
    }

    public Paint getSelectedTextFill() {
        return selectedTextFillProperty().get();
    }

    public void setSelectedTextFill(Paint paint) {
        selectedTextFillProperty().set(paint);
    }

    /**
     * 选中的文本背景颜色
     */
    private final StyleableObjectProperty<Paint> selectedBackgroundFill = new SimpleStyleableObjectProperty<>(StyleableProperties.SELECTED_BACKGROUND_FILL, SelectableLabel.this, "selectedBackgroundFill");

    public StyleableObjectProperty<Paint> selectedBackgroundFillProperty() {
        return selectedBackgroundFill;
    }

    public Paint getSelectedBackgroundFill() {
        return selectedBackgroundFill.get();
    }

    public void setSelectedBackgroundFill(Paint paint) {
        selectedBackgroundFill.set(paint);
    }

    private static class StyleableProperties {
        private static final CssMetaData<SelectableLabel, Paint> SELECTED_TEXT_FILL = new CssMetaData<>("-ifx-selected-text-fill", SelectedBackgroundFillConverter.getInstance(), Paint.valueOf("#FFFFFF")) {
            @Override
            public boolean isSettable(SelectableLabel styleable) {
                return styleable.selectedTextFill != null && !styleable.selectedTextFill.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(SelectableLabel styleable) {
                return styleable.selectedTextFillProperty();
            }
        };

        private static final CssMetaData<SelectableLabel, Paint> SELECTED_BACKGROUND_FILL = new CssMetaData<>("-ifx-selected-background-fill", SelectedBackgroundFillConverter.getInstance(), Paint.valueOf("#308DFC")) {
            @Override
            public boolean isSettable(SelectableLabel styleable) {
                return styleable.selectedBackgroundFill != null && !styleable.selectedBackgroundFill.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(SelectableLabel styleable) {
                return styleable.selectedBackgroundFillProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(Labeled.getClassCssMetaData());
            Collections.addAll(styleables, SELECTED_TEXT_FILL, SELECTED_BACKGROUND_FILL);
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
