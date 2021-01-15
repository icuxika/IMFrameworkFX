package com.icuxika.callback;

import com.icuxika.model.home.MessageModel;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * 对消息列表的单元格进行处理
 * 当前需要手动调用{@link MessageModel#initGraphic()}}来初始化Node
 */
public class MessageListViewCallback implements Callback<ListView<MessageModel>, ListCell<MessageModel>> {
    @Override
    public ListCell<MessageModel> call(ListView<MessageModel> param) {
        return new ListCell<>() {
            @Override
            protected void updateItem(MessageModel item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(null);
                    // 目前存在一个问题，若是在此处根据数据构造Node的话，由于
                    // ListView重绘时就会调用当前逻辑，那么Node的状态也会初始化，因此需要一个更合理
                    // 的更新此处Node的方式，暂时先将Node缓存到Model中，后面再调整。
                    setGraphic(item.getGraphic());
                }
            }
        };
    }
}
