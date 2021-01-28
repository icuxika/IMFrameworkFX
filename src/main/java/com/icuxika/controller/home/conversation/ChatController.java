package com.icuxika.controller.home.conversation;

import com.icuxika.AppView;
import com.icuxika.MainApp;
import com.icuxika.callback.MessageListViewCallback;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.controller.home.conversation.function.EmojiController;
import com.icuxika.controller.home.conversation.function.ScreenShotController;
import com.icuxika.event.FileChooseEventHandler;
import com.icuxika.framework.UserData;
import com.icuxika.mock.ReceivedMessageModel;
import com.icuxika.model.home.FileChooseType;
import com.icuxika.model.home.MessageModel;
import com.icuxika.model.home.MessageStatus;
import com.icuxika.model.home.MessageType;
import com.icuxika.util.SystemUtil;
import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXProgressBar;
import com.jfoenix.control.JFXSpinner;
import com.jfoenix.control.JFXTooltip;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Comparator;

public class ChatController {

    /**
     * 相同组件
     */
    @FXML
    protected BorderPane chatPane;
    @FXML
    protected Hyperlink nameLink;
    @FXML
    protected ListView<MessageModel> messageListView;
    @FXML
    protected HBox messageToolbox;
    @FXML
    protected StackPane messageInputContainer;
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
    protected final SortedList<MessageModel> messageModelSortedList = new SortedList<>(messageModelObservableList, Comparator.comparing(MessageModel::getTime));

    /**
     * Emoji表情管理面板
     */
    private static final AppView<EmojiController> emojiView = new AppView<>(EmojiController.class);

    /**
     * 屏幕截图
     */
    private static final ScreenShotController screenShotController = new ScreenShotController();

