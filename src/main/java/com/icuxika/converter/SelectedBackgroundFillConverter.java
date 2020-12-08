package com.icuxika.converter;

import javafx.css.ParsedValue;
import javafx.css.StyleConverter;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

public class SelectedBackgroundFillConverter extends StyleConverter<String, Paint> {

    public SelectedBackgroundFillConverter() {
        super();
    }

    public static class Holder {
        static final SelectedBackgroundFillConverter INSTANCE = new SelectedBackgroundFillConverter();

        private Holder() {
            throw new IllegalAccessError("Holder class");
        }
    }

    public static StyleConverter<String, Paint> getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public Paint convert(ParsedValue<String, Paint> value, Font font) {
        return super.convert(value, font);
    }
}
