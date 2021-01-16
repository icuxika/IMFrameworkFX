package com.jfoenix.transition;

import javafx.animation.Interpolator;
import javafx.beans.value.WritableValue;

import java.util.function.Supplier;

public class JFXKeyValue<T> {

    private WritableValue<T> target;
    private Supplier<WritableValue<T>> targetSupplier;
    private Supplier<T> endValueSupplier;
    private T endValue;
    private Supplier<Boolean> animateCondition = () -> true;
    private Interpolator interpolator;

    private JFXKeyValue() {
    }

    // this builder is created to ensure type inference from method arguments
    public static Builder builder() {
        return new Builder();
    }

    public T getEndValue() {
        return endValue == null ? endValueSupplier.get() : endValue;
    }

    public WritableValue<T> getTarget() {
        return target == null ? targetSupplier.get() : target;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public boolean isValid() {
        return animateCondition == null || animateCondition.get();
    }


    public static final class Builder {
        public <T> JFXKeyValueBuilder<T> setTarget(WritableValue<T> target) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setTarget(target);
            return builder;
        }

        public <T> JFXKeyValueBuilder<T> setTargetSupplier(Supplier<WritableValue<T>> targetSupplier) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setTargetSupplier(targetSupplier);
            return builder;
        }

        public <T> JFXKeyValueBuilder<T> setEndValueSupplier(Supplier<T> endValueSupplier) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setEndValueSupplier(endValueSupplier);
            return builder;
        }

        public <T> JFXKeyValueBuilder<T> setEndValue(T endValue) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setEndValue(endValue);
            return builder;
        }

        public <T> JFXKeyValueBuilder<T> setAnimateCondition(Supplier<Boolean> animateCondition) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setAnimateCondition(animateCondition);
            return builder;
        }

        public <T> JFXKeyValueBuilder<T> setInterpolator(Interpolator interpolator) {
            JFXKeyValueBuilder<T> builder = new JFXKeyValueBuilder<>();
            builder.setInterpolator(interpolator);
            return builder;
        }
    }


    public static final class JFXKeyValueBuilder<T> {

        private WritableValue<T> target;
        private Supplier<WritableValue<T>> targetSupplier;
        private Supplier<T> endValueSupplier;
        private T endValue;
        private Supplier<Boolean> animateCondition = () -> true;
        private Interpolator interpolator = Interpolator.EASE_BOTH;

        private JFXKeyValueBuilder() {
        }

        public JFXKeyValueBuilder<T> setTarget(WritableValue<T> target) {
            this.target = target;
            return this;
        }

        public JFXKeyValueBuilder<T> setTargetSupplier(Supplier<WritableValue<T>> targetSupplier) {
            this.targetSupplier = targetSupplier;
            return this;
        }

        public JFXKeyValueBuilder<T> setEndValueSupplier(Supplier<T> endValueSupplier) {
            this.endValueSupplier = endValueSupplier;
            return this;
        }

        public JFXKeyValueBuilder<T> setEndValue(T endValue) {
            this.endValue = endValue;
            return this;
        }

        public JFXKeyValueBuilder<T> setAnimateCondition(Supplier<Boolean> animateCondition) {
            this.animateCondition = animateCondition;
            return this;
        }

        public JFXKeyValueBuilder<T> setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public JFXKeyValue<T> build() {
            JFXKeyValue<T> jFXKeyValue = new JFXKeyValue<>();
            jFXKeyValue.target = this.target;
            jFXKeyValue.interpolator = this.interpolator;
            jFXKeyValue.targetSupplier = this.targetSupplier;
            jFXKeyValue.endValue = this.endValue;
            jFXKeyValue.endValueSupplier = this.endValueSupplier;
            jFXKeyValue.animateCondition = this.animateCondition;
            return jFXKeyValue;
        }
    }
}
