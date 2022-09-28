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
        return new DefaultLanguageResource().getContents();
    }

    static class DefaultLanguageResource implements LanguageConstants {
        @Override
        public String confirm() {
            return "确认";
        }

        @Override
        public String cancel() {
            return "取消";
        }

        @Override
        public String title() {
            return "即时通讯";
        }

        @Override
        public String login() {
            return "登录";
        }

        @Override
        public String qr_login() {
            return "二维码登录";
        }

        @Override
        public String login_username() {
            return "请输入用户名";
        }

        @Override
        public String login_username_need() {
            return "用户名不能为空！";
        }

        @Override
        public String login_password() {
            return "请输入密码";
        }

        @Override
        public String login_password_need() {
            return "密码不能为空！";
        }

        @Override
        public String verification_code() {
            return "请输入验证码";
        }

        @Override
        public String verification_code_need() {
            return "验证码不能为空！";
        }

        @Override
        public String obtain_verification_code() {
            return "获取验证码";
        }

        @Override
        public String re_obtain_verification_code() {
            return "重新获取";
        }

        @Override
        public String remember_password() {
            return "记住密码";
        }

        @Override
        public String auto_login() {
            return "自动登录";
        }

        @Override
        public String register() {
            return "注册";
        }

        @Override
        public String register_link() {
            return "注册账号";
        }

        @Override
        public String forgot_password_link() {
            return "忘记密码";
        }

        @Override
        public String chat_send_msg_btn_text() {
            return "发送";
        }

        @Override
        public String chat_msg_context_menu_copy() {
            return "复制";
        }

        @Override
        public String chat_msg_context_menu_delete() {
            return "删除";
        }

        @Override
        public String chat_msg_context_menu_revoke() {
            return "撤回";
        }

        @Override
        public String chat_msg_tool_icon_emoji() {
            return "选择表情";
        }

        @Override
        public String chat_msg_tool_icon_file() {
            return "发送文件";
        }

        @Override
        public String chat_msg_tool_icon_image() {
            return "发送图片";
        }

        @Override
        public String chat_msg_tool_icon_audio() {
            return "语音消息";
        }

        @Override
        public String chat_msg_tool_icon_share_music() {
            return "分享音乐";
        }

        @Override
        public String chat_msg_tool_icon_video() {
            return "视频文件";
        }

        @Override
        public String chat_msg_tool_icon_screen_shot() {
            return "屏幕截图";
        }

        @Override
        public String conversation_context_menu_top() {
            return "置顶";
        }

        @Override
        public String conversation_context_menu_cancel_top() {
            return "取消置顶";
        }
    }
}
