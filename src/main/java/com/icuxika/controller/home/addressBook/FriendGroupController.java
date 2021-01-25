package com.icuxika.controller.home.addressBook;

import com.google.gson.Gson;
import com.icuxika.control.AddressBookTreeNode;
import com.icuxika.framework.UserData;
import com.icuxika.model.addressBook.FriendTreeItemModel;
import com.icuxika.model.addressBook.TreeItemTransferModel;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class FriendGroupController {

    @FXML
    private TreeView<FriendTreeItemModel> friendTreeView;

    private final ContextMenu contextMenu = new ContextMenu();

    /**
     * 根节点数据
     * 通过只操作数据模型来操作TreeView
     */
    private final FriendTreeItemModel root = new FriendTreeItemModel(0L, 0L, false, "根节点");

    public void initialize() {
        friendTreeView.setShowRoot(false);
        friendTreeView.setRoot(new RecursiveTreeItem<>(root, FriendTreeItemModel::getChildren));
        friendTreeView.setCellFactory(new FriendGroupTreeViewCallback());

        // 初始化一些数据
        FriendTreeItemModel group1 = new FriendTreeItemModel(1L, 0L, false, "好友分组一");
        FriendTreeItemModel group2 = new FriendTreeItemModel(2L, 0L, false, "好友分组二");
        FriendTreeItemModel group3 = new FriendTreeItemModel(3L, 0L, false, "好友分组三");
        FriendTreeItemModel leaf1 = new FriendTreeItemModel(1L, 1L, true, "好友一");
        FriendTreeItemModel leaf2 = new FriendTreeItemModel(2L, 2L, true, "好友二");
        FriendTreeItemModel leaf3 = new FriendTreeItemModel(3L, 3L, true, "好友三");

        root.getChildren().addAll(group1, group2, group3);
        group1.getChildren().add(leaf1);
        group2.getChildren().add(leaf2);
        group3.getChildren().add(leaf3);

        // 右键菜单
        friendTreeView.setOnContextMenuRequested(event -> {
            contextMenu.getItems().clear();
            FriendTreeItemModel friendTreeItemModel = friendTreeView.getSelectionModel().getSelectedItem().getValue();
            if (friendTreeItemModel.getLeaf()) {
                MenuItem deleteMenuItem = new MenuItem("删除好友");
                deleteMenuItem.setOnAction(event1 -> deleteFriendOrGroup(friendTreeItemModel.getId(), true));
                contextMenu.getItems().add(deleteMenuItem);
            } else {
                MenuItem addGroupMenuItem = new MenuItem("新增分组");
                MenuItem deleteGroupMenuItem = new MenuItem("删除分组");
                deleteGroupMenuItem.setOnAction(event1 -> deleteFriendOrGroup(friendTreeItemModel.getId(), false));
                MenuItem renameGroupMenuItem = new MenuItem("重命名");
                contextMenu.getItems().addAll(addGroupMenuItem, deleteGroupMenuItem, renameGroupMenuItem);
            }
            contextMenu.show(friendTreeView, event.getScreenX(), event.getScreenY());
        });
    }

    /**
     * 删除好友或者好友分组
     *
     * @param id     好友id 或者 分组id
     * @param isLeaf 是否是叶子节点（好友节点）
     */
    public void deleteFriendOrGroup(long id, boolean isLeaf) {
        // 递归获取当前树中的所有数据
        List<FriendTreeItemModel> children = new ArrayList<>();
        getChildren(root, children);

        children.stream().filter(model -> model.getId().equals(id) && model.getLeaf().equals(isLeaf)).findFirst().ifPresent(deletedModel -> {
            children.stream().filter(model -> model.getId().equals(deletedModel.getParentId()) && !model.getLeaf()).findFirst().ifPresent(groupOrRootModel -> {
                if (isLeaf) {
                    // 好友节点直接删除
                    groupOrRootModel.getChildren().remove(deletedModel);
                } else {
                    // 好友分组为空，直接删除
                    if (deletedModel.getChildren().isEmpty()) {
                        groupOrRootModel.getChildren().remove(deletedModel);
                    } else {
                        // 好友分组不为空，先为该分组下的好友选择新的分组，再进行删除分组
                        children.stream().filter(model -> model.getParentId().equals(deletedModel.getId()) && model.getLeaf()).forEach(model -> {
//                            moveFriend2Group(model.getId(), 0);
                        });
//                        groupOrRootModel.getChildren().remove(deletedModel);
                    }
                }
            });
        });
    }

    /**
     * 移动好友到新分组
     *
     * @param id       好友id
     * @param parentId 好友分组id
     */
    public void moveFriend2Group(long id, long parentId) {
        // 递归获取当前树中的所有数据
        List<FriendTreeItemModel> children = new ArrayList<>();
        getChildren(root, children);

        children.stream().filter(model -> model.getId().equals(id) && model.getLeaf()).findFirst().ifPresent(operatedModel -> {
            // 先从旧的分组移除该好友
            children.stream().filter(model -> model.getId().equals(operatedModel.getParentId()) && !model.getLeaf()).findFirst().ifPresent(oldGroup -> {
                oldGroup.getChildren().remove(operatedModel);
            });

            // 将该好友的parentId更新，添加到新分组下
            children.stream().filter(model -> model.getId().equals(parentId) && !model.getLeaf()).findFirst().ifPresent(newGroup -> {
                operatedModel.setParentId(parentId);
                newGroup.getChildren().add(operatedModel);
            });
        });
    }

    private void getChildren(FriendTreeItemModel node, List<FriendTreeItemModel> children) {
        children.add(node);
        if (!node.getChildren().isEmpty()) {
            node.getChildren().forEach(model -> getChildren(model, children));
        }
    }

    /**
     * 好友树节点设置
     * 1、好友节点可拖拽更换分组
     * 2、分组可拖动排序
     */
    class FriendGroupTreeViewCallback implements Callback<TreeView<FriendTreeItemModel>, TreeCell<FriendTreeItemModel>> {

        private final Gson gson = new Gson();

        @Override
        public TreeCell<FriendTreeItemModel> call(TreeView<FriendTreeItemModel> param) {
            TreeCell<FriendTreeItemModel> treeCell = new TreeCell<>() {
                @Override
                protected void updateItem(FriendTreeItemModel item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null || empty) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        if (item.getLeaf()) {
                            // 查看 TreeCellSkin@layoutChildren
                            setPadding(new Insets(0, 0, 0, -18));

                            setText(null);
                            AddressBookTreeNode node = new AddressBookTreeNode();
                            node.setAvatar(UserData.avatarProperty());
                            node.setName(item.nameProperty());
                            node.setPrompt(new SimpleStringProperty("[离线请留言]"));
//                            node.setTime(new SimpleLongProperty(1608695815000L));
                            setGraphic(node);

                            setPrefHeight(48);
                        } else {
                            setPadding(Insets.EMPTY);
                            setText(item.getName());
                            setFont(Font.font(14));
                            setGraphic(null);

                            setPrefHeight(32);
                            // 调整小箭头的位置，目前存在一个问题，小箭头的大小和位置会在第一次点击到树节点后才更新
                            getChildrenUnmodifiable().stream().filter(node -> node instanceof StackPane).forEach(node -> {
                                StackPane stackPane = (StackPane) node;
                                stackPane.setAlignment(Pos.CENTER);
                                stackPane.setPadding(new Insets(10, 8, 10, 8));
                                stackPane.setPrefHeight(32);
                            });
                        }
                    }
                }
            };

            // 拖拽时刚进入经过的节点添加一个Border
            treeCell.setOnDragEntered(event -> {
                // 空白节点不显示
                if (treeCell.getItem() != null) {
                    treeCell.setBorder(new Border(new BorderStroke(null, null, Paint.valueOf("#eaeaea"), null, BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(0, 0, 2, 0), null)));
                }
            });

            // 拖拽时离开经过的节点，移除Border
            treeCell.setOnDragExited(event -> treeCell.setBorder(null));

            // 开始拖拽
            treeCell.setOnDragDetected(event -> {
                // 如果将新的好友一栏也显示在这里，则要进行判断是否允许拖拽
                Dragboard dragboard = treeCell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();

                // 因为实际的 FriendTreeItemModel，有一些绑定属性，难以序列化，所以此处通过序列化 TreeItemTransferModel，来传输数据
                TreeItemTransferModel transferModel = new TreeItemTransferModel(treeCell.getItem().getId(), treeCell.getItem().getParentId(), treeCell.getItem().getLeaf());

                clipboardContent.putString(gson.toJson(transferModel));
                dragboard.setContent(clipboardContent);
                WritableImage writableImage = new WritableImage((int) treeCell.getWidth(), (int) treeCell.getHeight());

                if (transferModel.getLeaf()) {
                    // 好友节点显示图形
                    treeCell.getGraphic().snapshot(new SnapshotParameters(), writableImage);
                } else {
                    // 分组节点显示文字
                    Text text = new Text(treeCell.getText());
                    text.snapshot(new SnapshotParameters(), writableImage);
                }
                dragboard.setDragView(writableImage);
            });

            // 判断是否接受拖拽
            treeCell.setOnDragOver(event -> {
                TreeItemTransferModel transferModel = gson.fromJson(event.getDragboard().getString(), TreeItemTransferModel.class);
                FriendTreeItemModel received = treeCell.getItem();
                if (received != null) {
                    if (transferModel.getLeaf()) {
                        // 允许叶子节点向叶子节点和非叶子节点移动
                        if (received.getLeaf()) {
                            // 判断是不是向同意分分组的好友节点移动
                            if (!received.getParentId().equals(transferModel.getParentId())) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                        } else {
                            // 判断是不是像原本所在的分组节点移动
                            if (!received.getId().equals(transferModel.getParentId())) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                        }
                    } else {
                        if (!received.getLeaf()) {
                            // 只允许非叶子节点向非叶子节点移动
                            if (!received.getId().equals(transferModel.getId())) {
                                event.acceptTransferModes(TransferMode.MOVE);
                            }
                        }
                    }
                }
            });

            // 拖拽结束
            treeCell.setOnDragDropped(event -> {
                TreeItemTransferModel transferModel = gson.fromJson(event.getDragboard().getString(), TreeItemTransferModel.class);
                FriendTreeItemModel received = treeCell.getItem();
                if (transferModel.getLeaf()) {
                    // 更换分组
                    if (received.getLeaf()) {
                        // 叶子 -> 叶子
                        moveFriend2Group(transferModel.getId(), received.getParentId());
                    } else {
                        // 叶子 -> 分组
                        moveFriend2Group(transferModel.getId(), received.getId());
                    }
                } else {
                    // 分组排序，先按照当前顺序为每一个分组设置一个rank（i * 100）
                    treeCell.getTreeItem().getParent().getValue().getChildren().forEach(friendTreeItemModel -> friendTreeItemModel.setRank((treeCell.getTreeItem().getParent().getValue().getChildren().indexOf(friendTreeItemModel) + 1) * 100));
                    // 获取鼠标经过的树节点 rank
                    int receivedRank = treeCell.getItem().getRank();
                    // 递归获取所有树节点对应的数据
                    List<FriendTreeItemModel> children = new ArrayList<>();
                    getChildren(root, children);
                    // 设置被拖拽的树节点的顺序在被鼠标经过的树节点后面
                    children.stream().filter(model -> model.getId().equals(transferModel.getId()) && !model.getLeaf()).findFirst().ifPresent(model -> {
                        model.setRank(receivedRank + 50);
                    });
                    // 根据 rank 更新树节点顺序
                    treeCell.getTreeItem().getParent().getChildren().sort(Comparator.comparing(o -> o.getValue().getRank()));
                }
            });
            return treeCell;
        }
    }

    public static class RecursiveTreeItem<T> extends TreeItem<T> {

        private final Callback<T, ObservableList<T>> childrenFactory;

        private final Callback<T, Node> graphicsFactory;

        public RecursiveTreeItem(Callback<T, ObservableList<T>> childrenFactory) {
            this(null, childrenFactory);
        }

        public RecursiveTreeItem(final T value, Callback<T, ObservableList<T>> childrenFactory) {
            this(value, (item) -> null, childrenFactory);
        }

        public RecursiveTreeItem(final T value, Callback<T, Node> graphicsFactory, Callback<T, ObservableList<T>> childrenFactory) {
            super(value, graphicsFactory.call(value));

            this.graphicsFactory = graphicsFactory;
            this.childrenFactory = childrenFactory;

            if (value != null) {
                addChildrenListener(value);
            }

            valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue != null) {
                    addChildrenListener(newValue);
                }
            });

            this.setExpanded(true);
        }

        private void addChildrenListener(T value) {
            final ObservableList<T> children = childrenFactory.call(value);

            children.forEach(child -> RecursiveTreeItem.this.getChildren().add(
                    new RecursiveTreeItem<>(child, this.graphicsFactory, childrenFactory)));

            children.addListener((ListChangeListener<T>) change -> {
                while (change.next()) {

                    if (change.wasAdded()) {
                        change.getAddedSubList().forEach(t -> RecursiveTreeItem.this.getChildren().add(
                                new RecursiveTreeItem<>(t, this.graphicsFactory, childrenFactory)));
                    }

                    if (change.wasRemoved()) {
                        change.getRemoved().forEach(t -> {
                            final List<TreeItem<T>> itemsToRemove = RecursiveTreeItem.this
                                    .getChildren()
                                    .stream()
                                    .filter(treeItem -> treeItem.getValue().equals(t))
                                    .collect(Collectors.toList());

                            RecursiveTreeItem.this.getChildren().removeAll(itemsToRemove);
                        });
                    }
                }
            });
        }
    }
}
