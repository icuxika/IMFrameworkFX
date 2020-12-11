package com.jfoenix.transition;

import javafx.animation.Interpolator;
import javafx.beans.value.WritableValue;

import java.util.function.Supplier;

/**
 * Wrapper for JFXDrawer animation key value
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2018-05-03
 */
public class JFXDrawerKeyValue<T> {

    private WritableValue<T> target;
    private Supplier<T> closeValueSupplier;
    private Supplier<T> openValueSupplier;
    private Interpolator interpolator;
    private Supplier<Boolean> animateCondition = () -> true;

    public static JFXDrawerKeyValueBuilder builder() {
        return new JFXDrawerKeyValueBuilder();
    }

    public WritableValue<T> getTarget() {
        return target;
    }

    public Supplier<T> getCloseValueSupplier() {
        return closeValueSupplier;
    }

    public Supplier<T> getOpenValueSupplier() {
        return openValueSupplier;
    }

    public Interpolator getInterpolator() {
        return interpolator;
    }

    public boolean isValid() {
        return animateCondition == null ? true : animateCondition.get();
    }

    public void applyOpenValues() {
        target.setValue(getOpenValueSupplier().get());
    }

    public void applyCloseValues() {
        target.setValue(getCloseValueSupplier().get());
    }

    public static final class JFXDrawerKeyValueBuilder<T> {
        private WritableValue<T> target;
        private Interpolator interpolator = Interpolator.EASE_BOTH;
        private Supplier<Boolean> animateCondition = () -> true;
        private Supplier<T> closeValueSupplier;
        private Supplier<T> openValueSupplier;

        private JFXDrawerKeyValueBuilder() {
        }

        public static JFXDrawerKeyValueBuilder aJFXDrawerKeyValue() {
            return new JFXDrawerKeyValueBuilder();
        }

        public JFXDrawerKeyValueBuilder setTarget(WritableValue<T> target) {
            this.target = target;
            return this;
        }

        public JFXDrawerKeyValueBuilder setInterpolator(Interpolator interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public JFXDrawerKeyValueBuilder setAnimateCondition(Supplier<Boolean> animateCondition) {
            this.animateCondition = animateCondition;
            return this;
        }

        public JFXDrawerKeyValueBuilder setCloseValue(T closeValue) {
            this.closeValueSupplier = () -> closeValue;
            return this;
        }

        public JFXDrawerKeyValueBuilder setCloseValueSupplier(Supplier<T> closeValueSupplier) {
            this.closeValueSupplier = closeValueSupplier;
            return this;
        }

        public JFXDrawerKeyValueBuilder setOpenValueSupplier(Supplier<T> openValueSupplier) {
            this.openValueSupplier = openValueSupplier;
            return this;
        }

        public JFXDrawerKeyValueBuilder setOpenValue(T openValue) {
            this.openValueSupplier = () -> openValue;
            return this;
        }

        public JFXDrawerKeyValue<T> build() {
            JFXDrawerKeyValue<T> jFXDrawerKeyValue = new JFXDrawerKeyValue();
            jFXDrawerKeyValue.openValueSupplier = this.openValueSupplier;
            jFXDrawerKeyValue.closeValueSupplier = this.closeValueSupplier;
            jFXDrawerKeyValue.target = this.target;
            jFXDrawerKeyValue.interpolator = this.interpolator;
            jFXDrawerKeyValue.animateCondition = this.animateCondition;
            return jFXDrawerKeyValue;
        }
    }
}
