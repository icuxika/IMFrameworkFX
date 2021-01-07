package com.jfoenix.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;

public class JFXChip<T> extends Region {

    protected final JFXChipView<T> view;
    // --- item
    private ObjectProperty<T> item = new SimpleObjectProperty<T>(this, "item");

    public JFXChip(JFXChipView<T> view, T item) {
        this.view = view;
        getStyleClass().add("jfx-chip");
        setItem(item);
    }

    public final ObjectProperty<T> itemProperty() {
        return item;
    }

    public final void setItem(T value) {
        item.set(value);
    }

    public final T getItem() {
        return item.get();
    }

    public final JFXChipView getChipView() {
        return view;
    }
}
