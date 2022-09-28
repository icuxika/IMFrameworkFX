package com.icuxika;

import com.icuxika.controller.LoginController;
import com.icuxika.framework.StartupLocation;
import com.icuxika.i18n.LanguageConstants;
import com.icuxika.i18n.LanguageResource;
import com.icuxika.i18n.ObservableResourceBundleFactory;
import javafx.application.Application;
import javafx.application.Preloader;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class MainApp extends Application {

    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);

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
     * 记录当前所选时区
     */
    private static final ObjectProperty<Locale> currentLocale = new SimpleObjectProperty<>();

    public static ObjectProperty<Locale> currentLocaleProperty() {
        return currentLocale;
    }

    public static void setCurrentLocale(Locale locale) {
        currentLocaleProperty().set(locale);
    }

    /**
     * 更换语言的组件使用此方法初始化自己的值，调用 {@link MainApp#setLanguage(Locale)} 来更新界面语言
     *
     * @return 当前界面语言
     */
    public static Locale getCurrentLocale() {
        return currentLocaleProperty().get();
    }

    /**
     * 更新界面语言
     *
     * @param locale 区域
     */
    public static void setLanguage(Locale locale) {
        setCurrentLocale(locale);
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
//        System.setProperty(PRELOADER_PROPERTY_NAME, AppPreloader.class.getTypeName());
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 初始化设置界面语言
        setLanguage(Locale.SIMPLIFIED_CHINESE);
        logger.info("Set Language: SIMPLIFIED_CHINESE");

        logger.trace("[trace]日志控制台输出");
        logger.debug("[debug]日志控制台输出");
        logger.info("[info]日志控制台输出");
        logger.warn("[warn]日志记录到build/application.log中");
        logger.error("[error]日志记录到build/application.log中");
    }

    @Override
    public void start(Stage primaryStage) {
//        preloadTask();
//        ready.addListener((observable, oldValue, newValue) -> {
//            if (newValue) {
//                primaryStage.titleProperty().bind(MainApp.getLanguageBinding("title"));
//                primaryStage.setResizable(false);
//                AppView<LoginController> loginView = new AppView<>(LoginController.class);
//                loginView.setStage(primaryStage).show();
//            }
//        });
        primaryStage.titleProperty().bind(MainApp.getLanguageBinding(LanguageConstants.title));
        primaryStage.setResizable(false);
        AppView<LoginController> loginView = new AppView<>(LoginController.class);
        loginView.setStage(primaryStage).show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }

    /**
     * 预加载任务
     */
    private void preloadTask() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int max = 10;
                for (int i = 0; i < max; i++) {
                    Thread.sleep(200);
                    notifyPreloader(new Preloader.ProgressNotification(((double) i) / max));
                }
                return null;
            }

            @Override
            protected void failed() {
                notifyPreloader(new Preloader.ErrorNotification("null", "出现了一些错误", new RuntimeException("运行时错误")));
            }

            @Override
            protected void succeeded() {
                ready.setValue(Boolean.TRUE);
                notifyPreloader(new Preloader.StateChangeNotification(Preloader.StateChangeNotification.Type.BEFORE_START));
            }
        };
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }
}
