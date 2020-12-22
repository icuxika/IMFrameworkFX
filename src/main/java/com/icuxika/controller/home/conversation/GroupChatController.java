package com.icuxika.controller.home.conversation;

import com.icuxika.annotation.AppFXML;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

/**
 * 群聊面板
 */
@AppFXML(fxml = "home/conversation/groupChat.fxml")
public class GroupChatController extends ChatController {

    /**
     * 侧面面板展示隐藏按钮
     */
    @FXML
    private TextFlow flexTextFlow;
    @FXML
    private Text flexText;
    @FXML
    private VBox sideBox;

    private final BooleanProperty sidePaneShow = new SimpleBooleanProperty(true);

    public BooleanProperty sidePaneShowProperty() {
        return this.sidePaneShow;
    }

    public boolean getSidePaneShow() {
        return sidePaneShowProperty().get();
    }

    public void setSidePaneShow(boolean value) {
        sidePaneShowProperty().set(value);
    }

    private final BooleanProperty flexTextFlowShow = new SimpleBooleanProperty(false);

    public BooleanProperty flexTextFlowShowProperty() {
        return this.flexTextFlowShow;
    }

    public void setFlexTextFlow(boolean value) {
        flexTextFlowShowProperty().set(value);
    }

    @Override
    public void initialize() {
        // 在父类中声明相同的组件并初始化
        super.initialize();
        // 设置控制侧边面板显示的图标的文字内容
        sidePaneShowProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) flexText.setText(">");
            else flexText.setText("<");
        });
        // 设置控制侧边面板显示的图标是否显示
        messageListView.setOnMouseMoved(event -> {
            Bounds bounds = messageListView.localToScreen(messageListView.getLayoutBounds());
            setFlexTextFlow(Math.abs(event.getScreenX() - bounds.getMaxX()) < 4);
        });
        messageToolbox.setOnMouseEntered(event -> setFlexTextFlow(false));
        messageInputTextArea.setOnMouseEntered(event -> setFlexTextFlow(false));
        flexTextFlow.visibleProperty().bind(flexTextFlowShowProperty());
        flexTextFlow.setOnMouseReleased(event -> setSidePaneShow(!getSidePaneShow()));
        // 设置侧边面板是否显示
        sideBox.managedProperty().bind(sidePaneShowProperty());
        sideBox.visibleProperty().bind(sidePaneShowProperty());
    }
}
