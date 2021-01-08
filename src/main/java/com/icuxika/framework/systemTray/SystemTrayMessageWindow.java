package com.icuxika.framework.systemTray;

import com.icuxika.MainApp;
import com.icuxika.control.SystemTrayMessageNode;
import com.icuxika.model.SystemTrayMessageModel;
import com.jfoenix.control.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;

/**
 * 托盘消息提示窗口
 * 1、为此窗口设定的一个固定宽度
 * 2、此窗口可以具有一个动态高度
 */
public class SystemTrayMessageWindow extends Stage {

    /**
     * 窗口固定宽度
     */
    private static final double WINDOW_WIDTH = 200.0;

    /**
     * 任务栏位置
     */
    private TaskbarPos taskbarPos = TaskbarPos.BOTTOM;

    private final Stage bearerStage = new Stage();

    private BorderPane contentPane = new BorderPane();
    private HBox header = new HBox();
    private Label title = new Label("Title");
    private HBox spacer = new HBox();
    private JFXButton cancelButton = new JFXButton("取消闪烁");

    private ObservableList<SystemTrayMessageModel> messageModelObservableList = FXCollections.observableArrayList();
    private ListView<SystemTrayMessageModel> listView = new ListView<>();

    public SystemTrayMessageWindow() {
        bearerStage.initStyle(StageStyle.UTILITY);
        bearerStage.setOpacity(0);

        contentPane.setMinWidth(WINDOW_WIDTH);
        contentPane.setMaxWidth(WINDOW_WIDTH);

        HBox.setMargin(title, new Insets(0, 0, 0, 4));
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox.setMargin(cancelButton, new Insets(0, 4, 0, 0));
        header.setPrefHeight(36);
        header.setAlignment(Pos.CENTER);
        header.getChildren().addAll(title, spacer, cancelButton);
        contentPane.setTop(header);

        listView.getStyleClass().add("systemTrayMessageListView");
        listView.setItems(messageModelObservableList);
        listView.setCellFactory(new ListViewCallback());
        listView.setMinHeight(0);
        listView.setMaxHeight(100);
        listView.setPrefHeight(0);
        contentPane.setCenter(listView);

        Scene scene = new Scene(contentPane);
        scene.setOnMouseExited(event -> hideWindow());
        scene.setFill(null);
        scene.getStylesheets().add(MainApp.class.getResource("css/systemTray/system-tray-message-window.css").toExternalForm());
        setScene(scene);
        initOwner(bearerStage);
        initStyle(StageStyle.UNDECORATED);
    }

    public void pushMessage(SystemTrayMessageModel message) {
        messageModelObservableList.add(message);
        refreshHeight();
    }

    private void refreshHeight() {
        listView.setPrefHeight(messageModelObservableList.size() * 24);
    }

    private static class ListViewCallback implements Callback<ListView<SystemTrayMessageModel>, ListCell<SystemTrayMessageModel>> {
        @Override
        public ListCell<SystemTrayMessageModel> call(ListView<SystemTrayMessageModel> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(SystemTrayMessageModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        setPadding(Insets.EMPTY);
                        setText(null);
                        setGraphic(new SystemTrayMessageNode());
                    }
                }
            };
        }
    }

    /**
     * 显示窗口（尽量中心）
     *
     * @param x 鼠标进入托盘图标的横坐标
     * @param y 鼠标进入托盘图标的纵坐标
     */
    public void showWindow(double x, double y) {
        // 托盘图标只会显示主屏幕
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        double screenWidth = visualBounds.getWidth();
        double screenHeight = visualBounds.getHeight();
        double screenMinX = visualBounds.getMinX();
        double screenMinY = visualBounds.getMinY();
        double screenMaxX = visualBounds.getMaxX();
        double screenMaxY = visualBounds.getMaxY();

        double windowHeight = contentPane.getHeight();
        // 不考虑任务栏隐藏了，算不动了
        // Windows 任务栏可以在上下左右（小任务栏上、下时高度30, 左、右时宽度78；大任务栏上、下时高度40，左、右时宽度78）
        if (Math.abs(screenMaxY - y) < 80) {
            //任务栏在下
            taskbarPos = TaskbarPos.BOTTOM;
        }
        if (Math.abs(screenMinY - y) < 80) {
            // 任务栏在上
            taskbarPos = TaskbarPos.TOP;
        }
        if (Math.abs(screenMinX - x) < 80) {
            // 任务栏在左
            taskbarPos = TaskbarPos.LEFT;
        }
        if (Math.abs(screenMaxX - x) < 80) {
            // 任务栏在右
            taskbarPos = TaskbarPos.RIGHT;
        }
        System.out.println(taskbarPos + "[" + x + ", " + y + "]" + " " + visualBounds);

        // 下 窗口的最大横坐标小于桌面宽度，最小纵坐标等于桌面（不包括任务栏高度）高度减去窗口实际高度
        // 先按照默认下计算
        double showX = x - WINDOW_WIDTH / 2;
        if (screenWidth - x < WINDOW_WIDTH / 2) {
            showX = screenWidth - WINDOW_WIDTH;
        }
        double showY = screenHeight - windowHeight;

        // 上 窗口的最大横坐标小于桌面宽度，最小纵坐标等于桌面（不包括任务栏高度）最小纵坐标
        if (taskbarPos == TaskbarPos.TOP) {
            // 横坐标不用变化
            showY = screenMinY;
        }

        // 左 窗口的最小横坐标等于桌面（不包括任务栏宽度）最小横坐标，最大纵坐标小于桌面高度
        if (taskbarPos == TaskbarPos.LEFT) {
            showX = screenMinX;
            showY = y - windowHeight / 2;
            if (screenHeight - y < windowHeight / 2) {
                showY = screenHeight - windowHeight / 2;
            }
        }

        // 右 窗口的最大横坐标等于桌面（不包括任务栏宽度）宽度，最大纵坐标小于桌面高度
        if (taskbarPos == TaskbarPos.RIGHT) {
            showX = screenMaxX - WINDOW_WIDTH;
            showY = y - windowHeight / 2;
            if (screenHeight - y < windowHeight / 2) {
                showY = screenHeight - windowHeight / 2;
            }
        }

        setX(showX);
        setY(showY);

        bearerStage.setAlwaysOnTop(true);
        setAlwaysOnTop(true);
        bearerStage.setAlwaysOnTop(false);
        setAlwaysOnTop(false);

        bearerStage.show();
        show();

    }

    /**
     * 隐藏窗口
     */
    public void hideWindow() {
        bearerStage.hide();
        hide();
    }

    enum TaskbarPos {
        TOP, BOTTOM, LEFT, RIGHT
    }
}
