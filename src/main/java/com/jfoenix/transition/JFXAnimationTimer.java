package com.jfoenix.transition;

import javafx.animation.AnimationTimer;
import javafx.beans.value.WritableValue;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.*;
import java.util.function.Supplier;

public class JFXAnimationTimer extends AnimationTimer {

    private Set<AnimationHandler> animationHandlers = new HashSet<>();
    private long startTime = -1;
    private boolean running = false;
    private List<CacheMemento> caches = new ArrayList<>();
    private double totalElapsedMilliseconds;
    private HashMap<JFXKeyFrame, AnimationHandler> mutableFrames = new HashMap<>();
    private Runnable onFinished = null;

    public JFXAnimationTimer(JFXKeyFrame... keyFrames) {
        for (JFXKeyFrame keyFrame : keyFrames) {
            Duration duration = keyFrame.getDuration();
            final Set<JFXKeyValue<?>> keyValuesSet = keyFrame.getValues();
            if (!keyValuesSet.isEmpty()) {
                animationHandlers.add(new AnimationHandler(duration, keyFrame.getAnimateCondition(), keyFrame.getValues()));
            }
        }
    }

    public void addKeyFrame(JFXKeyFrame keyFrame) throws Exception {
        if (isRunning()) {
            throw new Exception("Can't update animation timer while running");
        }
        Duration duration = keyFrame.getDuration();
        final Set<JFXKeyValue<?>> keyValuesSet = keyFrame.getValues();
        if (!keyValuesSet.isEmpty()) {
            final AnimationHandler handler = new AnimationHandler(duration, keyFrame.getAnimateCondition(), keyFrame.getValues());
            animationHandlers.add(handler);
            mutableFrames.put(keyFrame, handler);
        }
    }

    public void removeKeyFrame(JFXKeyFrame keyFrame) throws Exception {
        if (isRunning()) {
            throw new Exception("Can't update animation timer while running");
        }
        AnimationHandler handler = mutableFrames.get(keyFrame);
        animationHandlers.remove(handler);
    }

    @Override
    public void start() {
        super.start();
        running = true;
        startTime = -1;
        for (AnimationHandler animationHandler : animationHandlers) {
            animationHandler.init();
        }
        for (CacheMemento cache : caches) {
            cache.cache();
        }
    }

    @Override
    public void handle(long now) {
        startTime = startTime == -1 ? now : startTime;
        totalElapsedMilliseconds = (now - startTime) / 1000000.0;
        boolean stop = true;
        for (AnimationHandler handler : animationHandlers) {
            handler.animate(totalElapsedMilliseconds);
            if (!handler.finished) {
                stop = false;
            }
        }
        if (stop) {
            this.stop();
        }
    }

    /**
     * this method will pause the timer and reverse the animation if the timer already
     * started otherwise it will start the animation.
     */
    public void reverseAndContinue() {
        if (isRunning()) {
            super.stop();
            for (AnimationHandler handler : animationHandlers) {
                handler.reverse(totalElapsedMilliseconds);
            }
            startTime = -1;
            super.start();
        } else {
            start();
        }
    }

    @Override
    public void stop() {
        super.stop();
        running = false;
        for (AnimationHandler handler : animationHandlers) {
            handler.clear();
        }
        for (CacheMemento cache : caches) {
            cache.restore();
        }
        if (onFinished != null) {
            onFinished.run();
        }
    }

    public void applyEndValues() {
        if (isRunning()) {
            super.stop();
        }
        for (AnimationHandler handler : animationHandlers) {
            handler.applyEndValues();
        }
        startTime = -1;
    }

    public boolean isRunning() {
        return running;
    }

    public void setOnFinished(Runnable onFinished) {
        this.onFinished = onFinished;
    }

    public void setCacheNodes(Node... nodesToCache) {
        caches.clear();
        if (nodesToCache != null) {
            for (Node node : nodesToCache) {
                caches.add(new CacheMemento(node));
            }
        }
    }

    public void dispose() {
        caches.clear();
        for (AnimationHandler handler : animationHandlers) {
            handler.dispose();
        }
        animationHandlers.clear();
    }

    static class AnimationHandler {
        private double duration;
        private double currentDuration;
        private Set<JFXKeyValue<?>> keyValues;
        private Supplier<Boolean> animationCondition = null;
        private boolean finished = false;

        private HashMap<WritableValue<?>, Object> initialValuesMap = new HashMap<>();
        private HashMap<WritableValue<?>, Object> endValuesMap = new HashMap<>();

        AnimationHandler(Duration duration, Supplier<Boolean> animationCondition, Set<JFXKeyValue<?>> keyValues) {
            this.duration = duration.toMillis();
            currentDuration = this.duration;
            this.keyValues = keyValues;
            this.animationCondition = animationCondition;
        }

        public void init() {
            finished = animationCondition == null ? false : !animationCondition.get();
            for (JFXKeyValue keyValue : keyValues) {
                if (keyValue.getTarget() != null) {
                    // replaced putIfAbsent for mobile compatibility
                    if (!initialValuesMap.containsKey(keyValue.getTarget())) {
                        initialValuesMap.put(keyValue.getTarget(), keyValue.getTarget().getValue());
                    }
                    if (!endValuesMap.containsKey(keyValue.getTarget())) {
                        endValuesMap.put(keyValue.getTarget(), keyValue.getEndValue());
                    }
                }
            }
        }

        void reverse(double now) {
            finished = animationCondition == null ? false : !animationCondition.get();
            currentDuration = duration - (currentDuration - now);
            // update initial values
            for (JFXKeyValue keyValue : keyValues) {
                final WritableValue target = keyValue.getTarget();
                if (target != null) {
                    initialValuesMap.put(target, target.getValue());
                    endValuesMap.put(target, keyValue.getEndValue());
                }
            }
        }

        // now in milliseconds
        public void animate(double now) {
            // if animate condition for the key frame is not met then do nothing
            if (finished) {
                return;
            }
            if (now <= currentDuration) {
                for (JFXKeyValue keyValue : keyValues) {
                    if (keyValue.isValid()) {
                        final WritableValue target = keyValue.getTarget();
                        final Object endValue = endValuesMap.get(target);
                        if (endValue != null && target != null && !target.getValue().equals(endValue)) {
                            target.setValue(keyValue.getInterpolator().interpolate(initialValuesMap.get(target), endValue, now / currentDuration));
                        }
                    }
                }
            } else {
                if (!finished) {
                    finished = true;
                    for (JFXKeyValue keyValue : keyValues) {
                        if (keyValue.isValid()) {
                            final WritableValue target = keyValue.getTarget();
                            if (target != null) {
                                // set updated end value instead of cached
                                final Object endValue = keyValue.getEndValue();
                                if (endValue != null) {
                                    target.setValue(endValue);
                                }
                            }
                        }
                    }
                    currentDuration = duration;
                }
            }
        }

        public void applyEndValues() {
            for (JFXKeyValue keyValue : keyValues) {
                if (keyValue.isValid()) {
                    final WritableValue target = keyValue.getTarget();
                    if (target != null) {
                        final Object endValue = keyValue.getEndValue();
                        if (endValue != null && !target.getValue().equals(endValue)) {
                            target.setValue(endValue);
                        }
                    }
                }
            }
        }

        public void clear() {
            initialValuesMap.clear();
            endValuesMap.clear();
        }

        void dispose() {
            clear();
            keyValues.clear();
        }
    }
}
