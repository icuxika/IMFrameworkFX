package com.icuxika.framework.systemTray;

import com.icuxika.model.SystemTrayMessageModel;
import javafx.application.Platform;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

public class SystemTrayTaskManager {

    /**
     * 在 Windows 10 20H2版本下，托盘图标本身的尺寸一直是 16 x 16 SystemTrayManager.trayIcon.getSize()
     * 但是，监听托盘图标的鼠标事件实际上可以捕获到的宽度为 24
     */
    private static final double ICON_SIZE = 24.0;

    private final SystemTrayMessageWindow window = new SystemTrayMessageWindow();

    private final SystemTrayIconMouseListener systemTrayIconMouseListener = new SystemTrayIconMouseListener();
    private final SystemTrayIconMouseMotionListener systemTrayIconMouseMotionListener = new SystemTrayIconMouseMotionListener();

    public void initListeners() {
        SystemTrayManager.trayIcon.addMouseListener(systemTrayIconMouseListener);
        SystemTrayManager.trayIcon.addMouseMotionListener(systemTrayIconMouseMotionListener);
    }

    public void destroyListeners() {
        SystemTrayManager.trayIcon.removeMouseListener(systemTrayIconMouseListener);
        SystemTrayManager.trayIcon.removeMouseMotionListener(systemTrayIconMouseMotionListener);
    }

    public void pushMessage(SystemTrayMessageModel message) {
        window.pushMessage(message);
    }

    private class SystemTrayIconMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
                System.out.println("clicked tray con one time");
            }
            System.out.println(SystemTrayManager.trayIcon.getSize());

        }
    }

    private class SystemTrayIconMouseMotionListener extends MouseMotionAdapter {

        /**
         * 鼠标进入托盘图标的最小横坐标
         */
        private double mouseEnterMinX = 0.0;

        /**
         * 鼠标进入托盘图标的最大横坐标
         */
        private double mouseEnterMaxX = 0.0;

        /**
         * 记录一个固定横坐标，这样防止重新进入时，横坐标变化导致窗口左右横移
         */
        private double mouseFixedPointX = 0.0;

        /**
         * 鼠标进入托盘图标的最小纵坐标
         */
        private double mouseEnterMinY = 0.0;

        /**
         * 鼠标进入托盘图标的最大纵坐标
         */
        private double mouseEnterMaxY = 0.0;

        /**
         * 记录一个固定纵坐标，这样防止重新进入时，纵坐标变化导致窗口左右横移
         */
        private double mouseFixedPointY = 0.0;

        @Override
        public void mouseMoved(MouseEvent e) {
            // 初始化设置值
            if (mouseEnterMinX == 0.0) mouseEnterMinX = e.getXOnScreen();
            if (mouseEnterMaxX == 0.0) mouseEnterMaxX = e.getXOnScreen();
            if (mouseFixedPointX == 0.0) mouseFixedPointX = e.getXOnScreen();
            if (mouseEnterMinY == 0.0) mouseEnterMinY = e.getYOnScreen();
            if (mouseEnterMaxY == 0.0) mouseEnterMaxY = e.getYOnScreen();
            if (mouseFixedPointY == 0.0) mouseFixedPointY = e.getYOnScreen();

            // 1、鼠标第一次进入图标时可能不在两端；2、托盘图标位置变动
            if (e.getXOnScreen() < mouseEnterMinX) {
                mouseEnterMinX = e.getXOnScreen();
                if (mouseEnterMaxX - mouseEnterMinX > ICON_SIZE) {
                    mouseEnterMaxX = mouseEnterMinX + ICON_SIZE;
                }
            }
            if (e.getXOnScreen() > mouseEnterMaxX) {
                mouseEnterMaxX = e.getXOnScreen();
                if (mouseEnterMaxX - mouseEnterMinX > ICON_SIZE * 2) {
                    mouseEnterMinX = mouseEnterMaxX - ICON_SIZE;
                }
            }
            if (e.getYOnScreen() < mouseEnterMinY) {
                mouseEnterMinY = e.getYOnScreen();
                if (mouseEnterMaxY - mouseEnterMinY > ICON_SIZE * 2) {
                    mouseEnterMaxY = mouseEnterMinY + ICON_SIZE;
                }
            }
            if (e.getYOnScreen() > mouseEnterMaxY) {
                mouseEnterMaxY = e.getYOnScreen();
                if (mouseEnterMaxY - mouseEnterMinY > ICON_SIZE * 2) {
                    mouseEnterMinY = mouseEnterMaxY - ICON_SIZE;
                }
            }
            // 更新时保证固定点在最值之间
            if (mouseFixedPointX < mouseEnterMinX) mouseFixedPointX = mouseEnterMinX + ICON_SIZE / 2;
            if (mouseFixedPointX > mouseEnterMaxX) mouseFixedPointX = mouseEnterMaxX - ICON_SIZE / 2;
            if (mouseFixedPointY < mouseEnterMinY) mouseFixedPointY = mouseEnterMinY + ICON_SIZE / 2;
            if (mouseFixedPointY > mouseEnterMaxY) mouseFixedPointY = mouseEnterMaxY - ICON_SIZE / 2;

            // 纵向：监听 SystemTrayMessageWindow 的鼠标移出事件来隐藏窗口
            // 横向：判断鼠标事件横坐标是否在(min + 2, max - 2)的范围内，加减2是因为太边缘的鼠标事件可能无法捕获到，就会出现不隐藏的情况
            if (e.getXOnScreen() > mouseEnterMinX + 2 && e.getXOnScreen() < mouseEnterMaxX - 2) {
                // 显示窗口
                Platform.runLater(() -> {
                    window.showWindow(mouseFixedPointX, mouseFixedPointY);
                });
            } else {
                // 隐藏窗口
                Platform.runLater(window::hideWindow);
            }
        }
    }
}
