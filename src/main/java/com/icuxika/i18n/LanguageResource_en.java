package com.icuxika.i18n;

import java.util.ListResourceBundle;

public class LanguageResource_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new LanguageConstants() {
            @Override
            public String confirm() {
                return "Confirm";
            }

            @Override
            public String cancel() {
                return "Cancel";
            }

            @Override
            public String title() {
                return "Instant messaging";
            }

            @Override
            public String login() {
                return "Sign In";
            }

            @Override
            public String qr_login() {
                return "QR Login";
            }

            @Override
            public String login_username() {
                return "Please enter your username";
            }

            @Override
            public String login_username_need() {
                return "Username can't be empty!";
            }

            @Override
            public String login_password() {
                return "Please enter your password";
            }

            @Override
            public String login_password_need() {
                return "Password can't be empty";
            }

            @Override
            public String verification_code() {
                return "Please enter your verification code";
            }

            @Override
            public String verification_code_need() {
                return "Verification code can't be empty";
            }

            @Override
            public String obtain_verification_code() {
                return "Get code";
            }

            @Override
            public String re_obtain_verification_code() {
                return "Refresh";
            }

            @Override
            public String remember_password() {
                return "Remember me";
            }

            @Override
            public String auto_login() {
                return "Auto login";
            }

            @Override
            public String register() {
                return "Sign up";
            }

            @Override
            public String register_link() {
                return "Sign Up";
            }

            @Override
            public String forgot_password_link() {
                return "Forgot Password?";
            }

            @Override
            public String chat_send_msg_btn_text() {
                return "Send";
            }

            @Override
            public String chat_msg_context_menu_copy() {
                return "Copy";
            }

            @Override
            public String chat_msg_context_menu_delete() {
                return "Delete";
            }

            @Override
            public String chat_msg_context_menu_revoke() {
                return "Revoke";
            }

            @Override
            public String chat_msg_tool_icon_emoji() {
                return "Emoji";
            }

            @Override
            public String chat_msg_tool_icon_file() {
                return "File";
            }

            @Override
            public String chat_msg_tool_icon_image() {
                return "Image";
            }

            @Override
            public String chat_msg_tool_icon_audio() {
                return "Audio";
            }

            @Override
            public String chat_msg_tool_icon_share_music() {
                return "Music";
            }

            @Override
            public String chat_msg_tool_icon_video() {
                return "Video";
            }

            @Override
            public String chat_msg_tool_icon_screen_shot() {
                return "Screenshot";
            }

            @Override
            public String conversation_context_menu_top() {
                return "Set-top";
            }

            @Override
            public String conversation_context_menu_cancel_top() {
                return "Cancel set-top";
            }
        }.getContents();
    }
}
