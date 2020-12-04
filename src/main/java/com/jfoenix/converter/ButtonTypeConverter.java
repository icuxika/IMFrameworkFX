package com.jfoenix.converter;

import com.jfoenix.control.JFXButton;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public class ButtonTypeConverter extends StyleConverter<String, JFXButton.ButtonType> {

    public ButtonTypeConverter() {
        super();
    }

    private static class Holder {
        static final ButtonTypeConverter INSTANCE = new ButtonTypeConverter();

        private Holder() {
            throw new IllegalAccessError("Holder class");
        }
    }

    public static StyleConverter<String, JFXButton.ButtonType> getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public JFXButton.ButtonType convert(ParsedValue<String, JFXButton.ButtonType> value, Font font) {
        String string = value.getValue();
        try {
            return JFXButton.ButtonType.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return JFXButton.ButtonType.FLAT;
        }
    }

    @Override
    public String toString() {
        return "ButtonTypeConverter";
    }
}
