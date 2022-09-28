package com.icuxika.model.home;

import com.icuxika.MainApp;
import com.icuxika.control.message.*;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.framework.UserData;
import com.icuxika.i18n.LanguageConstants;
import com.icuxika.mock.ReceivedMessageModel;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.image.Image;

import java.io.File;
import java.net.MalformedURLException;

/**
 * 对应每一条消息的数据模型
 */
public class MessageModel {

    /**
     * 消息唯一id
     */
    private Long id;

    /**
     * 被操作的消息id
     * 撤回和删除操作本身作为一条消息，此属性记录被撤回或被删除的消息id
     */
    private Long operatedId;

    /**
     * 会话id
     */
    private Long conversationId;

    /**
     * 消息发送方用户id
     */
    private Long senderId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息发送时间
     */
    private Long time;

    /**
     * 消息类型
     */
    private MessageType type;

    /**
     * 消息状态
     */
    private MessageStatus status;

    /**
     * 消息属于哪个类型的会话
     */
    private ConversationProperty conversationProperty;

    /**
     * 消息发送方头像 【此属性不应该定义在此处，暂时为了方便】
     */
    private ObjectProperty<Image> avatarImage = new SimpleObjectProperty<>();

    /**
     * 消息发送方名称 【此属性不应该定义在此处，暂时为了方便】
     */
    private StringProperty name = new SimpleStringProperty();

    public MessageModel() {
    }

    public MessageModel(Long id, Long senderId, String message, Long time, MessageType type, MessageStatus status, ConversationProperty conversationProperty, ObjectProperty<Image> avatarImage) {
        this.id = id;
        this.senderId = senderId;
        this.message = message;
        this.time = time;
        this.type = type;
        this.status = status;
        this.conversationProperty = conversationProperty;
        this.avatarImage = avatarImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOperatedId() {
        return operatedId;
    }

    public void setOperatedId(Long operatedId) {
        this.operatedId = operatedId;
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public ConversationProperty getConversationProperty() {
        return conversationProperty;
    }

    public void setConversationProperty(ConversationProperty conversationProperty) {
        this.conversationProperty = conversationProperty;
    }

    public ObjectProperty<Image> getAvatarImageProperty() {
        return avatarImage;
    }

    public void setAvatarImageProperty(ObjectProperty<Image> avatarImage) {
        this.avatarImage = avatarImage;
    }

    public StringProperty getNameProperty() {
        return name;
    }

    public void setName(StringProperty name) {
        this.name = name;
    }

    private Node graphic;

    public Node getGraphic() {
        return graphic;
    }

    public void setGraphic(Node graphic) {
        this.graphic = graphic;
    }

    /**
     * {@link com.icuxika.callback.MessageListViewCallback}
     */
    public void initGraphic() {
        MessageNode messageNode = null;
        MessageType messageType = getType();
        ConversationProperty conversationProperty = getConversationProperty();
        boolean showLeft = !getSenderId().equals(UserData.userId);
        boolean showName = conversationProperty.equals(ConversationProperty.GROUP) && showLeft;
        switch (messageType) {
            case TEXT -> {
                TextMessageNode textMessageNode = new TextMessageNode(showLeft, showName);
                textMessageNode.setMessageText(getMessage());
                messageNode = textMessageNode;
            }
            case FILE -> {
                FileMessageNode fileMessageNode = new FileMessageNode(showLeft, showName);
                fileMessageNode.initFile(getMessage());
                messageNode = fileMessageNode;
            }
            case IMAGE -> {
                ImageMessageNode imageMessageNode = new ImageMessageNode(showLeft, showName);
                try {
                    imageMessageNode.initImage(new File(getMessage()).toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                messageNode = imageMessageNode;
            }
            case EMOJI -> {
                EmojiMessageNode emojiMessageNode = new EmojiMessageNode(showLeft, showName);
                try {
                    emojiMessageNode.initEmoji(new File(getMessage()).toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                messageNode = emojiMessageNode;
            }
            case PROMPT -> {
                PromptMessageNode promptMessageNode = new PromptMessageNode(showLeft, showName);
                promptMessageNode.setPromptMessage(getMessage());
                messageNode = promptMessageNode;
            }
            case AUDIO -> {
                AudioMessageNode audioMessageNode = new AudioMessageNode(showLeft, showName);
                try {
                    audioMessageNode.initAudio(new File(getMessage()).toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                messageNode = audioMessageNode;
            }
            case VIDEO -> {
                VideoMessageNode videoMessageNode = new VideoMessageNode(showLeft, showName);
                try {
                    videoMessageNode.initVideo(new File(getMessage()).toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                messageNode = videoMessageNode;
            }
            case SHARE_MUSIC -> {
                MusicMessageNode musicMessageNode = new MusicMessageNode(showLeft, showName);
                try {
                    musicMessageNode.initMusic(new File(getMessage()).toURI().toURL());
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                messageNode = musicMessageNode;
            }
            default -> {
            }
        }

        if (messageNode != null) {
            messageNode.setAvatar(getAvatarImageProperty());
            messageNode.setName(getNameProperty());
            setGraphic(messageNode);
            // 撤回消息
            messageNode.setMenuItem(MainApp.getLanguageBinding(LanguageConstants.chat_msg_context_menu_revoke), () -> {
                ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();
                receivedMessageModel.setConversationId(getConversationId());
                receivedMessageModel.setMessageType(MessageType.REVOKE);
                receivedMessageModel.setMessageId(System.currentTimeMillis());
                receivedMessageModel.setOperatedMessageId(getId());
                receivedMessageModel.setTime(getTime());
                receivedMessageModel.setSenderId(getSenderId());
                ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
            });

            // 删除消息
            messageNode.setMenuItem(MainApp.getLanguageBinding(LanguageConstants.chat_msg_context_menu_delete), () -> {
                ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();
                receivedMessageModel.setConversationId(getConversationId());
                receivedMessageModel.setMessageType(MessageType.DELETE);
                receivedMessageModel.setMessageId(System.currentTimeMillis());
                receivedMessageModel.setOperatedMessageId(getId());
                receivedMessageModel.setTime(getTime());
                receivedMessageModel.setSenderId(getSenderId());
                ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
            });
        }
    }
}
