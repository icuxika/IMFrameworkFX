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
                {"confirm", "确认"},
                {"cancel", "取消"},
                {"title", "即时通讯"},
                {"login", "登录"},
                {"qr-login", "二维码登录"},
                {"login-username", "请输入用户名"},
                {"login-username-need", "用户名不能为空！"},
                {"login-password", "请输入密码"},
                {"login-password-need", "密码不能为空！"},
                {"verification-code", "请输入验证码"},
                {"verification-code-need", "验证码不能为空！"},
                {"obtain-verification-code", "获取验证码"},
                {"re-obtain-verification-code", "重新获取"},
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
