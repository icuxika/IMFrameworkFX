package com.icuxika.control.message;

import com.jfoenix.control.JFXSpinner;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.net.URL;

public class ImageMessageNode extends MessageNode {

    private static final double IMAGE_MAX_WIDTH = 240.0;

    private StackPane imageContainer;
    private ImageView messageImageView;

    private StackPane progressContainer;
    private JFXSpinner spinner;

    /**
     * 目前默认设置为，登录用户不显示名称，单聊会话对方不显示名称，群聊显示其他人名称
     */
    public ImageMessageNode(boolean showLeft, boolean showName) {
        super(showLeft, showName);
    }

    @Override
    protected void initialize() {
        imageContainer = new StackPane();
        messageImageView = new ImageView();

        if (showLeft) {
            setLeftAnchor(imageContainer, 60.0);
            if (showName) {
                setTopAnchor(imageContainer, 30.0);
            } else {
                setTopAnchor(imageContainer, 12.0);
            }
        } else {
            setRightAnchor(imageContainer, 60.0);
            setTopAnchor(imageContainer, 12.0);
        }

        progressContainer = new StackPane();
        progressContainer.setPrefSize(48, 48);
        spinner = new JFXSpinner();
        progressContainer.getChildren().add(spinner);

        imageContainer.getChildren().addAll(messageImageView, progressContainer);
        getChildren().add(imageContainer);
    }

    public void initImage(URL url) {
        Image image = new Image(url.toString(), true);
        spinner.progressProperty().bind(image.progressProperty());
        messageImageView.setImage(image);
        image.progressProperty().addListener((observable, oldValue, newValue) -> {
            if ((double) newValue == 1.0) {
                imageContainer.getChildren().remove(progressContainer);
                if (image.getWidth() > IMAGE_MAX_WIDTH) messageImageView.setFitWidth(IMAGE_MAX_WIDTH);
                messageImageView.setPreserveRatio(true);

                imageContainer.setBorder(new Border(new BorderStroke(
                        Color.rgb(181, 181, 181),
                        Color.rgb(181, 181, 181),
                        Color.rgb(181, 181, 181),
                        Color.rgb(181, 181, 181),
                        BorderStrokeStyle.SOLID,
                        null, null, null,
                        new CornerRadii(4),
                        new BorderWidths(1, 1, 1, 1),
                        null
                )));

                DropShadow dropShadow = new DropShadow();
                dropShadow.setBlurType(BlurType.GAUSSIAN);
                dropShadow.setColor(Color.rgb(0, 0, 0, 0.26));
                dropShadow.setHeight(1.0);
                dropShadow.setOffsetX(2.0);
                dropShadow.setOffsetY(2.0);
                dropShadow.setSpread(0.12);
                imageContainer.setEffect(dropShadow);
            }
        });
    }
}
