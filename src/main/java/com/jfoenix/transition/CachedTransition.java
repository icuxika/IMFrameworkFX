package com.jfoenix.transition;

import javafx.animation.Timeline;
import javafx.animation.Transition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.util.Duration;

public class CachedTransition extends Transition {
    protected final Node node;
    protected ObjectProperty<Timeline> timeline = new SimpleObjectProperty<>();
    private CacheMemento[] mementos = new CacheMemento[0];

    public CachedTransition(final Node node, final Timeline timeline) {
        this.node = node;
        this.timeline.set(timeline);
        mementos = node == null ? mementos : new CacheMemento[]{new CacheMemento(node)};
        statusProperty().addListener(observable -> {
            switch (getStatus()) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
    }

    public CachedTransition(final Node node, final Timeline timeline, CacheMemento... cacheMomentos) {
        this.node = node;
        this.timeline.set(timeline);
        mementos = new CacheMemento[(node == null ? 0 : 1) + cacheMomentos.length];
        if (node != null) {
            mementos[0] = new CacheMemento(node);
        }
        System.arraycopy(cacheMomentos, 0, mementos, node == null ? 0 : 1, cacheMomentos.length);
        statusProperty().addListener(observable -> {
            switch (getStatus()) {
                case RUNNING:
                    starting();
                    break;
                default:
                    stopping();
                    break;
            }
        });
    }

    /**
     * Called when the animation is starting
     */
    protected void starting() {
        if (mementos != null) {
            for (int i = 0; i < mementos.length; i++) {
                mementos[i].cache();
            }
        }
    }

    /**
     * Called when the animation is stopping
     */
    protected void stopping() {
        if (mementos != null) {
            for (int i = 0; i < mementos.length; i++) {
                mementos[i].restore();
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void interpolate(double d) {
        timeline.get().playFrom(Duration.seconds(d));
        timeline.get().stop();
    }
}
