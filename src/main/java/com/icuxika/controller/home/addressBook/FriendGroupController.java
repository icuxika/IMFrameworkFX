package com.icuxika.controller.home.addressBook;

import com.google.gson.Gson;
import com.icuxika.model.addressBook.FriendTreeItemModel;
import com.icuxika.model.addressBook.TreeItemTransferModel;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.util.Callback;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class FriendGroupController {

    @FXML
    private Button addButton;
    @FXML
    private Button updateButton;
    @FXML
    private TreeView<FriendTreeItemModel> friendTreeView;

    /**
     * 根节点数据
     * 通过只操作数据模型来操作TreeView
     */
    private final FriendTreeItemModel root = new FriendTreeItemModel(0L, false, "根节点");

    public void initialize() {
        friendTreeView.setShowRoot(false);
        friendTreeView.setRoot(new RecursiveTreeItem<>(root, FriendTreeItemModel::getChildren));
        friendTreeView.setCellFactory(new FriendGroupTreeViewCallback());

        FriendTreeItemModel group1 = new FriendTreeItemModel(1L, false, "好友分组一");
        FriendTreeItemModel group2 = new FriendTreeItemModel(2L, false, "好友分组二");
        FriendTreeItemModel group3 = new FriendTreeItemModel(3L, false, "好友分组三");
        FriendTreeItemModel leaf1 = new FriendTreeItemModel(1L, true, "好友一");
        FriendTreeItemModel leaf2 = new FriendTreeItemModel(2L, true, "好友二");
        FriendTreeItemModel leaf3 = new FriendTreeItemModel(3L, true, "好友三");

        addButton.setOnAction(event -> {
            root.getChildren().addAll(group1, group2, group3);
            group1.getChildren().add(leaf1);
            group2.getChildren().add(leaf2);
            group3.getChildren().add(leaf3);
        });

        updateButton.setOnAction(event -> {
            group1.getChildren().remove(leaf1);
            group2.getChildren().add(leaf1);
        });
    }

    static class FriendGroupTreeViewCallback implements Callback<TreeView<FriendTreeItemModel>, TreeCell<FriendTreeItemModel>> {

        private final Gson gson = new Gson();

        private final HashMap<String, TreeItem<FriendTreeItemModel>> transferCacheMap = new HashMap<>();

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
                        setText(item.getName());
                        if (item.getLeaf()) {
                            setGraphic(new FontIcon(FontAwesomeSolid.PEOPLE_CARRY));
                        } else {
                            setGraphic(new FontIcon(FontAwesomeSolid.PLANE_DEPARTURE));
                        }
                    }
                }
            };

            treeCell.setOnDragEntered(event -> treeCell.setBorder(new Border(new BorderStroke(null, null, Paint.valueOf("#eaeaea"), null, BorderStrokeStyle.SOLID, null, null, null, null, new BorderWidths(0, 0, 2, 0), null))));
            treeCell.setOnDragExited(event -> treeCell.setBorder(null));

            treeCell.setOnDragDetected(event -> {
                Dragboard dragboard = treeCell.startDragAndDrop(TransferMode.MOVE);
                ClipboardContent clipboardContent = new ClipboardContent();

                TreeItemTransferModel transferModel = new TreeItemTransferModel(treeCell.getItem().getId(), treeCell.getItem().getLeaf());
                transferCacheMap.put(treeCell.getItem().getId() + "|" + treeCell.getItem().getLeaf(), treeCell.getTreeItem());

                clipboardContent.putString(gson.toJson(transferModel));
                dragboard.setContent(clipboardContent);
                WritableImage writableImage = new WritableImage((int) treeCell.getWidth(), (int) treeCell.getHeight());
                Text text = new Text(treeCell.getText());
                text.snapshot(new SnapshotParameters(), writableImage);
                dragboard.setDragView(writableImage);
            });

            treeCell.setOnDragOver(event -> {
                TreeItemTransferModel transferModel = gson.fromJson(event.getDragboard().getString(), TreeItemTransferModel.class);
                FriendTreeItemModel received = treeCell.getItem();
                if (transferModel.getLeaf()) {
                    // 允许叶子节点向叶子节点和非叶子节点移动
                    event.acceptTransferModes(TransferMode.MOVE);
                } else {
                    if (!received.getLeaf()) {
                        // 只允许非叶子节点向非叶子节点移动
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
            });

            treeCell.setOnDragDropped(event -> {
                TreeItemTransferModel transferModel = gson.fromJson(event.getDragboard().getString(), TreeItemTransferModel.class);
                TreeItem<FriendTreeItemModel> dragged = transferCacheMap.get(transferModel.getId() + "|" + transferModel.getLeaf());
                FriendTreeItemModel received = treeCell.getItem();
                if (transferModel.getLeaf()) {
                    // 更换分组
                    dragged.getParent().getValue().getChildren().remove(dragged.getValue());
                    if (received.getLeaf()) {
                        // 接收者为叶子节点
                        treeCell.getTreeItem().getParent().getValue().getChildren().add(dragged.getValue());
                    } else {
                        // 接收者为非叶子节点
                        treeCell.getItem().getChildren().add(dragged.getValue());
                    }
                } else {
                    // 分组排序
                    treeCell.getTreeItem().getParent().getValue().getChildren().forEach(friendTreeItemModel -> friendTreeItemModel.setRank((treeCell.getTreeItem().getParent().getValue().getChildren().indexOf(friendTreeItemModel) + 1) * 100));
                    int receivedRank = treeCell.getItem().getRank();
                    dragged.getValue().setRank(receivedRank + 50);
                    dragged.getParent().getValue().getChildren().remove(dragged.getValue());
                    treeCell.getTreeItem().getParent().getValue().getChildren().add(dragged.getValue());
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
