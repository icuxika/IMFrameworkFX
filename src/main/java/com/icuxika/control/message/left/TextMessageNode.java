package com.icuxika.control.message.left;

import com.icuxika.control.SelectableLabel;
import javafx.beans.binding.StringBinding;
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
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

/**
 * 文本消息组件
 */
public class TextMessageNode extends AnchorPane {

    /**
     * 头像
     */
    private ImageView avatarImageView;

    /**
     * 昵称
     */
    private SelectableLabel nameText;

    /**
     * 消息文本
     */
    private SelectableLabel messageText;

    /**
     * 是否为左消息组件
     */
    private boolean showLeft = true;

    /**
     * 是否展示姓名
     */
    private boolean showName = false;

    public TextMessageNode() {
        initialize();
    }

    public TextMessageNode(boolean showLeft, boolean showName) {
        this.showLeft = showLeft;
        this.showName = showName;
        if (showName) this.showLeft = true;
        initialize();
    }

    /**
     * 获取消息组件 - 左
     *
     * @param showName 是否展示昵称
     * @return 消息组件
     */
    public static TextMessageNode left(boolean showName) {
        TextMessageNode textMessageNode = new TextMessageNode(true, showName);
        textMessageNode.initialize();
        return textMessageNode;
    }

    /**
     * 获取消息组建 - 右
     *
     * @return 消息组件
     */
    public static TextMessageNode right() {
        TextMessageNode textMessageNode = new TextMessageNode(false, false);
        textMessageNode.initialize();
        ;
        return textMessageNode;
    }

    private void initialize() {
        avatarImageView = new ImageView();
        avatarImageView.setFitWidth(36);
        avatarImageView.setFitHeight(36);

        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageView.setClip(avatarImageClip);
//        avatarImageView.setEffect(new DropShadow(20, Color.BLACK));

        if (showName && showLeft) {
            nameText = new SelectableLabel();
            nameText.setMaxWidth(120);
            nameText.setTextFill(Color.rgb(181, 181, 181));
            AnchorPane.setLeftAnchor(nameText, 60.0);
            AnchorPane.setTopAnchor(nameText, 10.0);
            getChildren().add(nameText);
        }

        TextFlow textFlow = new TextFlow();
        textFlow.setPadding(new Insets(4, 8, 4, 12));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setBlurType(BlurType.GAUSSIAN);
        dropShadow.setColor(Color.rgb(0, 0, 0, 0.26));
        dropShadow.setHeight(1);
        dropShadow.setOffsetX(2.0);
        dropShadow.setOffsetY(2.0);
        dropShadow.setSpread(0.12);
        textFlow.setEffect(dropShadow);
        textFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFF"), new CornerRadii(4), new Insets(0))));

        messageText = new SelectableLabel();
        messageText.setFont(Font.font(14));
        messageText.setMaxWidth(240);
        messageText.setWrapText(true);
        textFlow.getChildren().add(messageText);

        AnchorPane.setTopAnchor(avatarImageView, 12.0);
        if (showLeft) {
            AnchorPane.setLeftAnchor(avatarImageView, 12.0);
        } else {
            AnchorPane.setRightAnchor(avatarImageView, 12.0);
        }

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

        setPadding(new Insets(0, 0, 8, 0));
        getChildren().addAll(avatarImageView, textFlow);
    }

    public void setAvatarImageView(String url) {
        Image image = new Image(url, true);
        avatarImageView.setImage(image);
    }

    public void setNameText(String text) {
        if (!showLeft) return;
        nameText.setText(text);
    }

    public void setNameText(StringBinding text) {
        if (!showLeft) return;
        nameText.textProperty().bind(text);
    }

    public void setMessageText(String text) {
        messageText.setText(text);
    }
}
