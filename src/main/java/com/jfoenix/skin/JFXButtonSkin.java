package com.jfoenix.skin;

import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXRippler;
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
    private JFXRippler buttonRippler;
    private Runnable releaseManualRippler = null;
    private boolean invalid = true;
    private boolean mousePressed = false;

    public JFXButtonSkin(JFXButton button) {
        super(button);

        buttonRippler = new JFXRippler(getSkinnable()) {
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
        button.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> mousePressed = true);
        button.addEventHandler(MouseEvent.MOUSE_RELEASED, event -> mousePressed = false);
        button.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> mousePressed = false);

        button.ripplerFillProperty().addListener((observable, oldValue, newValue) -> buttonRippler.setRipplerFill(newValue));

        button.armedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                if (!mousePressed) {
                    releaseManualRippler = buttonRippler.createManualRipple();
                    playClickAnimation(1);
                }
            } else {
                if (releaseManualRippler != null) {
                    releaseManualRippler.run();
                    releaseManualRippler = null;
                }
                playClickAnimation(-1);
            }
        });

        button.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!button.disableVisualFocusProperty().get()) {
                if (newValue) {
                    if (!getSkinnable().isPressed()) {
                        buttonRippler.setOverlayVisible(true);
                    } else {
                        buttonRippler.setOverlayVisible(false);
                    }
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
        if (buttonRippler != null) {
            getChildren().add(0, buttonRippler);
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
            if (((JFXButton) getSkinnable()).getRipplerFill() == null) {
                for (int i = getChildren().size() - 1; i >= 1; i--) {
                    if (getChildren().get(i) instanceof Text) {
                        buttonRippler.setRipplerFill(((Text) getChildren().get(i)).getFill());
                        ((Text) getChildren().get(i)).fillProperty().addListener((observable, oldValue, newValue) -> buttonRippler.setRipplerFill(newValue));
                        break;
                    } else if (getChildren().get(i) instanceof Label) {
                        buttonRippler.setRipplerFill((((Label) getChildren().get(i)).getTextFill()));
                        ((Label) getChildren().get(i)).textFillProperty().addListener((observable, oldValue, newValue) -> buttonRippler.setRipplerFill(newValue));
                        break;
                    }
                }
            } else {
                buttonRippler.setRipplerFill(((JFXButton) getSkinnable()).getRipplerFill());
            }
            invalid = false;
        }
        buttonRippler.resizeRelocate(
                getSkinnable().getLayoutBounds().getMinX(),
                getSkinnable().getLayoutBounds().getMinY(),
                getSkinnable().getWidth(), getSkinnable().getHeight()
        );
        layoutLabelInArea(x, y, w, h);
    }

    public void updateButtonType(JFXButton.ButtonType type) {
        switch (type) {
            case RAISED -> {
                JFXDepthManager.setDepth(getSkinnable(), 2);
                clickedAnimation = new ButtonClickTransition(getSkinnable(), (DropShadow) getSkinnable().getEffect());
                getSkinnable().setPickOnBounds(false);
            }

            default -> {
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
