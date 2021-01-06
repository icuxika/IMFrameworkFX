package com.icuxika;

import com.icuxika.controller.LoginController;
import com.icuxika.framework.StartupLocation;
import com.icuxika.i18n.LanguageResource;
import com.icuxika.i18n.ObservableResourceBundleFactory;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.application.Preloader;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static final Logger logger = LogManager.getLogger(MainApp.class.getName());

    /**
     * 默认语言文件 Base Name
     */
    private static final String LANGUAGE_RESOURCE_NAME = LanguageResource.class.getTypeName();

    /**
     * 语言资源工厂
     */
    private static final ObservableResourceBundleFactory LANGUAGE_RESOURCE_FACTORY = new ObservableResourceBundleFactory();

    /**
     * 支持的语言集合，应与语言资源文件同步手动更新
     */
    public static final List<Locale> SUPPORT_LANGUAGE_LIST = Arrays.asList(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH);

    /**
     * 更新界面语言
     *
     * @param locale 区域
     */
    public static void setLanguage(Locale locale) {
        LANGUAGE_RESOURCE_FACTORY.setResourceBundle(ResourceBundle.getBundle(LANGUAGE_RESOURCE_NAME, locale));
    }

    /**
     * 获取指定标识的字符串绑定
     *
     * @param key 标识
     * @return 对应该标识的字符串属性绑定
     */
    public static StringBinding getLanguageBinding(String key) {
        return LANGUAGE_RESOURCE_FACTORY.getStringBinding(key);
    }

    /**
     * 有此类所在路径决定相对路径基于/com/icuxika
     *
     * @param path 资源文件相对路径
     * @return 资源文件路径
     */
    public static URL load(String path) {
        return MainApp.class.getResource(path);
    }

    /**
     * 多个屏幕时，使窗口显示在鼠标所在的屏幕中央，根据StartupLocation的实现逻辑，此操作存在延迟
     *
     * @param stage stage
     */
    public static void showStageWithPointer(Stage stage, double width, double height) {
        StartupLocation startupLocation = new StartupLocation(width, height);
        double xPos = startupLocation.getXPos();
        double yPos = startupLocation.getYPos();
        if (xPos != 0 && yPos != 0) {
            stage.setX(xPos);
            stage.setY(yPos);
        } else {
            stage.centerOnScreen();
        }
    }

    /**
     * 设置此属性，启用JavaFX预加载类
     */
    private static final String PRELOADER_PROPERTY_NAME = "javafx.preloader";

    /**
     * 预加载任务完成加载
     */
    BooleanProperty ready = new SimpleBooleanProperty(false);

    public static void main(String[] args) {
        System.setProperty(PRELOADER_PROPERTY_NAME, AppPreloader.class.getTypeName());
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 初始化设置界面语言
        setLanguage(Locale.SIMPLIFIED_CHINESE);
        logger.info("Set Language: SIMPLIFIED_CHINESE");
    }

    @Override
    public void start(Stage primaryStage) {
        preloadTask();
        ready.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                Platform.runLater(() -> {
                    primaryStage.titleProperty().bind(MainApp.getLanguageBinding("title"));
                    AppView<LoginController> loginView = new AppView<>(LoginController.class);
                    loginView.setStage(primaryStage).show();
                });
            }
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * 预加载任务
     */
    private void preloadTask() {
        new Thread(new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                int max = 10;
                for (int i = 0; i < max; i++) {
                    Thread.sleep(200);
                    notifyPreloader(new Preloader.ProgressNotification(((double) i) / max));
                }
//                notifyPreloader(new Preloader.ErrorNotification("null" , "出现了一些错误", new RuntimeException("运行时错误")));
                ready.setValue(Boolean.TRUE);
                notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
                return null;
            }
        }).start();
    }
}
