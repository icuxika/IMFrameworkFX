package com.icuxika.model;

/**
 * 托盘图标提示消息
 */
public class SystemTrayMessageModel {

    /**
     * 消息类型
     */
    private Type messageType;


    private enum Type {

        /**
         * 会话消息
         */
        MESSAGE,

        /**
         * 好友请求
         */
        FRIEND_REQUEST
    }
}
