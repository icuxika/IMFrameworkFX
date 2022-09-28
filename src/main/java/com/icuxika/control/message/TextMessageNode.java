package com.icuxika.control.message;

import com.icuxika.MainApp;
import com.icuxika.control.SelectableLabel;
import com.icuxika.i18n.LanguageConstants;
import com.icuxika.util.ClipboardUtil;
import javafx.geometry.Insets;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

/**
 * 文本消息组件
 */
public class TextMessageNode extends MessageNode {

    /**
     * 消息文本
     */
    private SelectableLabel messageText;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public TextMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
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

        getChildren().addAll(textFlow);

        // 为TextFlow绑定右键菜单
        buildContextMenu();
        textFlow.setOnContextMenuRequested(event -> {
            if (messageText.getMouseReleasedPointSelected()) {
                messageText.setSelectedLastText(true);
            } else {
                messageText.setSelectedFullText(true);
            }
            contextMenu.getItems().clear();
            actionMenuItemMap.forEach((menuItem, runnable) -> {
                contextMenu.getItems().add(menuItem);
                menuItem.setOnAction(event1 -> runnable.run());
            });
            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(textFlow, event.getScreenX(), event.getScreenY());
            }
        });
    }

    /**
     * 设置文本消息
     *
     * @param text 文本消息
     */
    public void setMessageText(String text) {
        messageText.setText(text);
    }

    private void buildContextMenu() {
        MenuItem copyMenuItem = new MenuItem();
        copyMenuItem.textProperty().bind(MainApp.getLanguageBinding(LanguageConstants.chat_msg_context_menu_copy));
        actionMenuItemMap.put(copyMenuItem, () -> {
            String content = messageText.getSelectedText();
            ClipboardUtil.putString(content);
        });
    }
}
