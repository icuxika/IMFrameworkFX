package com.icuxika.controller.login;

import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXTextField;
import com.jfoenix.control.JFXTooltip;
import com.jfoenix.validation.RequiredFieldValidator;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.ByteArrayInputStream;
import java.util.Base64;
import java.util.function.Consumer;

@AppFXML(fxml = "login/graphValidateCode.fxml", stylesheets = "css/login/graph-validate-code.css")
public class GraphValidateCodeController {

    @FXML
    private Label titleLabel;
    @FXML
    private ImageView verificationCodeImageView;
    @FXML
    private VBox refreshButtonContainer;
    @FXML
    private HBox verificationCodeFieldContainer;
    @FXML
    private HBox operateBox;

    private FontIcon refreshIcon;
    private JFXTextField verificationCodeField;
    private JFXButton confirmButton;
    private JFXButton cancelButton;

    public void initialize() {
        titleLabel.textProperty().bind(MainApp.getLanguageBinding("verification-code"));

        String imageBase64String = "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";
        verificationCodeImageView.setImage(new Image(new ByteArrayInputStream(Base64.getDecoder().decode(imageBase64String))));

        refreshIcon = new FontIcon(FontAwesomeSolid.REDO);
        JFXTooltip.install(refreshIcon, new JFXTooltip(MainApp.getLanguageBinding("re-obtain-verification-code")));
        refreshButtonContainer.getChildren().add(refreshIcon);

        verificationCodeField = new JFXTextField("GEXQ");
        verificationCodeField.setFont(new Font(14));
        verificationCodeField.setPrefWidth(240);
        verificationCodeField.promptTextProperty().bind(MainApp.getLanguageBinding("verification-code"));
        verificationCodeField.setLabelFloat(false);
        verificationCodeField.setAlignment(Pos.CENTER);
        RequiredFieldValidator validator = new RequiredFieldValidator();
        validator.messageProperty().bind(MainApp.getLanguageBinding("verification-code-need"));
        FontIcon warnIcon = new FontIcon(FontAwesomeSolid.EXCLAMATION_CIRCLE);
        warnIcon.getStyleClass().add("error");
        validator.setIcon(warnIcon);
        verificationCodeField.getValidators().add(validator);
        verificationCodeField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) verificationCodeField.validate();
        });
        verificationCodeFieldContainer.getChildren().add(verificationCodeField);

        confirmButton = new JFXButton();
        confirmButton.textProperty().bind(MainApp.getLanguageBinding("confirm"));
        confirmButton.setFont(new Font(14));
        confirmButton.setButtonType(JFXButton.ButtonType.RAISED);
        confirmButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        confirmButton.setTextFill(Color.WHITE);
        confirmButton.setPrefWidth(84);
        confirmButton.setPrefHeight(32);
        cancelButton = new JFXButton();
        cancelButton.textProperty().bind(MainApp.getLanguageBinding("cancel"));
        cancelButton.setFont(new Font(14));
        cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        cancelButton.setPrefWidth(84);
        cancelButton.setPrefHeight(32);
        operateBox.setSpacing(8);
        operateBox.getChildren().addAll(confirmButton, cancelButton);

        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
    }

    public void confirm(Consumer<String> consumer) {
        confirmButton.setOnAction(event -> {
            if (verificationCodeField.validate()) {
                consumer.accept("GEXQ");
                ((Stage) confirmButton.getScene().getWindow()).close();
            }
        });
    }
}
