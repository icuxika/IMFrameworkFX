package com.icuxika.i18n;

import java.util.ListResourceBundle;

/**
 * 多国语言-默认
 * 文件规则：
 * 一、继承 ListResourceBundle
 * 二、后缀带区域，如 LanguageResource_en，与 Locale.ENGLISH 对应
 * 三、默认文件不带后缀，即当前文件，但当同时配置与JVM获取的区域的一致的语言文件时，该语言文件会生效，所以应用程序启动前应手动初始化语言设置。
 */
public class LanguageResource extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"title", "标题"},
                {"chat-send-msg-btn-text", "发送"},
                {"chat-msg-context-menu-copy", "复制"},
                {"chat-msg-context-menu-delete", "删除"},
                {"chat-msg-context-menu-revoke", "撤回"},
                {"chat-msg-tool-icon-emoji", "选择表情"},
                {"chat-msg-tool-icon-file", "发送文件"},
                {"chat-msg-tool-icon-image", "发送图片"},
                {"chat-msg-tool-icon-screen-shot", "屏幕截图"}
        };
    }
}
