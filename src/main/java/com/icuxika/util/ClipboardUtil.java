package com.icuxika.util;

import javafx.scene.image.WritableImage;
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

    public static void putImage(WritableImage image) {
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putImage(image);
        CLIPBOARD.setContent(clipboardContent);
    }
}
