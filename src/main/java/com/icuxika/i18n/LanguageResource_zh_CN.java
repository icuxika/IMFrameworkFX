package com.icuxika.i18n;

import java.util.ListResourceBundle;

/**
 * 中文环境下，若设置了 Locale.SIMPLIFIED_CHINESE，则判断此文件是否存在，
 * 不存在的话，默认文件 LanguageResource 的配置生效，
 * 存在的话，则当前文件生效
 */
public class LanguageResource_zh_CN extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"title", "即时通讯"},
                {"login", "登录"},
                {"login-username", "请输入用户名"},
                {"login-username-need", "用户名不能为空！"},
                {"login-password", "请输入密码"},
                {"login-password-need", "密码不能为空！"},
                {"register", "注册"},
                {"register-link", "注册账号"},
                {"forgot-password-link", "忘记密码"},
                {"chat-send-msg-btn-text", "发送"},
                {"chat-msg-context-menu-copy", "复制"},
                {"chat-msg-context-menu-delete", "删除"},
                {"chat-msg-context-menu-revoke", "撤回"},
                {"chat-msg-tool-icon-emoji", "选择表情"},
                {"chat-msg-tool-icon-file", "发送文件"},
                {"chat-msg-tool-icon-image", "发送图片"},
                {"chat-msg-tool-icon-screen-shot", "屏幕截图"},
                {"conversation-context-menu-top", "置顶"},
                {"conversation-context-menu-cancel-top", "取消置顶"}
        };
    }
}
