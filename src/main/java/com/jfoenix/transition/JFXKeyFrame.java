package com.jfoenix.transition;

import javafx.util.Duration;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.Supplier;

public class JFXKeyFrame {

    private Duration duration;
    private Set<JFXKeyValue<?>> keyValues = new CopyOnWriteArraySet<>();
    private Supplier<Boolean> animateCondition = null;

    public JFXKeyFrame(Duration duration, JFXKeyValue<?>... keyValues) {
        this.duration = duration;
        for (final JFXKeyValue<?> keyValue : keyValues) {
            if (keyValue != null) {
                this.keyValues.add(keyValue);
            }
        }
    }

    private JFXKeyFrame() {

    }

    public static Builder builder() {
        return new Builder();
    }

    public final Duration getDuration() {
        return duration;
    }

    public final Set<JFXKeyValue<?>> getValues() {
        return keyValues;
    }

    public Supplier<Boolean> getAnimateCondition() {
        return animateCondition;
    }

    public static final class Builder {
        private Duration duration;
        private Set<JFXKeyValue<?>> keyValues = new CopyOnWriteArraySet<>();
        private Supplier<Boolean> animateCondition = null;

        private Builder() {
        }

        public Builder setDuration(Duration duration) {
            this.duration = duration;
            return this;
        }

        public Builder setKeyValues(JFXKeyValue<?>... keyValues) {
            for (final JFXKeyValue<?> keyValue : keyValues) {
                if (keyValue != null) {
                    this.keyValues.add(keyValue);
                }
            }
            return this;
        }

        public Builder setAnimateCondition(Supplier<Boolean> animateCondition) {
            this.animateCondition = animateCondition;
            return this;
        }

        public JFXKeyFrame build() {
            JFXKeyFrame jFXKeyFrame = new JFXKeyFrame();
            jFXKeyFrame.duration = this.duration;
            jFXKeyFrame.keyValues = this.keyValues;
            jFXKeyFrame.animateCondition = this.animateCondition;
            return jFXKeyFrame;
        }
    }
}
