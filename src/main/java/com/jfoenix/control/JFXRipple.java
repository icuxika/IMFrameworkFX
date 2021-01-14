package com.jfoenix.control;

import com.jfoenix.converter.RippleMaskTypeConverter;
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

public class JFXRipple extends StackPane {

    public enum RipplePos {
        FRONT, BACK
    }

    public enum RippleMask {
        CIRCLE, RECT, FIT
    }

    protected RippleGenerator rippleGenerator;
    protected Pane ripplePane;
    protected Node control;

    protected static final double RIPPLE_MAX_RADIUS = 300;

    protected boolean enabled = true;
    protected boolean forceOverlay = false;
    private final Interpolator rippleInterpolator = Interpolator.SPLINE(
            0.0825, 0.3025, 0.0875, 0.9975
    );

    public JFXRipple() {
        this(null, RippleMask.RECT, RipplePos.FRONT);
    }

    public JFXRipple(Node control) {
        this(control, RippleMask.RECT, RipplePos.FRONT);
    }

    public JFXRipple(Node control, RipplePos pos) {
        this(control, RippleMask.RECT, pos);
    }

    public JFXRipple(Node control, RippleMask mask) {
        this(control, mask, RipplePos.FRONT);
    }

    public JFXRipple(Node control, RippleMask mask, RipplePos pos) {
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
        rippleGenerator = new RippleGenerator();
        ripplePane = new StackPane();
        ripplePane.setMouseTransparent(true);
        ripplePane.getChildren().add(rippleGenerator);
        getChildren().add(ripplePane);
    }

    public void setControl(Node control) {
        if (control != null) {
            this.control = control;
            positionControl(control);
            initControlListeners();
        }
    }

    protected void positionControl(Node control) {
        if (this.position.get() == RipplePos.BACK) {
            getChildren().add(control);
        } else {
            getChildren().add(0, control);
        }
    }

    protected void updateControlPosition() {
        if (this.position.get() == RipplePos.BACK) {
            ripplePane.toBack();
        } else {
            ripplePane.toFront();
        }
    }

    public Node getControl() {
        return control;
    }

    public void setEnabled(boolean enable) {
        this.enabled = enable;
    }

