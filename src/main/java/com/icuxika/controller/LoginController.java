package com.icuxika.controller;

import com.icuxika.LanguageListCell;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.jfoenix.control.JFXButton;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

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
    private Button loginButton;

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
    }

    public Button getLoginButton() {
        return loginButton;
    }
}
