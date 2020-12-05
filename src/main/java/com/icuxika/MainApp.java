package com.icuxika;

import com.icuxika.controller.LoginController;
import com.icuxika.i18n.LanguageResource;
import com.icuxika.i18n.ObservableResourceBundleFactory;
import javafx.application.Application;
import javafx.beans.binding.StringBinding;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.Scene;
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

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void init() throws Exception {
        super.init();
        // 初始化设置界面语言
        setLanguage(Locale.SIMPLIFIED_CHINESE);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        AppView<LoginController> appView = new AppView<>(LoginController.class);
        Parent root = appView.getRootNode();

        appView.getController().getLoginButton().setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                System.out.println(123);
            }
        });

        primaryStage.titleProperty().bind(MainApp.getLanguageBinding("title"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();

        logger.info("Set Language: zh_CN");
        logger.error("Set Language: zh_CN");
    }
}
