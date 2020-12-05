package com.icuxika;

import com.icuxika.annotation.AppFXML;
import com.icuxika.exception.FXMLNotFoundException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

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

    /**
     * 当前页面的根结点
     */
    private Parent rootNode;

    /**
     * 当前页面的 FXMLLoader
     */
    private final FXMLLoader fxmlLoader;

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