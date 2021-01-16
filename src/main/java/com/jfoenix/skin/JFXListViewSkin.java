package com.jfoenix.skin;

import com.jfoenix.control.JFXListView;
import com.jfoenix.effect.JFXDepthManager;
import javafx.scene.control.ListCell;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.layout.Region;

public class JFXListViewSkin<T> extends ListViewSkin<T> {

    private VirtualFlow<ListCell<T>> flow;

    public JFXListViewSkin(final JFXListView<T> listView) {
        super(listView);
        flow = (VirtualFlow<ListCell<T>>) getChildren().get(0);
        JFXDepthManager.setDepth(flow, listView.depthProperty().get());
        listView.depthProperty().addListener((o, oldVal, newVal) -> JFXDepthManager.setDepth(flow, newVal));
    }


    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        return 200;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        final int itemsCount = getSkinnable().getItems().size();
        if (getSkinnable().maxHeightProperty().isBound() || itemsCount <= 0) {
            return super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        }

        final double fixedCellSize = getSkinnable().getFixedCellSize();
        double computedHeight = fixedCellSize != Region.USE_COMPUTED_SIZE ?
                fixedCellSize * itemsCount + snapVerticalInsets() : estimateHeight();
        double height = super.computePrefHeight(width, topInset, rightInset, bottomInset, leftInset);
        if (height > computedHeight) {
            height = computedHeight;
        }

        if (getSkinnable().getMaxHeight() > 0 && computedHeight > getSkinnable().getMaxHeight()) {
            return getSkinnable().getMaxHeight();
        }

        return height;
    }

    private double estimateHeight() {
        // compute the border/padding for the list
        double borderWidth = snapVerticalInsets();
        // compute the gap between list cells

        JFXListView<T> listview = (JFXListView<T>) getSkinnable();
        double gap = listview.isExpanded() ? ((JFXListView<T>) getSkinnable()).getVerticalGap() * (getSkinnable().getItems()
                .size()) : 0;
        // compute the height of each list cell
        double cellsHeight = 0;
        for (int i = 0; i < flow.getCellCount(); i++) {
            ListCell<T> cell = flow.getCell(i);
            cellsHeight += cell.getHeight();
        }
        return cellsHeight + gap + borderWidth;
    }

    private double snapVerticalInsets() {
        return getSkinnable().snappedBottomInset() + getSkinnable().snappedTopInset();
    }

}
