package com.jfoenix.control;

import com.jfoenix.converter.RipplerMaskTypeConverter;
import com.jfoenix.util.JFXNodeUtil;
import javafx.animation.*;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.*;
import javafx.css.converter.BooleanConverter;
import javafx.css.converter.PaintConverter;
import javafx.css.converter.SizeConverter;
import javafx.geometry.Bounds;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class JFXRippler extends StackPane {

    public enum RipplerPos {
        FRONT, BACK
    }

    public enum RipplerMask {
        CIRCLE, RECT, FIT
    }

    protected RippleGenerator rippler;
    protected Pane ripplerPane;
    protected Node control;

    protected static final double RIPPLE_MAX_RADIUS = 300;

    protected boolean enabled = true;
    protected boolean forceOverlay = false;
    private Interpolator rippleInterpolator = Interpolator.SPLINE(
            0.0825, 0.3025, 0.0875, 0.9975
    );

    public JFXRippler() {
        this(null, RipplerMask.RECT, RipplerPos.FRONT);
    }

    public JFXRippler(Node control) {
        this(control, RipplerMask.RECT, RipplerPos.FRONT);
    }

    public JFXRippler(Node control, RipplerPos pos) {
        this(control, RipplerMask.RECT, pos);
    }

    public JFXRippler(Node control, RipplerMask mask) {
        this(control, mask, RipplerPos.FRONT);
    }

    public JFXRippler(Node control, RipplerMask mask, RipplerPos pos) {
        initialize();

        setMaskType(mask);
        setPosition(pos);
        createRippleUI();
        setControl(control);

        position.addListener(observable -> updateControlPosition());

        setPickOnBounds(false);
        setCache(true);
        setCacheHint(CacheHint.SPEED);
        setCacheShape(true);

    }

    protected final void createRippleUI() {
        rippler = new RippleGenerator();
        ripplerPane = new StackPane();
        ripplerPane.setMouseTransparent(true);
        ripplerPane.getChildren().add(rippler);
        getChildren().add(ripplerPane);
    }

    public void setControl(Node control) {
        if (control != null) {
            this.control = control;
            positionControl(control);
            initControlListeners();
        }
    }

    protected void positionControl(Node control) {
        if (this.position.get() == RipplerPos.BACK) {
            getChildren().add(control);
        } else {
            getChildren().add(0, control);
        }
    }

    protected void updateControlPosition() {
        if (this.position.get() == RipplerPos.BACK) {
            ripplerPane.toBack();
        } else {
            ripplerPane.toFront();
        }
    }

    public Node getControl() {
        return control;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    protected Node getMask() {
        double borderWidth = ripplerPane.getBorder() != null ? ripplerPane.getBorder().getInsets().getTop() : 0;
        Bounds bounds = control.getBoundsInParent();
        double width = control.getLayoutBounds().getWidth();
        double height = control.getLayoutBounds().getHeight();
        double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
        double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
        double diffMaxX = Math.abs(control.getBoundsInLocal().getMaxX() - control.getLayoutBounds().getMaxX());
        double diffMaxY = Math.abs(control.getBoundsInLocal().getMaxY() - control.getLayoutBounds().getMaxY());
        Node mask;

        switch (getMaskType()) {
            case RECT -> mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
                    bounds.getMinY() + diffMinY - snappedTopInset(),
                    width - 2 * borderWidth,
                    height - 2 * borderWidth);

            case CIRCLE -> {
                double radius = Math.min((width / 2) - 2 * borderWidth, (height / 2) - 2 * borderWidth);
                mask = new Circle((bounds.getMinX() + diffMinX + bounds.getMaxX() - diffMaxX) / 2 - snappedLeftInset(),
                        (bounds.getMinY() + diffMinY + bounds.getMaxY() - diffMaxY) / 2 - snappedTopInset(), radius, Color.BLUE);
            }

            case FIT -> {
                mask = new Region();
                if (control instanceof Shape) {
                    ((Region) mask).setShape((Shape) control);
                } else if (control instanceof Region) {
                    ((Region) mask).setShape(((Region) control).getShape());
                    JFXNodeUtil.updateBackground(((Region) control).getBackground(), (Region) mask);
                }
                mask.resize(width, height);
                mask.relocate(bounds.getMinX() + diffMinX, bounds.getMinY() + diffMinY);
            }

            default -> mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
                    bounds.getMinY() + diffMinY - snappedTopInset(),
                    width - 2 * borderWidth,
                    height - 2 * borderWidth);
        }

        return mask;
    }

    protected double computeRippleRadius() {
        double width2 = control.getLayoutBounds().getWidth() * control.getLayoutBounds().getWidth();
        double height2 = control.getLayoutBounds().getHeight() * control.getLayoutBounds().getHeight();
        return Math.min(Math.sqrt(width2 + height2), RIPPLE_MAX_RADIUS) * 1.1 + 5;
    }

    public void setOverLayBounds(Rectangle overlay) {
        overlay.setWidth(control.getLayoutBounds().getWidth());
        overlay.setHeight(control.getLayoutBounds().getHeight());
    }

    protected void initControlListeners() {
        control.layoutBoundsProperty().addListener(observable -> resetRippler());
        if (getChildren().contains(control)) {
            control.boundsInParentProperty().addListener(observable -> resetRippler());
        }
        control.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> createRipple(event.getX(), event.getY()));
        control.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> releaseRipple());
    }

    protected void createRipple(double x, double y) {
        if (!isRipplerDisabled()) {
            rippler.setGeneratorCenterX(x);
            rippler.setGeneratorCenterY(y);
            rippler.createRipple();
        }
    }

    protected void releaseRipple() {
        rippler.releaseRipple();
    }

    public Runnable createManualRipple() {
        if (!isRipplerDisabled()) {
            rippler.setGeneratorCenterX(control.getLayoutBounds().getWidth() / 2);
            rippler.setGeneratorCenterY(control.getLayoutBounds().getHeight() / 2);
            rippler.createRipple();
            return () -> {
                releaseRipple();
            };
        }
        return () -> {
        };
    }

    public void setOverlayVisible(boolean visible, boolean forceOverlay) {
        this.forceOverlay = forceOverlay;
        setOverlayVisible(visible);
    }

    public void setOverlayVisible(boolean visible) {
        if (visible) {
            showOverlay();
        } else {
            forceOverlay = !visible ? false : forceOverlay;
            hideOverlay();
        }
    }

    private void showOverlay() {
        if (rippler.overlayRect != null) {
            rippler.overlayRect.outAnimation.stop();
        }
        rippler.createOverlay();
        rippler.overlayRect.inAnimation.play();
    }

    private void hideOverlay() {
        if (!forceOverlay) {
            if (rippler.overlayRect != null) {
                rippler.overlayRect.inAnimation.stop();
            }
            if (rippler.overlayRect != null) {
                rippler.overlayRect.outAnimation.play();
            }
        } else {
            System.err.println("Ripple Overlay is forced!");
        }
    }

    final class RippleGenerator extends Group {

        private double generatorCenterX = 0;
        private double generatorCenterY = 0;
        private OverLayRipple overlayRect;
        private AtomicBoolean generating = new AtomicBoolean(false);
        private boolean cacheRipplerClip = false;
        private boolean resetClip = false;
        private Queue<Ripple> rippleQueue = new LinkedList<>();

        RippleGenerator() {
            this.setManaged(false);
            this.setCache(true);
            this.setCacheHint(CacheHint.SPEED);
        }

        void createRipple() {
            if (enabled) {
                if (!generating.getAndSet(true)) {
                    createOverlay();
                    if (this.getClip() == null || (getChildren().size() == 1 && !cacheRipplerClip) || resetClip) {
                        this.setClip(getMask());
                    }
                    this.resetClip = false;

                    final Ripple ripple = new Ripple(generatorCenterX, generatorCenterY);
                    getChildren().add(ripple);
                    rippleQueue.add(ripple);

                    overlayRect.outAnimation.stop();
                    overlayRect.inAnimation.play();
                    ripple.inAnimation.play();
                }
            }
        }

        private void releaseRipple() {
            Ripple ripple = rippleQueue.poll();
            if (ripple != null) {
                ripple.inAnimation.stop();
                ripple.outAnimation = new Timeline(
                        new KeyFrame(Duration.millis(Math.min(800, (0.9 * 500) / ripple.getScaleX())), ripple.outKeyValues)
                );
                ripple.outAnimation.setOnFinished((event) -> getChildren().remove(ripple));
                ripple.outAnimation.play();
                if (generating.getAndSet(false)) {
                    if (overlayRect != null) {
                        overlayRect.inAnimation.stop();
                        if (!forceOverlay) {
                            overlayRect.outAnimation.play();
                        }
                    }
                }
            }
        }

        void cacheRippleClip(boolean cached) {
            cacheRipplerClip = cached;
        }

        void createOverlay() {
            if (overlayRect == null) {
                overlayRect = new OverLayRipple();
                overlayRect.setClip(getMask());
                getChildren().add(0, overlayRect);
                overlayRect.fillProperty().bind(Bindings.createObjectBinding(() -> {
                    if (ripplerFill.get() instanceof Color) {
                        return new Color(((Color) ripplerFill.get()).getRed(),
                                ((Color) ripplerFill.get()).getGreen(),
                                ((Color) ripplerFill.get()).getBlue(), 0.2);
                    } else {
                        return Color.TRANSPARENT;
                    }
                }, ripplerFill));
            }
        }

        void setGeneratorCenterX(double generatorCenterX) {
            this.generatorCenterX = generatorCenterX;
        }

        void setGeneratorCenterY(double generatorCenterY) {
            this.generatorCenterY = generatorCenterY;
        }

        private final class OverLayRipple extends Rectangle {
            Animation inAnimation = new Timeline(new KeyFrame(Duration.millis(300),
                    new KeyValue(opacityProperty(), 1, Interpolator.EASE_IN)));

            Animation outAnimation = new Timeline(new KeyFrame(Duration.millis(300),
                    new KeyValue(opacityProperty(), 0, Interpolator.EASE_OUT)));

            OverLayRipple() {
                super();
                setOverLayBounds(this);
                this.getStyleClass().add("jfx-rippler-overlay");
                if (JFXRippler.this.getChildrenUnmodifiable().contains(control)) {
                    double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
                    double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
                    Bounds bounds = control.getBoundsInParent();
                    this.setX(bounds.getMinX() + diffMinX - snappedLeftInset());
                    this.setY(bounds.getMinY() + diffMinY - snappedTopInset());
                }
                setOpacity(0);
                setCache(true);
                setCacheHint(CacheHint.SPEED);
                setCacheShape(true);
                setManaged(false);
            }
        }

        private final class Ripple extends Circle {

            KeyValue[] outKeyValues;
            Animation outAnimation = null;
            Animation inAnimation = null;

            private Ripple(double centerX, double centerY) {
                super(centerX, centerY, ripplerRadius.get().doubleValue() == Region.USE_COMPUTED_SIZE ? computeRippleRadius() : ripplerRadius.get().doubleValue(), null);
                setCache(true);
                setCacheHint(CacheHint.SPEED);
                setCacheShape(true);
                setManaged(false);
                setSmooth(true);

                KeyValue[] inKeyValues = new KeyValue[isRipplerRecenter() ? 4 : 2];
                outKeyValues = new KeyValue[isRipplerRecenter() ? 5 : 3];

                inKeyValues[0] = new KeyValue(scaleXProperty(), 0.9, rippleInterpolator);
                inKeyValues[1] = new KeyValue(scaleYProperty(), 0.9, rippleInterpolator);

                outKeyValues[0] = new KeyValue(this.scaleXProperty(), 1, rippleInterpolator);
                outKeyValues[1] = new KeyValue(this.scaleYProperty(), 1, rippleInterpolator);
                outKeyValues[2] = new KeyValue(this.opacityProperty(), 0, rippleInterpolator);

                if (isRipplerRecenter()) {
                    double dx = (control.getLayoutBounds().getWidth() / 2 - centerX) / 1.55;
                    double dy = (control.getLayoutBounds().getHeight() / 2 - centerY) / 1.55;
                    inKeyValues[2] = outKeyValues[3] = new KeyValue(translateXProperty(),
                            Math.signum(dx) * Math.min(Math.abs(dx),
                                    this.getRadius() / 2), rippleInterpolator);
                    inKeyValues[3] = outKeyValues[4] = new KeyValue(translateYProperty(),
                            Math.signum(dy) * Math.min(Math.abs(dy),
                                    this.getRadius() / 2), rippleInterpolator);
                }

                inAnimation = new Timeline(new KeyFrame(Duration.ZERO,
                        new KeyValue(scaleXProperty(), 0, rippleInterpolator),
                        new KeyValue(scaleYProperty(), 0, rippleInterpolator),
                        new KeyValue(translateXProperty(), 0, rippleInterpolator),
                        new KeyValue(translateYProperty(), 0, rippleInterpolator),
                        new KeyValue(opacityProperty(), 1, rippleInterpolator)
                ), new KeyFrame(Duration.millis(900), inKeyValues));

                setScaleX(0);
                setScaleY(0);
                if (ripplerFill.get() instanceof Color) {
                    Color circleColor = new Color(((Color) ripplerFill.get()).getRed(),
                            ((Color) ripplerFill.get()).getGreen(),
                            ((Color) ripplerFill.get()).getBlue(), 0.3);
                    setStroke(circleColor);
                    setFill(circleColor);
                } else {
                    setStroke(ripplerFill.get());
                    setFill(ripplerFill.get());
                }
            }
        }

        public void clear() {
            getChildren().clear();
            rippler.overlayRect = null;
            generating.set(false);
        }
    }

    private void resetOverLay() {
        if (rippler.overlayRect != null) {
            rippler.overlayRect.inAnimation.stop();
            final RippleGenerator.OverLayRipple oldOverlay = rippler.overlayRect;
            rippler.overlayRect.outAnimation.setOnFinished((finish) -> rippler.getChildren().remove(oldOverlay));
            rippler.overlayRect.outAnimation.play();
            rippler.overlayRect = null;
        }
    }

    private void resetClip() {
        this.rippler.resetClip = true;
    }

    protected void resetRippler() {
        resetOverLay();
        resetClip();
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-rippler";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    private StyleableObjectProperty<Boolean> ripplerRecenter = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLER_RECENTER, JFXRippler.this, "ripplerRecenter", false);

    public Boolean isRipplerRecenter() {
        return ripplerRecenter == null ? false : ripplerRecenter.get();
    }

    public StyleableObjectProperty<Boolean> ripplerRecenterProperty() {
        return this.ripplerRecenter;
    }

    public void setRipplerRecenter(Boolean radius) {
        this.ripplerRecenter.set(radius);
    }

    private StyleableObjectProperty<Number> ripplerRadius = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLER_RADIUS, JFXRippler.this, "ripplerRadius", Region.USE_COMPUTED_SIZE);

    public Number getRipplerRadius() {
        return ripplerRadius == null ? Region.USE_COMPUTED_SIZE : ripplerRadius.get();
    }

    public StyleableObjectProperty<Number> ripplerRadiusProperty() {
        return this.ripplerRadius;
    }

    public void setRipplerRadius(Number radius) {
        this.ripplerRadius.set(radius);
    }

    private StyleableObjectProperty<Paint> ripplerFill = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLER_FILL, JFXRippler.this, "ripplerFill", Color.rgb(0, 200, 255));

    public Paint getRipplerFill() {
        return this.ripplerFill == null ? Color.rgb(0, 200, 255) : ripplerFill.get();
    }

    public StyleableObjectProperty<Paint> ripplerFillProperty() {
        return this.ripplerFill;
    }

    public void setRipplerFill(Paint color) {
        this.ripplerFill.set(color);
    }

    private StyleableObjectProperty<RipplerMask> maskType = new SimpleStyleableObjectProperty<>(StyleableProperties.MASK_TYPE, JFXRippler.this, "maskType", RipplerMask.RECT);

    public RipplerMask getMaskType() {
        return maskType == null ? RipplerMask.RECT : maskType.get();
    }

    public StyleableObjectProperty<RipplerMask> maskTypeProperty() {
        return this.maskType;
    }

    public void setMaskType(RipplerMask type) {
        this.maskType.set(type);
    }

    private StyleableBooleanProperty ripplerDisabled = new SimpleStyleableBooleanProperty(StyleableProperties.RIPPLER_DISABLED, JFXRippler.this, "ripplerDisabled", false);

    public Boolean isRipplerDisabled() {
        return this.ripplerDisabled.get();
    }

    public StyleableBooleanProperty ripplerDisabledProperty() {
        return this.ripplerDisabled;
    }

    public void setRipplerDisabled(Boolean disabled) {
        this.ripplerDisabled.set(disabled);
    }

    protected ObjectProperty<RipplerPos> position = new SimpleObjectProperty<>();

    public void setPosition(RipplerPos pos) {
        this.position.set(pos);
    }

    public RipplerPos getPosition() {
        return position == null ? RipplerPos.FRONT : position.get();
    }

    public ObjectProperty<RipplerPos> positionProperty() {
        return this.position;
    }

    private static final class StyleableProperties {
        private static final CssMetaData<JFXRippler, Boolean> RIPPLER_RECENTER = new CssMetaData<JFXRippler, Boolean>("-jfx-rippler-recenter", BooleanConverter.getInstance(), false) {
            @Override
            public boolean isSettable(JFXRippler styleable) {
                return styleable.ripplerRecenter == null || !styleable.ripplerRecenter.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(JFXRippler styleable) {
                return styleable.ripplerRecenterProperty();
            }
        };

        private static final CssMetaData<JFXRippler, Boolean> RIPPLER_DISABLED = new CssMetaData<JFXRippler, Boolean>("-jfx-rippler-disabled", BooleanConverter.getInstance(), false) {
            @Override
            public boolean isSettable(JFXRippler styleable) {
                return styleable.ripplerDisabled == null || !styleable.ripplerDisabled.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(JFXRippler styleable) {
                return styleable.ripplerDisabledProperty();
            }
        };

        private static final CssMetaData<JFXRippler, Paint> RIPPLER_FILL = new CssMetaData<JFXRippler, Paint>("-jfx-rippler-fill", PaintConverter.getInstance(), Color.rgb(0, 200, 250)) {
            @Override
            public boolean isSettable(JFXRippler styleable) {
                return styleable.ripplerFill == null || !styleable.ripplerFill.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(JFXRippler styleable) {
                return styleable.ripplerFillProperty();
            }
        };

        private static CssMetaData<JFXRippler, Number> RIPPLER_RADIUS = new CssMetaData<JFXRippler, Number>("-jfx-rippler-radius", SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
            @Override
            public boolean isSettable(JFXRippler styleable) {
                return styleable.ripplerRadius == null || !styleable.ripplerRadius.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(JFXRippler styleable) {
                return styleable.ripplerRadiusProperty();
            }
        };

        private static final CssMetaData<JFXRippler, RipplerMask> MASK_TYPE = new CssMetaData<JFXRippler, RipplerMask>("-jfx-mask-type", RipplerMaskTypeConverter.getInstance(), RipplerMask.RECT) {
            @Override
            public boolean isSettable(JFXRippler styleable) {
                return styleable.maskType == null || !styleable.maskType.isBound();
            }

            @Override
            public StyleableProperty<RipplerMask> getStyleableProperty(JFXRippler styleable) {
                return styleable.maskTypeProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(StackPane.getClassCssMetaData());
            Collections.addAll(styleables,
                    RIPPLER_RECENTER,
                    RIPPLER_RADIUS,
                    RIPPLER_FILL,
                    MASK_TYPE,
                    RIPPLER_DISABLED
            );
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        return getClassCssMetaData();
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.STYLEABLES;
    }
}
