package com.icuxika.callback;

import com.icuxika.MainApp;
import com.icuxika.control.message.ImageMessageNode;
import com.icuxika.control.message.MessageNode;
import com.icuxika.control.message.TextMessageNode;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.framework.UserData;
import com.icuxika.mock.ReceivedMessageModel;
import com.icuxika.model.home.ConversationProperty;
import com.icuxika.model.home.MessageModel;
import com.icuxika.model.home.MessageType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;

/**
 * 对消息列表的单元格进行处理
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
                    MessageNode messageNode = null;
                    MessageType messageType = item.getType();
                    ConversationProperty conversationProperty = item.getConversationProperty();
                    boolean showLeft = !item.getSenderId().equals(UserData.userId);
                    boolean showName = conversationProperty.equals(ConversationProperty.GROUP) && showLeft;
                    switch (messageType) {
                        case TEXT -> {
                            TextMessageNode textMessageNode = new TextMessageNode(showLeft, showName);
                            textMessageNode.setMessageText(item.getMessage());

                            textMessageNode.setMenuItem(MainApp.getLanguageBinding("chat-msg-context-menu-revoke"), () -> {
                                ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();
                                receivedMessageModel.setConversationId(item.getConversationId());
                                receivedMessageModel.setMessageType(MessageType.REVOKE);
                                receivedMessageModel.setMessageId(System.currentTimeMillis());
                                receivedMessageModel.setOperatedMessageId(item.getId());
                                receivedMessageModel.setTime(item.getTime());
                                ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
                            });

                            textMessageNode.setMenuItem(MainApp.getLanguageBinding("chat-msg-context-menu-delete"), () -> {
                                ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();
                                receivedMessageModel.setConversationId(item.getConversationId());
                                receivedMessageModel.setMessageType(MessageType.DELETE);
                                receivedMessageModel.setMessageId(System.currentTimeMillis());
                                receivedMessageModel.setOperatedMessageId(item.getId());
                                receivedMessageModel.setTime(item.getTime());
                                ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
                            });

                            messageNode = textMessageNode;
                        }
                        case FILE -> {
                        }
                        case IMAGE -> {
                            ImageMessageNode imageMessageNode = new ImageMessageNode(showLeft, showName);
//                            imageMessageNode.setImage("https://scpic.chinaz.net/files/pic/pic9/202101/apic30090.jpg");
//                            imageMessageNode.setImage("file:/Users/icuxika/Downloads/mountains-5819652.jpg");
                            imageMessageNode.setImage("file:" + item.getMessage());
                            messageNode = imageMessageNode;
                        }
                        case EMOJI -> {
                        }
                        default -> {
                        }
                    }

                    setText(null);
                    if (messageNode != null) {
                        messageNode.setAvatar(item.getAvatarImageProperty());
                        messageNode.setName(item.getNameProperty());

                        setGraphic(messageNode);
                    }
                }
            }
        };
    }
}
