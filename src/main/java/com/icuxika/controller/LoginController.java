package com.icuxika.controller;

import com.icuxika.AppView;
import com.icuxika.LanguageListCell;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.util.SystemUtil;
import com.jfoenix.control.*;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Locale;

@AppFXML(fxml = "login.fxml")
public class LoginController {

    @FXML
    private Label flyleafTitleLabel;

    @FXML
    private ComboBox<Locale> languageComboBox;
    @FXML
    private Button homeButton;

    @FXML
    private Label loginTitleLabel;
    @FXML
    private HBox usernameContainer;
    @FXML
    private HBox passwordContainer;
    @FXML
    private Hyperlink registerLink;
    @FXML
    private Hyperlink forgotPasswordLink;
    @FXML
    private HBox loginButtonContainer;
    @FXML
    private HBox progressContainer;

    public void initialize() {
        flyleafTitleLabel.textProperty().bind(MainApp.getLanguageBinding("title"));

        languageComboBox.getItems().addAll(MainApp.SUPPORT_LANGUAGE_LIST);
        languageComboBox.setValue(Locale.SIMPLIFIED_CHINESE);
        languageComboBox.setCellFactory(param -> new LanguageListCell());
        languageComboBox.setButtonCell(new LanguageListCell());
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                MainApp.setLanguage(newValue);
            }
        });
        // 打开主页面
        homeButton.setOnAction(event -> {
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
            scene.getStylesheets().addAll(
                    MainApp.load("css/home.css").toExternalForm()
            );
            stage.setScene(scene);
            stage.show();

            Stage currentStage = (Stage) homeButton.getScene().getWindow();
            currentStage.close();
        });

        loginTitleLabel.textProperty().bind(MainApp.getLanguageBinding("login"));

        JFXTextField usernameField = new JFXTextField();
        usernameField.setPrefWidth(240);
        usernameField.promptTextProperty().bind(MainApp.getLanguageBinding("login-username"));
        usernameField.setLabelFloat(true);
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.messageProperty().bind(MainApp.getLanguageBinding("login-username-need"));
        FontIcon warnIcon1 = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        warnIcon1.getStyleClass().add("error");
        validator.setIcon(warnIcon1);
        usernameField.getValidators().add(validator);
        usernameField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                usernameField.validate();
            }
        });
        usernameContainer.getChildren().add(usernameField);

        JFXPasswordField passwordField = new JFXPasswordField();
        passwordField.setPrefWidth(240);
        passwordField.promptTextProperty().bind(MainApp.getLanguageBinding("login-password"));
        passwordField.setLabelFloat(true);
        validator = new RequiredFieldValidator();
        validator.messageProperty().bind(MainApp.getLanguageBinding("login-password-need"));
        FontIcon warnIcon2 = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        warnIcon2.getStyleClass().add("error");
        validator.setIcon(warnIcon2);
        passwordField.getValidators().add(validator);
        passwordField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                passwordField.validate();
            }
        });
        passwordContainer.getChildren().add(passwordField);

        registerLink.textProperty().bind(MainApp.getLanguageBinding("register-link"));
        forgotPasswordLink.textProperty().bind(MainApp.getLanguageBinding("forgot-password-link"));

        JFXButton loginButton = new JFXButton(MainApp.getLanguageBinding("login"));
        loginButton.setPrefWidth(120);
        loginButton.setTextFill(Color.WHITE);
        loginButton.setBackground(new Background(new BackgroundFill(Paint.valueOf("#8d7ac7"), new CornerRadii(4), Insets.EMPTY)));
        loginButtonContainer.getChildren().add(loginButton);

        JFXProgressBar jfxProgressBar = new JFXProgressBar();
        jfxProgressBar.getStyleClass().add("login-progress-bar");
        progressContainer.getChildren().add(jfxProgressBar);

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Task<Void> task = new Task<Void>() {
                    @Override
                    protected Void call() throws Exception {
                        for (int i = 0; i < 10; i++) {
                            Thread.sleep(1000);
                            updateProgress(i + 1, 10);
                        }
                        return null;
                    }
                };
                jfxProgressBar.progressProperty().bind(task.progressProperty());
                Thread thread = new Thread(task);
                thread.setDaemon(true);
                thread.start();
            }
        });
    }
}
