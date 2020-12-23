package com.icuxika.control;

import com.icuxika.MainApp;
import com.icuxika.util.DateUtil;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 会话组件
 */
public class ConversationNode extends AnchorPane {

    /**
     * 头像
     */
    private ImageView avatarImageView;

    /**
     * 名称
     */
    private Label nameLabel;

    /**
     * 最近一次会话时间
     */
    private Label timeLabel;

    /**
     * 最近一次消息
     */
    private Label messageLabel;

    /**
     * 消息未读数
     */
    private Label unreadCountLabel;

    /**
     * 会话置顶或取消会话置顶菜单
     */
    private final MenuItem topMenuItem = new MenuItem();

    /**
     * 右键菜单
     */
    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * 菜单项-行为
     */
    private final Map<MenuItem, Runnable> actionMenuItemMap = new LinkedHashMap<>();

    public ConversationNode() {
        initialize();
    }

    public void initialize() {
        setPrefHeight(80);

        avatarImageView = new ImageView();
        avatarImageView.setFitWidth(48);
        avatarImageView.setFitHeight(48);

        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageClip.setArcWidth(8);
        avatarImageClip.setArcHeight(8);
        avatarImageView.setClip(avatarImageClip);
        avatarImageView.setEffect(new DropShadow(2, Color.BLACK));

        AnchorPane.setTopAnchor(avatarImageView, 16.0);
        AnchorPane.setLeftAnchor(avatarImageView, 16.0);

        nameLabel = new Label();
        nameLabel.setFont(Font.font(14));
        nameLabel.setPrefWidth(150);
        AnchorPane.setTopAnchor(nameLabel, 20.0);
        AnchorPane.setLeftAnchor(nameLabel, 80.0);
        nameLabel.maxWidthProperty().bind(widthProperty().subtract(150));
        nameLabel.visibleProperty().bind(new When(widthProperty().lessThan(160)).then(false).otherwise(true));
        nameLabel.managedProperty().bind(new When(widthProperty().lessThan(160)).then(false).otherwise(true));

        messageLabel = new Label();
        messageLabel.setPrefWidth(150);
        messageLabel.setTextFill(Color.rgb(181, 181, 181));
        AnchorPane.setBottomAnchor(messageLabel, 20.0);
        AnchorPane.setLeftAnchor(messageLabel, 80.0);
        messageLabel.maxWidthProperty().bind(widthProperty().subtract(150));
        messageLabel.visibleProperty().bind(new When(widthProperty().lessThan(160)).then(false).otherwise(true));
        messageLabel.managedProperty().bind(new When(widthProperty().lessThan(160)).then(false).otherwise(true));

        timeLabel = new Label();
        timeLabel.setTextFill(Color.rgb(181, 181, 181));

        unreadCountLabel = new Label();
        unreadCountLabel.setTextFill(Paint.valueOf("#FFFFFF"));
        unreadCountLabel.setContentDisplay(ContentDisplay.CENTER);
        Circle unreadCountLabelClip = new Circle();
        unreadCountLabelClip.setRadius(8);
        unreadCountLabelClip.setFill(Paint.valueOf("#D0CFD1"));
        unreadCountLabelClip.setStroke(Paint.valueOf("#D0CFD1"));
        unreadCountLabel.setGraphic(unreadCountLabelClip);

        VBox rightShadeBox = new VBox();
        rightShadeBox.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFFE6"), null, null)));
        rightShadeBox.setAlignment(Pos.CENTER_RIGHT);
        rightShadeBox.setSpacing(6);
        rightShadeBox.setPrefWidth(60);
        rightShadeBox.setPrefHeight(80);
        AnchorPane.setTopAnchor(rightShadeBox, 0.0);
        AnchorPane.setRightAnchor(rightShadeBox, 0.0);

        VBox.setMargin(timeLabel, new Insets(0, 4, 0, 0));
        VBox.setMargin(unreadCountLabel, new Insets(0, 4, 0, 0));
        rightShadeBox.getChildren().addAll(timeLabel, unreadCountLabel);

        getChildren().addAll(avatarImageView, nameLabel, messageLabel, rightShadeBox);

        // 此处生效需要在ListView的CellFactory中为当前node设置ListCell的背景
        backgroundProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.getFills().isEmpty()) {
                final BackgroundFill[] fills = new BackgroundFill[newValue.getFills().size()];
                for (int i = 0; i < newValue.getFills().size(); i++) {
                    BackgroundFill bf = newValue.getFills().get(i);
                    String fillValue = String.valueOf(bf.getFill());
                    // 设置背景与ListCell同色，但是透明度为90%
                    fills[i] = new BackgroundFill(Paint.valueOf("#" + fillValue.substring(2, fillValue.length() - 2) + "E6"), null, null);
                }
                rightShadeBox.setBackground(new Background(fills));
            }
        });

        // 构建右键菜单
        buildContextMenu();
        setOnContextMenuRequested(event -> {
            contextMenu.getItems().clear();
            actionMenuItemMap.forEach((menuItem, runnable) -> {
                contextMenu.getItems().add(menuItem);
                menuItem.setOnAction(event1 -> runnable.run());
            });
            if (!contextMenu.getItems().isEmpty()) {
                contextMenu.show(this, event.getScreenX(), event.getScreenY());
            }
        });
    }

    public void setAvatar(ObjectProperty<Image> avatar) {
        avatarImageView.imageProperty().bind(avatar);
    }

    public void setName(StringProperty name) {
        nameLabel.textProperty().bind(name);
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

    public void setMessage(StringProperty message) {
        messageLabel.textProperty().bind(message);
    }

    public void setUnreadCountLabel(IntegerProperty unreadCount) {
        unreadCountLabel.textProperty().bind(new StringBinding() {
            {
                bind(unreadCount);
            }

            @Override
            protected String computeValue() {
                if (unreadCount.get() == 0) {
                    unreadCountLabel.setVisible(false);
                } else {
                    unreadCountLabel.setVisible(true);
                    int value = unreadCount.get();
                    if (value > 99) {
                        value = 99;
                    }
                    return String.valueOf(value);
                }
                return "";
            }
        });
    }

    /**
     * 会话置顶属性
     */
    private BooleanProperty top;

    public void setTop(BooleanProperty top) {
        this.top = top;

        if (top.get()) {
            topMenuItem.textProperty().bind(MainApp.getLanguageBinding("conversation-context-menu-cancel-top"));
        } else {
            topMenuItem.textProperty().bind(MainApp.getLanguageBinding("conversation-context-menu-top"));
        }
        top.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                topMenuItem.textProperty().bind(MainApp.getLanguageBinding("conversation-context-menu-cancel-top"));
            } else {
                topMenuItem.textProperty().bind(MainApp.getLanguageBinding("conversation-context-menu-top"));
            }
        });
    }

    public void buildContextMenu() {
        actionMenuItemMap.put(topMenuItem, () -> top.set(!top.get()));
    }
}
