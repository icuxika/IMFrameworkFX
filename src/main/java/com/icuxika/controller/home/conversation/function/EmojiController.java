package com.icuxika.controller.home.conversation.function;

import com.icuxika.annotation.AppFXML;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.CacheHint;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

/**
 * Emoji表情管理窗口
 */
@AppFXML(fxml = "home/conversation/function/emoji.fxml", stylesheets = "css/home/emoji.css")
public class EmojiController {

    @FXML
    private TabPane emojiTabPane;
    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * 当打开子窗口时，当前窗口不自动隐藏，关闭后恢复，子窗口最好设置为模态框模式
     */
    private boolean autoHide = true;

    private Consumer<File> consumer;

    public void initialize() {
        emojiTabPane.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        newWindow.focusedProperty().addListener((observable, oldValue, newValue) -> {
                            if (!newValue) {
                                // 防止此时打开新窗口时失去焦点后窗口自动隐藏
                                if (autoHide) ((Stage) newWindow).close();
                            }
                        });
                    }
                });
            }
        });

        Task<Map<String, List<File>>> task = new TwitterEmojiReadTask();
        task.setOnSucceeded(event -> {
            try {
                task.get().forEach((s, fileList) -> {
                    Tab newTab = new Tab(s);
                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setCache(true);
                    scrollPane.setCacheHint(CacheHint.SPEED);
                    newTab.setContent(scrollPane);

                    FlowPane flowPane = new FlowPane(Orientation.HORIZONTAL, 10, 8);
                    flowPane.setCache(true);
                    flowPane.setCacheHint(CacheHint.SPEED);
                    flowPane.setPadding(new Insets(8));
                    flowPane.prefWidthProperty().bind(scrollPane.widthProperty());
                    scrollPane.setContent(flowPane);

                    fileList.forEach(file -> {
                        ImageView imageView = new ImageView();
                        imageView.setCache(true);
                        imageView.setCacheHint(CacheHint.SPEED);
                        imageView.setFitWidth(32);
                        imageView.setFitHeight(32);
                        imageView.setImage(new Image("file:" + file.getAbsolutePath(), true));
                        imageView.setOnMouseClicked(event1 -> {
                            if (consumer != null) consumer.accept(file);
                        });
                        flowPane.getChildren().add(imageView);
                    });

                    emojiTabPane.getTabs().add(newTab);
                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    public void setConsumer(Consumer<File> consumer) {
        this.consumer = consumer;
    }

    /**
     * 将 emojipedia 文件夹放在项目根目录下，当前只加载 twitter部分
     * 后面补充一个下拉菜单来切换不同品牌
     */
    static class TwitterEmojiReadTask extends Task<Map<String, List<File>>> {
        @Override
        protected Map<String, List<File>> call() throws Exception {
            Map<String, List<File>> twitterMap = new HashMap<>();
            File twitterDir = new File("emojipedia/twitter");
            File[] categories = twitterDir.listFiles();
            if (categories != null) {
                for (File category : categories) {
                    String categoryName = category.getName();
                    List<File> emojiFileList = new ArrayList<>();
                    twitterMap.put(categoryName, emojiFileList);
                    getFileList(category.getAbsolutePath(), emojiFileList);
                }
            }
            return twitterMap;
        }

        /**
         * 递归读取目录下的文件
         *
         * @param path     开始目录
         * @param fileList 文件列表
         */
        private void getFileList(String path, List<File> fileList) {
            File rootDir = new File(path);
            File[] files = rootDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        getFileList(file.getAbsolutePath(), fileList);
                    } else {
                        fileList.add(file);
                    }
                }
            }
        }
    }
}
