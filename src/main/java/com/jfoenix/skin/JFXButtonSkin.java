package com.jfoenix.skin;

import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXRipple;
import com.jfoenix.effect.JFXDepthManager;
import com.jfoenix.transition.CachedTransition;
import com.jfoenix.util.JFXNodeUtil;
import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.skin.ButtonSkin;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class JFXButtonSkin extends ButtonSkin {

    private Transition clickedAnimation;
    private final JFXRipple buttonRipple;
    private Runnable releaseManualRipple = null;
    private boolean invalid = true;
    private boolean mousePressed = false;

    public JFXButtonSkin(JFXButton button) {
        super(button);

        buttonRipple = new JFXRipple(getSkinnable()) {
            @Override
            protected Node getMask() {
                StackPane mask = new StackPane();
                mask.shapeProperty().bind(getSkinnable().shapeProperty());
                JFXNodeUtil.updateBackground(getSkinnable().getBackground(), mask);
                mask.resize(getWidth() - snappedRightInset() - snappedLeftInset(),
                        getHeight() - snappedBottomInset() - snappedTopInset());
                return mask;
            }

            @Override
            protected void positionControl(Node control) {
//                super.positionControl(control);
            }
        };

        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> playClickAnimation(1));
        button.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> mousePressed = true);
        button.addEventFilter(MouseEvent.MOUSE_RELEASED, event -> mousePressed = false);
        button.addEventFilter(MouseEvent.MOUSE_DRAGGED, event -> mousePressed = false);

        button.rippleFillProperty().addListener((observable, oldValue, newValue) -> buttonRipple.setRippleFill(newValue));

        button.armedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!mousePressed) {
                    releaseManualRipple = buttonRipple.createManualRipple();
                    playClickAnimation(1);
                }
            } else {
                if (releaseManualRipple != null) {
                    releaseManualRipple.run();
                    releaseManualRipple = null;
                }
                playClickAnimation(-1);
            }
        });

        button.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!button.disableVisualFocusProperty().get()) {
                if (newValue) {
                    buttonRipple.setOverlayVisible(!getSkinnable().isPressed());
                }
            }
        });

        button.buttonTypeProperty().addListener((observable, oldValue, newValue) -> updateButtonType(newValue));

        updateButtonType(button.getButtonType());

        updateChildren();
    }

    @Override
    protected void updateChildren() {
        super.updateChildren();
        if (buttonRipple != null) {
            getChildren().add(0, buttonRipple);
        }
        for (int i = 0; i < getChildren().size(); i++) {
            final Node child = getChildren().get(i);
            if (child instanceof Text) {
                child.setMouseTransparent(true);
            }
        }
    }

    @Override
    protected void layoutChildren(double x, double y, double w, double h) {
        if (invalid) {
            if (((JFXButton) getSkinnable()).getRippleFill() == null) {
                for (int i = getChildren().size() - 1; i >= 1; i--) {
                    if (getChildren().get(i) instanceof Text) {
                        buttonRipple.setRippleFill(((Text) getChildren().get(i)).getFill());
                        ((Text) getChildren().get(i)).fillProperty().addListener((observable, oldValue, newValue) -> buttonRipple.setRippleFill(newValue));
                        break;
                    } else if (getChildren().get(i) instanceof Label) {
                        buttonRipple.setRippleFill((((Label) getChildren().get(i)).getTextFill()));
                        ((Label) getChildren().get(i)).textFillProperty().addListener((observable, oldValue, newValue) -> buttonRipple.setRippleFill(newValue));
                        break;
                    }
                }
            } else {
                buttonRipple.setRippleFill(((JFXButton) getSkinnable()).getRippleFill());
            }
            invalid = false;
        }
        buttonRipple.resizeRelocate(
                getSkinnable().getLayoutBounds().getMinX(),
                getSkinnable().getLayoutBounds().getMinY(),
                getSkinnable().getWidth(), getSkinnable().getHeight()
        );
        layoutLabelInArea(x, y, w, h);
    }

    public void updateButtonType(JFXButton.ButtonType type) {
        switch (type) {
            case RAISED: {
                JFXDepthManager.setDepth(getSkinnable(), 2);
                clickedAnimation = new ButtonClickTransition(getSkinnable(), (DropShadow) getSkinnable().getEffect());
                getSkinnable().setPickOnBounds(false);
                break;
            }

            default: {
                getSkinnable().setEffect(null);
                getSkinnable().setPickOnBounds(true);
            }
        }
    }

    public void playClickAnimation(double rate) {
        if (clickedAnimation != null) {
            if (!clickedAnimation.getCurrentTime().equals(clickedAnimation.getCycleDuration()) || rate != 1) {
                clickedAnimation.setRate(rate);
                clickedAnimation.play();
            }
        }
    }

    private static class ButtonClickTransition extends CachedTransition {
        ButtonClickTransition(Node node, DropShadow shadowEffect) {
            super(node, new Timeline(
                    new KeyFrame(Duration.ZERO,
                            new KeyValue(shadowEffect.radiusProperty(),
                                    JFXDepthManager.getShadowAt(2).radiusProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.spreadProperty(),
                                    JFXDepthManager.getShadowAt(2).spreadProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.offsetXProperty(),
                                    JFXDepthManager.getShadowAt(2).offsetXProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.offsetYProperty(),
                                    JFXDepthManager.getShadowAt(2).offsetYProperty().get(),
                                    Interpolator.EASE_BOTH)
                    ),
                    new KeyFrame(Duration.millis(1000),
                            new KeyValue(shadowEffect.radiusProperty(),
                                    JFXDepthManager.getShadowAt(5).radiusProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.spreadProperty(),
                                    JFXDepthManager.getShadowAt(5).spreadProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.offsetXProperty(),
                                    JFXDepthManager.getShadowAt(5).offsetXProperty().get(),
                                    Interpolator.EASE_BOTH),
                            new KeyValue(shadowEffect.offsetYProperty(),
                                    JFXDepthManager.getShadowAt(5).offsetYProperty().get(),
                                    Interpolator.EASE_BOTH)
                    )
            ));
            setCycleDuration(Duration.seconds(0.2));
            setDelay(Duration.seconds(0));
        }
    }
}
