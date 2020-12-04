package com.jfoenix.converter;

import com.jfoenix.control.JFXRippler;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public class RipplerMaskTypeConverter extends StyleConverter<String, JFXRippler.RipplerMask> {

    private static class Holder {
        static final RipplerMaskTypeConverter INSTANCE = new RipplerMaskTypeConverter();
    }

    public static StyleConverter<String, JFXRippler.RipplerMask> getInstance() {
        return Holder.INSTANCE;
    }

    private RipplerMaskTypeConverter() {
    }

    @Override
    public JFXRippler.RipplerMask convert(ParsedValue<String, JFXRippler.RipplerMask> value, Font font) {
        String string = value.getValue();
        try {
            return JFXRippler.RipplerMask.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return JFXRippler.RipplerMask.RECT;
        }
    }

    @Override
    public String toString() {
        return "RipplerMaskTypeConverter";
    }
}
