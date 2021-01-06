package com.icuxika.controller.home.conversation;

import com.icuxika.AppView;
import com.icuxika.MainApp;
import com.icuxika.callback.MessageListViewCallback;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.controller.home.conversation.function.EmojiController;
import com.icuxika.mock.ReceivedMessageModel;
import com.icuxika.model.home.MessageModel;
import com.icuxika.model.home.MessageStatus;
import com.icuxika.model.home.MessageType;
import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXTooltip;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

public class ChatController {

    /**
     * 相同组件
     */
    @FXML
    protected Hyperlink nameLink;
    @FXML
    protected ListView<MessageModel> messageListView;
    @FXML
    protected HBox messageToolbox;
    @FXML
    protected TextArea messageInputTextArea;
    @FXML
    protected HBox messageSendBox;

    protected JFXButton sendMsgButton;
    protected JFXButton msgSendTypeButton;
    private final ObjectProperty<MessageSendType> messageSendTypeProperty = new SimpleObjectProperty<>(MessageSendType.ENTER);

    private ObjectProperty<MessageSendType> getMessageSendTypeProperty() {
        return this.messageSendTypeProperty;
    }

    public void setMessageSendType(MessageSendType messageSendType) {
        getMessageSendTypeProperty().set(messageSendType);
    }

    public MessageSendType getMessageSendType() {
        return getMessageSendTypeProperty().get();
    }

    /**
     * 通过操作此集合来更新消息列表
     */
    protected final ObservableList<MessageModel> messageModelObservableList = FXCollections.observableArrayList();

    /**
     * 排序过的消息集合
     * 目前暂时以消息时间为排序条件
     */
    protected final SortedList<MessageModel> messageModelSortedList = new SortedList<>(messageModelObservableList, (o1, o2) -> {
        if (o1.getTime() < o2.getTime()) {
            return -1;
        } else {
            if (o1.getTime().equals(o2.getTime())) {
                return 0;
            } else {
                return 1;
            }
        }
    });

    private static AppView<EmojiController> emojiView = new AppView<>(EmojiController.class);

