package com.icuxika.i18n;

import java.util.ListResourceBundle;

public class LanguageResource_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"title", "title"},
                {"chat-send-msg-btn-text", "Send"},
                {"chat-msg-context-menu-copy", "Copy"},
                {"chat-msg-context-menu-delete", "Delete"},
                {"chat-msg-context-menu-revoke", "Revoke"},
                {"chat-msg-tool-icon-emoji", "Emoji"},
                {"chat-msg-tool-icon-file", "File"},
                {"chat-msg-tool-icon-image", "Image"},
                {"chat-msg-tool-icon-screen-shot", "Screenshot"},
                {"conversation-context-menu-top", "Set-top"},
                {"conversation-context-menu-cancel-top", "Cancel set-top"}
        };
    }
}
