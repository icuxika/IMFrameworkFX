package com.icuxika.control.message;

import com.icuxika.control.SelectableLabel;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class MessageNode extends AnchorPane {

    /**
     * 消息组件展示是否展示在左侧
     */
    protected boolean showLeft;

    /**
     * 消息组件是否显示发送方的名称
     */
    protected boolean showName;

    /**
     * 相同组件 - 头像
     */
    protected ImageView avatarImageView;

    /**
     * 相同组件 - 名称
     */
    protected SelectableLabel nameText;

    /**
     * 右键菜单
     */
    protected final ContextMenu contextMenu = new ContextMenu();

    /**
     * 菜单项 - 任务
     */
    protected final Map<MenuItem, Runnable> actionMenuItemMap = new LinkedHashMap<>();

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    protected MessageNode(boolean showLeft, boolean showName) {
        this.showLeft = showLeft;
        this.showName = showName;
        if (showName) this.showLeft = true;
        initialize0();
        initialize();
    }

    private void initialize0() {
        // ---------- 头像
        avatarImageView = new ImageView();
        avatarImageView.setFitWidth(36);
        avatarImageView.setFitHeight(36);

        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageClip.setArcWidth(8);
        avatarImageClip.setArcHeight(8);
        avatarImageView.setClip(avatarImageClip);
        avatarImageView.setEffect(new DropShadow(2, Color.BLACK));

        AnchorPane.setTopAnchor(avatarImageView, 12.0);
        if (showLeft) {
            AnchorPane.setLeftAnchor(avatarImageView, 12.0);
        } else {
            AnchorPane.setRightAnchor(avatarImageView, 12.0);
        }
        getChildren().add(avatarImageView);

        // ---------- 昵称
        if (showName && showLeft) {
            nameText = new SelectableLabel();
            nameText.setMaxWidth(120);
            nameText.setTextFill(Color.rgb(181, 181, 181));
            AnchorPane.setLeftAnchor(nameText, 60.0);
            AnchorPane.setTopAnchor(nameText, 10.0);
            getChildren().add(nameText);
        }
    }

    /**
     * 各类消息组件实现
     */
    protected abstract void initialize();


    /**
     * 设置头像绑定
     *
     * @param avatar 头像
     */
    public void setAvatar(ObjectProperty<Image> avatar) {
        avatarImageView.imageProperty().bind(avatar);
    }

    /**
     * 设置昵称绑定
     *
     * @param name 名称
     */
    public void setName(StringProperty name) {
        if (!showName) return;
        nameText.textProperty().bind(name);
    }

    /**
     * 添加右键菜单选项
     *
     * @param menuItemName 菜单名称属性绑定
     * @param action       行为
     */
    public void setMenuItem(StringBinding menuItemName, Runnable action) {
        MenuItem menuItem = new MenuItem();
        menuItem.textProperty().bind(menuItemName);
        actionMenuItemMap.put(menuItem, action);
    }
}
