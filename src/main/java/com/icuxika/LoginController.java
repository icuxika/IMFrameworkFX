package com.icuxika;

import com.icuxika.annotation.AppFXML;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;

import java.util.Locale;

@AppFXML(fxml = "login.fxml")
public class LoginController {

    @FXML
    private Label titleLabel;

    @FXML
    private ComboBox<Locale> languageComboBox;

    @FXML
    BorderPane containerPane;

    @FXML
    Button loginButton;

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
    }
}
