package com.icuxika.control.message;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;

/**
 * 提示消息组件
 */
public class PromptMessageNode extends MessageNode {

    private HBox messageContainer;

    private Label promptMessageLabel;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public PromptMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
        initialize();
    }

    @Override
    protected void initialize() {
        getChildren().remove(avatarImageView);

        messageContainer = new HBox();
        messageContainer.setAlignment(Pos.CENTER);

        promptMessageLabel = new Label();
        promptMessageLabel.setAlignment(Pos.CENTER);
        promptMessageLabel.setPadding(new Insets(4));
        promptMessageLabel.setBackground(new Background(new BackgroundFill(Paint.valueOf("#eaeaea"), new CornerRadii(4), Insets.EMPTY)));
        HBox.setMargin(promptMessageLabel, new Insets(4, 0, 4, 0));

        messageContainer.getChildren().add(promptMessageLabel);

        setLeftAnchor(messageContainer, 0.0);
        setRightAnchor(messageContainer, 0.0);
        setTopAnchor(messageContainer, 0.0);
        setBottomAnchor(messageContainer, 0.0);
//        messageContainer.prefWidthProperty().bind(widthProperty());
        getChildren().add(messageContainer);
    }

    public void setPromptMessage(String message) {
        promptMessageLabel.setText(message);
    }
}
