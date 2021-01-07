package com.icuxika.i18n;

import java.util.ListResourceBundle;

public class LanguageResource_en extends ListResourceBundle {
    @Override
    protected Object[][] getContents() {
        return new Object[][]{
                {"confirm", "Confirm"},
                {"cancel", "Cancel"},
                {"title", "Instant messaging"},
                {"login", "Sign In"},
                {"qr-login", "QR Login"},
                {"login-username", "Please enter your username"},
                {"login-username-need", "Username can't be empty!"},
                {"login-password", "Please enter your password"},
                {"login-password-need", "Password can't be empty"},
                {"verification-code", "Please enter your verification code"},
                {"verification-code-need", "Verification code can't be empty"},
                {"obtain-verification-code", "Get Code"},
                {"re-obtain-verification-code", "Refresh "},
                {"remember-password", "Remember me"},
                {"auto-login", "Auto login"},
                {"register", "Sign up"},
                {"register-link", "Sign Up"},
                {"forgot-password-link", "Forgot Password?"},
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
