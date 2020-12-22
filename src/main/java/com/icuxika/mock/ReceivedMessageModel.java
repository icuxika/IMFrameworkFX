package com.icuxika.mock;

import com.icuxika.model.home.MessageStatus;
import com.icuxika.model.home.MessageType;

/**
 * 模拟服务端传过来的消息
 */
public class ReceivedMessageModel {

    /**
     * 会话id
     */
    private Long conversationId;

    /**
     * 消息id
     */
    private Long messageId;

    /**
     * 当消息类型为撤回或删除时被操作的消息id
     */
    private Long operatedMessageId;

    /**
     * 消息发送方
     */
    private Long senderId;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 消息时间
     */
    private Long time;

    /**
     * 消息状态
     */
    private MessageStatus messageStatus;

    /**
     * 消息类型 【服务端传过来的数据，此处一般是数字类型】
     */
    private MessageType messageType;

    public ReceivedMessageModel() {
    }

    public Long getConversationId() {
        return conversationId;
    }

    public void setConversationId(Long conversationId) {
        this.conversationId = conversationId;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public Long getOperatedMessageId() {
        return operatedMessageId;
    }

    public void setOperatedMessageId(Long operatedMessageId) {
        this.operatedMessageId = operatedMessageId;
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

    public MessageStatus getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(MessageStatus messageStatus) {
        this.messageStatus = messageStatus;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }
}
