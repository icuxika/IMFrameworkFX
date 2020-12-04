package com.icuxika;

import javafx.scene.control.ListCell;

import java.util.Locale;

public class LanguageListCell extends ListCell<Locale> {

    @Override
    protected void updateItem(Locale item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            setText(item.getDisplayLanguage(item));
        }
    }
}
