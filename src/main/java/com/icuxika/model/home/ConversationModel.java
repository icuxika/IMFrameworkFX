package com.icuxika.model.home;

import javafx.beans.property.*;
import javafx.scene.image.Image;

/**
 * 对应每一个会话的数据模型
 */
public class ConversationModel {

    /**
     * 标识该会话id
     */
    private Long id;

    /**
     * 对方 id  由会话属性决定此id性质 如 用户id、群id 等
     */
    private Long targetId;

    /**
     * 会话属性
     */
    private ConversationProperty conversationProperty;

    /**
     * 头像
     */
    private final ObjectProperty<Image> avatar = new SimpleObjectProperty<>();

    /**
     * 会话对象名称
     */
    private final StringProperty name = new SimpleStringProperty();

    /**
     * 最近一次会话时间
     */
    private final LongProperty time = new SimpleLongProperty();

    /**
     * 最近一次消息
     */
    private final StringProperty message = new SimpleStringProperty();

    /**
     * 消息未读数
     */
    private final IntegerProperty unreadCount = new SimpleIntegerProperty();


    public ConversationModel() {
    }

    public ConversationModel(Long id, Long targetId, ConversationProperty conversationProperty) {
        this.id = id;
        this.targetId = targetId;
        this.conversationProperty = conversationProperty;
    }

    public ConversationModel(Long id, Long targetId, ConversationProperty conversationProperty, Image avatar, String name, long time, String message, int unreadCount) {
        this.id = id;
        this.targetId = targetId;
        this.conversationProperty = conversationProperty;
        setAvatar(avatar);
        setName(name);
        setTime(time);
        setMessage(message);
        setUnreadCount(unreadCount);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTargetId() {
        return targetId;
    }

    public void setTargetId(Long targetId) {
        this.targetId = targetId;
    }

    public ConversationProperty getConversationProperty() {
        return conversationProperty;
    }

    public void setConversationProperty(ConversationProperty conversationProperty) {
        this.conversationProperty = conversationProperty;
    }

    public ObjectProperty<Image> getAvatarProperty() {
        return this.avatar;
    }

    public void setAvatar(Image avatar) {
        getAvatarProperty().set(avatar);
    }

    public Image getAvatar() {
        return getAvatarProperty().get();
    }

    public StringProperty getNameProperty() {
        return this.name;
    }

    public void setName(String name) {
        getNameProperty().set(name);
    }

    public String getName() {
        return getNameProperty().get();
    }

    public LongProperty getTimeProperty() {
        return this.time;
    }

    public void setTime(long time) {
        getTimeProperty().set(time);
    }

    public long getTime() {
        return getTimeProperty().get();
    }

    public StringProperty getMessageProperty() {
        return this.message;
    }

    public void setMessage(String message) {
        getMessageProperty().set(message);
    }

    public String getMessage() {
        return getMessageProperty().get();
    }

    public IntegerProperty getUnreadCountProperty() {
        return this.unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        getUnreadCountProperty().set(unreadCount);
    }

    public Integer getUnreadCount() {
        return getUnreadCountProperty().get();
    }
}
