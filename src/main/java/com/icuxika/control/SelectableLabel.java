package com.icuxika.control;

import com.icuxika.skin.SelectableLabelSkin;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Labeled;
import javafx.scene.control.Skin;
import javafx.scene.paint.Paint;

/**
 * 可选择Label
 */
public class SelectableLabel extends Labeled {

    public SelectableLabel() {
    }

    public SelectableLabel(String text) {
        super(text);
    }

    public SelectableLabel(String text, Node graphic) {
        super(text, graphic);
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new SelectableLabelSkin(this);
    }

    /**
     * 选中的文本
     */
    private final StringProperty selectedText = new SimpleStringProperty();

    public StringProperty getSelectedTextProperty() {
        return selectedText;
    }

    public String getSelectedText() {
        return getSelectedTextProperty().get();
    }

    public void setSelectedText(String text) {
        getSelectedTextProperty().set(text);
    }

    /**
     * 选中的文本的颜色
     */
    private final ObjectProperty<Paint> selectedTextFill = new SimpleObjectProperty<>(Paint.valueOf("#308dfc"));

    public ObjectProperty<Paint> getSelectedTextFillProperty() {
        return selectedTextFill;
    }

    public Paint getSelectedTextFill() {
        return getSelectedTextFillProperty().get();
    }

    public void setSelectedTextFill(Paint paint) {
        getSelectedTextFillProperty().set(paint);
    }
}
