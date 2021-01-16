package com.jfoenix.control.base;

import javafx.css.StyleableBooleanProperty;
import javafx.css.StyleableObjectProperty;
import javafx.scene.paint.Paint;

public interface IFXLabelFloatControl extends IFXValidatableControl, IFXStaticControl {

    StyleableBooleanProperty labelFloatProperty();

    boolean isLabelFloat();

    void setLabelFloat(boolean labelFloat);

    Paint getUnFocusColor();

    StyleableObjectProperty<Paint> unFocusColorProperty();

    void setUnFocusColor(Paint color);

    Paint getFocusColor();

    StyleableObjectProperty<Paint> focusColorProperty();

    void setFocusColor(Paint color);
}
