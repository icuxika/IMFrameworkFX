package com.icuxika.controller.home.addressBook.detail;

import com.icuxika.annotation.AppFXML;
import com.icuxika.control.SelectableLabel;
import com.jfoenix.control.JFXButton;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

@AppFXML(fxml = "home/addressBook/detail/friendDetail.fxml", stylesheets = "css/home/friend-detail.css")
public class FriendDetailController {

    @FXML
    private StackPane header;

    @FXML
    private ImageView avatarImageView;

    @FXML
    private HBox nameContainer;

    @FXML
    private ScrollPane signatureContainer;

    @FXML
    private ScrollPane detailPane;

    @FXML
    private HBox operateContainer;

    private SelectableLabel nameText;

    private SelectableLabel signatureText;

    private VBox detailContainer;

    private JFXButton sendMsgButton;

    public void initialize() {
        header.setBorder(new Border(new BorderStroke(null, null, Paint.valueOf("#eaeaea"), null, BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(0, 0, 2, 0), null)));

        nameText = new SelectableLabel();
        nameText.setFont(Font.font(16));
        nameContainer.getChildren().add(nameText);

        signatureText = new SelectableLabel();
        signatureText.setFont(Font.font(13));
        signatureText.setTextFill(Color.rgb(181, 181, 181));
        signatureContainer.setContent(signatureText);

        // 根据拖拽位置更新ScrollPane进度
        signatureText.setOnMouseDragged(event -> {
            double mouseX = event.getScreenX();
            Bounds bounds = signatureContainer.localToScreen(signatureContainer.getLayoutBounds());
            double minX = bounds.getMinX();
            double maxX = bounds.getMaxX();
            if (Math.abs(minX - mouseX) < 10) {
                if (signatureContainer.getHvalue() != 0.0) {
                    signatureContainer.setHvalue(signatureContainer.getHvalue() - 0.1);
                }
            }
            if (Math.abs(maxX - mouseX) < 10) {
                if (signatureContainer.getHvalue() != 1.0) {
                    signatureContainer.setHvalue(signatureContainer.getHvalue() + 0.1);
                }
            }
        });

        detailContainer = new VBox();
        detailContainer.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        detailContainer.prefWidthProperty().bind(detailPane.widthProperty());
        detailContainer.setPadding(new Insets(4, 12, 4, 12));
        detailPane.setContent(detailContainer);
        detailPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        sendMsgButton = new JFXButton("发送消息");
        sendMsgButton.setButtonType(JFXButton.ButtonType.RAISED);
        sendMsgButton.setPrefSize(200, 44);
        sendMsgButton.setFont(Font.font(14));
        sendMsgButton.setTextFill(Color.WHITE);
        sendMsgButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));

        operateContainer.getChildren().add(sendMsgButton);

        // 测试数据
        detailContainer.getChildren().add(createDetailElement("用 户 名", new SelectableLabel("12345678910")));
        detailContainer.getChildren().add(createDetailElement("性 别", new SelectableLabel("男")));
        detailContainer.getChildren().add(createDetailElement("兴 趣", new SelectableLabel("睡觉")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));
        detailContainer.getChildren().add(createDetailElement("生 日", new SelectableLabel("2000年1月1号")));

    }

    public void setAvatar(ObjectProperty<Image> avatar) {
        avatarImageView.imageProperty().bind(avatar);
    }

    public void setName(StringProperty name) {
        nameText.textProperty().bind(name);
    }

    public void setSignature(StringProperty signature) {
        signatureText.textProperty().bind(signature);
    }

    private HBox createDetailElement(String text, Node node) {
        HBox leftContainer = new HBox();
        leftContainer.setPrefSize(84, 32);
        leftContainer.setAlignment(Pos.CENTER_LEFT);
        SelectableLabel label = new SelectableLabel(text);
        label.setFont(Font.font(13));
        label.setTextFill(Color.rgb(181, 181, 181));
        leftContainer.getChildren().add(label);
        HBox rightContainer = new HBox();
        rightContainer.setAlignment(Pos.CENTER_LEFT);
        rightContainer.getChildren().add(node);
        HBox.setHgrow(rightContainer, Priority.ALWAYS);
        return new HBox(leftContainer, rightContainer);
    }
}
