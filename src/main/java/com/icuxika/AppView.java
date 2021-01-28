package com.icuxika;

import com.icuxika.annotation.AppFXML;
import com.icuxika.exception.FXMLNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.URL;

/**
 * 通过 {@link AppFXML} 注解或是根据类名加载FXML文件，并可以返回根结点及控制器
 * <p>
 * 本类使用泛型的目的，使{@link AppView#getController()}可以直接返回对应Controller
 *
 * @param <T> Controller 类
 */
public class AppView<T> {

    private static final Logger logger = LogManager.getLogger(AppView.class.getName());

    /**
     * 当前页面的根结点
     */
    private Parent rootNode;

    /**
     * 当前页面的 FXMLLoader
     */
    private final FXMLLoader fxmlLoader;

    /**
     * 注解中指定的样式表
     */
    private String[] stylesheets;

    public AppView(Class<T> controllerClass) {

        fxmlLoader = new FXMLLoader();
        URL fxmlUrl = null;

        // 检查是否存在 @AppFXML 注解
        AppFXML appFXML = null;
        Annotation[] annotations = controllerClass.getAnnotations();
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().equals(AppFXML.class)) {
                appFXML = (AppFXML) annotation;
            }
        }
        if (appFXML != null) {
            // 注解不为空
            String fxml = appFXML.fxml();
            if (isFXMLValid(fxml)) {
                if (MainApp.load(fxml) != null) {
                    fxmlUrl = MainApp.load(fxml);
                } else {
                    // 此处抛出异常的原因，在注解值有效的情况下，以注解值为唯一依据进行判断
                    throw new FXMLNotFoundException("注解加载FXML文件失败");
                }
            } else {
                String defaultFXML = getDefaultFXML(controllerClass);
                if (isFXMLValid(defaultFXML)) {
                    if (MainApp.load(defaultFXML) != null) {
                        fxmlUrl = MainApp.load(defaultFXML);
                    } else {
                        throw new FXMLNotFoundException("在有注解值无效的条件下，根据类名加载FXML文件失败");
                    }
                }
            }
        } else {
            String defaultFXML = getDefaultFXML(controllerClass);
            if (isFXMLValid(defaultFXML)) {
                if (MainApp.load(defaultFXML) != null) {
                    fxmlUrl = MainApp.load(defaultFXML);
                } else {
                    throw new FXMLNotFoundException("在无注解的条件下，根据类名加载FXML文件失败，资源加载失败");
                }
            } else {
                throw new FXMLNotFoundException("在无注解的条件下，根据类名加载FXML文件失败，Controller类命名不符合规则");
            }
        }

        if (fxmlUrl != null) {
            fxmlLoader.setLocation(fxmlUrl);
            try {
                rootNode = fxmlLoader.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new FXMLNotFoundException("FXML文件加载失败，异常未知");
        }

        // 样式表
        if (appFXML != null) {
            stylesheets = appFXML.stylesheets();
        }
    }

    /**
     * 判断FXML路径是否有效
     *
     * @param fxml FXML文件路径
     * @return true 有效 false 无效
     */
    private boolean isFXMLValid(String fxml) {
        return fxml != null && !fxml.isBlank() && fxml.endsWith(".fxml");
    }

    /**
     * 根据类名获取FXML文件路径
     * 此方式只有fxml所在路径与MainApp所在包路径一致才可以
     *
     * @param controllerClass Controller类class文件
     * @return FXML文件路径
     */
    private String getDefaultFXML(Class<T> controllerClass) {
        String defaultFXML = null;
        // 判断Controller类是否以 Controller 结尾
        if (controllerClass.getSimpleName().endsWith("Controller")) {
            defaultFXML = controllerClass.getSimpleName().substring(0, controllerClass.getSimpleName().length() - "Controller".length()) + ".fxml";
        }
        return defaultFXML;
    }

    /**
     * 返回根结点Node
     *
     * @return Node
     */
    public Parent getRootNode() {
        return rootNode;
    }

    /**
     * 返回控制器
     *
     * @return Controller
     */
    public T getController() {
        return fxmlLoader.getController();
    }

    /**
     * 为场景设置样式表
     *
     * @param scene 场景
     */
    public void assembleStylesheets(Scene scene) {
        if (stylesheets != null && stylesheets.length > 0) {
            for (String stylesheet : stylesheets) {
                if (MainApp.load(stylesheet) != null) {
                    scene.getStylesheets().add(MainApp.load(stylesheet).toExternalForm());
                } else {
                    logger.warn("样式表 [" + stylesheet + "] 加载失败，请检查路径是否正确");
                }
            }
        }
    }

    private Stage stage;
    private Scene scene;

    /**
     * 设置Stage
     *
     * @param stage stage
     * @return this
     */
    public AppView<T> setStage(Stage stage) {
        this.stage = stage;
        return this;
    }

    /**
     * 设置Scene
     *
     * @param scene scene
     * @return this
     */
    public AppView<T> setScene(Scene scene) {
        this.scene = scene;
        return this;
    }

    /**
     * 窗口展示
     */
    public void show() {
        if (stage == null) {
            stage = new Stage();
        }
        if (scene == null) {
            scene = new Scene(getRootNode());
            scene.setFill(null);
        }
        // 为场景设置样式表
        assembleStylesheets(scene);
        stage.setScene(scene);
        MainApp.showStageWithPointer(stage, getRootNode().prefWidth(-1), getRootNode().prefHeight(-1));
        stage.show();
    }

    /**
     * 显示一个模态框
     *
     * @param owner 父窗口
     */
    public void modalShow(Window owner) {
        this.show(owner, false, false, false, 0.0, 0.0);
    }

    /**
     * 重复展示单个窗口
     *
     * @param owner 父窗口
     * @param x     event -> screenX
     * @param y     event -> screenY
     */
    public void repeatShow(Window owner, Double x, Double y) {
        this.show(owner, true, true, true, x, y);
    }

    /**
     * 展示窗口
     *
     * @param owner       父窗口
     * @param isSingleton 是否是单例
     * @param autoHide    当前仅决定是否设置模态框模式，是否随着鼠标焦点变化自动隐藏，此部分逻辑在个子窗口Controller中实现，如果需要子窗口中再开启子窗口的话，在此处监听焦点变化就会比较麻烦
     * @param relocate    是否更新坐标
     * @param x           event -> screenX
     * @param y           event -> screenY
     */
    public void show(Window owner, boolean isSingleton, boolean autoHide, boolean relocate, Double x, Double y) {
        if (isSingleton) {
            if (stage == null) {
                assembleStage(owner, autoHide);
            }
        } else {
            assembleStage(owner, autoHide);
            MainApp.showStageWithPointer(stage, getRootNode().prefWidth(-1), getRootNode().prefHeight(-1));
        }
        if (relocate) {
            stage.setX(x);
            stage.setY(y);
        }
        stage.show();
    }

    public void hide() {
        if (stage != null) stage.hide();
    }

    public void close() {
        if (stage != null) stage.hide();
    }

    private void assembleStage(Window owner, boolean autoHide) {
        stage = new Stage();
        // 此处设置父窗口，可使当前窗口不会出现在任务栏
        stage.initOwner(owner);
        stage.initStyle(StageStyle.TRANSPARENT);
        Scene scene = new Scene(getRootNode());
        scene.setFill(null);
        // 对场景设置样式表
        assembleStylesheets(scene);
        stage.setScene(scene);
        // 如果不设置成自动隐藏，则设置模态框模式
        if (!autoHide) stage.initModality(Modality.APPLICATION_MODAL);
    }

    /**
     * 获取类的基本相对路径
     *
     * @param clazz 类
     * @return 相对路径 如 com.icuxika.MainApp 将返回 /com/icuxika/
     */
    private String getClassRelativePath(Class<?> clazz) {
        String classTypeName = clazz.getTypeName();
        System.out.println(classTypeName);
        String[] strings = classTypeName.split("\\.");
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < strings.length - 1; i++) {
            stringBuilder.append(strings[i]);
            stringBuilder.append("/");
        }
        return stringBuilder.toString();
    }
}
