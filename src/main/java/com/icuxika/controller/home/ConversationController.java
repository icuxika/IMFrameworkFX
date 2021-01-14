package com.icuxika.controller.home;

import com.icuxika.AppView;
import com.icuxika.MainApp;
import com.icuxika.annotation.AppFXML;
import com.icuxika.control.ConversationNode;
import com.icuxika.controller.home.conversation.ChatController;
import com.icuxika.controller.home.conversation.GroupChatController;
import com.icuxika.controller.home.conversation.SingleChatController;
import com.icuxika.framework.UserData;
import com.icuxika.mock.ReceivedMessageModel;
import com.icuxika.model.home.ConversationModel;
import com.icuxika.model.home.ConversationProperty;
import com.icuxika.model.home.MessageModel;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * 会话页面
 */
@AppFXML(fxml = "home/conversation.fxml")
public class ConversationController {

    /**
     * 会话列表
     */
    @FXML
    private ListView<ConversationModel> conversationListView;

    /**
     * 会话详情页面容器，包括单聊、群聊、及。。。 每一个都是一个单独的组件
     */
    @FXML
    private StackPane conversationContainer;

    /**
     * 通过操作此集合来更新会话列表
     */
    private static final ObservableList<ConversationModel> conversationModelObservableList = FXCollections.observableArrayList(param -> {
        // time和top属性的变化将能引起会话列表排序
        LongProperty time = param.getTimeProperty();
        BooleanProperty top = param.getTopProperty();
        return new Observable[]{time, top};
    });

    /**
     * 排序过的会话集合，目前以最近会话时间为排序条件，这里使用Comparator的静态方法来简写，也可以实现Comparator接口
     * {@link ConversationController.ConversationComparator}
     */
    private final SortedList<ConversationModel> conversationModelSortedList = new SortedList<>(conversationModelObservableList, Comparator.comparing(ConversationModel::getTop).reversed().thenComparing(ConversationModel::getTime, Comparator.reverseOrder()));

    /**
     * 已经加载的会话组件 KEY 为会话id
     */
    private final Map<Long, AppView<? extends ChatController>> chatViewMap = new HashMap<>();

    /**
     * 模拟收到消息
     */
    public static final ObservableList<ReceivedMessageModel> receivedMessageModelObservableList = FXCollections.observableArrayList();

