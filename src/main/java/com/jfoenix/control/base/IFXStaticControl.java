package com.jfoenix.control.base;

import javafx.css.StyleableBooleanProperty;

public interface IFXStaticControl {

    StyleableBooleanProperty disableAnimationProperty();

    Boolean isDisableAnimation();

    void setDisableAnimation(Boolean disabled);
}
