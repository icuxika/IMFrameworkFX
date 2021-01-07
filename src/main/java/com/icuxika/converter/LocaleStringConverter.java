package com.icuxika.converter;

import javafx.util.StringConverter;

import java.util.Locale;

public class LocaleStringConverter extends StringConverter<Locale> {
    @Override
    public String toString(Locale object) {
        if (object != null) {
            return object.getDisplayLanguage(object);
        }
        return null;
    }

    @Override
    public Locale fromString(String string) {
        if (string != null) {
            return new Locale(string);
        }
        return null;
    }
}