    public void initialize() {
        conversationListView.setItems(conversationModelSortedList);
        conversationListView.setCellFactory(new ConversationListViewCallback());
        conversationListView.getSelectionModel().selectedItemProperty().addListener(new ConversationListViewSelectedListener());

        conversationContainer.setBackground(new Background(new BackgroundImage(new Image(MainApp.load("img/logo.png").toExternalForm()), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, BackgroundSize.DEFAULT)));

        conversationModelObservableList.add(new ConversationModel(1L, 1L, ConversationProperty.SINGLE, new Image("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=3397537705,1180362904&fm=26&gp=0.jpg", true), "一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟", 1608695815000L, "消息一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟一号抵达嘀嘀嘀嘀嘀嘀提嘟嘟嘟", 0, true));
        conversationModelObservableList.add(new ConversationModel(2L, 2L, ConversationProperty.SINGLE, new Image("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1587601794,489963968&fm=11&gp=0.jpg", true), "二号", 1608609415000L, "消息", 99, true));
        conversationModelObservableList.add(new ConversationModel(3L, 3L, ConversationProperty.SINGLE, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348820068&di=5014ab46c14192efe1b4c90ca2c39523&imgtype=0&src=http%3A%2F%2Fb-ssl.duitang.com%2Fuploads%2Fitem%2F201505%2F02%2F20150502175901_McUKY.jpeg", true), "天蓬元帅猪八戒", 1608523015000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(4L, 4L, ConversationProperty.SINGLE, new Image("https://ss0.bdstatic.com/70cFuHSh_Q1YnxGkpoWK1HF6hhy/it/u=2057588226,2402156864&fm=11&gp=0.jpg", true), "四号", 1608436615000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(5L, 5L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348846874&di=680c6dadcd0e4ad6cead7c95b5283239&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fitem%2F202005%2F26%2F20200526014717_sblut.thumb.400_0.jpeg", true), "五号", 1608350215000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(6L, 6L, ConversationProperty.GROUP, new Image("https://ss1.bdstatic.com/70cFvXSh_Q1YnxGkpoWK1HF6hhy/it/u=2233854763,678386514&fm=11&gp=0.jpg", true), "六号", 1608263815000L, "消息", 12, false));
        conversationModelObservableList.add(new ConversationModel(7L, 7L, ConversationProperty.GROUP, new Image("https://ss2.bdstatic.com/70cFvnSh_Q1YnxGkpoWK1HF6hhy/it/u=1361713587,2920206203&fm=11&gp=0.jpg", true), "七号", 1608177415000L, "消息", 7, false));
        conversationModelObservableList.add(new ConversationModel(8L, 8L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348846874&di=5d6cecfbc4a71d988b93df776abe102c&imgtype=0&src=http%3A%2F%2Fww3.sinaimg.cn%2Fmw690%2F6961aadegy1gh12eaw4hqj20u00u00ur.jpg", true), "八号", 1608091015000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(9L, 9L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348970942&di=5634de35fd9ef1cc84c6e382283b1278&imgtype=0&src=http%3A%2F%2Fww1.sinaimg.cn%2Fmw690%2F0073VjWaly1ghpdxy883vj30u00u0h17.jpg", true), "九号", 1608004615000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(10L, 10L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348970942&di=b9212efd48dc8dda983985e53de72143&imgtype=0&src=http%3A%2F%2Fwx2.sinaimg.cn%2Fmw690%2F0068LpS2gy1ge086xgt2aj30ku0kuaew.jpg", true), "十号", 1607918215000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(11L, 11L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348970942&di=57de067af96a541123b982c62ccab52a&imgtype=0&src=http%3A%2F%2Fwx2.sinaimg.cn%2Fmw690%2F66b6e1c4ly1fe74swo37rj20le0ladg5.jpg", true), "十一号", 1607831815000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(12L, 12L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348970942&di=a47690283d0cc2104b48ccc6d3e61c12&imgtype=0&src=http%3A%2F%2Fp9-bcy.byteimg.com%2Fimg%2Fbanciyuan%2F2d1dad5743a7491baa91bbee9741a8fc%7Etplv-banciyuan-w650.image", true), "十二号", 1607745415000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(13L, 13L, ConversationProperty.GROUP, new Image("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1608348970942&di=0719231f4e10d29e58e6187250552320&imgtype=0&src=http%3A%2F%2Fc-ssl.duitang.com%2Fuploads%2Fblog%2F202011%2F16%2F20201116144255_fbec6.thumb.1000_0.jpeg", true), "十三号", 1607659015000L, "消息", 0, false));
        conversationModelObservableList.add(new ConversationModel(14L, 14L, ConversationProperty.GROUP, new Image("https://ss0.bdstatic.com/70cFvHSh_Q1YnxGkpoWK1HF6hhy/it/u=2235240708,3390568978&fm=26&gp=0.jpg", true), "十四号", 1607572615000L, "消息", 0, false));

        // 模拟收到消息的处理
        receivedMessageModelObservableList.addListener((ListChangeListener<ReceivedMessageModel>) c -> {
            while (c.next()) {
                if (c.wasAdded()) {
                    c.getAddedSubList().forEach((Consumer<ReceivedMessageModel>) this::handleReceivedMessage);
                }
            }
        });
    }

    /**
     * 会话列表选中元素监听
     */
    private class ConversationListViewSelectedListener implements ChangeListener<ConversationModel> {
        @Override
        public void changed(ObservableValue<? extends ConversationModel> observable, ConversationModel oldValue, ConversationModel newValue) {
            if (newValue != null) {
                // 根据选中的会话数据更新右侧会话面板
                long id = newValue.getId();
                long targetId = newValue.getTargetId();
                ConversationProperty conversationProperty = newValue.getConversationProperty();
                // 根据会话属性决定加载单聊还是群聊面板
                Parent chatNode;
                // 判断该会话组件是否已经加载
                if (chatViewMap.containsKey(id)) {
                    chatNode = chatViewMap.get(id).getRootNode();
                } else {
                    switch (conversationProperty) {
                        case SINGLE: {
                            AppView<SingleChatController> singleChatView = new AppView<>(SingleChatController.class);
                            singleChatView.getController().setName(newValue.getNameProperty());
                            singleChatView.getController().setConversationId(id);
                            chatNode = singleChatView.getRootNode();
                            chatViewMap.put(id, singleChatView);
                            break;
                        }
                        case GROUP: {
                            AppView<GroupChatController> groupChatView = new AppView<>(GroupChatController.class);
                            groupChatView.getController().setName(newValue.getNameProperty());
                            groupChatView.getController().setConversationId(id);
                            chatNode = groupChatView.getRootNode();
                            chatViewMap.put(id, groupChatView);
                            break;
                        }
                        default:
                            throw new IllegalStateException("未定义该会话属性: " + conversationProperty);
                    }
                }

                if (chatNode != null) {
                    conversationContainer.getChildren().clear();
                    conversationContainer.getChildren().add(chatNode);
                }
            }
        }
    }

    /**
     * 对会话列表的单元格进行处理
     */
    private static class ConversationListViewCallback implements Callback<ListView<ConversationModel>, ListCell<ConversationModel>> {
        @Override
        public ListCell<ConversationModel> call(ListView<ConversationModel> param) {
            return new ListCell<>() {
                @Override
                protected void updateItem(ConversationModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        // 防止显示异常
                        setText(null);
                        setGraphic(null);
                    } else {
                        long targetId = item.getTargetId();
                        ConversationProperty conversationProperty = item.getConversationProperty();

                        ConversationNode conversationNode = new ConversationNode();
                        conversationNode.setAvatar(item.getAvatarProperty());
                        conversationNode.setName(item.getNameProperty());
                        conversationNode.setTime(item.getTimeProperty());
                        conversationNode.setMessage(item.getMessageProperty());
                        conversationNode.setUnreadCountLabel(item.getUnreadCountProperty());
                        conversationNode.setTop(item.getTopProperty());

                        // 此处对node设置cell的背景可以在 ConversationNode 中观察到背景颜色的变化
                        conversationNode.setBackground(this.getBackground());
                        this.hoverProperty().addListener((observable, oldValue, newValue) -> {
                            if (newValue) {
                                conversationNode.setBackground(new Background(new BackgroundFill(Paint.valueOf("#EBEBEBE6"), null, null)));
                            } else {
                                if (!(param.getSelectionModel().getSelectedItem() == item)) {
                                    conversationNode.setBackground(new Background(new BackgroundFill(Paint.valueOf("#FFFFFFE6"), null, null)));
                                }
                            }
                        });

                        // 消除默认边距，也可对 .list-cell 设置 -fx-padding: 0px;
                        setPadding(Insets.EMPTY);
                        setText(null);
                        setGraphic(conversationNode);
                    }
                }
            };
        }
    }

    /**
     * 会话排序比较器
     * 对于本会话列表来说来说，添加一个新对象会自动应用排序，而对列表已有的对象进行更新操作，则需要在构建可观察列表时，传入指定参数
     * 如 FXCollections.observableArrayList(param -> new LongProperty[]{param.getTimeProperty()});
     * <p>
     * 排序规则：先比较会话的置顶的设置，再比较最近会话时间
     */
    private static class ConversationComparator implements Comparator<ConversationModel> {
        @Override
        public int compare(ConversationModel o1, ConversationModel o2) {
            if (o1.getTop()) {
                // 如果o1置顶
                if (o2.getTop()) {
                    // 如果o2置顶
                    return Long.compare(o2.getTime(), o1.getTime());
                } else {
                    return -1;
                }
            } else {
                if (o2.getTop()) {
                    return 1;
                } else {
                    return Long.compare(o2.getTime(), o1.getTime());
                }
            }
        }
    }

    /**
     * 根据会话id查询会话数据
     *
     * @param conversationId 会话id
     * @return 会话数据
     */
    public static ConversationModel getConversationData(Long conversationId) {
        return conversationModelObservableList.stream().filter(model -> model.getId().equals(conversationId)).findFirst().orElse(null);
    }

    public static ConversationModel getConversationData(Long targetId, ConversationProperty conversationProperty) {
        return conversationModelObservableList.stream().filter(model -> model.getTargetId().equals(targetId) && model.getConversationProperty() == conversationProperty).findFirst().orElse(null);
    }

    /**
     * 处理收到的聊天消息
     *
     * @param receivedMessageModel 消息
     */
    public void handleReceivedMessage(ReceivedMessageModel receivedMessageModel) {
        // 一堆空指针检查没做

        MessageModel messageModel = new MessageModel();

        messageModel.setId(receivedMessageModel.getMessageId());
        messageModel.setType(receivedMessageModel.getMessageType());
        messageModel.setMessage(receivedMessageModel.getMessage());
        messageModel.setStatus(receivedMessageModel.getMessageStatus());
        messageModel.setTime(receivedMessageModel.getTime());
        messageModel.setSenderId(receivedMessageModel.getSenderId());
        messageModel.setOperatedId(receivedMessageModel.getOperatedMessageId());
        messageModel.setConversationId(receivedMessageModel.getConversationId());

        ConversationModel conversationModel = getConversationData(receivedMessageModel.getConversationId());
        if (conversationModel != null) {
            if (receivedMessageModel.getSenderId().equals(UserData.userId)) {
                // 登录用户所发出的消息
                messageModel.setName(UserData.nameProperty());
                messageModel.setAvatarImageProperty(UserData.avatarProperty());
            } else {
                // 如果是单聊，则直接以会话的数据来设置名称和头像
                // 如果是群聊，则需要根据用户id再进行查询
                messageModel.setName(conversationModel.getNameProperty());
                messageModel.setAvatarImageProperty(conversationModel.getAvatarProperty());
            }
            messageModel.setConversationProperty(conversationModel.getConversationProperty());
            // 通过更新会话时间来触发会话更新
            conversationModel.setTime(messageModel.getTime());
        }

        // 此处对于消息操作，由于会话面板设置了公共的父类，有关消息的操作可无视子类是单聊还是群聊这种会话属性的因素直接操作父类的方法
        // 当需要使用具体子类Controller中的方法时，也可根据会话属性进行强转
        chatViewMap.get(receivedMessageModel.getConversationId()).getController().receiveMessage(messageModel);
    }
}
