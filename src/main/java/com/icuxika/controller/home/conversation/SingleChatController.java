package com.icuxika.controller.home.conversation;

import com.icuxika.annotation.AppFXML;

/**
 * 单聊面板
 */
@AppFXML(fxml = "home/conversation/singleChat.fxml")
public class SingleChatController extends ChatController {


    @Override
    public void initialize() {
        // 在父类中声明相同的组件并初始化
        super.initialize();
    }
}
