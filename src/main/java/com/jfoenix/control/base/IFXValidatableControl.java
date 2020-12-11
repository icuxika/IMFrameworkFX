package com.jfoenix.control.base;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

public interface IFXValidatableControl {

    ValidatorBase getActiveValidator();

    ReadOnlyObjectProperty<ValidatorBase> activeValidatorProperty();

    ObservableList<ValidatorBase> getValidators();

    void setValidators(ValidatorBase... validators);

    boolean validate();

    void resetValidation();
}
