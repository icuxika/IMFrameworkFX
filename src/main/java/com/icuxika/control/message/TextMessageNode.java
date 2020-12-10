package com.icuxika.control.message;

import com.icuxika.control.SelectableLabel;
import javafx.beans.binding.StringBinding;
import javafx.geometry.Insets;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
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

import java.util.HashMap;
import java.util.Map;

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

    /**
     * 右键菜单
     */
    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * 菜单项-任务
     */
    private final Map<MenuItem, Runnable> actionMenItemMap = new HashMap<>();

    public TextMessageNode() {
        // 执行 initialize() 需要先确定左右，因此此方式构建需要手动调用show方法
    }

    public TextMessageNode(boolean showLeft, boolean showName) {
        show(showLeft, showName);
    }

    /**
     * 获取消息组件 - 左
     *
     * @param showName 是否展示昵称
     * @return 消息组件
     */
    public static TextMessageNode left(boolean showName) {
        return new TextMessageNode(true, showName);
    }

    /**
     * 获取消息组建 - 右
     *
     * @return 消息组件
     */
    public static TextMessageNode right() {
        return new TextMessageNode(false, false);
    }

    public void show(boolean showLeft, boolean showName) {
        this.showLeft = showLeft;
        this.showName = showName;
        if (showName) this.showLeft = true;
        initialize();
    }

    private void initialize() {
        // ---------- 头像
        avatarImageView = new ImageView();
        avatarImageView.setFitWidth(36);
        avatarImageView.setFitHeight(36);

        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageView.setClip(avatarImageClip);
        avatarImageView.setEffect(new DropShadow(20, Color.BLACK));

        AnchorPane.setTopAnchor(avatarImageView, 12.0);
        if (showLeft) {
            AnchorPane.setLeftAnchor(avatarImageView, 12.0);
        } else {
            AnchorPane.setRightAnchor(avatarImageView, 12.0);
        }

        // ---------- 昵称
        if (showName && showLeft) {
            nameText = new SelectableLabel();
            nameText.setMaxWidth(120);
            nameText.setTextFill(Color.rgb(181, 181, 181));
            AnchorPane.setLeftAnchor(nameText, 60.0);
            AnchorPane.setTopAnchor(nameText, 10.0);
            getChildren().add(nameText);
        }

        // ---------- 文本消息
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

        // 为TextFlow绑定右键菜单
        textFlow.setOnContextMenuRequested(event -> {
            if (messageText.getMouseReleasedPointSelected()) {
                messageText.setSelectedLastText(true);
            } else {
                messageText.setSelectedFullText(true);
            }
            contextMenu.getItems().clear();
            actionMenItemMap.forEach((menuItem, runnable) -> {
                contextMenu.getItems().add(menuItem);
                menuItem.setOnAction(event1 -> runnable.run());
            });
            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(textFlow, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * 设置头像
     *
     * @param url 头像
     */
    public void setAvatarImageView(String url) {
        Image image = new Image(url, true);
        avatarImageView.setImage(image);
    }

    /**
     * 设置昵称
     *
     * @param text 昵称
     */
    public void setNameText(String text) {
        if (!showLeft) return;
        nameText.setText(text);
    }

    /**
     * 设置昵称绑定
     *
     * @param text 字符串绑定
     */
    public void setNameText(StringBinding text) {
        if (!showLeft) return;
        nameText.textProperty().bind(text);
    }

    /**
     * 设置文本消息
     *
     * @param text 文本消息
     */
    public void setMessageText(String text) {
        messageText.setText(text);
    }

    /**
     * 添加右键菜单项
     *
     * @param menuItemName 子菜单
     * @param action       子菜单对应操作
     */
    public void putMenuItem(String menuItemName, Runnable action) {
        MenuItem menuItem = new MenuItem(menuItemName);
        actionMenItemMap.put(menuItem, action);
    }
}
