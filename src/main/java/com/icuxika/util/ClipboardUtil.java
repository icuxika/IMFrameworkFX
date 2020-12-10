package com.icuxika.util;

import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * 系统剪贴板工具
 */
public class ClipboardUtil {

    public static Clipboard CLIPBOARD = Clipboard.getSystemClipboard();

    public static void putString(String content) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(content);
        CLIPBOARD.setContent(clipboardContent);
    }
}
