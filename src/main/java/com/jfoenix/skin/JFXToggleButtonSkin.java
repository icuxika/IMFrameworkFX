package com.jfoenix.skin;

import com.jfoenix.control.JFXRipple;
import com.jfoenix.control.JFXToggleButton;
import com.jfoenix.effect.JFXDepthManager;
import com.jfoenix.transition.JFXAnimationTimer;
import com.jfoenix.transition.JFXKeyFrame;
import com.jfoenix.transition.JFXKeyValue;
import javafx.animation.Interpolator;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.skin.ToggleButtonSkin;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.StrokeLineCap;
import javafx.util.Duration;

public class JFXToggleButtonSkin extends ToggleButtonSkin {

    private Runnable releaseManualRippler = null;

    private JFXAnimationTimer timer;
    private final Circle circle;
    private final Line line;

    public JFXToggleButtonSkin(JFXToggleButton toggleButton) {
        super(toggleButton);

        double circleRadius = toggleButton.getSize();

        line = new Line();
        line.setStroke(getSkinnable().isSelected() ? toggleButton.getToggleLineColor() : toggleButton.getUnToggleLineColor());
        line.setStartX(0);
        line.setStartY(0);
        line.setEndX(circleRadius * 2 + 2);
        line.setEndY(0);
        line.setStrokeWidth(circleRadius * 1.5);
        line.setStrokeLineCap(StrokeLineCap.ROUND);
        line.setSmooth(true);

        circle = new Circle();
        circle.setFill(getSkinnable().isSelected() ? toggleButton.getToggleColor() : toggleButton.getUnToggleColor());
        circle.setCenterX(-circleRadius);
        circle.setCenterY(0);
        circle.setRadius(circleRadius);
        circle.setSmooth(true);
        JFXDepthManager.setDepth(circle, 1);

        StackPane circlePane = new StackPane();
        circlePane.getChildren().add(circle);
        circlePane.setPadding(new Insets(circleRadius * 1.5));

        JFXRipple rippler = new JFXRipple(circlePane, JFXRipple.RippleMask.CIRCLE, JFXRipple.RipplePos.BACK);
        rippler.setRippleFill(getSkinnable().isSelected() ? toggleButton.getToggleLineColor() : toggleButton.getUnToggleLineColor());
        rippler.setTranslateX(computeTranslation(circleRadius, line));

        final StackPane main = new StackPane();
        main.getChildren().setAll(line, rippler);
        main.setCursor(Cursor.HAND);

        // show focus traversal effect
        getSkinnable().armedProperty().addListener((o, oldVal, newVal) -> {
            if (newVal) {
                releaseManualRippler = rippler.createManualRipple();
            } else if (releaseManualRippler != null) {
                releaseManualRippler.run();
            }
        });
        toggleButton.focusedProperty().addListener((o, oldVal, newVal) -> {
            if (!toggleButton.isDisableVisualFocus()) {
                if (newVal) {
                    if (!getSkinnable().isPressed()) {
                        rippler.setOverlayVisible(true);
                    }
                } else {
                    rippler.setOverlayVisible(false);
                }
            }
        });
        toggleButton.pressedProperty().addListener(observable -> rippler.setOverlayVisible(false));

        // add change listener to selected property
        getSkinnable().selectedProperty().addListener(observable -> {
            rippler.setRippleFill(toggleButton.isSelected() ? toggleButton.getToggleLineColor() : toggleButton.getUnToggleLineColor());
            if (!toggleButton.isDisableAnimation()) {
                timer.reverseAndContinue();
            } else {
                rippler.setTranslateX(computeTranslation(circleRadius, line));
            }
        });

        getSkinnable().setGraphic(main);

        timer = new JFXAnimationTimer(
                new JFXKeyFrame(Duration.millis(100),
                        JFXKeyValue.builder()
                                .setTarget(rippler.translateXProperty())
                                .setEndValueSupplier(() -> computeTranslation(circleRadius, line))
                                .setInterpolator(Interpolator.EASE_BOTH)
                                .setAnimateCondition(() -> !((JFXToggleButton) getSkinnable()).isDisableAnimation())
                                .build(),

                        JFXKeyValue.builder()
                                .setTarget(line.strokeProperty())
                                .setEndValueSupplier(() -> getSkinnable().isSelected() ?
                                        ((JFXToggleButton) getSkinnable()).getToggleLineColor()
                                        : ((JFXToggleButton) getSkinnable()).getUnToggleLineColor())
                                .setInterpolator(Interpolator.EASE_BOTH)
                                .setAnimateCondition(() -> !((JFXToggleButton) getSkinnable()).isDisableAnimation())
                                .build(),

                        JFXKeyValue.builder()
                                .setTarget(circle.fillProperty())
                                .setEndValueSupplier(() -> getSkinnable().isSelected() ?
                                        ((JFXToggleButton) getSkinnable()).getToggleColor()
                                        : ((JFXToggleButton) getSkinnable()).getUnToggleColor())
                                .setInterpolator(Interpolator.EASE_BOTH)
                                .setAnimateCondition(() -> !((JFXToggleButton) getSkinnable()).isDisableAnimation())
                                .build()
                )
        );
        timer.setCacheNodes(circle, line);

        registerChangeListener(toggleButton.toggleColorProperty(), observableValue -> {
            if (getSkinnable().isSelected()) {
                circle.setFill(((JFXToggleButton) getSkinnable()).getToggleColor());
            }
        });
        registerChangeListener(toggleButton.unToggleColorProperty(), observableValue -> {
            if (!getSkinnable().isSelected()) {
                circle.setFill(((JFXToggleButton) getSkinnable()).getUnToggleColor());
            }
        });
        registerChangeListener(toggleButton.toggleLineColorProperty(), observableValue -> {
            if (getSkinnable().isSelected()) {
                line.setStroke(((JFXToggleButton) getSkinnable()).getToggleLineColor());
            }
        });
        registerChangeListener(toggleButton.unToggleColorProperty(), observableValue -> {
            if (!getSkinnable().isSelected()) {
                line.setStroke(((JFXToggleButton) getSkinnable()).getUnToggleLineColor());
            }
        });
    }

    private double computeTranslation(double circleRadius, Line line) {
        return (getSkinnable().isSelected() ? 1 : -1) * ((line.getLayoutBounds().getWidth() / 2) - circleRadius + 2);
    }

    @Override
    public void dispose() {
        super.dispose();
        timer.dispose();
        timer = null;
    }
}
