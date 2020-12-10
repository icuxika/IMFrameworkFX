package com.icuxika.skin;

import com.icuxika.control.SelectableLabel;
import com.icuxika.util.ClipboardUtil;
import com.icuxika.util.MathUtil;
import com.sun.javafx.scene.control.LabeledText;
import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Labeled;
import javafx.scene.control.skin.LabeledSkinBase;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Path;
import javafx.scene.text.HitInfo;
import javafx.scene.text.Text;

import java.text.BreakIterator;

public class SelectableLabelSkin extends LabeledSkinBase<SelectableLabel> {

    private Text text;
    private Path selectedBackground;
    private HitInfo selectedStart;
    private HitInfo selectedEnd;

    private final SelectableLabel node;

    /**
     * 记录上一次文本选择开始位置
     */
    private int lastStart;

    /**
     * 记录上一次文本选择结束位置
     */
    private int lastEnd;

    /**
     * 记录上一次文本选择背景布局信息
     */
    private Bounds lastSelectedBackgroundBounds;

    public SelectableLabelSkin(SelectableLabel labeled) {
        super(labeled);
        node = labeled;
        if (!isIgnoreText()) {
            ObservableList<Node> children = getChildren();
            for (Node child : children) {
                if (child instanceof LabeledText) {
                    text = (Text) child;
                    break;
                }
            }
            text.selectionFillProperty().bind(labeled.selectedTextFillProperty());
            // 选择文本背景
            selectedBackground = new Path();
            selectedBackground.setStroke(null);
            selectedBackground.fillProperty().bind(labeled.selectedBackgroundFillProperty());
            children.add(0, selectedBackground);

            text.setOnMousePressed(event -> {
                selectedStart = getIndex(event.getX(), event.getY());
                clearSelected();
                labeled.requestFocus();
            });

            text.setOnMouseDragged(event -> {
                selectedEnd = getIndex(event.getX(), event.getY());
                int start;
                int end;
                if (selectedEnd.getCharIndex() < selectedStart.getCharIndex()) {
                    // 从后往前
                    start = selectedEnd.isLeading() ? selectedEnd.getCharIndex() : selectedEnd.getCharIndex() + 1;
                    end = selectedStart.isLeading() ? selectedStart.getCharIndex() : selectedStart.getCharIndex() + 1;
                } else {
                    // 从前往后
                    start = selectedStart.isLeading() ? selectedStart.getCharIndex() : selectedStart.getCharIndex() + 1;
                    end = selectedEnd.isLeading() ? selectedEnd.getCharIndex() : selectedEnd.getCharIndex() + 1;
                }
                selectText(start, end);
            });

            text.setOnMouseClicked(event -> {
                int clickCount = event.getClickCount();
                if (clickCount == 2) {
                    // 双击选中单词
                    previousWord(getIndex(event.getX(), event.getY()).getCharIndex());
                } else if (clickCount == 3) {
                    // 三击选中全部
                    selectText(0, text.getText().length());
                }
            });

            // 鼠标释放时判断是否处于文本选择区域
            text.setOnMouseReleased(event -> labeled.setMouseReleasedPointSelected(pointSelected(event.getScreenX(), event.getScreenY())));

            labeled.setOnKeyPressed(event -> {
                if (event.isControlDown() && (event.getCode() == KeyCode.C)) {
                    if (labeled.getSelectedText() != null && !labeled.getSelectedText().isEmpty()) {
                        ClipboardUtil.putString(labeled.getSelectedText());
                    }
                }
            });

            // 鼠标焦点变化时，清除文本选中
            labeled.focusedProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue) {
                    clearSelected();
                    lastSelectedBackgroundBounds = null;
                }
            });

            // 选中全文属性监听
            labeled.selectedFullTextProperty().addListener((observable, oldValue, newValue) -> {
                System.out.println(newValue);
                if (newValue) {
                    clearSelected();
                    selectText(0, text.getText().length());
                    labeled.setSelectedFullText(false);
                }
            });

            // 再次选中属性监听
            labeled.selectedLastTextProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue) {
                    clearSelected();
                    selectText(lastStart, lastEnd);
                    labeled.setSelectedLastText(false);
                }
            });
        }
    }

    private BreakIterator wordIterator;

    /**
     * 选中一个单词块
     *
     * @param index 鼠标点击到的文字索引
     */
    private void previousWord(int index) {
        final int textLength = text.getText().length();
        final String text = this.text.getText();
        if (textLength <= 0) {
            return;
        }
        if (wordIterator == null) {
            // 返回一个定位单词间的边界的BreakIterator
            wordIterator = BreakIterator.getWordInstance();
        }
        wordIterator.setText(text);

        // 返回当前边界之前的边界
        int pos1 = wordIterator.preceding(MathUtil.clamp(0, index, textLength));
        // 返回指定字符偏移量后的第一个边界
        int pos2 = wordIterator.following(MathUtil.clamp(0, index, textLength));
        selectText(pos1, pos2);
    }

    /**
     * 判断屏幕上一点是否位于选中区域
     *
     * @param x x
     * @param y y
     * @return 是 否
     */
    public boolean pointSelected(Double x, Double y) {
        Bounds nodeBounds = node.localToScreen(node.getLayoutBounds());
        if (lastSelectedBackgroundBounds == null) return false;
        return (x >= nodeBounds.getMinX() + lastSelectedBackgroundBounds.getMinX()) &&
                (x <= nodeBounds.getMinX() + lastSelectedBackgroundBounds.getMaxX()) &&
                (y >= nodeBounds.getMinY() + lastSelectedBackgroundBounds.getMinY()) &&
                (y <= nodeBounds.getMinY() + lastSelectedBackgroundBounds.getMaxY());
    }

    /**
     * 选中文本
     *
     * @param begin 索引开始位置
     * @param end   索引结束位置
     */
    private void selectText(int begin, int end) {
        lastStart = begin;
        lastEnd = end;
        // TODO 双击选中的区域无法正确获取此值
        lastSelectedBackgroundBounds = selectedBackground.getBoundsInParent();

        text.setSelectionStart(begin);
        text.setSelectionEnd(end);
        selectedBackground.layoutXProperty().bind(text.layoutXProperty());
        selectedBackground.layoutYProperty().bind(text.layoutYProperty());
        selectedBackground.getElements().setAll(text.getSelectionShape());
        if (begin < end) {
            node.setSelectedText(text.getText().substring(begin, Math.min(end, text.getText().length())));
        } else {
            node.setSelectedText(text.getText().substring(end, begin));
        }
    }

    /**
     * 清除文本选中
     */
    private void clearSelected() {
        text.setSelectionStart(0);
        text.setSelectionEnd(0);
        selectedBackground.getElements().clear();
    }

    /**
     * 获取Text在字符处的信息
     *
     * @param x x
     * @param y y
     * @return 字符信息
     */
    private HitInfo getIndex(double x, double y) {
        Point2D point2D = new Point2D(x, y);
        return text.hitTest(point2D);
    }

    /**
     * 文本是否应该被忽略
     *
     * @return true false
     */
    private boolean isIgnoreText() {
        final Labeled labeled = getSkinnable();
        final String text = labeled.getText();
        return text == null || text.isBlank() || labeled.getContentDisplay() == ContentDisplay.GRAPHIC_ONLY;
    }
}
