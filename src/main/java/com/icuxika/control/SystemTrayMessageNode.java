package com.icuxika.control;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class SystemTrayMessageNode extends HBox {

    private FontIcon messageTypeIcon;

    private Label titleLabel;

    private HBox spacer = new HBox();

    private Label countLabel;

    public SystemTrayMessageNode() {
        setPrefWidth(200);
        setPrefHeight(24);

        messageTypeIcon = new FontIcon(FontAwesomeSolid.COMMENT_DOTS);
        messageTypeIcon.setIconSize(16);

        titleLabel = new Label("名称");

        countLabel = new Label("1");

        HBox.setMargin(messageTypeIcon, new Insets(0, 0, 0, 4));
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(countLabel, new Insets(0, 4, 0, 0));
        setSpacing(4.0);
        setAlignment(Pos.CENTER);
        getChildren().addAll(messageTypeIcon, titleLabel, spacer, countLabel);
    }
}
