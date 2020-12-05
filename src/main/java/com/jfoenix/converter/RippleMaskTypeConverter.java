package com.jfoenix.converter;

import com.jfoenix.control.JFXRipple;
import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.text.Font;

public class RippleMaskTypeConverter extends StyleConverter<String, JFXRipple.RippleMask> {

    private static class Holder {
        static final RippleMaskTypeConverter INSTANCE = new RippleMaskTypeConverter();
    }

    public static StyleConverter<String, JFXRipple.RippleMask> getInstance() {
        return Holder.INSTANCE;
    }

    private RippleMaskTypeConverter() {
    }

    @Override
    public JFXRipple.RippleMask convert(ParsedValue<String, JFXRipple.RippleMask> value, Font font) {
        String string = value.getValue();
        try {
            return JFXRipple.RippleMask.valueOf(string);
        } catch (IllegalArgumentException | NullPointerException exception) {
            return JFXRipple.RippleMask.RECT;
        }
    }

    @Override
    public String toString() {
        return "RippleMaskTypeConverter";
    }
}
