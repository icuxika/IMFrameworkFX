package com.jfoenix.validation;

import com.jfoenix.validation.base.ValidatorBase;
import javafx.beans.DefaultProperty;
import javafx.scene.control.ComboBoxBase;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;

/**
 * An example of required field validation, that is applied on text input
 * controls such as {@link TextField} and {@link TextArea}
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
@DefaultProperty(value = "icon")
public class RequiredFieldValidator extends ValidatorBase {

    public RequiredFieldValidator(String message) {
        super(message);
    }

    public RequiredFieldValidator() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void eval() {
        if (srcControl.get() instanceof TextInputControl) {
            evalTextInputField();
        }
        if (srcControl.get() instanceof ComboBoxBase) {
            evalComboBoxField();
        }
    }

    private void evalTextInputField() {
        TextInputControl textField = (TextInputControl) srcControl.get();
        if (textField.getText() == null || textField.getText().isEmpty()) {
            hasErrors.set(true);
        } else {
            hasErrors.set(false);
        }
    }

    private void evalComboBoxField() {
        ComboBoxBase comboField = (ComboBoxBase) srcControl.get();
        Object value = comboField.getValue();
        hasErrors.set(value == null || value.toString().isEmpty());
    }
}
