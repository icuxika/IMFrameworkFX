package com.icuxika.controller.home;

import com.icuxika.controller.home.addressBook.FriendGroupController;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

/**
 * 通讯录页面
 */
public class AddressBookController {

    /**
     * 好友分组
     */
    @FXML
    private VBox friendGroup;
    @FXML
    private FriendGroupController friendGroupController;
    @FXML
    private StackPane addressBookContainer;

    public void initialize() {
        friendGroupController.setShowInParentHook(showInParentHook);
    }

    private final Consumer<Node> showInParentHook = new Consumer<>() {
        @Override
        public void accept(Node node) {
            addressBookContainer.getChildren().clear();
            addressBookContainer.getChildren().add(node);
        }
    };
}
