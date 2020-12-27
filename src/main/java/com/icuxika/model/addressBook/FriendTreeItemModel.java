package com.icuxika.model.addressBook;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class FriendTreeItemModel extends TreeItemTransferModel {

    private final StringProperty name = new SimpleStringProperty();

    private ObservableList<FriendTreeItemModel> children = FXCollections.observableArrayList();

    public FriendTreeItemModel() {
    }

    public FriendTreeItemModel(Long id, Boolean leaf, String name) {
        super(id, leaf);
        setName(name);
    }

    public StringProperty nameProperty() {
        return this.name;
    }

    public String getName() {
        return nameProperty().get();
    }

    public void setName(String name) {
        nameProperty().set(name);
    }

    public ObservableList<FriendTreeItemModel> getChildren() {
        return children;
    }

    public void setChildren(ObservableList<FriendTreeItemModel> children) {
        this.children = children;
    }
}
