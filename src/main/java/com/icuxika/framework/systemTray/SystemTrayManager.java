package com.icuxika.framework.systemTray;

import javafx.application.Platform;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SystemTrayManager {

    private static final SystemTray systemTray = SystemTray.getSystemTray();

    /**
     * 托盘图标
     */
    public static TrayIcon trayIcon;

    /**
     * 托盘图标菜单
     */
    private static final PopupMenu popupMenu = new PopupMenu();

    /**
     * 默认彩色图标
     */
    private static final Image defaultImage = new ImageIcon(SystemTrayManager.class.getResource("/com/icuxika/img/logo.png")).getImage();

    /**
     * 默认黑白图标
     */
    private static final Image grayImage = new ImageIcon(SystemTrayManager.class.getResource("/com/icuxika/img/logo-gray.png")).getImage();

    /**
     * 标记托盘图标是否已经加载过
     */
    private static boolean loaded = false;

    /**
     * 记录当前窗口
     */
    private static Stage currentStage;

    /**
     * 执行退出流程
     */
    private static Runnable onExitAction;

    public static void initSystemTray(Stage stage) {
        currentStage = stage;
        if (!loaded) {
            loaded = true;
            Platform.setImplicitExit(false);

            trayIcon = new TrayIcon(defaultImage, "即时通讯", popupMenu);
            trayIcon.setImageAutoSize(true);
            try {
                systemTray.add(trayIcon);

                // 双击图标打开页面
                trayIcon.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
                            Platform.runLater(() -> {
                                if (currentStage.isIconified()) {
                                    currentStage.setIconified(false);
                                }
                                currentStage.show();
                                currentStage.setAlwaysOnTop(true);
                                currentStage.setAlwaysOnTop(false);
                            });
                        }
                    }
                });
            } catch (AWTException e) {
                e.printStackTrace();
            }
        }
    }

    private static void showDefaultImage() {
        if (loaded) {
            trayIcon.setImage(defaultImage);
        }
    }

    private static void showGrayImage() {
        if (loaded) {
            trayIcon.setImage(grayImage);
        }
    }

    public static void showImage(String url) {
        if (loaded) {
            trayIcon.setImage(new ImageIcon(SystemTrayManager.class.getResource(url)).getImage());
        }
    }

    public static void showLoggedIn() {
        if (loaded) {
            popupMenu.removeAll();

            MenuItem statusOnlineMenuItem = new MenuItem("在线");
            statusOnlineMenuItem.addActionListener(e -> {
                // 更新用户状态为在线
            });
            MenuItem statusHideMenuItem = new MenuItem("隐身");
            statusHideMenuItem.addActionListener(e -> {
                // 更新用户状态为隐身
            });
            popupMenu.add(statusOnlineMenuItem);
            popupMenu.add(statusHideMenuItem);

            buildDefaultMenu();
            showDefaultImage();
        }
    }

    public static void showNotLogged() {
        if (loaded) {
            popupMenu.removeAll();
            buildDefaultMenu();
            showGrayImage();
        }
    }

    private static void buildDefaultMenu() {
        MenuItem showMenuItem = new MenuItem("打开");
        MenuItem hideMenuItem = new MenuItem("最小化");
        MenuItem exitMenuItem = new MenuItem("退出");
        popupMenu.add(showMenuItem);
        popupMenu.add(hideMenuItem);
        popupMenu.add(exitMenuItem);

        showMenuItem.addActionListener(e -> Platform.runLater(() -> {
            if (currentStage.isIconified()) {
                currentStage.setIconified(false);
            }
            currentStage.show();
            currentStage.setAlwaysOnTop(true);
            currentStage.setAlwaysOnTop(false);
        }));

        hideMenuItem.addActionListener(e -> Platform.runLater(() -> currentStage.hide()));

        exitMenuItem.addActionListener(e -> {
            Platform.setImplicitExit(true);

            if (onExitAction != null) {
                Platform.runLater(() -> onExitAction.run());
            }

            exit();
            System.exit(0);
        });
    }

    public static void setOnExitAction(Runnable onExitAction) {
        SystemTrayManager.onExitAction = onExitAction;
    }

    /**
     * 退出
     */
    public static void exit() {
        Platform.setImplicitExit(true);
        // macOS上不使用 EventQueue 时，点击关闭按钮，程序会卡住
        EventQueue.invokeLater(() -> systemTray.remove(trayIcon));
    }
}
