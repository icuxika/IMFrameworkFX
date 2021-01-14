package com.icuxika.controller;

import com.icuxika.AppView;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.controller.login.GraphValidateCodeController;
import com.icuxika.converter.LocaleStringConverter;
import com.icuxika.framework.QRCodeGenerator;
import com.icuxika.framework.systemTray.SystemTrayManager;
import com.icuxika.framework.systemTray.SystemTrayTaskManager;
import com.icuxika.model.SystemTrayMessageModel;
import com.icuxika.util.SystemUtil;
import com.jfoenix.control.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.beans.binding.When;
import javafx.beans.property.*;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;
import java.util.function.Consumer;

@AppFXML(fxml = "login.fxml", stylesheets = "css/login.css")
public class LoginController {

    @FXML
    private StackPane containerPane;

    @FXML
    private Label flyleafTitleLabel;

    @FXML
    private HBox header;

    @FXML
    private VBox testContainer;

    @FXML
    private Button alertTestButton;

    @FXML
    private Button dialogTestButton;

    @FXML
    private Button snackbarTestButton;

    @FXML
    private VBox generalLoginBox;
    @FXML
    private HBox generaLoginTypeChooseContainer;
    @FXML
    private HBox usernameContainer;
    @FXML
    private HBox passwordContainer;
    @FXML
    private HBox verificationCodeContainer;
    @FXML
    private HBox loginButtonContainer;
    @FXML
    private HBox qrCodeIconContainer;
    @FXML
    private HBox rememberContainer;
    private JFXCheckBox rememberCheckBox;
    @FXML
    private HBox autoLoginContainer;
    private JFXCheckBox autoLoginCheckBox;
    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private VBox loginProgressContainer;

    @FXML
    private VBox qrLoginBox;
    @FXML
    private ImageView qrCodeImageView;
    @FXML
    private HBox qrCodeShadeBox;
    @FXML
    private Label qrLoginPromptLabel;
    @FXML
    private Hyperlink qrLoginPromptLink;
    @FXML
    private HBox qrLoginReturnContainer;

    private JFXComboBox<Locale> languageComboBox;
    private FontIcon passwordIcon;
    private FontIcon smsIcon;

    private JFXTextField usernameField;
    private JFXPasswordField passwordField;
    private JFXTextField verificationCodeField;
    private JFXButton verificationCodeRefreshButton;
    private JFXButton loginButton;
    private FontIcon qrCodeIcon;
    private Label loginProgressPromptLabel;
    private JFXProgressBar loginProgressBar;

    private JFXButton qrLoginReturnButton;
    private JFXButton qrCodeValidButton;
    private JFXButton qrCodeInvalidButton;

    /**
     * 二维码是否有效
     */
    private BooleanProperty qrCodeValidity = new SimpleBooleanProperty(true);

    public BooleanProperty qrCodeValidityProperty() {
        return this.qrCodeValidity;
    }

    public void setQrCodeValidity(boolean validity) {
        this.qrCodeValidityProperty().set(validity);
    }

    public boolean getQrCodeValidity() {
        return this.qrCodeValidityProperty().get();
    }

    private final SMSResendCountDownService smsResendCountDownService = new SMSResendCountDownService(60);

    private SystemTrayTaskManager systemTrayTaskManager = new SystemTrayTaskManager();

    public void initialize() {
        flyleafTitleLabel.sceneProperty().addListener((observableScene, oldScene, newScene) -> {
            if (oldScene == null && newScene != null) {
                newScene.windowProperty().addListener((observableWindow, oldWindow, newWindow) -> {
                    if (oldWindow == null && newWindow != null) {
                        // 设置托盘图标
                        SystemTrayManager.initSystemTray((Stage) newWindow);
                        SystemTrayManager.showNotLogged();
                        SystemTrayManager.setOnExitAction(() -> {
                            // 从托盘图标执行退出逻辑
                            // 关闭
                        });
                        systemTrayTaskManager.initListeners();

                        newWindow.setOnCloseRequest(event -> SystemTrayManager.exit());
                    }
                });
            }
        });

        flyleafTitleLabel.textProperty().bind(MainApp.getLanguageBinding("title"));
        // 测试一下托盘图标任务窗口的高度变化
        flyleafTitleLabel.setOnMouseReleased(event -> systemTrayTaskManager.pushMessage(new SystemTrayMessageModel()));

        alertTestButton.setOnAction(event -> {
            JFXAlert<Stage> alert = new JFXAlert<>(alertTestButton.getScene().getWindow());
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setOverlayClose(false);
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("对话框标题"));
            layout.setBody(new Label("内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容"));
            JFXButton closeButton = new JFXButton("确认");
            closeButton.setButtonType(JFXButton.ButtonType.FLAT);
            closeButton.setTextFill(Paint.valueOf("#03A9F4"));
            closeButton.setOnAction(e -> alert.hideWithAnimation());
            layout.setActions(closeButton);
            alert.setContent(layout);
            alert.show();
        });

