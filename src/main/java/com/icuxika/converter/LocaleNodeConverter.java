package com.icuxika.converter;

import com.icuxika.MainApp;
import com.jfoenix.converter.base.NodeConverter;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;

import java.util.Locale;

/**
 * 此类要配合 {@link com.jfoenix.control.JFXComboBox} 的 #updateDisplayText方法，自定义相关数据处理逻辑
 */
public class LocaleNodeConverter extends NodeConverter<Locale> {
    @Override
    public Node toNode(Locale object) {
        if (object == null) return null;
        StackPane selectedValueContainer = new StackPane();
        selectedValueContainer.getStyleClass().add("combo-box-selected-value-container");
        selectedValueContainer.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        Label selectedValueLabel = new Label();
        selectedValueLabel.setContentDisplay(ContentDisplay.RIGHT);
        selectedValueLabel.setGraphicTextGap(8);
        selectedValueLabel.setText(object.getDisplayLanguage(object));
        ImageView imageView = new ImageView();
        if (object.equals(Locale.SIMPLIFIED_CHINESE)) {
            imageView.setImage(new Image(MainApp.load("img/guoqi.png").toExternalForm()));
        }
        imageView.setFitHeight(20);
        imageView.setPreserveRatio(true);
        selectedValueLabel.setGraphic(imageView);
        selectedValueLabel.setTextFill(Color.BLACK);
        selectedValueContainer.getChildren().add(selectedValueLabel);
        StackPane.setAlignment(selectedValueLabel, Pos.CENTER_LEFT);
        StackPane.setMargin(selectedValueLabel, new Insets(0, 0, 0, 4));
        return selectedValueContainer;
    }

    @Override
    public Locale fromNode(Node node) {
        return null;
    }

    @Override
    public String toString(Locale object) {
        return null;
    }
}
