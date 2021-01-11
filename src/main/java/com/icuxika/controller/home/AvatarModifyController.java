package com.icuxika.controller.home;

import com.icuxika.annotation.AppFXML;
import com.icuxika.control.ResizableRectangle;
import com.icuxika.framework.UserData;
import com.jfoenix.control.JFXButton;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

/**
 * 头像修改
 */
@AppFXML(fxml = "home/avatarModify.fxml")
public class AvatarModifyController {

    @FXML
    private HBox header;
    @FXML
    private HBox mainImageContainer;
    @FXML
    private HBox footer;

    private JFXButton openImageButton;
    private JFXButton cropImageButton;
    private JFXButton saveButton;
    private JFXButton cancelButton;

    private Group selectionGroup;
    private ImageView mainImageView;
    private Image mainImage;

    /**
     * 是否选中了裁切区域
     */
    private final BooleanProperty isAreaSelected = new SimpleBooleanProperty(false);

    public BooleanProperty isAreaSelectedProperty() {
        return this.isAreaSelected;
    }

    public boolean getIsAreaSelected() {
        return isAreaSelectedProperty().get();
    }

    public void setIsAreaSelected(boolean value) {
        isAreaSelectedProperty().set(value);
    }

    private WritableImage cachedImage;

    /**
     * 点击裁切图片后设置为true，重新选择文件或选择区域设置为false
     */
    private final BooleanProperty isImageCropped = new SimpleBooleanProperty(false);

    public BooleanProperty isImageCroppedProperty() {
        return this.isImageCropped;
    }

    public boolean getIsImageCropped() {
        return isImageCroppedProperty().get();
    }

    public void setIsImageCropped(boolean value) {
        isImageCroppedProperty().set(value);
    }

    private AreaSelection areaSelection;

    public void initialize() {
        openImageButton = new JFXButton("打开图片");
        openImageButton.setButtonType(JFXButton.ButtonType.RAISED);
        openImageButton.setTextFill(Color.WHITE);
        openImageButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        openImageButton.setPrefWidth(84);
        openImageButton.setPrefHeight(32);
        openImageButton.setOnAction(event -> openImage());

        cropImageButton = new JFXButton("裁切图片");
        cropImageButton.setButtonType(JFXButton.ButtonType.RAISED);
        cropImageButton.setTextFill(Color.WHITE);
        cropImageButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        cropImageButton.setPrefWidth(84);
        cropImageButton.setPrefHeight(32);
        cropImageButton.setOnAction(event -> cropImage(areaSelection.getSelectionRectangle().getBoundsInLocal(), mainImageView));
        cropImageButton.disableProperty().bind(isAreaSelectedProperty().isEqualTo(new SimpleBooleanProperty(false)));
        HBox.setMargin(openImageButton, new Insets(0, 0, 0, 8));
        header.setSpacing(8);
        header.getChildren().addAll(openImageButton, cropImageButton);

        saveButton = new JFXButton("保存");
        saveButton.setButtonType(JFXButton.ButtonType.RAISED);
        saveButton.setTextFill(Color.WHITE);
        saveButton.setBackground(new Background(new BackgroundFill(Color.DODGERBLUE, new CornerRadii(4), Insets.EMPTY)));
        saveButton.setPrefWidth(84);
        saveButton.setPrefHeight(32);
        saveButton.setOnAction(event -> saveCroppedImage(saveButton.getScene().getWindow()));
        saveButton.disableProperty().bind(isImageCroppedProperty().isEqualTo(new SimpleBooleanProperty(false)));

        cancelButton = new JFXButton("取消");
        cancelButton.setButtonType(JFXButton.ButtonType.RAISED);
        cancelButton.setPrefWidth(84);
        cancelButton.setPrefHeight(32);
        cancelButton.setOnAction(event -> ((Stage) cancelButton.getScene().getWindow()).close());
        HBox.setMargin(cancelButton, new Insets(0, 8, 0, 0));
        footer.setSpacing(8);
        footer.getChildren().addAll(saveButton, cancelButton);

        selectionGroup = new Group();
        mainImageView = new ImageView();
        selectionGroup.getChildren().add(mainImageView);
        mainImageContainer.getChildren().add(selectionGroup);

        // 初始化选择框
        areaSelection = new AreaSelection(selectionGroup);
    }

