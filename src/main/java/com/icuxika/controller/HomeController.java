package com.icuxika.controller;

import com.icuxika.AppView;
import com.icuxika.LanguageListCell;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.controller.home.AddressBookController;
import com.icuxika.controller.home.AvatarModifyController;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.framework.UserData;
import com.icuxika.framework.UserStatus;
import com.icuxika.framework.systemTray.SystemTrayManager;
import com.jfoenix.control.JFXTooltip;
import com.jfoenix.svg.SVGGlyph;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;

@AppFXML(fxml = "home.fxml", stylesheets = "css/home.css")
public class HomeController {

    @FXML
    private ImageView avatarImageView;
    @FXML
    private TextFlow userStatusTextFlow;
    @FXML
    private VBox homePageIconContainer;
    @FXML
    private ComboBox<Locale> languageComboBox;

    /**
     * 子页面容器
     */
    @FXML
    private StackPane pageContainer;

    /**
     * 会话子页面
     * 【待验证】似乎 fx:include 子页面时设置的 fx:id 需要与 controller前缀或是文件名相对应
     */
    @FXML
    private SplitPane conversation;
    @FXML
    private ConversationController conversationController;

    /**
     * 通讯录子页面
     */
    @FXML
    private SplitPane addressBook;
    @FXML
    private AddressBookController addressBookController;

    /**
     * 用户状态右键菜单
     */
    private ContextMenu userStatusContextMenu;

    private FontIcon conversationIcon;
    private FontIcon addressBookIcon;