        dialogTestButton.setOnAction(event -> {
            JFXDialog dialog = new JFXDialog();
            JFXDialogLayout layout = new JFXDialogLayout();
            layout.setHeading(new Label("对话框标题"));
            layout.setBody(new Label("内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容内容"));
            JFXButton closeButton = new JFXButton("确认");
            closeButton.setButtonType(JFXButton.ButtonType.FLAT);
            closeButton.setTextFill(Paint.valueOf("#03A9F4"));
            closeButton.setOnAction(e -> dialog.close());
            layout.setActions(closeButton);
            dialog.setContent(layout);
            dialog.setTransitionType(JFXDialog.DialogTransition.BOTTOM);
            dialog.show(containerPane);
        });

        JFXSnackbar snackbar = new JFXSnackbar(containerPane);
        snackbar.setPrefWidth(200);
        snackbarTestButton.setOnAction(event -> {
//            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Toast Message")));
//            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Toast Message", "关闭", action -> snackbar.close()), Duration.INDEFINITE, null));
            snackbar.fireEvent(new JFXSnackbar.SnackbarEvent(new JFXSnackbarLayout("Toast Message Toast Message Toast Message", "关闭", action -> snackbar.close()), Duration.millis(3000), null));
        });

        JFXTextArea textArea = new JFXTextArea();
        textArea.setLabelFloat(true);
        textArea.setPromptText("提示文字");
        textArea.setMaxWidth(300);
        textArea.setMaxHeight(100);
        testContainer.getChildren().add(textArea);

        JFXTabPane tabPane = new JFXTabPane();
        tabPane.setPrefHeight(100);
        for (int i = 0; i < 2; i++) {
            Tab tab = new Tab();
            tab.setText("text" + i);
            tab.setContent(new Label(String.valueOf(i)));
            tabPane.getTabs().add(tab);
        }
        testContainer.getChildren().add(tabPane);

        JFXChipView<String> chipView = new JFXChipView<>();
        chipView.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        chipView.getChips().addAll("123", "1231");
        chipView.getSuggestions().addAll("avc", "adfa");
        testContainer.getChildren().add(chipView);