    /**
     * 打开图片
     */
    private void openImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("选择图片");
        File selectedFile = fileChooser.showOpenDialog(mainImageView.getScene().getWindow());
        if (selectedFile != null) {
            clearSelection(selectionGroup);
            mainImage = convertFileToImage(selectedFile);
            // 对于长款比例相差较大的图片显示很奇怪
            mainImageView.setFitHeight(300);
            mainImageView.setPreserveRatio(true);
            mainImageView.setSmooth(true);
            mainImageView.setImage(mainImage);
            // 是裁切区域的计算有一个初始数值
            mainImageView.setFitWidth(mainImageView.prefWidth(-1));
        }
    }

    /**
     * 裁切图片
     *
     * @param bounds    选中区域
     * @param imageView 图片
     */
    private void cropImage(Bounds bounds, ImageView imageView) {
        int width = (int) bounds.getWidth();
        int height = (int) bounds.getHeight();

        SnapshotParameters parameters = new SnapshotParameters();
        parameters.setFill(Color.TRANSPARENT);
        parameters.setViewport(new Rectangle2D(bounds.getMinX(), bounds.getMinY(), width, height));

        WritableImage wi = new WritableImage(width, height);
        Image croppedImage = imageView.snapshot(parameters, wi);
        // 更新头像
        UserData.setAvatar(croppedImage);
        // 缓存用于保存
        cachedImage = wi;
        setIsImageCropped(true);
    }

    /**
     * 保存被裁切的图片
     */
    private void saveCroppedImage(Window window) {
        if (cachedImage != null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("保存图片");
            fileChooser.setInitialFileName("avatar.png");

            File file = fileChooser.showSaveDialog(window);
            if (file == null)
                return;
            BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(cachedImage, null);
            BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(),
                    bufImageARGB.getHeight(), BufferedImage.BITMASK);

            Graphics2D graphics = bufImageRGB.createGraphics();
            graphics.drawImage(bufImageARGB, 0, 0, null);

            try {
                ImageIO.write(bufImageRGB, "png", file);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                graphics.dispose();
                System.gc();
            }
        }
    }

    /**
     * 清除选中
     */
    private void clearSelection(Group group) {
        setIsAreaSelected(false);
        setIsImageCropped(false);
        group.getChildren().remove(1, group.getChildren().size());
    }

    /**
     * 文件转为图片
     *
     * @param imageFile 文件
     * @return 图片
     */
    private Image convertFileToImage(File imageFile) {
        Image image = null;
        try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
            image = new Image(fileInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 裁切区域
     */
    private class AreaSelection {

        private Group group;

        private ResizableRectangle selectionRectangle = null;
        private double rectangleStartX;
        private double rectangleStartY;
        private final Paint darkAreaColor = Color.color(0, 0, 0, 0.5);

        public AreaSelection(Group group) {
            this.group = group;

            this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
            this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
            this.group.getChildren().get(0).addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
        }

        public ResizableRectangle getSelectionRectangle() {
            return selectionRectangle;
        }

        EventHandler<MouseEvent> onMousePressedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            rectangleStartX = event.getX();
            rectangleStartY = event.getY();

            clearSelection(group);

            selectionRectangle = new ResizableRectangle(rectangleStartX, rectangleStartY, 0, 0, group, mainImageView.prefWidth(-1), mainImageView.prefHeight(-1));

            darkenOutsideRectangle(selectionRectangle);

        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            double offsetX = event.getX() - rectangleStartX;
            double offsetY = event.getY() - rectangleStartY;

            if (offsetX > 0) {
                if (event.getX() > mainImageView.prefWidth(-1))
                    selectionRectangle.setWidth(mainImageView.prefWidth(-1) - rectangleStartX);
                else
                    selectionRectangle.setWidth(offsetX);
            } else {
                if (event.getX() < 0)
                    selectionRectangle.setX(0);
                else
                    selectionRectangle.setX(event.getX());
                selectionRectangle.setWidth(rectangleStartX - selectionRectangle.getX());
            }

            if (offsetY > 0) {
                if (event.getY() > mainImageView.prefHeight(-1))
                    selectionRectangle.setHeight(mainImageView.prefHeight(-1) - rectangleStartY);
                else
                    selectionRectangle.setHeight(offsetY);
            } else {
                if (event.getY() < 0)
                    selectionRectangle.setY(0);
                else
                    selectionRectangle.setY(event.getY());
                selectionRectangle.setHeight(rectangleStartY - selectionRectangle.getY());
            }
        };

        EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> {
            if (selectionRectangle != null) {
                setIsAreaSelected(true);
                setIsImageCropped(false);
            }
        };

        /**
         * 裁切区域之外的图片加上一层遮罩
         *
         * @param rectangle 裁切区域
         */
        private void darkenOutsideRectangle(Rectangle rectangle) {
            Rectangle darkAreaTop = new Rectangle(0, 0, darkAreaColor);
            Rectangle darkAreaLeft = new Rectangle(0, 0, darkAreaColor);
            Rectangle darkAreaRight = new Rectangle(0, 0, darkAreaColor);
            Rectangle darkAreaBottom = new Rectangle(0, 0, darkAreaColor);

            darkAreaTop.widthProperty().bind(mainImageView.fitWidthProperty());
            darkAreaTop.heightProperty().bind(rectangle.yProperty());

            darkAreaLeft.yProperty().bind(rectangle.yProperty());
            darkAreaLeft.widthProperty().bind(rectangle.xProperty());
            darkAreaLeft.heightProperty().bind(rectangle.heightProperty());

            darkAreaRight.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
            darkAreaRight.yProperty().bind(rectangle.yProperty());
            darkAreaRight.widthProperty().bind(mainImageView.fitWidthProperty().subtract(
                    rectangle.xProperty().add(rectangle.widthProperty())));
            darkAreaRight.heightProperty().bind(rectangle.heightProperty());

            darkAreaBottom.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()));
            darkAreaBottom.widthProperty().bind(mainImageView.fitWidthProperty());
            darkAreaBottom.heightProperty().bind(mainImageView.fitHeightProperty().subtract(
                    rectangle.yProperty().add(rectangle.heightProperty())));

            group.getChildren().add(1, darkAreaTop);
            group.getChildren().add(1, darkAreaLeft);
            group.getChildren().add(1, darkAreaBottom);
            group.getChildren().add(1, darkAreaRight);

            // 不影响重新选择裁切起始点
            darkAreaTop.setMouseTransparent(true);
            darkAreaLeft.setMouseTransparent(true);
            darkAreaRight.setMouseTransparent(true);
            darkAreaBottom.setMouseTransparent(true);
        }
    }
}