    /**
     * 初始化
     * 【一些说明】使子页面自适应父容器宽高 需要设置子页面的 Min、Max尺寸为 USE_COMPUTED_SIZE，而将子页面组件绑定到父容器上面的方式，会使得父容器所在的Stage、Scene等尺寸无法变更，便外表却可以缩小，诡异
     */
    public void initialize() {
        avatarImageView.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // 设置托盘图标
                        SystemTrayManager.initSystemTray((Stage) newWindow);
                        SystemTrayManager.showLoggedIn();
                        SystemTrayManager.setOnExitAction(() -> {
                            // 从托盘图标执行退出逻辑
                            // 关闭
                        });
                    }
                });
            }
        });

        // 绑定用户头像
        avatarImageView.imageProperty().bind(UserData.avatarProperty());
        Rectangle avatarImageClip = new Rectangle(0, 0, avatarImageView.getFitWidth(), avatarImageView.getFitHeight());
        avatarImageClip.setArcWidth(8);
        avatarImageClip.setArcHeight(8);
        avatarImageView.setClip(avatarImageClip);
        avatarImageView.setEffect(new DropShadow(2, Color.BLACK));
        UserData.setAvatar(new Image(MainApp.load("img/avatar.jpg").toExternalForm(), true));

        // 修改头像
        avatarImageView.setOnMouseClicked(event -> {
            AppView<AvatarModifyController> avatarModifyView = new AppView<>(AvatarModifyController.class);
            Stage stage = new Stage();
            stage.setResizable(false);
            stage.initOwner(avatarImageView.getScene().getWindow());
            avatarModifyView.setStage(stage).show();
        });

        // 绑定用户在线状态图标
        userStatusTextFlow.shapeProperty().bind(UserData.userStatusIconShapeProperty());

        // 监听用户状态改变
        UserData.userStatusProperty().addListener((observable, oldValue, newValue) -> {
            SVGPath userStatusIcon = new SVGPath();
            switch (newValue) {
                case ONLINE -> {
                    userStatusIcon.setContent("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM480.664569 672.184259l-64.396228 64.370053-64.431021-64.370053L223.009047 543.351032l64.397251-64.393589L416.244804 607.744621l320.325361-320.298933 64.419765 64.393589L480.664569 672.184259z");
                    userStatusTextFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#73d74d"), null, null)));
                }
                case OFFLINE -> {
                    userStatusIcon.setContent("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM705.974102 640.246873l-65.770584 65.73105L512.022002 577.75561 383.770898 705.978946l-65.746024-65.73105 128.20403-128.246873L318.024874 383.776663l65.746024-65.75561 128.22859 128.223337 128.20403-128.223337 65.770584 65.75561L577.745512 512 705.974102 640.246873z");
                    userStatusTextFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#b4b8ba"), null, null)));
                }
                case HIDE -> {
                    userStatusIcon.setContent("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM266.371377 306.294988l491.256222 0 0 33.772174L266.371377 340.067162 266.371377 306.294988zM757.627599 717.706035 266.371377 717.706035l0-33.773198 491.256222 0L757.627599 717.706035zM140.487278 528.886599l0-33.771151 743.024421 0 0 33.771151L140.487278 528.886599z");
                    userStatusTextFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#e8c35e"), null, null)));
                }
                case BUSY -> {
                    userStatusIcon.setContent("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM739.818272 558.50415l-181.308634-0.033769-93.020298 0.024559-181.325008-0.017396-0.00921-92.983741 181.342405 0 92.986528 0.008186 181.360825 0.017396L739.818272 558.50415z");
                    userStatusTextFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#ed695e"), null, null)));
                }
                case LEAVE -> {
                    userStatusIcon.setContent("M511.999488 61.745273c-248.681782 0-450.273176 201.543226-450.273176 450.214818 0 248.711502 201.591394 450.294636 450.273176 450.294636s450.273176-201.583135 450.273176-450.294636C962.272664 263.288498 760.68127 61.745273 511.999488 61.745273zM511.999488 877.79308c-202.051901 0-365.867422-163.810856-365.867422-365.832989 0-202.023156 163.814498-365.8115 365.867422-365.8115 202.049854 0 365.867422 163.789367 365.867422 365.8115C877.865887 713.982224 714.049343 877.79308 511.999488 877.79308zM547.17708 476.78394 547.17708 171.950234 476.821896 171.950234 476.821896 547.176151 521.393824 547.176151 547.17708 547.176151 830.976076 547.176151 830.976076 476.78394Z");
                    userStatusTextFlow.setBackground(new Background(new BackgroundFill(Paint.valueOf("#b4b8ba"), null, null)));
                }
            }
            UserData.setUserStatusIconShape(userStatusIcon);
        });
        // 设置为在线状态
        UserData.setUserStatus(UserStatus.ONLINE);

        // 用户状态鼠标事件监听
        userStatusContextMenu = buildUserStatusContextMenu();
        userStatusTextFlow.setOnMouseClicked(event -> userStatusContextMenu.show(userStatusTextFlow, event.getScreenX(), event.getScreenY()));

        // 会话图标
        conversationIcon = new FontIcon(FontAwesomeSolid.COMMENTS);
        JFXTooltip.install(conversationIcon, new JFXTooltip("会话"), Pos.CENTER_RIGHT);
        addressBookIcon = new FontIcon(FontAwesomeSolid.ADDRESS_BOOK);
        // 通讯录图标
        JFXTooltip.install(addressBookIcon, new JFXTooltip("通讯录"), Pos.CENTER_RIGHT);
        homePageIconContainer.getChildren().addAll(conversationIcon, addressBookIcon);

        // 默认选择会话页面
        switchPage(HomePageType.CONVERSATION);

        conversationIcon.setOnMouseClicked(event -> switchPage(HomePageType.CONVERSATION));
        addressBookIcon.setOnMouseClicked(event -> switchPage(HomePageType.ADDRESS_BOOK));

        languageComboBox.getItems().addAll(MainApp.SUPPORT_LANGUAGE_LIST);
        languageComboBox.setValue(MainApp.getCurrentLocale());
        languageComboBox.setCellFactory(param -> new LanguageListCell());
        languageComboBox.setButtonCell(new LanguageListCell());
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                MainApp.setLanguage(newValue);
            }
        });
    }

    /**
     * 构建用户状态切换菜单
     *
     * @return contextMenu
     */
    private ContextMenu buildUserStatusContextMenu() {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem onlineMenuItem = new MenuItem("在线", new SVGGlyph("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM480.664569 672.184259l-64.396228 64.370053-64.431021-64.370053L223.009047 543.351032l64.397251-64.393589L416.244804 607.744621l320.325361-320.298933 64.419765 64.393589L480.664569 672.184259z", Paint.valueOf("#73d74d")));
        onlineMenuItem.setOnAction(event -> UserData.setUserStatus(UserStatus.ONLINE));
        MenuItem offlineMenuItem = new MenuItem("离线", new SVGGlyph("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM705.974102 640.246873l-65.770584 65.73105L512.022002 577.75561 383.770898 705.978946l-65.746024-65.73105 128.20403-128.246873L318.024874 383.776663l65.746024-65.75561 128.22859 128.223337 128.20403-128.223337 65.770584 65.75561L577.745512 512 705.974102 640.246873z", Paint.valueOf("#b4b8ba")));
        offlineMenuItem.setOnAction(event -> UserData.setUserStatus(UserStatus.OFFLINE));
        MenuItem hideMenuItem = new MenuItem("隐身", new SVGGlyph("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM266.371377 306.294988l491.256222 0 0 33.772174L266.371377 340.067162 266.371377 306.294988zM757.627599 717.706035 266.371377 717.706035l0-33.773198 491.256222 0L757.627599 717.706035zM140.487278 528.886599l0-33.771151 743.024421 0 0 33.771151L140.487278 528.886599z", Paint.valueOf("#e8c35e")));
        hideMenuItem.setOnAction(event -> UserData.setUserStatus(UserStatus.HIDE));
        MenuItem busyMenuItem = new MenuItem("忙碌", new SVGGlyph("M511.999488 61.745273c-248.678712 0-450.273176 201.585181-450.273176 450.254727s201.594464 450.254727 450.273176 450.254727c248.678712 0 450.273176-201.585181 450.273176-450.254727S760.6782 61.745273 511.999488 61.745273zM739.818272 558.50415l-181.308634-0.033769-93.020298 0.024559-181.325008-0.017396-0.00921-92.983741 181.342405 0 92.986528 0.008186 181.360825 0.017396L739.818272 558.50415z", Paint.valueOf("#ed695e")));
        busyMenuItem.setOnAction(event -> UserData.setUserStatus(UserStatus.BUSY));
        MenuItem leaveMenuItem = new MenuItem("离开", new SVGGlyph("M511.999488 61.745273c-248.681782 0-450.273176 201.543226-450.273176 450.214818 0 248.711502 201.591394 450.294636 450.273176 450.294636s450.273176-201.583135 450.273176-450.294636C962.272664 263.288498 760.68127 61.745273 511.999488 61.745273zM511.999488 877.79308c-202.051901 0-365.867422-163.810856-365.867422-365.832989 0-202.023156 163.814498-365.8115 365.867422-365.8115 202.049854 0 365.867422 163.789367 365.867422 365.8115C877.865887 713.982224 714.049343 877.79308 511.999488 877.79308zM547.17708 476.78394 547.17708 171.950234 476.821896 171.950234 476.821896 547.176151 521.393824 547.176151 547.17708 547.176151 830.976076 547.176151 830.976076 476.78394Z", Paint.valueOf("#b4b8ba")));
        leaveMenuItem.setOnAction(event -> UserData.setUserStatus(UserStatus.LEAVE));
        contextMenu.getItems().addAll(onlineMenuItem, hideMenuItem, busyMenuItem, leaveMenuItem, offlineMenuItem);
        return contextMenu;
    }

    /**
     * 子页面类型枚举
     */
    public enum HomePageType {
        CONVERSATION, ADDRESS_BOOK
    }

    /**
     * 切换页面
     *
     * @param pageType 子页面类型
     */
    public void switchPage(HomePageType pageType) {
        switch (pageType) {
            case CONVERSATION -> {
                conversation.toFront();
            }
            case ADDRESS_BOOK -> {
                addressBook.toFront();
            }
        }
    }
}
