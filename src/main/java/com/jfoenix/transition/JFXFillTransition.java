package com.jfoenix.transition;

import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ObjectPropertyBase;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.CacheHint;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public final class JFXFillTransition extends Transition {

    private static final Duration DEFAULT_DURATION = Duration.millis(400);
    private static final Color DEFAULT_FROM_VALUE = null;
    private static final Color DEFAULT_TO_VALUE = null;
    private Color start;

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/
    private Color end;
    private CacheHint oldCacheHint = CacheHint.DEFAULT;
    private boolean oldCache = false;
    /**
     * The target region of this {@code JFXFillTransition}.
     */
    private ObjectProperty<Region> region;
    /**
     * The duration of this {@code JFXFillTransition}.
     * <p>
     * Note: While the unit of {@code duration} is a millisecond, the
     * granularity depends on the underlying operating system and will in
     * general be larger. For example animations on desktop systems usually run
     * with a maximum of 60fps which gives a granularity of ~17 ms.
     * <p>
     * Setting duration to value lower than {@link Duration#ZERO} will result
     * in {@link IllegalArgumentException}.
     *
     * @defaultValue 400ms
     */
    private ObjectProperty<Duration> duration;
    /**
     * Specifies the start color value for this {@code JFXFillTransition}.
     *
     * @defaultValue {@code null}
     */
    private ObjectProperty<Color> fromValue;
    /**
     * Specifies the stop color value for this {@code JFXFillTransition}.
     *
     * @defaultValue {@code null}
     */
    private ObjectProperty<Color> toValue;
    /**
     * Called when the animation is starting
     */
    private CornerRadii radii;
    private Insets insets;

    /**
     * The constructor of {@code JFXFillTransition}
     *
     * @param duration  The duration of the {@code JFXFillTransition}
     * @param region    The {@code region} which filling will be animated
     * @param fromValue The start value of the color-animation
     * @param toValue   The end value of the color-animation
     */
    public JFXFillTransition(Duration duration, Region region, Color fromValue,
                             Color toValue) {
        setDuration(duration);
        setRegion(region);
        setFromValue(fromValue);
        setToValue(toValue);
        setCycleDuration(duration);
        statusProperty().addListener(new ChangeListener<Status>() {
            @Override
            public void changed(ObservableValue<? extends Status> ov, Status t, Status newStatus) {
                switch (newStatus) {
                    case RUNNING:
                        starting();
                        break;
                    default:
                        stopping();
                        break;
                }
            }
        });
    }

    /**
     * The constructor of {@code JFXFillTransition}
     *
     * @param duration  The duration of the {@code JFXFillTransition}
     * @param fromValue The start value of the color-animation
     * @param toValue   The end value of the color-animation
     */
    public JFXFillTransition(Duration duration, Color fromValue, Color toValue) {
        this(duration, null, fromValue, toValue);
    }

    /**
     * The constructor of {@code JFXFillTransition}
     *
     * @param duration The duration of the {@code JFXFillTransition}
     * @param region   The {@code region} which filling will be animated
     */
    public JFXFillTransition(Duration duration, Region region) {
        this(duration, region, null, null);
    }

    /**
     * The constructor of {@code JFXFillTransition}
     *
     * @param duration The duration of the {@code FadeTransition}
     */
    public JFXFillTransition(Duration duration) {
        this(duration, null, null, null);
    }

    /**
     * The constructor of {@code JFXFillTransition}
     */
    public JFXFillTransition() {
        this(DEFAULT_DURATION, null);
    }

    public final Region getRegion() {
        return (region == null) ? null : region.get();
    }

    public final void setRegion(Region value) {
        if ((region != null) || (value != null /* DEFAULT_SHAPE */)) {
            regionProperty().set(value);
        }
    }

    public final ObjectProperty<Region> regionProperty() {
        if (region == null) {
            region = new SimpleObjectProperty<>(this, "region", null);
        }
        return region;
    }

    public final Duration getDuration() {
        return (duration == null) ? DEFAULT_DURATION : duration.get();
    }

    public final void setDuration(Duration value) {
        if ((duration != null) || (!DEFAULT_DURATION.equals(value))) {
            durationProperty().set(value);
        }
    }

    public final ObjectProperty<Duration> durationProperty() {
        if (duration == null) {
            duration = new ObjectPropertyBase<Duration>(DEFAULT_DURATION) {

                @Override
                public void invalidated() {
                    try {
                        setCycleDuration(getDuration());
                    } catch (IllegalArgumentException e) {
                        if (isBound()) {
                            unbind();
                        }
                        set(getCycleDuration());
                        throw e;
                    }
                }

                @Override
                public Object getBean() {
                    return JFXFillTransition.this;
                }

                @Override
                public String getName() {
                    return "duration";
                }
            };
        }
        return duration;
    }

    public final Color getFromValue() {
        return (fromValue == null) ? DEFAULT_FROM_VALUE : fromValue.get();
    }

    public final void setFromValue(Color value) {
        if ((fromValue != null) || (value != null /* DEFAULT_FROM_VALUE */)) {
            fromValueProperty().set(value);
        }
    }

    public final ObjectProperty<Color> fromValueProperty() {
        if (fromValue == null) {
            fromValue = new SimpleObjectProperty<>(this, "fromValue", DEFAULT_FROM_VALUE);
        }
        return fromValue;
    }

    public final Color getToValue() {
        return (toValue == null) ? DEFAULT_TO_VALUE : toValue.get();
    }

    public final void setToValue(Color value) {
        if ((toValue != null) || (value != null /* DEFAULT_TO_VALUE */)) {
            toValueProperty().set(value);
        }
    }

    public final ObjectProperty<Color> toValueProperty() {
        if (toValue == null) {
            toValue = new SimpleObjectProperty<>(this, "toValue", DEFAULT_TO_VALUE);
        }
        return toValue;
    }

    protected void starting() {
        // init animation values
        if (start == null) {
            oldCache = region.get().isCache();
            oldCacheHint = region.get().getCacheHint();
            radii = region.get().getBackground() == null ? null : region.get()
                    .getBackground()
                    .getFills()
                    .get(0)
                    .getRadii();
            insets = region.get().getBackground() == null ? null : region.get()
                    .getBackground()
                    .getFills()
                    .get(0)
                    .getInsets();
            start = fromValue.get();
            end = toValue.get();
            region.get().setCache(true);
            region.get().setCacheHint(CacheHint.SPEED);
        }
    }

    /**
     * Called when the animation is stopping
     */
    protected void stopping() {
        region.get().setCache(oldCache);
        region.get().setCacheHint(oldCacheHint);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void interpolate(double frac) {
        if (start == null) {
            starting();
        }
        Color newColor = start.interpolate(end, frac);
        if (Color.TRANSPARENT.equals(start)) {
            newColor = new Color(end.getRed(), end.getGreen(), end.getBlue(), newColor.getOpacity());
        }
        region.get().setBackground(new Background(new BackgroundFill(newColor, radii, insets)));
    }
}