    /**
     * 初始化
     */
    public void initialize() {
        // 设置消息列表
        messageListView.setItems(messageModelSortedList);
        messageListView.setCellFactory(new MessageListViewCallback());

        FontIcon emojiIcon = new FontIcon(FontAwesomeRegular.SMILE_BEAM);
        JFXTooltip.install(emojiIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-emoji")));
        FontIcon fileIcon = new FontIcon(FontAwesomeRegular.FOLDER);
        JFXTooltip.install(fileIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-file")));
        FontIcon imageIcon = new FontIcon(FontAwesomeRegular.IMAGE);
        JFXTooltip.install(imageIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-image")));
        FontIcon screenShotIcon = new FontIcon(FontAwesomeSolid.CUT);
        JFXTooltip.install(screenShotIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-screen-shot")));
        HBox.setMargin(emojiIcon, new Insets(0, 0, 0, 10));
        messageToolbox.getChildren().addAll(emojiIcon, fileIcon, imageIcon, screenShotIcon);

        // 消息输入框回车发送消息
        messageInputTextArea.setOnKeyPressed(new MessageSendKeyEventHandler(() -> sendTextMessage(0L, messageInputTextArea.getText())));

        // 发送消息按钮
        sendMsgButton = new JFXButton(MainApp.getLanguageBinding("chat-send-msg-btn-text"));
        sendMsgButton.setFont(Font.font(12));
        sendMsgButton.setPrefHeight(24);
        sendMsgButton.setButtonType(JFXButton.ButtonType.RAISED);
        sendMsgButton.setContentDisplay(ContentDisplay.RIGHT);
        sendMsgButton.setBackground(new Background(new BackgroundFill(Paint.valueOf("#1e6fff"), new CornerRadii(4), Insets.EMPTY)));
        sendMsgButton.setTextFill(Color.WHITE);
        // 切换发送消息快捷键方式
        msgSendTypeButton = new JFXButton();
        FontIcon downIcon = new FontIcon(FontAwesomeSolid.ANGLE_DOWN);
        downIcon.setIconColor(Color.WHITE);
        msgSendTypeButton.setGraphic(downIcon);
        msgSendTypeButton.setPrefHeight(18);
        msgSendTypeButton.setButtonType(JFXButton.ButtonType.FLAT);

        ContextMenu contextMenu = buildMessageSendTypeContextMenu();
        msgSendTypeButton.setOnMouseClicked(event -> contextMenu.show(msgSendTypeButton, event.getScreenX(), event.getScreenY()));

        sendMsgButton.setOnAction(event -> sendTextMessage(1L, messageInputTextArea.getText()));
        sendMsgButton.setGraphic(msgSendTypeButton);
        HBox.setMargin(sendMsgButton, new Insets(0, 8, 0, 0));
        messageSendBox.getChildren().add(sendMsgButton);

        // Emoji 面板
        emojiIcon.setOnMouseReleased(event -> {
            emojiView.repeatShow(emojiIcon.getScene().getWindow(), event.getScreenX() - 222, event.getSceneY() - 266);
        });
    }

    /**
     * 设置对方名称绑定
     *
     * @param name 名称
     */
    public void setName(StringProperty name) {
        nameLink.textProperty().bind(name);
    }

    /**
     * 对于消息列表来说，只存在收到消息的情况
     * 删除和撤回操作本身作为一条消息
     * <p>
     * 一、对于主动拉取的消息（历史消息等），应当不处理删除类型和撤回类型的消息，因为数据不定，而是将处于对应状态的消息直接替换为提示消息
     * 二、对于运行过程中收到的消息，不会收到状态为被删除或被撤回的消息，因此根据收到消息的类型来做处理
     * 三、由一、二知，需保证先加载完毕历史消息之后，才能处理新收到的消息
     *
     * @param messageModel 消息
     */
    public void receiveMessage(MessageModel messageModel) {
        MessageType messageType = messageModel.getType();
        switch (messageType) {
            case REVOKE -> {
                MessageModel revokedMessage = messageModelObservableList.stream().filter(model -> model.getId().equals(messageModel.getOperatedId())).findFirst().orElse(null);

                MessageModel promptMessage = new MessageModel();
                promptMessage.setId(System.currentTimeMillis());
                promptMessage.setType(MessageType.TEXT);
                promptMessage.setTime(revokedMessage.getTime());
                promptMessage.setMessage("这条消息被撤回了");
                promptMessage.setSenderId(revokedMessage.getSenderId());
                promptMessage.setConversationProperty(revokedMessage.getConversationProperty());
                messageModelObservableList.add(promptMessage);

                messageModelObservableList.remove(revokedMessage);
            }
            case DELETE -> {
                MessageModel revokedMessage = messageModelObservableList.stream().filter(model -> model.getId().equals(messageModel.getOperatedId())).findFirst().orElse(null);

                MessageModel promptMessage = new MessageModel();
                promptMessage.setId(System.currentTimeMillis());
                promptMessage.setType(MessageType.TEXT);
                promptMessage.setTime(revokedMessage.getTime());
                promptMessage.setMessage("这条消息被删除了");
                promptMessage.setSenderId(revokedMessage.getSenderId());
                promptMessage.setConversationProperty(revokedMessage.getConversationProperty());
                messageModelObservableList.add(promptMessage);

                messageModelObservableList.remove(revokedMessage);
            }
            default -> messageModelObservableList.add(messageModel);
        }
    }

    /**
     * 存储此id以获取必要的会话数据
     */
    private Long conversationId;

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    /**
     * 发送一条文本消息
     */
    public void sendTextMessage(Long userId, String message) {
        if (message.isBlank()) {
            messageInputTextArea.setText("");
            return;
        }
        messageInputTextArea.setText("");

        // 消息发送到服务端
        // ......
        // 服务端向对方转发这条消息
        ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();

        receivedMessageModel.setConversationId(conversationId);
        receivedMessageModel.setSenderId(userId);
        receivedMessageModel.setMessageStatus(MessageStatus.NORMAL);
        receivedMessageModel.setMessageType(MessageType.TEXT);
        receivedMessageModel.setMessageId(System.currentTimeMillis());
        receivedMessageModel.setOperatedMessageId(null);
        receivedMessageModel.setMessage(message);
        receivedMessageModel.setTime(System.currentTimeMillis());

        ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
        // 对方收到此消息
    }

    /**
     * 消息输入框按键事件监听
     */
    protected class MessageSendKeyEventHandler implements EventHandler<KeyEvent> {

        private final Runnable callback;

        public MessageSendKeyEventHandler(Runnable callback) {
            this.callback = callback;
        }

        private final KeyCombination ENTER = new KeyCodeCombination(KeyCode.ENTER);
        private final KeyCombination CTRL_ENTER = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.CONTROL_DOWN);
        private final KeyCombination SHIFT_ENTER = new KeyCodeCombination(KeyCode.ENTER, KeyCombination.SHIFT_DOWN);

        @Override
        public void handle(KeyEvent event) {
            MessageSendType messageSendType = getMessageSendType();
            if (ENTER.match(event)) {
                if (messageSendType == MessageSendType.ENTER) {
                    // 直接发送
                    callback.run();
                }
                if (messageSendType == MessageSendType.CTRL_ENTER) {
                    messageInputTextArea.deleteText(messageInputTextArea.getSelection());
                    messageInputTextArea.insertText(messageInputTextArea.getCaretPosition(), "");
                }
            } else if (CTRL_ENTER.match(event)) {
                if (messageSendType == MessageSendType.ENTER) {
                    messageInputTextArea.deleteText(messageInputTextArea.getSelection());
                    messageInputTextArea.insertText(messageInputTextArea.getCaretPosition(), "\n");
                }
                if (messageSendType == MessageSendType.CTRL_ENTER) {
                    // 直接发送
                    callback.run();
                }
            } else if (SHIFT_ENTER.match(event)) {
                messageInputTextArea.deleteText(messageInputTextArea.getSelection());
                messageInputTextArea.insertText(messageInputTextArea.getCaretPosition(), "\n");
            }
        }
    }

    /**
     * 构建发送消息快捷切换右键菜单
     *
     * @return context menu
     */
    private ContextMenu buildMessageSendTypeContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        CheckMenuItem enterMenuItem = new CheckMenuItem("按Enter键发送消息");
        enterMenuItem.setSelected(true);
        enterMenuItem.setOnAction(event -> getMessageSendTypeProperty().set(MessageSendType.ENTER));
        CheckMenuItem ctrlEnterMenuItem = new CheckMenuItem("按Ctrl+Enter键发送消息");
        ctrlEnterMenuItem.setOnAction(event -> getMessageSendTypeProperty().set(MessageSendType.CTRL_ENTER));
        contextMenu.getItems().add(enterMenuItem);
        contextMenu.getItems().add(ctrlEnterMenuItem);
        getMessageSendTypeProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                enterMenuItem.setSelected(newValue == MessageSendType.ENTER);
                ctrlEnterMenuItem.setSelected(newValue == MessageSendType.CTRL_ENTER);
            }
        });
        return contextMenu;
    }

    /**
     * 消息发送快捷方式
     */
    private enum MessageSendType {

        /**
         * 按Enter键发送消息
         */
        ENTER,

        /**
         * 按Ctrl+Enter键发送消息
         */
        CTRL_ENTER
    }

}
