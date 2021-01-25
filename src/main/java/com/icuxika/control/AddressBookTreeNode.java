package com.icuxika.control;

import com.icuxika.util.DateUtil;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

/**
 * 通讯录树节点（好友、群组等）
 */
public class AddressBookTreeNode extends HBox {

    private StackPane avatarContainer;

    private ImageView avatarImageView;

    private AnchorPane devicePane;

    private Label nameLabel;

    private Label promptLabel;

    private Label timeLabel;

    public AddressBookTreeNode() {
        initialize();
    }

    private void initialize() {
        setPrefHeight(48);
        setAlignment(Pos.CENTER);

        avatarImageView = new ImageView();
        avatarImageView.setFitWidth(32);
        avatarImageView.setFitHeight(32);
        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageClip.setArcWidth(4);
        avatarImageClip.setArcHeight(4);
        avatarImageView.setClip(avatarImageClip);

        devicePane = new AnchorPane();
        avatarContainer = new StackPane();

        avatarContainer.getChildren().addAll(avatarImageView, devicePane);
        HBox.setMargin(avatarContainer, new Insets(0, 0, 0, 8));

        nameLabel = new Label();
        nameLabel.setFont(Font.font(14));
        HBox.setMargin(nameLabel, new Insets(0, 0, 0, 8));
        promptLabel = new Label();
        promptLabel.setTextFill(Color.rgb(181, 181, 181));
        HBox.setMargin(promptLabel, new Insets(0, 0, 0, 2));

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        timeLabel = new Label();
        timeLabel.setTextFill(Color.rgb(181, 181, 181));
        HBox.setMargin(timeLabel, new Insets(0, 8, 0, 0));

        getChildren().addAll(avatarContainer, nameLabel, promptLabel, spacer, timeLabel);
    }

    public void setAvatar(ObjectProperty<Image> avatar) {
        avatarImageView.imageProperty().bind(avatar);
    }

    public void setName(StringProperty name) {
        nameLabel.textProperty().bind(name);
    }

    public void setPrompt(StringProperty prompt) {
        promptLabel.textProperty().bind(prompt);
    }

    public void setTime(LongProperty time) {
        timeLabel.textProperty().bind(new StringBinding() {
            {
                bind(time);
            }

            @Override
            protected String computeValue() {
                return DateUtil.mills2ReduceMessageTime(time.get());
            }
        });
    }
}
