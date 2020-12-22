package com.icuxika.callback;

import com.icuxika.MainApp;
import com.icuxika.control.message.TextMessageNode;
import com.icuxika.controller.home.ConversationController;
import com.icuxika.mock.ReceivedMessageModel;
import com.icuxika.model.home.ConversationProperty;
import com.icuxika.model.home.MessageModel;
import com.icuxika.model.home.MessageType;
import javafx.scene.Node;
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
                    Node messageNode = null;
                    MessageType messageType = item.getType();
                    ConversationProperty conversationProperty = item.getConversationProperty();
                    switch (messageType) {
                        case TEXT -> {
                            TextMessageNode textMessageNode = null;
                            if (item.getSenderId() == 0) {
                                textMessageNode = new TextMessageNode();
                            } else {
                                switch (conversationProperty) {
                                    case SINGLE -> textMessageNode = TextMessageNode.left(false);
                                    case GROUP -> {
                                        textMessageNode = TextMessageNode.left(true);
                                        textMessageNode.setName(item.getNameProperty());
                                    }
                                }

                            }
                            if (textMessageNode != null) {
                                textMessageNode.setAvatar(item.getAvatarImageProperty());
                                textMessageNode.setMessageText(item.getMessage());

                                textMessageNode.putMenuItem(MainApp.getLanguageBinding("chat-msg-context-menu-revoke"), () -> {
                                    ReceivedMessageModel receivedMessageModel = new ReceivedMessageModel();
                                    receivedMessageModel.setConversationId(item.getConversationId());
                                    receivedMessageModel.setMessageType(MessageType.REVOKE);
                                    receivedMessageModel.setMessageId(System.currentTimeMillis());
                                    receivedMessageModel.setOperatedMessageId(item.getId());
                                    receivedMessageModel.setTime(item.getTime());
                                    ConversationController.receivedMessageModelObservableList.add(receivedMessageModel);
                                });

                                textMessageNode.putMenuItem(MainApp.getLanguageBinding("chat-msg-context-menu-delete"), () -> {
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
                        }
                        case FILE -> {
                        }
                        case IMAGE -> {
                        }
                        case EMOJI -> {
                        }
                        default -> {
                        }
                    }

                    setText(null);
                    if (messageNode != null) {
                        setGraphic(messageNode);
                    }
                }
            }
        };
    }
}