    /**
     * 初始化
     */
    public void initialize() {
        // 设置消息列表
        messageListView.setItems(messageModelSortedList);
        messageListView.setCellFactory(new MessageListViewCallback());

        FontIcon emojiIcon = new FontIcon(FontAwesomeRegular.SMILE_BEAM);
        JFXTooltip.install(emojiIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-emoji")));
        emojiIcon.setOnMouseReleased(event -> {
            emojiView.getController().setConsumer(file -> {
                sendEmojiMessage(file);
                emojiView.close();
            });
            emojiView.repeatShow(emojiIcon.getScene().getWindow(), event.getScreenX() - 222, event.getScreenY() - 266);
        });
        FontIcon fileIcon = new FontIcon(FontAwesomeRegular.FOLDER);
        JFXTooltip.install(fileIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-file")));
        fileIcon.setOnMouseReleased(new FileChooseEventHandler(fileIcon, FileChooseType.ALL, this::sendFileMessage));
        FontIcon imageIcon = new FontIcon(FontAwesomeRegular.IMAGE);
        JFXTooltip.install(imageIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-image")));
        imageIcon.setOnMouseReleased(new FileChooseEventHandler(imageIcon, FileChooseType.IMAGE, file -> sendImageMessage(0L, file)));
        FontIcon screenShotIcon = new FontIcon(FontAwesomeSolid.CUT);
        JFXTooltip.install(screenShotIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-screen-shot")));
        screenShotIcon.setOnMouseReleased(event -> screenShotController.startScreenShot());
        FontIcon microphoneIcon = new FontIcon(FontAwesomeSolid.MICROPHONE);
        JFXTooltip.install(microphoneIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-audio")));
        microphoneIcon.setOnMouseReleased(event -> generateAudioMessage());
        FontIcon musicIcon = new FontIcon(FontAwesomeSolid.MUSIC);
        JFXTooltip.install(musicIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-share-music")));
        musicIcon.setOnMouseReleased(new FileChooseEventHandler(musicIcon, FileChooseType.ALL, this::sendMusicMessage));
        FontIcon videoIcon = new FontIcon(FontAwesomeSolid.FILM);
        JFXTooltip.install(videoIcon, new JFXTooltip(MainApp.getLanguageBinding("chat-msg-tool-icon-video")));
        videoIcon.setOnMouseReleased(new FileChooseEventHandler(videoIcon, FileChooseType.ALL, this::sendVideoMessage));
        HBox.setMargin(emojiIcon, new Insets(0, 0, 0, 10));
        messageToolbox.getChildren().addAll(emojiIcon, fileIcon, imageIcon, screenShotIcon, microphoneIcon, musicIcon, videoIcon);

        // 消息输入框回车发送消息
        messageInputTextArea.setOnKeyPressed(new MessageSendKeyEventHandler(() -> sendTextMessage(UserData.userId, messageInputTextArea.getText())));

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
        // 初始化消息Node
        messageModel.initGraphic();
        MessageType messageType = messageModel.getType();

        // 消息是主动拉取的历史消息还是登录之后产生的消息
        // 当前不考虑历史拉取消息的情况
        boolean fromHistory = false;
        if (fromHistory) {
            if (messageModel.getStatus().equals(MessageStatus.REVOKED) || messageModel.getStatus().equals(MessageStatus.DELETED)) {
                // 状态为被撤回的和被删除的消息直接替换成提示消息（亦或者直接丢弃）
                MessageModel deletedOrRevokedMessage = buildDeletedOrRevokedMessage(messageModel);
                deletedOrRevokedMessage.initGraphic();
                messageModelObservableList.add(deletedOrRevokedMessage);
            } else {
                messageModelObservableList.add(messageModel);
            }
        } else {
            if (messageType.equals(MessageType.REVOKE) || messageType.equals(MessageType.DELETE)) {
                // 找到消息列表中被撤回或者删除的那条消息
                MessageModel operatedMessageModel = messageModelObservableList.stream().filter(model -> model.getId().equals(messageModel.getOperatedId())).findFirst().orElse(null);
                if (operatedMessageModel != null) {
                    // 以这条消息构建被撤回消息的数据，保留了消息时间这个排序条件属性
                    if (messageType.equals(MessageType.REVOKE)) operatedMessageModel.setStatus(MessageStatus.REVOKED);
                    if (messageType.equals(MessageType.DELETE)) operatedMessageModel.setStatus(MessageStatus.DELETED);
                    MessageModel deletedOrRevokedMessage = buildDeletedOrRevokedMessage(operatedMessageModel);
                    // 初始化消息Node
                    deletedOrRevokedMessage.initGraphic();
                    // 添加提示消息
                    messageModelObservableList.add(deletedOrRevokedMessage);
                    // 移除被操作的消息
                    messageModelObservableList.remove(operatedMessageModel);
                }
            } else {
                messageModelObservableList.add(messageModel);
                // 滚动到最新消息
                messageListView.scrollTo(messageModel);
            }
        }
    }

    /**
     * 根据消息状态构建提示消息
     */
    private MessageModel buildDeletedOrRevokedMessage(MessageModel messageModel) {
        MessageModel promptMessage = new MessageModel();
        promptMessage.setId(System.currentTimeMillis());
        promptMessage.setType(MessageType.PROMPT);
        promptMessage.setTime(messageModel.getTime());
        if (messageModel.getStatus().equals(MessageStatus.DELETED)) {
            promptMessage.setMessage("这条消息被删除了");
        } else {
            promptMessage.setMessage("这条消息被撤回了");
        }
        promptMessage.setSenderId(messageModel.getSenderId());
        promptMessage.setConversationProperty(messageModel.getConversationProperty());
        return promptMessage;
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
        sendMessage(userId, message, null, MessageType.TEXT);
        // 对方收到此消息
    }

    /**
     * 发送一条图片消息
     */
    public void sendImageMessage(Long userId, File file) {
        sendMessage(userId, null, file, MessageType.IMAGE);
    }

    /**
     * 发送一条Emoji消息
     */
    public void sendFileMessage(File file) {
        sendMessage(UserData.userId, null, file, MessageType.FILE);
    }


    /**
     * 发送一条Emoji消息
     */
    public void sendEmojiMessage(File file) {
        sendMessage(UserData.userId, null, file, MessageType.EMOJI);
    }

    /**
     * 发送一条音频消息
     */
    public void sendAudioMessage(File file) {
        sendMessage(UserData.userId, null, file, MessageType.AUDIO);
    }

    /**
     * 发送一条视频消息
     */
    public void sendVideoMessage(File file) {
        sendMessage(UserData.userId, null, file, MessageType.VIDEO);
    }

    /**
     * 发送一条音乐分享消息
     */
    public void sendMusicMessage(File file) {
        sendMessage(UserData.userId, null, file, MessageType.SHARE_MUSIC);
    }

    /**
     * 发送一条提示消息
     *
     * @param message 消息
     */
    public void sendPromptMessage(String message) {
        sendMessage(UserData.userId, message, null, MessageType.PROMPT);
    }

    /**
     * 发送消息
     *
     * @param senderId    发送方，此处用来测试消息在左右侧的显示情况
     * @param text        文本消息
     * @param file        文件
     * @param messageType 消息类型
     */
    public void sendMessage(long senderId, String text, File file, MessageType messageType) {
        ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();

        receivedMessageModel.setConversationId(conversationId);
        receivedMessageModel.setSenderId(senderId);
        receivedMessageModel.setMessageStatus(MessageStatus.NORMAL);
        receivedMessageModel.setMessageType(messageType);
        receivedMessageModel.setMessageId(System.currentTimeMillis());
        receivedMessageModel.setOperatedMessageId(null);
        switch (messageType) {
            case TEXT, PROMPT -> receivedMessageModel.setMessage(text);
            default -> receivedMessageModel.setMessage(file.getAbsolutePath());
        }
        receivedMessageModel.setTime(System.currentTimeMillis());

        ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
    }

    private TargetDataLine line;
    private ScheduledService<Integer> recordingCountdownService;
    /**
     * 语音消息缓存文件
     */
    File waveFile = new File(SystemUtil.getOtherCacheDirectory() + "AudioMsg.wav");
    /**
     * 上一个录音面板
     */
    private VBox currentContainer;

    /**
     * 语音消息正在录制中
     */
    private final BooleanProperty recording = new SimpleBooleanProperty(false);

    private BooleanProperty recordingProperty() {
        return this.recording;
    }

    private boolean getRecording() {
        return this.recordingProperty().get();
    }

    private void setRecording(boolean value) {
        this.recordingProperty().set(value);
    }

    /**
     * 语音消息生成
     */
    private void generateAudioMessage() {
        AudioFileFormat.Type type = AudioFileFormat.Type.WAVE;
        AudioFormat format = new AudioFormat(48000, 16, 2, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
            sendPromptMessage("未发现录音设备或驱动程序未安装");
            return;
        }

        VBox container = new VBox();
        container.setAlignment(Pos.CENTER);
        container.setSpacing(4);

        FontIcon fontIcon = new FontIcon(FontAwesomeSolid.MICROPHONE);
        fontIcon.setIconSize(48);
        JFXSpinner spinner = new JFXSpinner(-1);
        spinner.visibleProperty().bind(recordingProperty());
        StackPane iconContainer = new StackPane(fontIcon, spinner);
        iconContainer.setPrefSize(60, 60);

        HBox operateBox = new HBox();
        operateBox.setAlignment(Pos.CENTER);
        operateBox.setSpacing(4);

        JFXButton startButton = new JFXButton("开始");
        startButton.setButtonType(JFXButton.ButtonType.RAISED);
        startButton.setTextFill(Color.WHITE);
        startButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        startButton.disableProperty().bind(recordingProperty());

        JFXButton stopButton = new JFXButton("停止");
        stopButton.setButtonType(JFXButton.ButtonType.RAISED);
        stopButton.setTextFill(Color.WHITE);
        stopButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        stopButton.disableProperty().bind(recordingProperty());
        stopButton.disableProperty().bind(recordingProperty().not());

        JFXButton sendButton = new JFXButton("发送");
        sendButton.setButtonType(JFXButton.ButtonType.RAISED);
        sendButton.setTextFill(Color.WHITE);
        sendButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));

        JFXButton cancelButton = new JFXButton("取消");
        cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        cancelButton.setTextFill(Color.WHITE);
        cancelButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        cancelButton.disableProperty().bind(recordingProperty());

        operateBox.getChildren().addAll(startButton, stopButton, sendButton, cancelButton);

        JFXProgressBar progressBar = new JFXProgressBar(1);

        Label label = new Label("最多录制10秒的语音消息");
        label.setTextFill(Color.rgb(181, 181, 181));

        container.getChildren().addAll(iconContainer, operateBox, progressBar, label);
        if (currentContainer != null) {
            messageInputContainer.getChildren().remove(currentContainer);
        }
        currentContainer = container;
        messageInputContainer.getChildren().add(container);
        fontIcon.getScene().addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                messageInputContainer.getChildren().remove(container);
            }
        });

        startButton.setOnAction(event -> {
            setRecording(true);
            // 清除旧的缓存文件
            if (waveFile.exists()) {
                if (waveFile.delete()) {
                    System.out.println("上一份语音消息缓存文件已清除");
                } else {
                    System.out.println("上一份语音消息缓存文件清除失败");
                }
            }

            try {
                line = (TargetDataLine) AudioSystem.getLine(info);
                line.open(format);
                line.start();
                // 开启一个线程将录音写入到文件中
                new Thread(() -> {
                    AudioInputStream audioInputStream = new AudioInputStream(line);
                    try {
                        AudioSystem.write(audioInputStream, type, waveFile);
                    } catch (IOException e) {
                        System.out.println("语音消息缓存文件写入时发生异常，可能是停止了录制");
                    }
                }).start();
                // 开启一个倒计时任务，限制最大录制时间
                recordingCountdownService = new ScheduledService<Integer>() {
                    private int count = 10;

                    @Override
                    protected Task<Integer> createTask() {
                        return new Task<>() {
                            @Override
                            protected Integer call() throws Exception {
                                return count--;
                            }
                        };
                    }
                };
                recordingCountdownService.lastValueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        progressBar.setProgress(newValue / 10.0);
                        if (newValue <= 0) {
                            recordingCountdownService.cancel();
                        }
                    }
                });
                recordingCountdownService.setOnCancelled(event1 -> {
                    progressBar.setProgress(1);
                    if (line != null && line.isOpen()) {
                        line.stop();
                        line.close();
                    }
                    setRecording(false);
                });
                recordingCountdownService.setPeriod(Duration.seconds(1));
                recordingCountdownService.start();
            } catch (Exception e) {
                if (waveFile.exists()) {
                    if (waveFile.delete()) {
                        System.out.println("语音录制失败，语音消息缓存文件已清除");
                    }
                }
            }
        });

        sendButton.setOnAction(event -> {
            if (waveFile.exists()) {
                sendAudioMessage(waveFile);
            } else {
                System.out.println("语音消息缓存文件不存在");
            }
        });

        stopButton.setOnAction(event -> {
            if (recordingCountdownService != null && recordingCountdownService.isRunning()) {
                recordingCountdownService.cancel();
            } else {
                System.out.println("当前没有录音任务正在执行");
            }
        });

        cancelButton.setOnAction(event -> {
            if (recordingCountdownService != null && recordingCountdownService.isRunning()) {
                recordingCountdownService.cancel();
            }
            if (line != null && line.isOpen()) {
                line.stop();
                line.close();
            }
            messageInputContainer.getChildren().remove(container);
        });
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
