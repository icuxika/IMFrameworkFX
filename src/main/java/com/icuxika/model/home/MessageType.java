package com.icuxika.model.home;

/**
 * 消息类型
 */
public enum MessageType {

    /**
     * 撤回消息
     */
    REVOKE,

    /**
     * 删除消息
     */
    DELETE,

    /**
     * 文本消息
     */
    TEXT,

    /**
     * 文件消息
     */
    FILE,

    /**
     * 图片消息
     */
    IMAGE,

    /**
     * Emoji消息
     */
    EMOJI,

    /**
     * 音频消息
     */
    AUDIO,

    /**
     * 视频消息
     */
    VIDEO,

    /**
     * 分享消息 - 音乐
     */
    SHARE_MUSIC,

    /**
     * 分享消息 - 链接
     */
    SHARE_URL
}
