package com.jfoenix.control;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;

public class JFXSnackbarLayout extends BorderPane {

    private Label toast;
    private JFXButton action;
    private StackPane actionContainer;

    public JFXSnackbarLayout(String message) {
        this(message, null, null);
    }

    public JFXSnackbarLayout(String message, String actionText, EventHandler<ActionEvent> actionHandler) {
        initialize();

        toast = new Label();
        toast.setMinWidth(Control.USE_PREF_SIZE);
        toast.getStyleClass().add("jfx-snackbar-toast");
        toast.setWrapText(true);
        toast.setText(message);
        StackPane toastContainer = new StackPane(toast);
        toastContainer.setPadding(new Insets(20));
        actionContainer = new StackPane();
        actionContainer.setPadding(new Insets(0, 10, 0, 0));

        toast.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
            if (getPrefWidth() == -1) {
                return getPrefWidth();
            }
            double actionWidth = actionContainer.isVisible() ? actionContainer.getWidth() : 0.0;
            return prefWidthProperty().get() - actionWidth;
        }, prefWidthProperty(), actionContainer.widthProperty(), actionContainer.visibleProperty()));

        setLeft(toastContainer);
        setRight(actionContainer);

        if (actionText != null) {
            action = new JFXButton();
            action.setText(actionText);
            action.setOnAction(actionHandler);
            action.setMinWidth(Control.USE_PREF_SIZE);
            action.setButtonType(JFXButton.ButtonType.FLAT);
            action.getStyleClass().add("jfx-snackbar-action");
            // actions will be added upon showing the snackbar if needed
            actionContainer.getChildren().add(action);

            if (actionText != null && !actionText.isEmpty()) {
                action.setVisible(true);
                actionContainer.setVisible(true);
                actionContainer.setManaged(true);
                // to force updating the layout bounds
                action.setText("");
                action.setText(actionText);
                action.setOnAction(actionHandler);
            } else {
                actionContainer.setVisible(false);
                actionContainer.setManaged(false);
                action.setVisible(false);
            }
        }
    }

    private static final String DEFAULT_STYLE_CLASS = "jfx-snackbar-layout";

    public String getToast() {
        return toast.getText();
    }

    public void setToast(String toast) {
        this.toast.setText(toast);
    }


    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}