        // 语言切换 JFXComboBox
        languageComboBox = new JFXComboBox<>();
        languageComboBox.getItems().addAll(MainApp.SUPPORT_LANGUAGE_LIST);
        languageComboBox.setValue(MainApp.getCurrentLocale());
        languageComboBox.setConverter(new LocaleStringConverter());
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) MainApp.setLanguage(newValue);
        });
        header.getChildren().add(languageComboBox);

        // 密码登录和短信登录切换
        passwordIcon = new FontIcon(FontAwesomeSolid.LOCK);
        JFXTooltip.install(passwordIcon, new JFXTooltip("密码登录", Pos.TOP_CENTER));
        smsIcon = new FontIcon(FontAwesomeSolid.SMS);
        JFXTooltip.install(smsIcon, new JFXTooltip("短信登录", Pos.TOP_CENTER));
        generaLoginTypeChooseContainer.getChildren().addAll(passwordIcon, smsIcon);

        // 用户名
        usernameField = new JFXTextField();
        usernameField.setFont(new Font(14));
        usernameField.setPrefWidth(240);
        usernameField.promptTextProperty().bind(MainApp.getLanguageBinding("login-username"));
        usernameField.setLabelFloat(true);
        RequiredFieldValidator usernameValidator = new RequiredFieldValidator();
        usernameValidator.messageProperty().bind(MainApp.getLanguageBinding("login-username-need"));
        FontIcon usernameWarnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        usernameWarnIcon.getStyleClass().add("error");
        usernameValidator.setIcon(usernameWarnIcon);
        usernameField.getValidators().add(usernameValidator);
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) usernameField.validate();
        });
        usernameContainer.getChildren().add(usernameField);

        // 密码
        passwordField = new JFXPasswordField();
        passwordField.setFont(new Font(14));
        passwordField.setPrefWidth(240);
        passwordField.promptTextProperty().bind(MainApp.getLanguageBinding("login-password"));
        passwordField.setLabelFloat(true);
        RequiredFieldValidator passwordValidator = new RequiredFieldValidator();
        passwordValidator.messageProperty().bind(MainApp.getLanguageBinding("login-password-need"));
        FontIcon passwordWarnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        passwordWarnIcon.getStyleClass().add("error");
        passwordValidator.setIcon(passwordWarnIcon);
        passwordField.getValidators().add(passwordValidator);
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) passwordField.validate();
        });
        passwordContainer.getChildren().add(passwordField);

        // 短信验证码
        verificationCodeField = new JFXTextField();
        verificationCodeField.setFont(new Font(14));
        verificationCodeField.setPrefWidth(150);
        verificationCodeField.promptTextProperty().bind(MainApp.getLanguageBinding("verification-code"));
        verificationCodeField.setLabelFloat(true);
        RequiredFieldValidator verificationCodeValidator = new RequiredFieldValidator();
        verificationCodeValidator.messageProperty().bind(MainApp.getLanguageBinding("verification-code-need"));
        FontIcon verificationCodeWarnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        verificationCodeWarnIcon.getStyleClass().add("error");
        verificationCodeValidator.setIcon(verificationCodeWarnIcon);
        verificationCodeField.getValidators().add(verificationCodeValidator);
        verificationCodeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) verificationCodeField.validate();
        });

        // 获取短信验证码
        verificationCodeRefreshButton = new JFXButton();
        verificationCodeRefreshButton.textProperty().bind(MainApp.getLanguageBinding("obtain-verification-code"));
        verificationCodeRefreshButton.setFont(new Font(13));
        verificationCodeRefreshButton.setPrefWidth(90);
        verificationCodeRefreshButton.setPrefHeight(32);
        verificationCodeRefreshButton.setTextFill(Color.WHITE);
        verificationCodeRefreshButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        verificationCodeRefreshButton.disableProperty().bind(usernameField.textProperty().isEmpty());

        verificationCodeContainer.getChildren().addAll(verificationCodeField, verificationCodeRefreshButton);

        // 登录按钮
        loginButton = new JFXButton();
        loginButton.textProperty().bind(MainApp.getLanguageBinding("login"));
        loginButton.setFont(new Font(16));
        loginButton.setPrefWidth(176.0);
        loginButton.setPrefHeight(32.0);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        loginButtonContainer.getChildren().add(loginButton);

        // 切换二维码登录
        qrCodeIcon = new FontIcon(FontAwesomeSolid.QRCODE);
        JFXTooltip.install(qrCodeIcon, new JFXTooltip(MainApp.getLanguageBinding("qr-login"), Pos.CENTER_RIGHT));
        qrCodeIconContainer.getChildren().add(qrCodeIcon);

        rememberCheckBox = new JFXCheckBox();
        rememberCheckBox.textProperty().bind(MainApp.getLanguageBinding("remember-password"));
        rememberContainer.getChildren().add(rememberCheckBox);
        autoLoginCheckBox = new JFXCheckBox();
        autoLoginCheckBox.textProperty().bind(MainApp.getLanguageBinding("auto-login"));
        autoLoginContainer.getChildren().add(autoLoginCheckBox);
        forgotPasswordLink.textProperty().bind(MainApp.getLanguageBinding("forgot-password-link"));
        registerLink.textProperty().bind(MainApp.getLanguageBinding("register-link"));

        // 登录进度文字提示
        loginProgressPromptLabel = new Label();
        loginProgressPromptLabel.setVisible(false);

        // 登录进度条
        loginProgressBar = new JFXProgressBar();
        loginProgressBar.getStyleClass().add("login-progress-bar");
        loginProgressBar.prefWidthProperty().bind(loginProgressContainer.widthProperty());
        loginProgressContainer.setSpacing(8.0);
        loginProgressContainer.getChildren().addAll(loginProgressPromptLabel, loginProgressBar);

        // 二维码
        Image qrCodeImage = QRCodeGenerator.toImage("你好，123");
        Image logoImage = new Image(MainApp.load("img/logo.png").toExternalForm());
        Image image = QRCodeGenerator.encodeQRCodeLogo(qrCodeImage, logoImage, 30);
        qrCodeImageView.setImage(image);

        qrCodeShadeBox.backgroundProperty().bind(new When(qrCodeValidityProperty().isEqualTo(new SimpleBooleanProperty(true))).then(new SimpleObjectProperty<Background>(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)))).otherwise(new SimpleObjectProperty<>(new Background(new BackgroundFill(Paint.valueOf("#FFFFFFE6"), CornerRadii.EMPTY, Insets.EMPTY)))));
        qrLoginPromptLabel.textProperty().bind(new When(qrCodeValidityProperty().isEqualTo(new SimpleBooleanProperty(true))).then("请使用").otherwise("二维码已失效，请点击"));
        qrLoginPromptLink.textProperty().bind(new When(qrCodeValidityProperty().isEqualTo(new SimpleBooleanProperty(true))).then("即时通讯App").otherwise("刷新"));
        qrLoginPromptLink.onActionProperty().bind(new When(qrCodeValidityProperty().isEqualTo(new SimpleBooleanProperty(true))).then(new SimpleObjectProperty<EventHandler<ActionEvent>>(event -> System.out.println("download"))).otherwise(new SimpleObjectProperty<>(event -> System.out.println("refresh"))));

        qrLoginReturnButton = new JFXButton("返回");
        qrLoginReturnButton.setPrefWidth(64.0);
        qrLoginReturnButton.setPrefHeight(28.0);
        qrLoginReturnButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        qrLoginReturnButton.setTextFill(Color.WHITE);
        qrCodeValidButton = new JFXButton("有效");
        qrCodeValidButton.setPrefWidth(64.0);
        qrCodeValidButton.setPrefHeight(28.0);
        qrCodeValidButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        qrCodeValidButton.setTextFill(Color.WHITE);
        qrCodeValidButton.setOnAction(event -> setQrCodeValidity(true));
        qrCodeInvalidButton = new JFXButton("无效");
        qrCodeInvalidButton.setPrefWidth(64.0);
        qrCodeInvalidButton.setPrefHeight(28.0);
        qrCodeInvalidButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        qrCodeInvalidButton.setTextFill(Color.WHITE);
        qrCodeInvalidButton.setOnAction(event -> setQrCodeValidity(false));
        qrLoginReturnContainer.setSpacing(8.0);
        qrLoginReturnContainer.getChildren().addAll(qrLoginReturnButton, qrCodeValidButton, qrCodeInvalidButton);

        loginTypeProperty().addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case PASSWORD: {
                    generalLoginBox.toFront();
                    passwordContainer.toFront();
                    break;
                }
                case SMS: {
                    generalLoginBox.toFront();
                    verificationCodeContainer.toFront();
                    break;
                }
                case QR: {
                    qrLoginBox.toFront();
                    break;
                }
            }
        });
        passwordIcon.setOnMouseReleased(event -> setLoginType(LoginType.PASSWORD));
        smsIcon.setOnMouseReleased(event -> setLoginType(LoginType.SMS));
        qrCodeIcon.setOnMouseReleased(event -> setLoginType(LoginType.QR));
        qrLoginReturnButton.setOnAction(event -> setLoginType(LoginType.SMS));

        verificationCodeRefreshButton.setOnAction(event -> {
            AppView<GraphValidateCodeController> graphValidateCodeControllerAppView = new AppView<>(GraphValidateCodeController.class);
            graphValidateCodeControllerAppView.getController().confirm(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    System.out.println(s);
                    // 省略验证此验证码的正确性
                    // 图形验证码验证通过后，服务端会发送短信验证码，开始倒计时
                    verificationCodeRefreshButton.disableProperty().unbind();
                    verificationCodeRefreshButton.setDisable(true);
                    smsResendCountDownService.setPeriod(Duration.seconds(1));
                    smsResendCountDownService.setRestartOnFailure(false);
                    verificationCodeRefreshButton.textProperty().bind(MainApp.getLanguageBinding("re-obtain-verification-code").concat(smsResendCountDownService.lastValueProperty().asString()));
                    smsResendCountDownService.valueProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue != null && newValue <= 0) smsResendCountDownService.cancel();
                    });
                    smsResendCountDownService.setOnCancelled(event1 -> {
                        smsResendCountDownService.reset();
                        smsResendCountDownService.setCount(60);
                        verificationCodeRefreshButton.textProperty().bind(MainApp.getLanguageBinding("obtain-verification-code"));
                        verificationCodeRefreshButton.disableProperty().bind(usernameField.textProperty().isEmpty());
                    });
                    smsResendCountDownService.start();
                }
            });
            graphValidateCodeControllerAppView.modalShow(verificationCodeRefreshButton.getScene().getWindow());
        });

        // 打开主页面
        loginButton.setOnAction(event -> {
            if (usernameField.validate()) {
                boolean execute = false;
                if (getLoginType() == LoginType.PASSWORD) {
                    if (passwordField.validate()) {
                        execute = true;
                    } else {
                        passwordField.requestFocus();
                    }
                } else {
                    if (verificationCodeField.validate()) {
                        execute = true;
                    } else {
                        verificationCodeField.requestFocus();
                    }
                }
                if (execute) {
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            updateProgress(0, 5);
                            for (int i = 0; i < 5; i++) {
                                Thread.sleep(1000);
                                updateProgress(i + 1, 5);
                            }
                            return null;
                        }
                    };
                    task.setOnSucceeded(e -> {
                        AppView<HomeController> homeView = new AppView<>(HomeController.class);
                        Stage stage = new Stage();
                        Scene scene;
                        if (SystemUtil.platformIsWindows()) {
                            JFXDecorator decorator = new JFXDecorator(stage, homeView.getRootNode());
                            decorator.setCustomMaximize(true);
                            decorator.setGraphic(new SVGGlyph(""));
                            decorator.titleProperty().bind(MainApp.getLanguageBinding("title"));
                            scene = new Scene(decorator, 800, 600);
                        } else {
                            scene = new Scene(homeView.getRootNode(), 800, 600);
                        }
                        homeView.setScene(scene).setStage(stage).show();

                        // 关闭登录窗口
                        ((Stage) loginButton.getScene().getWindow()).close();
                    });
                    loginProgressPromptLabel.visibleProperty().bind(task.runningProperty());
                    loginProgressPromptLabel.textProperty().bind(new SimpleStringProperty("数据加载中 ").concat(task.progressProperty().multiply(100).asString("%.1f").concat("%")));
                    loginProgressBar.progressProperty().bind(task.progressProperty());
                    Thread thread = new Thread(task);
                    thread.setDaemon(true);
                    thread.start();
                }
            } else {
                usernameField.requestFocus();
            }
        });

        setLoginType(LoginType.SMS);
    }

    /**
     * 验证码短信重发倒计时
     */
    static class SMSResendCountDownService extends ScheduledService<Integer> {

        private Integer count;

        public SMSResendCountDownService(Integer count) {
            this.count = count;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        @Override
        protected Task<Integer> createTask() {
            return new Task<>() {
                @Override
                protected Integer call() {
                    return count--;
                }
            };
        }
    }

    /**
     * 用户登录类型，监听此属性来控制登录页面切换
     */
    private final ObjectProperty<LoginType> loginType = new SimpleObjectProperty<>();

    public ObjectProperty<LoginType> loginTypeProperty() {
        return this.loginType;
    }

    public void setLoginType(LoginType loginType) {
        this.loginTypeProperty().set(loginType);
    }

    public LoginType getLoginType() {
        return this.loginTypeProperty().get();
    }

    /**
     * 登陆方式枚举
     */
    enum LoginType {

        /**
         * 密码登录
         */
        PASSWORD,

        /**
         * 短信登录
         */
        SMS,

        /**
         * 二维码登录
         */
        QR
    }
}
