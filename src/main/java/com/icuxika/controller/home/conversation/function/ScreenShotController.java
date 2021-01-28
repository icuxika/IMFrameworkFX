package com.icuxika.controller.home.conversation.function;

import com.icuxika.MainApp;
import com.icuxika.control.ResizableRectangle;
import com.icuxika.util.ClipboardUtil;
import com.icuxika.util.SystemUtil;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

/**
 * 屏幕截图
 */
public class ScreenShotController {

    private Stage screenStage;
    private AnchorPane anchorPane;
    private AnchorPane topAnchorPane;
    private AreaSelection areaSelection;
    private Label rangeLabel;
    private HBox operateBox;

    public void startScreenShot() {
        screenStage = new Stage();

        StackPane stackPane = new StackPane();
        stackPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));

        Group group = new Group();
        anchorPane = new AnchorPane();
        anchorPane.prefWidthProperty().bind(screenStage.widthProperty());
        anchorPane.prefHeightProperty().bind(screenStage.heightProperty());
        anchorPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("#B5B5B522"), CornerRadii.EMPTY, Insets.EMPTY)));
        group.getChildren().add(anchorPane);

        stackPane.getChildren().add(group);
        topAnchorPane = new AnchorPane();
        topAnchorPane.setPickOnBounds(false);
        topAnchorPane.prefWidthProperty().bind(screenStage.widthProperty());
        topAnchorPane.prefHeightProperty().bind(screenStage.heightProperty());

        stackPane.getChildren().add(topAnchorPane);

        Scene screenScene = new Scene(stackPane);
        screenScene.setFill(Paint.valueOf("#FFFFFF00"));

        screenStage.setScene(screenScene);
        screenStage.setFullScreenExitHint("");
        screenStage.initStyle(StageStyle.TRANSPARENT);
        screenStage.setFullScreen(true);

        MainApp.showStageWithPointer(screenStage, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getWidth());
        screenStage.show();
        initScreenShot(group);

        screenScene.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.ESCAPE)) screenStage.close();
        });
    }

    private void initScreenShot(Group group) {
        areaSelection = new AreaSelection(group);

        rangeLabel = new Label();
        rangeLabel.setPrefSize(100, 24);
        rangeLabel.setTextFill(Color.WHITE);
        rangeLabel.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        anchorPane.getChildren().add(rangeLabel);
        rangeLabel.setVisible(false);

        operateBox = new HBox();
        operateBox.setAlignment(Pos.CENTER_RIGHT);
        operateBox.setSpacing(4);
        operateBox.setPadding(new Insets(4));
        topAnchorPane.getChildren().add(operateBox);
        operateBox.setVisible(false);

        FontIcon saveIcon = new FontIcon(FontAwesomeSolid.SAVE);
        saveIcon.setIconSize(20);
        saveIcon.setOnMouseReleased(event -> saveImage(areaSelection.getSelectionRectangle().getBoundsInLocal()));

        FontIcon copyIcon = new FontIcon(FontAwesomeSolid.COPY);
        copyIcon.setIconSize(20);
        copyIcon.setOnMouseReleased(event -> {
            screenStage.close();
            screenShot(areaSelection.getSelectionRectangle().getBoundsInLocal());
        });

        operateBox.getChildren().addAll(saveIcon, copyIcon);
    }

    /**
     * 清除选中
     */
    private void clearSelection(Group group) {
        group.getChildren().remove(1, group.getChildren().size());
    }

    /**
     * 获取指定区域的图片数据
     */
    private WritableImage getScreenImage(Bounds bounds) {
        return new Robot().getScreenCapture(null, bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
    }

    /**
     * 将图片放入剪切板
     */
    private void screenShot(Bounds bounds) {
        ClipboardUtil.putImage(getScreenImage(bounds));
    }

    /**
     * 保存图片
     */
    private void saveImage(Bounds bounds) {
        screenStage.close();
        WritableImage writableImage = getScreenImage(bounds);
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("保存文件");
        fileChooser.setInitialDirectory(new File(SystemUtil.USER_HOME));
        fileChooser.setInitialFileName("ScreenShot" + System.currentTimeMillis() + ".png");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("所有文件", "*.*"));
        File file = fileChooser.showSaveDialog(screenStage);
        if (file != null) {
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null), "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

            selectionRectangle = new ResizableRectangle(rectangleStartX, rectangleStartY, 0, 0, group, anchorPane.prefWidth(-1), anchorPane.prefHeight(-1));

            darkenOutsideRectangle(selectionRectangle);
            rangeLabel.textProperty().bind(selectionRectangle.widthProperty().asString().concat(" X ").concat(selectionRectangle.heightProperty()));
            rangeLabel.setVisible(true);
            operateBox.setVisible(true);
            selectionRectangle.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> {
                AnchorPane.setLeftAnchor(rangeLabel, newValue.getMinX());
                AnchorPane.setTopAnchor(rangeLabel, newValue.getMinY());

                AnchorPane.setLeftAnchor(operateBox, newValue.getMaxX() - operateBox.prefWidth(-1));
                AnchorPane.setTopAnchor(operateBox, newValue.getMaxY() - operateBox.prefHeight(-1));
            });
        };

        EventHandler<MouseEvent> onMouseDraggedEventHandler = event -> {
            if (event.isSecondaryButtonDown())
                return;

            double offsetX = event.getX() - rectangleStartX;
            double offsetY = event.getY() - rectangleStartY;

            if (offsetX > 0) {
                if (event.getX() > anchorPane.prefWidth(-1))
                    selectionRectangle.setWidth(anchorPane.prefWidth(-1) - rectangleStartX);
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
                if (event.getY() > anchorPane.prefHeight(-1))
                    selectionRectangle.setHeight(anchorPane.prefHeight(-1) - rectangleStartY);
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

        EventHandler<MouseEvent> onMouseReleasedEventHandler = event -> screenShot(selectionRectangle.getBoundsInLocal());

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

            darkAreaTop.widthProperty().bind(anchorPane.widthProperty());
            darkAreaTop.heightProperty().bind(rectangle.yProperty());

            darkAreaLeft.yProperty().bind(rectangle.yProperty());
            darkAreaLeft.widthProperty().bind(rectangle.xProperty());
            darkAreaLeft.heightProperty().bind(rectangle.heightProperty());

            darkAreaRight.xProperty().bind(rectangle.xProperty().add(rectangle.widthProperty()));
            darkAreaRight.yProperty().bind(rectangle.yProperty());
            darkAreaRight.widthProperty().bind(anchorPane.widthProperty().subtract(
                    rectangle.xProperty().add(rectangle.widthProperty())));
            darkAreaRight.heightProperty().bind(rectangle.heightProperty());

            darkAreaBottom.yProperty().bind(rectangle.yProperty().add(rectangle.heightProperty()));
            darkAreaBottom.widthProperty().bind(anchorPane.widthProperty());
            darkAreaBottom.heightProperty().bind(anchorPane.heightProperty().subtract(
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
