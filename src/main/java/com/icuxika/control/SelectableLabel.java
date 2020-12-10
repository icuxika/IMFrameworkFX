package com.icuxika.control;

import com.icuxika.converter.SelectedBackgroundFillConverter;
import com.icuxika.skin.SelectableLabelSkin;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

    /**
     * 是否选中全文
     */
    public final BooleanProperty selectedFullText = new SimpleBooleanProperty(false);

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

    /**
     * 鼠标释放时点是否位于文本选择区
     */
    private final BooleanProperty mouseReleasedPointSelected = new SimpleBooleanProperty();
    /**
     * 是否再次选中
     */
    public BooleanProperty selectedLastText = new SimpleBooleanProperty(false);

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SelectableLabelSkin(this);
    }

    public SelectableLabel(String text) {
        this(text, null);
    }

    public SelectableLabel(StringBinding text, ObjectBinding<Node> graphics) {
        textProperty().bind(text);
        graphicProperty().bind(graphics);
        initialize();
    }

    public void initialize() {
//        setSelectedTextFill(Paint.valueOf("#FFFFFF"));
//        setSelectedBackgroundFill(Paint.valueOf("#308DFC"));
        setStyle("""
                            -ifx-selected-text-fill: #FFFFFF;
                            -ifx-selected-background-fill: #308DFC;
                """);
    }

    /**
     * 点是否位于文本选择区 ${@link SelectableLabel#getMouseReleasedPointSelected()} 方法不用传参，也可获取结果
     *
     * @param x x
     * @param y y
     * @return 是 否
     */
    public boolean pointSelected(Double x, Double y) {
        return ((SelectableLabelSkin) getSkin()).pointSelected(x, y);
    }

    public BooleanProperty mouseReleasedPointSelectedProperty() {
        return mouseReleasedPointSelected;
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

    public boolean getMouseReleasedPointSelected() {
        return mouseReleasedPointSelectedProperty().get();
    }

    public void setMouseReleasedPointSelected(boolean value) {
        mouseReleasedPointSelectedProperty().set(value);
    }

    public BooleanProperty selectedLastTextProperty() {
        return selectedLastText;
    }

    public boolean getSelectedLastText() {
        return selectedLastTextProperty().get();
    }

    public void setSelectedLastText(boolean value) {
        selectedLastTextProperty().set(value);
    }

    public BooleanProperty selectedFullTextProperty() {
        return selectedFullText;
    }

    public boolean getSelectedFullText() {
        return selectedFullTextProperty().get();
    }

    public void setSelectedFullText(boolean value) {
        selectedFullTextProperty().set(value);
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
