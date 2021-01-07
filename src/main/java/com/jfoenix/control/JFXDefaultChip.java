package com.jfoenix.control;

import com.jfoenix.svg.SVGGlyph;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class JFXDefaultChip<T> extends JFXChip<T> {

    protected final HBox root;

    public JFXDefaultChip(JFXChipView<T> view, T item) {
        super(view, item);
        JFXButton closeButton = new JFXButton("", new SVGGlyph());
        closeButton.getStyleClass().add("close-button");
        closeButton.setOnAction((event) -> view.getChips().remove(item));

        String tagString = null;
        if (getItem() instanceof String) {
            tagString = (String) getItem();
        } else {
            tagString = view.getConverter().toString(getItem());
        }
        Label label = new Label(tagString);
        label.setWrapText(true);
        root = new HBox(label, closeButton);
        getChildren().setAll(root);
        label.setMaxWidth(100);
    }
}
