package com.icuxika.i18n;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public interface LanguageConstants {
    default Object[][] getContents() {
        Method[] methods = getClass().getDeclaredMethods();
        Object[][] contents = new Object[methods.length][2];
        for (int i = 0; i < methods.length; i++) {
            Method declaredMethod = methods[i];
            contents[i][0] = declaredMethod.getName();
            try {
                contents[i][1] = declaredMethod.invoke(this);
            } catch (IllegalAccessException | InvocationTargetException e) {
                contents[i][1] = "";
            }
        }
        return contents;
    }

    String confirm = "confirm";
    String cancel = "cancel";
    String title = "title";
    String login = "login";
    String qr_login = "qr_login";
    String login_username = "login_username";
    String login_username_need = "login_username_need";
    String login_password = "login_password";
    String login_password_need = "login_password_need";
    String verification_code = "verification_code";
    String verification_code_need = "verification_code_need";
    String obtain_verification_code = "obtain_verification_code";
    String re_obtain_verification_code = "re_obtain_verification_code";
    String remember_password = "remember_password";
    String auto_login = "auto_login";
    String register = "register";
    String register_link = "register_link";
    String forgot_password_link = "forgot_password_link";
    String chat_send_msg_btn_text = "chat_send_msg_btn_text";
    String chat_msg_context_menu_copy = "chat_msg_context_menu_copy";
    String chat_msg_context_menu_delete = "chat_msg_context_menu_delete";
    String chat_msg_context_menu_revoke = "chat_msg_context_menu_revoke";
    String chat_msg_tool_icon_emoji = "chat_msg_tool_icon_emoji";
    String chat_msg_tool_icon_file = "chat_msg_tool_icon_file";
    String chat_msg_tool_icon_image = "chat_msg_tool_icon_image";
    String chat_msg_tool_icon_audio = "chat_msg_tool_icon_audio";
    String chat_msg_tool_icon_share_music = "chat_msg_tool_icon_share_music";
    String chat_msg_tool_icon_video = "chat_msg_tool_icon_video";
    String chat_msg_tool_icon_screen_shot = "chat_msg_tool_icon_screen_shot";
    String conversation_context_menu_top = "conversation_context_menu_top";
    String conversation_context_menu_cancel_top = "conversation_context_menu_cancel_top";

    String confirm();

    String cancel();

    String title();

    String login();

    String qr_login();

    String login_username();

    String login_username_need();

    String login_password();

    String login_password_need();

    String verification_code();

    String verification_code_need();

    String obtain_verification_code();

    String re_obtain_verification_code();

    String remember_password();

    String auto_login();

    String register();

    String register_link();

    String forgot_password_link();

    String chat_send_msg_btn_text();

    String chat_msg_context_menu_copy();

    String chat_msg_context_menu_delete();

    String chat_msg_context_menu_revoke();

    String chat_msg_tool_icon_emoji();

    String chat_msg_tool_icon_file();

    String chat_msg_tool_icon_image();

    String chat_msg_tool_icon_audio();

    String chat_msg_tool_icon_share_music();

    String chat_msg_tool_icon_video();

    String chat_msg_tool_icon_screen_shot();

    String conversation_context_menu_top();

    String conversation_context_menu_cancel_top();
}