    protected Node getMask() {
        double borderWidth = ripplePane.getBorder() != null ? ripplePane.getBorder().getInsets().getTop() : 0;
        Bounds bounds = control.getBoundsInParent();
        double width = control.getLayoutBounds().getWidth();
        double height = control.getLayoutBounds().getHeight();
        double diffMinX = Math.abs(control.getBoundsInLocal().getMinX() - control.getLayoutBounds().getMinX());
        double diffMinY = Math.abs(control.getBoundsInLocal().getMinY() - control.getLayoutBounds().getMinY());
        double diffMaxX = Math.abs(control.getBoundsInLocal().getMaxX() - control.getLayoutBounds().getMaxX());
        double diffMaxY = Math.abs(control.getBoundsInLocal().getMaxY() - control.getLayoutBounds().getMaxY());
        Node mask;

        switch (getMaskType()) {
            case RECT: {
                mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
                        bounds.getMinY() + diffMinY - snappedTopInset(),
                        width - 2 * borderWidth,
                        height - 2 * borderWidth);
                break;
            }

            case CIRCLE: {
                double radius = Math.min((width / 2) - 2 * borderWidth, (height / 2) - 2 * borderWidth);
                mask = new Circle((bounds.getMinX() + diffMinX + bounds.getMaxX() - diffMaxX) / 2 - snappedLeftInset(),
                        (bounds.getMinY() + diffMinY + bounds.getMaxY() - diffMaxY) / 2 - snappedTopInset(), radius, Color.BLUE);
                break;
            }

            case FIT: {
                mask = new Region();
                if (control instanceof Shape) {
                    ((Region) mask).setShape((Shape) control);
                } else if (control instanceof Region) {
                    ((Region) mask).setShape(((Region) control).getShape());
                    JFXNodeUtil.updateBackground(((Region) control).getBackground(), (Region) mask);
                }
                mask.resize(width, height);
                mask.relocate(bounds.getMinX() + diffMinX, bounds.getMinY() + diffMinY);
                break;
            }

            default:
                mask = new Rectangle(bounds.getMinX() + diffMinX - snappedLeftInset(),
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
        control.layoutBoundsProperty().addListener(observable -> resetRipple());
        if (getChildren().contains(control)) {
            control.boundsInParentProperty().addListener(observable -> resetRipple());
        }
        control.addEventHandler(MouseEvent.MOUSE_PRESSED, (event) -> createRipple(event.getX(), event.getY()));
        control.addEventHandler(MouseEvent.MOUSE_RELEASED, e -> releaseRipple());
    }

    protected void createRipple(double x, double y) {
        if (!isRippleDisabled()) {
            rippleGenerator.setGeneratorCenterX(x);
            rippleGenerator.setGeneratorCenterY(y);
            rippleGenerator.createRipple();
        }
    }

    protected void releaseRipple() {
        rippleGenerator.releaseRipple();
    }

    public Runnable createManualRipple() {
        if (!isRippleDisabled()) {
            rippleGenerator.setGeneratorCenterX(control.getLayoutBounds().getWidth() / 2);
            rippleGenerator.setGeneratorCenterY(control.getLayoutBounds().getHeight() / 2);
            rippleGenerator.createRipple();
            return this::releaseRipple;
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
            forceOverlay = false;
            hideOverlay();
        }
    }

    private void showOverlay() {
        if (rippleGenerator.overlayRect != null) {
            rippleGenerator.overlayRect.outAnimation.stop();
        }
        rippleGenerator.createOverlay();
        rippleGenerator.overlayRect.inAnimation.play();
    }

    private void hideOverlay() {
        if (!forceOverlay) {
            if (rippleGenerator.overlayRect != null) {
                rippleGenerator.overlayRect.inAnimation.stop();
            }
            if (rippleGenerator.overlayRect != null) {
                rippleGenerator.overlayRect.outAnimation.play();
            }
        } else {
            System.err.println("Ripple Overlay is forced!");
        }
    }

    final class RippleGenerator extends Group {

        private double generatorCenterX = 0;
        private double generatorCenterY = 0;
        private OverLayRipple overlayRect;
        private final AtomicBoolean generating = new AtomicBoolean(false);
        private boolean cacheRippleClip = false;
        private boolean resetClip = false;
        private final Queue<Ripple> rippleQueue = new LinkedList<>();

        RippleGenerator() {
            this.setManaged(false);
            this.setCache(true);
            this.setCacheHint(CacheHint.SPEED);
        }

        void createRipple() {
            if (enabled) {
                if (!generating.getAndSet(true)) {
                    createOverlay();
                    if (this.getClip() == null || (getChildren().size() == 1 && !cacheRippleClip) || resetClip) {
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
            cacheRippleClip = cached;
        }

        void createOverlay() {
            if (overlayRect == null) {
                overlayRect = new OverLayRipple();
                overlayRect.setClip(getMask());
                getChildren().add(0, overlayRect);
                overlayRect.fillProperty().bind(Bindings.createObjectBinding(() -> {
                    if (rippleFill.get() instanceof Color) {
                        return new Color(((Color) rippleFill.get()).getRed(),
                                ((Color) rippleFill.get()).getGreen(),
                                ((Color) rippleFill.get()).getBlue(), 0.2);
                    } else {
                        return Color.TRANSPARENT;
                    }
                }, rippleFill));
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
                if (JFXRipple.this.getChildrenUnmodifiable().contains(control)) {
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
                super(centerX, centerY, rippleRadius.get().doubleValue() == Region.USE_COMPUTED_SIZE ? computeRippleRadius() : rippleRadius.get().doubleValue(), null);
                setCache(true);
                setCacheHint(CacheHint.SPEED);
                setCacheShape(true);
                setManaged(false);
                setSmooth(true);

                KeyValue[] inKeyValues = new KeyValue[isRippleRecenter() ? 4 : 2];
                outKeyValues = new KeyValue[isRippleRecenter() ? 5 : 3];

                inKeyValues[0] = new KeyValue(scaleXProperty(), 0.9, rippleInterpolator);
                inKeyValues[1] = new KeyValue(scaleYProperty(), 0.9, rippleInterpolator);

                outKeyValues[0] = new KeyValue(this.scaleXProperty(), 1, rippleInterpolator);
                outKeyValues[1] = new KeyValue(this.scaleYProperty(), 1, rippleInterpolator);
                outKeyValues[2] = new KeyValue(this.opacityProperty(), 0, rippleInterpolator);

                if (isRippleRecenter()) {
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
                if (rippleFill.get() instanceof Color) {
                    Color circleColor = new Color(((Color) rippleFill.get()).getRed(),
                            ((Color) rippleFill.get()).getGreen(),
                            ((Color) rippleFill.get()).getBlue(), 0.3);
                    setStroke(circleColor);
                    setFill(circleColor);
                } else {
                    setStroke(rippleFill.get());
                    setFill(rippleFill.get());
                }
            }
        }

        public void clear() {
            getChildren().clear();
            rippleGenerator.overlayRect = null;
            generating.set(false);
        }
    }

    private void resetOverLay() {
        if (rippleGenerator.overlayRect != null) {
            rippleGenerator.overlayRect.inAnimation.stop();
            final RippleGenerator.OverLayRipple oldOverlay = rippleGenerator.overlayRect;
            rippleGenerator.overlayRect.outAnimation.setOnFinished((finish) -> rippleGenerator.getChildren().remove(oldOverlay));
            rippleGenerator.overlayRect.outAnimation.play();
            rippleGenerator.overlayRect = null;
        }
    }

    private void resetClip() {
        this.rippleGenerator.resetClip = true;
    }

    protected void resetRipple() {
        resetOverLay();
        resetClip();
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-ripple";

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }

    private final StyleableObjectProperty<Boolean> rippleRecenter = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLE_RECENTER, JFXRipple.this, "rippleRecenter", false);

    public Boolean isRippleRecenter() {
        return rippleRecenter != null && rippleRecenter.get();
    }

    public StyleableObjectProperty<Boolean> rippleRecenterProperty() {
        return this.rippleRecenter;
    }

    public void setRippleRecenter(Boolean radius) {
        this.rippleRecenter.set(radius);
    }

    private final StyleableObjectProperty<Number> rippleRadius = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLE_RADIUS, JFXRipple.this, "rippleRadius", Region.USE_COMPUTED_SIZE);

    public Number getRippleRadius() {
        return rippleRadius == null ? Region.USE_COMPUTED_SIZE : rippleRadius.get();
    }

    public StyleableObjectProperty<Number> rippleRadiusProperty() {
        return this.rippleRadius;
    }

    public void setRippleRadius(Number radius) {
        this.rippleRadius.set(radius);
    }

    private final StyleableObjectProperty<Paint> rippleFill = new SimpleStyleableObjectProperty<>(StyleableProperties.RIPPLE_FILL, JFXRipple.this, "rippleFill", Color.rgb(0, 200, 255));

    public Paint getRippleFill() {
        return this.rippleFill == null ? Color.rgb(0, 200, 255) : rippleFill.get();
    }

    public StyleableObjectProperty<Paint> rippleFillProperty() {
        return this.rippleFill;
    }

    public void setRippleFill(Paint color) {
        this.rippleFill.set(color);
    }

    private final StyleableObjectProperty<RippleMask> maskType = new SimpleStyleableObjectProperty<>(StyleableProperties.MASK_TYPE, JFXRipple.this, "maskType", RippleMask.RECT);

    public RippleMask getMaskType() {
        return maskType == null ? RippleMask.RECT : maskType.get();
    }

    public StyleableObjectProperty<RippleMask> maskTypeProperty() {
        return this.maskType;
    }

    public void setMaskType(RippleMask type) {
        this.maskType.set(type);
    }

    private final StyleableBooleanProperty rippleDisabled = new SimpleStyleableBooleanProperty(StyleableProperties.RIPPLE_DISABLED, JFXRipple.this, "rippleDisabled", false);

    public Boolean isRippleDisabled() {
        return this.rippleDisabled.get();
    }

    public StyleableBooleanProperty rippleDisabledProperty() {
        return this.rippleDisabled;
    }

    public void setRippleDisabled(Boolean disabled) {
        this.rippleDisabled.set(disabled);
    }

    protected ObjectProperty<RipplePos> position = new SimpleObjectProperty<>();

    public void setPosition(RipplePos pos) {
        this.position.set(pos);
    }

    public RipplePos getPosition() {
        return position == null ? RipplePos.FRONT : position.get();
    }

    public ObjectProperty<RipplePos> positionProperty() {
        return this.position;
    }

    private static final class StyleableProperties {
        private static final CssMetaData<JFXRipple, Boolean> RIPPLE_RECENTER = new CssMetaData<JFXRipple, Boolean>("-jfx-ripple-recenter", BooleanConverter.getInstance(), false) {
            @Override
            public boolean isSettable(JFXRipple styleable) {
                return styleable.rippleRecenter == null || !styleable.rippleRecenter.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(JFXRipple styleable) {
                return styleable.rippleRecenterProperty();
            }
        };

        private static final CssMetaData<JFXRipple, Boolean> RIPPLE_DISABLED = new CssMetaData<JFXRipple, Boolean>("-jfx-ripple-disabled", BooleanConverter.getInstance(), false) {
            @Override
            public boolean isSettable(JFXRipple styleable) {
                return styleable.rippleDisabled == null || !styleable.rippleDisabled.isBound();
            }

            @Override
            public StyleableProperty<Boolean> getStyleableProperty(JFXRipple styleable) {
                return styleable.rippleDisabledProperty();
            }
        };

        private static final CssMetaData<JFXRipple, Paint> RIPPLE_FILL = new CssMetaData<JFXRipple, Paint>("-jfx-ripple-fill", PaintConverter.getInstance(), Color.rgb(0, 200, 250)) {
            @Override
            public boolean isSettable(JFXRipple styleable) {
                return styleable.rippleFill == null || !styleable.rippleFill.isBound();
            }

            @Override
            public StyleableProperty<Paint> getStyleableProperty(JFXRipple styleable) {
                return styleable.rippleFillProperty();
            }
        };

        private static final CssMetaData<JFXRipple, Number> RIPPLE_RADIUS = new CssMetaData<JFXRipple, Number>("-jfx-ripple-radius", SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
            @Override
            public boolean isSettable(JFXRipple styleable) {
                return styleable.rippleRadius == null || !styleable.rippleRadius.isBound();
            }

            @Override
            public StyleableProperty<Number> getStyleableProperty(JFXRipple styleable) {
                return styleable.rippleRadiusProperty();
            }
        };

        private static final CssMetaData<JFXRipple, RippleMask> MASK_TYPE = new CssMetaData<JFXRipple, RippleMask>("-jfx-mask-type", RippleMaskTypeConverter.getInstance(), RippleMask.RECT) {
            @Override
            public boolean isSettable(JFXRipple styleable) {
                return styleable.maskType == null || !styleable.maskType.isBound();
            }

            @Override
            public StyleableProperty<RippleMask> getStyleableProperty(JFXRipple styleable) {
                return styleable.maskTypeProperty();
            }
        };

        private static final List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables = new ArrayList<>(StackPane.getClassCssMetaData());
            Collections.addAll(styleables,
                    RIPPLE_RECENTER,
                    RIPPLE_RADIUS,
                    RIPPLE_FILL,
                    MASK_TYPE,
                    RIPPLE_DISABLED
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
