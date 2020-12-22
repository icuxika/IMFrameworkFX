package com.icuxika.controller;

import com.icuxika.LanguageListCell;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.controller.home.AddressBookController;
import com.icuxika.controller.home.ConversationController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;

import java.util.Locale;

@AppFXML(fxml = "home.fxml")
public class HomeController {

    @FXML
    private Button conversationButton;
    @FXML
    private Button addressBookButton;
    @FXML
    private ComboBox<Locale> languageComboBox;

    /**
     * 子页面容器
     */
    @FXML
    private StackPane pageContainer;

    /**
     * 会话子页面
     * 【待验证】似乎 fx:include 子页面时设置的 fx:id 需要与 controller前缀或是文件名相对应
     */
    @FXML
    private SplitPane conversation;
    @FXML
    private ConversationController conversationController;

    /**
     * 通讯录子页面
     */
    @FXML
    private SplitPane addressBook;
    @FXML
    private AddressBookController addressBookController;

    /**
     * 初始化
     * 【一些说明】使子页面自适应父容器宽高 需要设置子页面的 Min、Max尺寸为 USE_COMPUTED_SIZE，而将子页面组件绑定到父容器上面的方式，会使得父容器所在的Stage、Scene等尺寸无法变更，便外表却可以缩小，诡异
     */
    public void initialize() {
        conversation.toFront();

        conversationButton.setOnAction(event -> switchPage(HomePageType.CONVERSATION));
        addressBookButton.setOnAction(event -> switchPage(HomePageType.ADDRESS_BOOK));

        languageComboBox.getItems().addAll(MainApp.SUPPORT_LANGUAGE_LIST);
        languageComboBox.setValue(Locale.SIMPLIFIED_CHINESE);
        languageComboBox.setCellFactory(param -> new LanguageListCell());
        languageComboBox.setButtonCell(new LanguageListCell());
        languageComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                MainApp.setLanguage(newValue);
            }
        });
    }

    /**
     * 子页面类型枚举
     */
    public enum HomePageType {
        CONVERSATION, ADDRESS_BOOK
    }

    /**
     * 切换页面
     *
     * @param pageType 子页面类型
     */
    public void switchPage(HomePageType pageType) {
        switch (pageType) {
            case CONVERSATION -> {
                conversation.toFront();
            }
            case ADDRESS_BOOK -> {
                addressBook.toFront();
            }
        }
    }
}
