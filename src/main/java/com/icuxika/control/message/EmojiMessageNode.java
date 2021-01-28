package com.icuxika.control.message;

import javafx.geometry.Insets;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;

import java.net.URL;

/**
 * Emoji 消息组件
 */
public class EmojiMessageNode extends MessageNode {

    private ImageView messageImageView;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public EmojiMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(4));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.26));
        dropShadow.setHeight(1);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setSpread(0.12);
        textFlow.setEffect(dropShadow);
        textFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFF"), new CornerRadii(4), new Insets(0))));

        messageImageView = new ImageView();
        messageImageView.setFitWidth(32);
        messageImageView.setFitHeight(32);
        textFlow.getChildren().add(messageImageView);

        if (showLeft) {
            AnchorPane.setLeftAnchor(textFlow, 60.0);
            if (showName) {
                AnchorPane.setTopAnchor(textFlow, 30.0);
            } else {
                AnchorPane.setTopAnchor(textFlow, 12.0);
            }
        } else {
            AnchorPane.setRightAnchor(textFlow, 60.0);
            AnchorPane.setTopAnchor(textFlow, 12.0);
        }

        getChildren().addAll(textFlow);
    }

    public void initEmoji(URL url) {
        Image image = new Image(url.toString(), true);
        messageImageView.setImage(image);
    }
}
