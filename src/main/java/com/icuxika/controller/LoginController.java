package com.icuxika.controller;

import com.icuxika.AppView;
import com.icuxika.LanguageListCell;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.control.SelectableLabel;
import com.icuxika.control.message.TextMessageNode;
import com.jfoenix.control.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;

@AppFXML(fxml = "login.fxml")
public class LoginController {

    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<Locale> languageComboBox;

    @FXML
    private BorderPane containerPane;

    @FXML
    private ListView<AnchorPane> messageListView;

    public void initialize() {
        titleLabel.textProperty().bind(MainApp.getLanguageBinding("title"));

        languageComboBox.getItems().addAll(MainApp.SUPPORT_LANGUAGE_LIST);
        languageComboBox.setValue(Locale.SIMPLIFIED_CHINESE);
        languageComboBox.setCellFactory(param -> new LanguageListCell());
        languageComboBox.setButtonCell(new LanguageListCell());
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                MainApp.setLanguage(newValue);
            }
        });

        JFXButton raisedButton1 = new JFXButton(MainApp.getLanguageBinding("title"));
        raisedButton1.setButtonType(JFXButton.ButtonType.RAISED);
        raisedButton1.setBackground(new Background(new BackgroundFill(Paint.valueOf("#5264AE"), new CornerRadii(4), Insets.EMPTY)));
        raisedButton1.setTextFill(Paint.valueOf("#FFFFFF"));
        raisedButton1.setPrefHeight(40);
        raisedButton1.setFont(Font.font(14));

        JFXButton raisedButton2 = new JFXButton(MainApp.getLanguageBinding("title"));
        raisedButton2.setButtonType(JFXButton.ButtonType.RAISED);
        raisedButton2.setPrefHeight(40);
        raisedButton2.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFF"), new CornerRadii(4), Insets.EMPTY)));
        raisedButton2.setFont(Font.font(14));

        JFXButton flatButton = new JFXButton(MainApp.getLanguageBinding("title"));
        flatButton.setButtonType(JFXButton.ButtonType.FLAT);
        flatButton.setPrefHeight(40);
        flatButton.setTextFill(Paint.valueOf("#5264AE"));
        flatButton.setFont(Font.font(14));

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(raisedButton1, raisedButton2, flatButton);

        containerPane.setBottom(buttonBox);

        SelectableLabel label = new SelectableLabel("你好你是谁你是谁似 懂非懂 舒服收到了 饭 睡觉 啊了你好你是谁你是谁似懂非懂舒服收到了饭睡觉啊了你好你是谁你是谁似懂非懂舒服收到了饭睡觉啊了");
        label.setWrapText(true);
        label.setMaxWidth(200);
        containerPane.setLeft(label);

        TextMessageNode reduceTextMessageNode = TextMessageNode.left(false);
        reduceTextMessageNode.setMessageText("你是世界的风景脸上的肌肤 收到了咖啡就困了撒减肥了失联飞机拉萨到家 是老骥伏枥撒减肥是连接方式了解卡");
        reduceTextMessageNode.setAvatarImageView(MainApp.load("img/logo.png").toExternalForm());
        messageListView.getItems().add(reduceTextMessageNode);

        TextMessageNode complexTextMessageNode = TextMessageNode.left(true);
        complexTextMessageNode.setNameText("李三1231321");
        complexTextMessageNode.setNameText(MainApp.getLanguageBinding("title"));
        complexTextMessageNode.setMessageText("就放开了撒娇加夫里什大家里电视机放了假的撒了将脸上的肌肤来说打卡了可节省空间方式打开了几分可乐洒进来了大家福师大");
        complexTextMessageNode.setAvatarImageView(MainApp.load("img/logo.png").toExternalForm());
        messageListView.getItems().add(complexTextMessageNode);

        TextMessageNode rightTextMessageNode = TextMessageNode.right();
        rightTextMessageNode.setMessageText("收到就放开了看撒开几分当时就解放了撒娇  使肌肤来说就啊法拉利经典刷卡即可但是看了索朗多吉咖啡连锁店健康啊 是否短发 第三方");
        rightTextMessageNode.setAvatarImageView(MainApp.load("img/logo.png").toExternalForm());
        rightTextMessageNode.putMenuItem("测试", () -> System.out.println("123"));
        messageListView.getItems().add(rightTextMessageNode);

        JFXTooltip jfxTooltip = new JFXTooltip(MainApp.getLanguageBinding("title"));
        JFXTooltip.install(flatButton, jfxTooltip);

        JFXTextField jfxTextField = new JFXTextField();
        jfxTextField.setPromptText("请输入用户名");
        jfxTextField.setLabelFloat(true);
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.setMessage("需要输入用户名");
        FontIcon warnIcon1 = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        warnIcon1.getStyleClass().add("error");
        validator.setIcon(warnIcon1);
        jfxTextField.getValidators().add(validator);
        jfxTextField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                jfxTextField.validate();
            }
        });

        JFXPasswordField jfxPasswordField = new JFXPasswordField();
        jfxPasswordField.setPromptText("请输入密码");
        jfxPasswordField.setLabelFloat(true);
        validator = new RequiredFieldValidator();
        validator.setMessage("需要输入密码");
        FontIcon warnIcon2 = new FontIcon(FontAwesomeSolid.EXCLAMATION_TRIANGLE);
        warnIcon2.getStyleClass().add("error");
        validator.setIcon(warnIcon2);
        jfxPasswordField.getValidators().add(validator);
        jfxPasswordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                jfxPasswordField.validate();
            }
        });

        VBox fieldBox = new VBox();
        fieldBox.setSpacing(40);
        fieldBox.setPrefHeight(200);
        fieldBox.setAlignment(Pos.CENTER);

        fieldBox.getChildren().addAll(jfxTextField, jfxPasswordField);

        VBox bottomBox = new VBox();
        bottomBox.setPrefHeight(300);
        bottomBox.setAlignment(Pos.CENTER);
        bottomBox.setSpacing(40);
        bottomBox.getChildren().addAll(buttonBox, fieldBox);

        containerPane.setBottom(bottomBox);

        flatButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                AppView<HomeController> appView = new AppView<>(HomeController.class);
                Stage stage = new Stage();
                JFXDecorator decorator = new JFXDecorator(stage, appView.getRootNode());
                decorator.setCustomMaximize(true);
                decorator.setGraphic(new SVGGlyph(""));
                decorator.titleProperty().bind(MainApp.getLanguageBinding("title"));
                Scene scene = new Scene(decorator, 800, 600);
                scene.getStylesheets().addAll(
                        MainApp.load("css/home.css").toExternalForm()
                );
                stage.setScene(scene);
                stage.show();
            }
        });
    }
}
