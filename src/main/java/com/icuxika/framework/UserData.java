package com.icuxika.framework;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

/**
 * 用户数据
 */
public class UserData {

    /**
     * 暂定当前用户id为0
     */
    public static Long userId = 0L;

    /**
     * 当前登录用户名称属性
     */
    private static final StringProperty name = new SimpleStringProperty();

    public static StringProperty nameProperty() {
        return name;
    }

    public static void setName(String name) {
        nameProperty().set(name);
    }

    /**
     * 当前登录用户头像属性
     */
    private static final ObjectProperty<Image> avatar = new SimpleObjectProperty<>();

    public static ObjectProperty<Image> avatarProperty() {
        return avatar;
    }

    public static void setAvatar(Image avatar) {
        avatarProperty().set(avatar);
    }

    /**
     * 当前登录用户在线状态属性
     */
    private static final ObjectProperty<UserStatus> userStatus = new SimpleObjectProperty<>();

    public static ObjectProperty<UserStatus> userStatusProperty() {
        return userStatus;
    }

    public static void setUserStatus(UserStatus userStatus) {
        userStatusProperty().set(userStatus);
    }

    /**
     * 当前登录用户在线状态图标形状属性
     */
    private static final ObjectProperty<Shape> userStatusIconShape = new SimpleObjectProperty<>();

    public static ObjectProperty<Shape> userStatusIconShapeProperty() {
        return userStatusIconShape;
    }

    public static void setUserStatusIconShape(Shape shape) {
        userStatusIconShapeProperty().set(shape);
    }

}
