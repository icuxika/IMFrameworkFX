package com.icuxika;

import javafx.application.Preloader;
import javafx.scene.Scene;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * 应用程序预加载类
 */
public class AppPreloader extends Preloader {

    ProgressBar progressBar;
    Stage stage;
    boolean noLoadingProgress = true;

    private Scene createPreloaderScene() {
        progressBar = new ProgressBar(0);
        BorderPane pane = new BorderPane();
        pane.setCenter(progressBar);
        return new Scene(pane, 300, 150);
    }

    @Override
    public void init() throws Exception {
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.stage = primaryStage;
        MainApp.showStageWithPointer(stage, 300, 150);
        primaryStage.setScene(createPreloaderScene());
        primaryStage.show();
    }

    /**
     * 此方法并未发现会被触发
     *
     * @throws Exception 异常
     */
    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * 此API没啥用，统一在 ${@link AppPreloader#handleApplicationNotification(PreloaderNotification)} 中进行通知处理
     *
     * @param info 进度变更信息 会默认有0.0和1.0两个值，执行顺序位于 ${@link Preloader#start(Stage)} 之后
     */
    @Override
    public void handleProgressNotification(ProgressNotification info) {
        // do nothing
        progressBar.setProgress(info.getProgress() / 2);
        if (info.getProgress() > 0) {
            noLoadingProgress = false;
        }
    }

    /**
     * 此API没啥用，统一在 ${@link AppPreloader#handleApplicationNotification(PreloaderNotification)} 中进行通知处理
     *
     * @param info 状态变更信息 会默认有BEFORE_LOAD、BEFORE_INIT和BEFORE_START三个值，执行顺序位于 ${@link Preloader#handleProgressNotification(ProgressNotification)} 之后
     */
    @Override
    public void handleStateChangeNotification(StateChangeNotification info) {
        // do nothing
        super.handleStateChangeNotification(info);
    }

    /**
     * 处理MainApp传送过来的消息，执行顺序位于 ${@link Preloader#handleStateChangeNotification(StateChangeNotification)} 之后
     *
     * @param info 通知信息
     */
    @Override
    public void handleApplicationNotification(PreloaderNotification info) {
        if (info instanceof ProgressNotification) {
            // 加载进度
            double v = ((ProgressNotification) info).getProgress();
            if (!noLoadingProgress) {
                v = 0.5 + v / 2;
            }
            progressBar.setProgress(v);
        } else if (info instanceof ErrorNotification) {
            // 加载过程中出现错误
            ErrorNotification errorInfo = (ErrorNotification) info;
        } else if (info instanceof StateChangeNotification) {
            // 加载结束
            stage.close();
        }
    }

    /**
     * 此API没啥用，统一在 ${@link AppPreloader#handleApplicationNotification(PreloaderNotification)} 中进行通知处理
     *
     * @param info 错误信息
     * @return 处理结果
     */
    @Override
    public boolean handleErrorNotification(ErrorNotification info) {
        // do nothing
        return super.handleErrorNotification(info);
    }
}
