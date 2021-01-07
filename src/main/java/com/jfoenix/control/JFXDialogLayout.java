package com.jfoenix.control;

import com.jfoenix.JFoenixResource;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class JFXDialogLayout extends VBox {
    private StackPane heading = new StackPane();
    private StackPane body = new StackPane();
    private FlowPane actions = new FlowPane();

    /**
     * creates empty dialog layout
     */
    public JFXDialogLayout() {
        initialize();
        heading.getStyleClass().addAll("jfx-layout-heading", "title");
        body.getStyleClass().add("jfx-layout-body");
        VBox.setVgrow(body, Priority.ALWAYS);
        actions.getStyleClass().add("jfx-layout-actions");
        getChildren().setAll(heading, body, actions);
    }

    /***************************************************************************
     *                                                                         *
     * Setters / Getters                                                       *
     *                                                                         *
     **************************************************************************/

    public ObservableList<Node> getHeading() {
        return heading.getChildren();
    }

    /**
     * set header node
     *
     * @param titleContent
     */
    public void setHeading(Node... titleContent) {
        this.heading.getChildren().setAll(titleContent);
    }

    public ObservableList<Node> getBody() {
        return body.getChildren();
    }

    /**
     * set body node
     *
     * @param body
     */
    public void setBody(Node... body) {
        this.body.getChildren().setAll(body);
    }

    public ObservableList<Node> getActions() {
        return actions.getChildren();
    }

    /**
     * set actions of the dialog (Accept, Cancel,...)
     *
     * @param actions
     */
    public void setActions(Node... actions) {
        this.actions.getChildren().setAll(actions);
    }

    public void setActions(List<? extends Node> actions) {
        this.actions.getChildren().setAll(actions);
    }

    /***************************************************************************
     *                                                                         *
     * Stylesheet Handling                                                     *
     *                                                                         *
     **************************************************************************/
    /**
     * Initialize the style class to 'jfx-dialog-layout'.
     * <p>
     * This is the selector class from which CSS can be used to style
     * this control.
     */
    private static final String DEFAULT_STYLE_CLASS = "jfx-dialog-layout";

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUserAgentStylesheet() {
        return JFoenixResource.load("css/control/jfx-dialog-layout.css").toExternalForm();
    }

    private void initialize() {
        this.getStyleClass().add(DEFAULT_STYLE_CLASS);
    }
}
