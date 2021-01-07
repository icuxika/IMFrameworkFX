package com.jfoenix.skin;

import com.jfoenix.control.JFXButton;
import com.jfoenix.control.JFXRipple;
import com.jfoenix.control.JFXTabPane;
import com.jfoenix.effect.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.jfoenix.transition.CachedTransition;
import com.sun.javafx.scene.control.LambdaMultiplePropertyChangeListenerHandler;
import com.sun.javafx.scene.control.behavior.TabPaneBehavior;
import javafx.animation.*;
import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.collections.WeakListChangeListener;
import javafx.css.PseudoClass;
import javafx.geometry.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JFXTabPaneSkin extends SkinBase<JFXTabPane> {

    private HeaderContainer header;
    private ObservableList<TabContentHolder> tabContentHolders;
    private Rectangle clip;
    private Rectangle tabsClip;
    private Tab selectedTab;
    private boolean isSelectingTab = false;
    private double dragStart, offsetStart;
    private AnchorPane tabsContainer;
    private AnchorPane tabsContainerHolder;
    private static final int SPACER = 10;
    private double maxWidth = 0.0d;
    private double maxHeight = 0.0d;
    private final TabPaneBehavior behavior;

    public JFXTabPaneSkin(JFXTabPane tabPane) {
        super(tabPane);
        behavior = new TabPaneBehavior(tabPane);
        tabContentHolders = FXCollections.observableArrayList();
        header = new HeaderContainer();
        getChildren().add(JFXDepthManager.createMaterialNode(header, 1));

        tabsContainer = new AnchorPane();
        tabsContainerHolder = new AnchorPane();
        tabsContainerHolder.getChildren().add(tabsContainer);
        tabsClip = new Rectangle();
        tabsContainerHolder.setClip(tabsClip);
        getChildren().add(0, tabsContainerHolder);

        // add tabs
        for (Tab tab : getSkinnable().getTabs()) {
            addTabContentHolder(tab);
        }

        // clipping tabpane/header pane
        clip = new Rectangle(tabPane.getWidth(), tabPane.getHeight());
        getSkinnable().setClip(clip);
        if (getSkinnable().getTabs().size() == 0) {
            header.setVisible(false);
        }

        // select a tab
        selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
        if (selectedTab == null && getSkinnable().getSelectionModel().getSelectedIndex() != -1) {
            getSkinnable().getSelectionModel().select(getSkinnable().getSelectionModel().getSelectedIndex());
            selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
        }
        // if no selected tab, then select the first tab
        if (selectedTab == null) {
            getSkinnable().getSelectionModel().selectFirst();
        }
        selectedTab = getSkinnable().getSelectionModel().getSelectedItem();

        header.headersRegion.setOnMouseDragged(me -> {
            header.updateScrollOffset(offsetStart
                    + (isHorizontal() ? me.getSceneX() : me.getSceneY())
                    - dragStart);
            me.consume();
        });

        getSkinnable().addEventHandler(MouseEvent.MOUSE_PRESSED, me -> {
            dragStart = (isHorizontal() ? me.getSceneX() : me.getSceneY());
            offsetStart = header.scrollOffset;
        });

        // add listeners on tab list
        getSkinnable().getTabs().addListener((ListChangeListener<Tab>) change -> {
            List<Tab> tabsToBeRemoved = new ArrayList<>();
            List<Tab> tabsToBeAdded = new ArrayList<>();
            int insertIndex = -1;
            while (change.next()) {
                if (change.wasPermutated()) {
                    Tab selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
                    List<Tab> permutatedTabs = new ArrayList<>(change.getTo() - change.getFrom());
                    getSkinnable().getSelectionModel().clearSelection();
                    for (int i = change.getFrom(); i < change.getTo(); i++) {
                        permutatedTabs.add(getSkinnable().getTabs().get(i));
                    }
                    removeTabs(permutatedTabs);
                    addTabs(permutatedTabs, change.getFrom());
                    getSkinnable().getSelectionModel().select(selectedTab);
                }
                if (change.wasRemoved()) {
                    tabsToBeRemoved.addAll(change.getRemoved());
                }
                if (change.wasAdded()) {
                    tabsToBeAdded.addAll(change.getAddedSubList());
                    insertIndex = change.getFrom();
                }
            }
            // only remove the tabs that are not in tabsToBeAdded
            tabsToBeRemoved.removeAll(tabsToBeAdded);
            removeTabs(tabsToBeRemoved);
            // add the new tabs
            if (!tabsToBeAdded.isEmpty()) {
                for (TabContentHolder tabContentHolder : tabContentHolders) {
                    TabHeaderContainer tabHeaderContainer = header.getTabHeaderContainer(tabContentHolder.tab);
                    if (!tabHeaderContainer.isClosing && tabsToBeAdded.contains(tabContentHolder.tab)) {
                        tabsToBeAdded.remove(tabContentHolder.tab);
                    }
                }
                addTabs(tabsToBeAdded, insertIndex == -1 ? tabContentHolders.size() : insertIndex);
            }
            getSkinnable().requestLayout();
        });

        registerChangeListener(tabPane.getSelectionModel().selectedItemProperty(), obs -> {
            isSelectingTab = true;
            selectedTab = getSkinnable().getSelectionModel().getSelectedItem();
            getSkinnable().requestLayout();
        });
        registerChangeListener(tabPane.widthProperty(), obs -> {
            clip.setWidth(getSkinnable().getWidth());
        });
        registerChangeListener(tabPane.heightProperty(), obs -> {
            clip.setHeight(getSkinnable().getHeight());
        });

    }

    private boolean removedTabs = false;

    private void removeTabs(List<? extends Tab> removedTabs) {
        for (Tab tab : removedTabs) {
            TabHeaderContainer tabHeaderContainer = header.getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                tabHeaderContainer.isClosing = true;
                removeTab(tab);
                // if tabs list is empty hide the header container
                if (getSkinnable().getTabs().isEmpty()) {
                    header.setVisible(false);
                }
            }
        }
        this.removedTabs = !removedTabs.isEmpty();
    }

    private void addTabs(List<? extends Tab> addedTabs, int startIndex) {
        int i = 0;
        for (Tab tab : addedTabs) {
            // show header container if we are adding the 1st tab
            if (!header.isVisible()) {
                header.setVisible(true);
            }
            header.addTab(tab, startIndex + i++, false);
            addTabContentHolder(tab);
            final TabHeaderContainer tabHeaderContainer = header.getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                tabHeaderContainer.setVisible(true);
                tabHeaderContainer.inner.requestLayout();
            }
        }
    }

    private void addTabContentHolder(Tab tab) {
        // create new content place holder
        TabContentHolder tabContentHolder = new TabContentHolder(tab);
        tabContentHolder.setClip(new Rectangle());
        tabContentHolders.add(tabContentHolder);
        // always add tab content holder below its header
        tabsContainer.getChildren().add(0, tabContentHolder);
    }

    private void removeTabContentHolder(Tab tab) {
        for (TabContentHolder tabContentHolder : tabContentHolders) {
            if (tabContentHolder.tab.equals(tab)) {
                tabContentHolder.removeListeners(tab);
                getChildren().remove(tabContentHolder);
                tabContentHolders.remove(tabContentHolder);
                tabsContainer.getChildren().remove(tabContentHolder);
                break;
            }
        }
    }

    private void removeTab(Tab tab) {
        final TabHeaderContainer tabHeaderContainer = header.getTabHeaderContainer(tab);
        if (tabHeaderContainer != null) {
            tabHeaderContainer.removeListeners();
        }
        header.removeTab(tab);
        removeTabContentHolder(tab);
        header.requestLayout();
    }


    private boolean isHorizontal() {
        final Side tabPosition = getSkinnable().getSide();
        return Side.TOP.equals(tabPosition) || Side.BOTTOM.equals(tabPosition);
    }

    private static int getRotation(Side pos) {
        switch (pos) {
            case TOP:
                return 0;
            case BOTTOM:
                return 180;
            case LEFT:
                return -90;
            case RIGHT:
                return 90;
            default:
                return 0;
        }
    }


    @Override
    protected double computePrefWidth(double height, double topInset, double rightInset, double bottomInset, double leftInset) {
        for (TabContentHolder tabContentHolder : tabContentHolders) {
            maxWidth = Math.max(maxWidth, snapSize(tabContentHolder.prefWidth(-1)));
        }
        final double headerContainerWidth = snapSize(header.prefWidth(-1));
        double prefWidth = Math.max(maxWidth, headerContainerWidth);
        return snapSize(prefWidth) + rightInset + leftInset;
    }

    @Override
    protected double computePrefHeight(double width, double topInset, double rightInset, double bottomInset, double leftInset) {
        for (TabContentHolder tabContentHolder : tabContentHolders) {
            maxHeight = Math.max(maxHeight, snapSize(tabContentHolder.prefHeight(-1)));
        }
        final double headerContainerHeight = snapSize(header.prefHeight(-1));
        double prefHeight = maxHeight + snapSize(headerContainerHeight);
        return snapSize(prefHeight) + topInset + bottomInset;
    }

    @Override
    public double computeBaselineOffset(double topInset, double rightInset, double bottomInset, double leftInset) {
        return header.getBaselineOffset() + topInset;
    }

    /*
     *  keep track of indices after changing the tabs, it used to fix
     *  tabs animation after changing the tabs (remove/add)
     */
    private int diffTabsIndices = 0;


    @Override
    protected void layoutChildren(final double x, final double y, final double w, final double h) {
        final double headerHeight = snapSize(header.prefHeight(-1));
        final Side side = getSkinnable().getSide();
        double tabsX = side == Side.RIGHT ? x + w - headerHeight : x;
        double tabsY = side == Side.BOTTOM ? y + h - headerHeight : y;
        final int rotation = getRotation(side);

        // update header
        switch (side) {
            case TOP:
                header.resize(w, headerHeight);
                header.relocate(tabsX, tabsY);
                break;
            case LEFT:
                header.resize(h, headerHeight);
                header.relocate(tabsX + headerHeight, h - headerHeight);
                break;
            case RIGHT:
                header.resize(h, headerHeight);
                header.relocate(tabsX, y - headerHeight);
                break;
            case BOTTOM:
                header.resize(w, headerHeight);
                header.relocate(w, tabsY - headerHeight);
                break;
        }
        header.getTransforms().setAll(new Rotate(rotation, 0, headerHeight, 1));

        // update header clip
//        header.clip.setX(0);
//        header.clip.setY(0);
//        header.clip.setWidth(isHorizontal() ? w : h);
//        header.clip.setHeight(headerHeight + 10); // 10 is the height of the shadow effect

        // position the tab content of the current selected tab
        double contentStartX = x + (side == Side.LEFT ? headerHeight : 0);
        double contentStartY = y + (side == Side.TOP ? headerHeight : 0);
        double contentWidth = w - (isHorizontal() ? 0 : headerHeight);
        double contentHeight = h - (isHorizontal() ? headerHeight : 0);

        // update tabs container
        tabsClip.setWidth(contentWidth);
        tabsClip.setHeight(contentHeight);
        tabsContainerHolder.resize(contentWidth, contentHeight);
        tabsContainerHolder.relocate(contentStartX, contentStartY);
        tabsContainer.resize(contentWidth * tabContentHolders.size(), contentHeight);

        for (int i = 0, max = tabContentHolders.size(); i < max; i++) {
            TabContentHolder tabContentHolder = tabContentHolders.get(i);
            tabContentHolder.setVisible(true);
            tabContentHolder.setTranslateX(contentWidth * i);
            if (tabContentHolder.getClip() != null) {
                ((Rectangle) tabContentHolder.getClip()).setWidth(contentWidth);
                ((Rectangle) tabContentHolder.getClip()).setHeight(contentHeight);
            }
            if (tabContentHolder.tab == selectedTab) {
                int index = getSkinnable().getTabs().indexOf(selectedTab);
                if (index != i) {
                    tabsContainer.setTranslateX(-contentWidth * i);
                    diffTabsIndices = i - index;
                } else {
                    // fix X translation after changing the tabs
                    if (diffTabsIndices != 0) {
                        tabsContainer.setTranslateX(tabsContainer.getTranslateX() + contentWidth * diffTabsIndices);
                        diffTabsIndices = 0;
                    }
                    // animate upon tab selection only otherwise just translate the selected tab
                    if (isSelectingTab && !((JFXTabPane) getSkinnable()).isDisableAnimation()) {
                        new CachedTransition(tabsContainer,
                                new Timeline(new KeyFrame(Duration.millis(1000),
                                        new KeyValue(tabsContainer.translateXProperty(),
                                                -contentWidth * index,
                                                Interpolator.EASE_BOTH)))) {{
                            setCycleDuration(Duration.seconds(0.320));
                            setDelay(Duration.seconds(0));
                        }}.play();
                    } else {
                        tabsContainer.setTranslateX(-contentWidth * index);
                    }
                }
            }
            tabContentHolder.resize(contentWidth, contentHeight);
//            tabContentHolder.relocate(contentStartX, contentStartY);
        }
    }

    /**************************************************************************
     *																		  *
     * HeaderContainer: tabs headers container 						     	  *
     *																		  *
     **************************************************************************/
    protected class HeaderContainer extends StackPane {

        private Rectangle clip;
        private StackPane headersRegion;
        private StackPane headerBackground;

        private HeaderControl rightControlButton;
        private HeaderControl leftControlButton;
        private StackPane selectedTabLine;
        private boolean initialized = false;
        private boolean measureClosingTabs = false;
        private double scrollOffset, selectedTabLineOffset;

        private final Scale scale;
        private final Rotate rotate;
        private int direction;
        private Timeline timeline;
        private final double translateScaleFactor = 1.3;

        public HeaderContainer() {
            // keep track of the current side
            getSkinnable().sideProperty().addListener(observable -> updateDirection());
            updateDirection();

            getStyleClass().setAll("tab-header-area");
            setManaged(false);
            clip = new Rectangle();
            headersRegion = new StackPane() {
                @Override
                protected double computePrefWidth(double height) {
                    double width = 0.0F;
                    for (Node child : getChildren()) {
                        if (child instanceof TabHeaderContainer
                                && child.isVisible()
                                && (measureClosingTabs || !((TabHeaderContainer) child).isClosing)) {
                            width += child.prefWidth(height);
                        }
                    }
                    return snapSize(width) + snappedLeftInset() + snappedRightInset();
                }

                @Override
                protected double computePrefHeight(double width) {
                    double height = 0.0F;
                    for (Node child : getChildren()) {
                        if (child instanceof TabHeaderContainer) {
                            height = Math.max(height, child.prefHeight(width));
                        }
                    }
                    return snapSize(height) + snappedTopInset() + snappedBottomInset();
                }

                @Override
                protected void layoutChildren() {
                    if (isTabsFitHeaderWidth()) {
                        updateScrollOffset(0.0);
                    } else {
                        if (!removedTabsHeaders.isEmpty()) {
                            double offset = 0;
                            double w = header.getWidth() - snapSize(rightControlButton.prefWidth(-1)) - snapSize(
                                    leftControlButton.prefWidth(-1)) - snappedLeftInset() - SPACER;
                            Iterator<Node> itr = getChildren().iterator();
                            while (itr.hasNext()) {
                                Node temp = itr.next();
                                if (temp instanceof TabHeaderContainer) {
                                    TabHeaderContainer tabHeaderContainer = (TabHeaderContainer) temp;
                                    double containerPrefWidth = snapSize(tabHeaderContainer.prefWidth(-1));
                                    // if tab has been removed
                                    if (removedTabsHeaders.contains(tabHeaderContainer)) {
                                        if (offset < w) {
                                            isSelectingTab = true;
                                        }
                                        itr.remove();
                                        removedTabsHeaders.remove(tabHeaderContainer);
                                        if (removedTabsHeaders.isEmpty()) {
                                            break;
                                        }
                                    }
                                    offset += containerPrefWidth;
                                }
                            }
                        }
                    }

                    if (removedTabs) {
                        updateSelectionLine(false);
                        removedTabs = false;
                    }

                    if (isSelectingTab) {
                        // make sure the selected tab is visible
                        updateSelectionLine(true);
                        isSelectingTab = false;
                    } else {
                        // validate scroll offset
                        updateScrollOffset(scrollOffset);
                    }

                    final double tabBackgroundHeight = snapSize(prefHeight(-1));
                    final Side side = getSkinnable().getSide();
                    double tabStartX = (side == Side.LEFT || side == Side.BOTTOM) ?
                            snapSize(getWidth()) - scrollOffset : scrollOffset;
                    updateHeaderContainerClip();
                    for (Node node : getChildren()) {
                        if (node instanceof TabHeaderContainer) {
                            TabHeaderContainer tabHeaderContainer = (TabHeaderContainer) node;
                            double tabHeaderPrefWidth = snapSize(tabHeaderContainer.prefWidth(-1));
                            double tabHeaderPrefHeight = snapSize(tabHeaderContainer.prefHeight(-1));
                            tabHeaderContainer.resize(tabHeaderPrefWidth, tabHeaderPrefHeight);

                            double tabStartY = side == Side.BOTTOM ?
                                    0 : tabBackgroundHeight - tabHeaderPrefHeight - snappedBottomInset();
                            if (side == Side.LEFT || side == Side.BOTTOM) {
                                // build from the right
                                tabStartX -= tabHeaderPrefWidth;
                                tabHeaderContainer.relocate(tabStartX, tabStartY);
                            } else {
                                // build from the left
                                tabHeaderContainer.relocate(tabStartX, tabStartY);
                                tabStartX += tabHeaderPrefWidth;
                            }
                        }
                    }
                    selectedTabLine.resizeRelocate((side == Side.LEFT || side == Side.BOTTOM) ?
                                    snapSize(headersRegion.getWidth()) : 0
                            , tabBackgroundHeight - selectedTabLine.prefHeight(-1)
                            , snapSize(selectedTabLine.prefWidth(-1))
                            , snapSize(selectedTabLine.prefHeight(-1)));
                }
            };

            headersRegion.getStyleClass().setAll("headers-region");
            headersRegion.setCache(true);
            headersRegion.setClip(clip);

            headerBackground = new StackPane();
            headerBackground.getStyleClass().setAll("tab-header-background");
            selectedTabLine = new StackPane();
            selectedTabLine.setManaged(false);
            scale = new Scale(1, 1, 0, 0);
            rotate = new Rotate(0, 0, 1);
            rotate.pivotYProperty().bind(selectedTabLine.heightProperty().divide(2));

            selectedTabLine.getTransforms().addAll(scale, rotate);
            selectedTabLine.setCache(true);
            selectedTabLine.getStyleClass().add("tab-selected-line");
            headersRegion.getChildren().add(selectedTabLine);

            rightControlButton = new HeaderControl(ArrowPosition.RIGHT);
            leftControlButton = new HeaderControl(ArrowPosition.LEFT);
            rightControlButton.setVisible(false);
            leftControlButton.setVisible(false);
            rightControlButton.inner.prefHeightProperty().bind(headersRegion.heightProperty());
            leftControlButton.inner.prefHeightProperty().bind(headersRegion.heightProperty());

            getChildren().addAll(headerBackground, headersRegion, leftControlButton, rightControlButton);

            int i = 0;
            for (Tab tab : getSkinnable().getTabs()) {
                addTab(tab, i++, true);
            }

            // support for mouse scroll of header area
            addEventHandler(ScrollEvent.SCROLL, (ScrollEvent e) ->
                    updateScrollOffset(scrollOffset + e.getDeltaY() * (isHorizontal() ? -1 : 1)));
        }

        private void updateDirection() {
            final Side side = getSkinnable().getSide();
            direction = (side == Side.BOTTOM || side == Side.LEFT) ? -1 : 1;
        }

        private void updateHeaderContainerClip() {
            final double clipOffset = getClipOffset();
            final Side side = getSkinnable().getSide();
            double controlPrefWidth = 2 * snapSize(rightControlButton.prefWidth(-1));
            // Add the spacer if the control buttons are shown
//            controlPrefWidth = controlPrefWidth > 0 ? controlPrefWidth + SPACER : controlPrefWidth;

            measureClosingTabs = true;
            final double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            final double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));
            measureClosingTabs = false;

            final double maxWidth = snapSize(getWidth()) - controlPrefWidth - clipOffset;
            final double clipWidth = headersPrefWidth < maxWidth ? headersPrefWidth : maxWidth;
            final double clipHeight = headersPrefHeight;

            clip.setX((side == Side.LEFT || side == Side.BOTTOM)
                    && headersPrefWidth >= maxWidth ? headersPrefWidth - maxWidth : 0);
            clip.setY(0);
            clip.setWidth(clipWidth);
            clip.setHeight(clipHeight);
        }

        private double getClipOffset() {
            return isHorizontal() ? snappedLeftInset() : snappedRightInset();
        }

        private void addTab(Tab tab, int addToIndex, boolean visible) {
            TabHeaderContainer tabHeaderContainer = new TabHeaderContainer(tab);
            tabHeaderContainer.setVisible(visible);
            headersRegion.getChildren().add(addToIndex, tabHeaderContainer);
        }

        private List<TabHeaderContainer> removedTabsHeaders = new ArrayList<>();

        private void removeTab(Tab tab) {
            TabHeaderContainer tabHeaderContainer = getTabHeaderContainer(tab);
            if (tabHeaderContainer != null) {
                if (isTabsFitHeaderWidth()) {
                    headersRegion.getChildren().remove(tabHeaderContainer);
                } else {
                    // we need to keep track of the removed tab headers
                    // to compute scroll offset of the header
                    removedTabsHeaders.add(tabHeaderContainer);
                    tabHeaderContainer.removeListeners();
                }
            }
        }

        private TabHeaderContainer getTabHeaderContainer(Tab tab) {
            for (Node child : headersRegion.getChildren()) {
                if (child instanceof TabHeaderContainer) {
                    if (((TabHeaderContainer) child).tab.equals(tab)) {
                        return (TabHeaderContainer) child;
                    }
                }
            }
            return null;
        }

        private boolean isTabsFitHeaderWidth() {
            double headerPrefWidth = snapSize(headersRegion.prefWidth(-1));
            double rightControlWidth = 2 * snapSize(rightControlButton.prefWidth(-1));
            double visibleWidth = headerPrefWidth + rightControlWidth + snappedLeftInset() + SPACER;
            return visibleWidth <= getWidth();
        }

        private void runTimeline(double newTransX, double newWidth) {
            if (selectedTabLine.getTranslateX() == newTransX
                    && scale.getX() == newWidth) return;

            double tempScaleX = 0;
            double tempWidth = 0;
            final double lineWidth = selectedTabLine.prefWidth(-1);

            if ((isAnimating())) {
                timeline.stop();
                tempScaleX = scale.getX();
                if (rotate.getAngle() != 0) {
                    rotate.setAngle(0);
                    tempWidth = tempScaleX * lineWidth;
                    selectedTabLine.setTranslateX(selectedTabLine.getTranslateX() - tempWidth);
                }
            }

            final double oldScaleX = scale.getX();
            final double oldWidth = lineWidth * oldScaleX;
            final double oldTransX = selectedTabLine.getTranslateX();
            final double newScaleX = (newWidth * oldScaleX) / oldWidth;
            // keep track of the original offset
            selectedTabLineOffset = newTransX;
            // add offset to the computed translation
            newTransX = newTransX + offsetStart * direction;
            final double transDiff = newTransX - oldTransX;


            double midScaleX = tempScaleX != 0 ?
                    tempScaleX : ((Math.abs(transDiff) / translateScaleFactor + oldWidth) * oldScaleX) / oldWidth;

            if (midScaleX > Math.abs(transDiff) + newWidth) {
                midScaleX = Math.abs(transDiff) + newWidth;
            }
            if (transDiff < 0) {
                selectedTabLine.setTranslateX(selectedTabLine.getTranslateX() + oldWidth);
                newTransX += newWidth;
                rotate.setAngle(180);
            }

            timeline = new Timeline(
                    new KeyFrame(
                            Duration.ZERO,
                            new KeyValue(selectedTabLine.translateXProperty(), selectedTabLine.getTranslateX(), Interpolator.EASE_BOTH)),
                    new KeyFrame(
                            Duration.seconds(.12),
                            new KeyValue(scale.xProperty(), midScaleX, Interpolator.EASE_BOTH),
                            new KeyValue(selectedTabLine.translateXProperty(), selectedTabLine.getTranslateX(), Interpolator.EASE_BOTH)),
                    new KeyFrame(
                            Duration.seconds(.24),
                            new KeyValue(scale.xProperty(), newScaleX, Interpolator.EASE_BOTH),
                            new KeyValue(selectedTabLine.translateXProperty(), newTransX, Interpolator.EASE_BOTH))
            );

            timeline.setOnFinished(finish -> {
                if (rotate.getAngle() != 0) {
                    rotate.setAngle(0);
                    selectedTabLine.setTranslateX(selectedTabLine.getTranslateX() - newWidth);
                }
            });
            timeline.play();
        }

        private boolean isAnimating() {
            return timeline != null && timeline.getStatus() == Animation.Status.RUNNING;
        }

        private void updateScrollOffset(double newOffset) {
            double tabPaneWidth = snapSize(isHorizontal() ?
                    getSkinnable().getWidth() : getSkinnable().getHeight());
            double controlTabWidth = 2 * snapSize(rightControlButton.getWidth());
            double visibleWidth = tabPaneWidth - controlTabWidth - snappedLeftInset() - SPACER;

            // compute all tabs headers width
            double offset = 0.0;
            for (Node node : headersRegion.getChildren()) {
                if (node instanceof TabHeaderContainer) {
                    double tabHeaderPrefWidth = snapSize(node.prefWidth(-1));
                    offset += tabHeaderPrefWidth;
                }
            }

            double actualOffset = newOffset;
            if ((visibleWidth - newOffset) > offset && newOffset < 0) {
                actualOffset = visibleWidth - offset;
            } else if (newOffset > 0) {
                actualOffset = 0;
            }

            if (actualOffset != scrollOffset) {
                scrollOffset = actualOffset;
                headersRegion.requestLayout();
                if (!isAnimating()) {
                    selectedTabLine.setTranslateX(selectedTabLineOffset + scrollOffset * direction);
                }
            }
        }

        @Override
        protected double computePrefWidth(double height) {
            final double padding = isHorizontal() ?
                    2 * snappedLeftInset() + snappedRightInset() :
                    2 * snappedTopInset() + snappedBottomInset();
            return snapSize(headersRegion.prefWidth(height))
                    + 2 * rightControlButton.prefWidth(height)
                    + padding + SPACER;
        }

        @Override
        protected double computePrefHeight(double width) {
            final double padding = isHorizontal() ?
                    snappedTopInset() + snappedBottomInset() :
                    snappedLeftInset() + snappedRightInset();
            return snapSize(headersRegion.prefHeight(-1)) + padding;
        }

        @Override
        public double getBaselineOffset() {
            return getSkinnable().getSide() == Side.TOP ?
                    headersRegion.getBaselineOffset() + snappedTopInset() : 0;
        }

        @Override
        protected void layoutChildren() {
            final double leftInset = snappedLeftInset();
            final double rightInset = snappedRightInset();
            final double topInset = snappedTopInset();
            final double bottomInset = snappedBottomInset();
            final double padding = isHorizontal() ?
                    leftInset + rightInset : topInset + bottomInset;
            final double w = snapSize(getWidth()) - padding;
            final double h = snapSize(getHeight()) - padding;
            final double tabBackgroundHeight = snapSize(prefHeight(-1));
            final double headersPrefWidth = snapSize(headersRegion.prefWidth(-1));
            final double headersPrefHeight = snapSize(headersRegion.prefHeight(-1));

            rightControlButton.showTabsMenu(!isTabsFitHeaderWidth());
            leftControlButton.showTabsMenu(!isTabsFitHeaderWidth());

            updateHeaderContainerClip();
            headersRegion.requestLayout();

            // layout left/right controls buttons
            final double btnWidth = snapSize(rightControlButton.prefWidth(-1));
            final double btnHeight = rightControlButton.prefHeight(btnWidth);
            rightControlButton.resize(btnWidth, btnHeight);
            leftControlButton.resize(btnWidth, btnHeight);

            // layout tabs
            headersRegion.resize(headersPrefWidth, headersPrefHeight);
            headerBackground.resize(snapSize(getWidth()), snapSize(getHeight()));

            final Side side = getSkinnable().getSide();
            double startX = 0;
            double startY = 0;
            double controlStartX = 0;
            double controlStartY = 0;
            switch (side) {
                case TOP:
                    startX = leftInset;
                    startY = tabBackgroundHeight - headersPrefHeight - bottomInset;
                    controlStartX = w - btnWidth + leftInset;
                    controlStartY = snapSize(getHeight()) - btnHeight - bottomInset;
                    break;
                case BOTTOM:
                    startX = snapSize(getWidth()) - headersPrefWidth - leftInset;
                    startY = tabBackgroundHeight - headersPrefHeight - topInset;
                    controlStartX = rightInset;
                    controlStartY = snapSize(getHeight()) - btnHeight - topInset;
                    break;
                case LEFT:
                    startX = snapSize(getWidth()) - headersPrefWidth - topInset;
                    startY = tabBackgroundHeight - headersPrefHeight - rightInset;
                    controlStartX = leftInset;
                    controlStartY = snapSize(getHeight()) - btnHeight - rightInset;
                    break;
                case RIGHT:
                    startX = topInset;
                    startY = tabBackgroundHeight - headersPrefHeight - leftInset;
                    controlStartX = w - btnWidth + topInset;
                    controlStartY = snapSize(getHeight()) - btnHeight - leftInset;
                    break;
            }

            if (headerBackground.isVisible()) {
                positionInArea(headerBackground, 0, 0, snapSize(getWidth()),
                        snapSize(getHeight()), 0, HPos.CENTER, VPos.CENTER);
            }

            positionInArea(headersRegion,
                    startX + btnWidth * ((side == Side.LEFT || side == Side.BOTTOM) ? -1 : 1),
                    startY, w, h, 0, HPos.LEFT, VPos.CENTER);

            positionInArea(rightControlButton, controlStartX, controlStartY, btnWidth, btnHeight,
                    0, HPos.CENTER, VPos.CENTER);

            positionInArea(leftControlButton, (side == Side.LEFT || side == Side.BOTTOM) ?
                            w - btnWidth : 0, controlStartY, btnWidth, btnHeight, 0,
                    HPos.CENTER, VPos.CENTER);

            rightControlButton.setRotate((side == Side.LEFT || side == Side.BOTTOM) ? 180.0F : 0.0F);
            leftControlButton.setRotate((side == Side.LEFT || side == Side.BOTTOM) ? 180.0F : 0.0F);

            if (!initialized) {
                updateSelectionLine(true);
                initialized = true;
            }
        }

        private void updateSelectionLine(boolean animate) {
            double offset = 0.0;
            double selectedTabOffset = 0.0;
            double selectedTabWidth = 0.0;
            final Side side = getSkinnable().getSide();
            for (Node node : headersRegion.getChildren()) {
                if (node instanceof TabHeaderContainer) {
                    TabHeaderContainer tabHeader = (TabHeaderContainer) node;
                    double tabHeaderPrefWidth = snapSize(tabHeader.prefWidth(-1));
                    if (selectedTab != null && selectedTab.equals(tabHeader.tab)) {
                        selectedTabOffset = (side == Side.LEFT || side == Side.BOTTOM) ?
                                -offset - tabHeaderPrefWidth : offset;
                        selectedTabWidth = tabHeaderPrefWidth;
                        break;
                    }
                    offset += tabHeaderPrefWidth;
                }
            }
            // animate the tab selection
            if (selectedTabWidth > 0) {
                if (animate) {
                    runTimeline(selectedTabOffset, selectedTabWidth);
                } else {
                    selectedTabLine.setTranslateX(selectedTabOffset + scrollOffset * direction);
                    scale.setX(selectedTabWidth);
                    selectedTabLineOffset = selectedTabOffset;
                }
            }
        }
    }


    /**************************************************************************
     *																		  *
     * TabHeaderContainer: each tab Container								  *
     *																		  *
     **************************************************************************/

    protected class TabHeaderContainer extends StackPane {

        private Tab tab = null;
        private Label tabLabel;
        private Tooltip oldTooltip;
        private Tooltip tooltip;
        private HBox inner;
        private Button closeButton;
        private JFXRipple rippler;
        private boolean isClosing = false;

        private final LambdaMultiplePropertyChangeListenerHandler listener =
                new LambdaMultiplePropertyChangeListenerHandler();

        private final ListChangeListener<String> styleClassListener =
                (Change<? extends String> change) -> getStyleClass().setAll(tab.getStyleClass());

        private final WeakListChangeListener<String> weakStyleClassListener =
                new WeakListChangeListener<>(styleClassListener);

        public TabHeaderContainer(final Tab tab) {
            this.tab = tab;

            getStyleClass().setAll(tab.getStyleClass());
            setId(tab.getId());
            setStyle(tab.getStyle());

            tabLabel = new Label(tab.getText(), tab.getGraphic());
            tabLabel.getStyleClass().setAll("tab-label");

            closeButton = new JFXButton("", new SVGGlyph());
            closeButton.getStyleClass().add("tab-close-button");
            closeButton.setOnAction(action -> {
                if (behavior.canCloseTab(tab)) {
                    behavior.closeTab(tab);
                    setOnMouseClicked(null);
                }
            });

            inner = new HBox();
            inner.getChildren().setAll(tabLabel, closeButton);
            inner.setAlignment(Pos.CENTER);
            inner.getStyleClass().add("tab-container");
            inner.setRotate(getSkinnable().getSide().equals(Side.BOTTOM) ? 180.0F : 0.0F);

            rippler = new JFXRipple(inner);
            getChildren().addAll(rippler);

            tooltip = tab.getTooltip();
            if (tooltip != null) {
                Tooltip.install(this, tooltip);
                oldTooltip = tooltip;
            }

            listener.registerChangeListener(tab.selectedProperty(), obs -> {
                pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
                updateInnerUI();
            });
            listener.registerChangeListener(tab.textProperty(), obs -> tabLabel.setText(tab.getText()));
            listener.registerChangeListener(tab.graphicProperty(), obs -> tabLabel.setGraphic(tab.getGraphic()));
            listener.registerChangeListener(widthProperty(), obs -> header.updateSelectionLine(true));
            listener.registerChangeListener(tab.tooltipProperty(), obs -> {
                // install new Toolip/ uninstall the old one
                if (oldTooltip != null) {
                    Tooltip.uninstall(this, oldTooltip);
                }
                tooltip = tab.getTooltip();
                if (tooltip != null) {
                    Tooltip.install(this, tooltip);
                    oldTooltip = tooltip;
                }
            });
            listener.registerChangeListener(tab.disableProperty(), obs -> {
                pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
                updateInnerUI();
            });
            listener.registerChangeListener(tab.styleProperty(), obs -> setStyle(tab.getStyle()));
            listener.registerChangeListener(getSkinnable().tabMinWidthProperty(), obs -> updateSkinnableUI());
            listener.registerChangeListener(getSkinnable().tabMaxWidthProperty(), obs -> updateSkinnableUI());
            listener.registerChangeListener(getSkinnable().tabMinHeightProperty(), obs -> updateSkinnableUI());
            listener.registerChangeListener(getSkinnable().tabMaxHeightProperty(), obs -> updateSkinnableUI());
            listener.registerChangeListener(getSkinnable().sideProperty(), obs -> {
                final Side side = getSkinnable().getSide();
                pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
                pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
                pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
                pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
                inner.setRotate(side == Side.BOTTOM ? 180.0F : 0.0F);
            });
            listener.registerChangeListener(getSkinnable().tabClosingPolicyProperty(), obs -> updateInnerUI());

            tab.getStyleClass().addListener(weakStyleClassListener);

            getProperties().put(Tab.class, tab);

            setOnMouseClicked((event) -> {
                if (tab.isDisable() || !event.isStillSincePress()) {
                    return;
                }
                if (event.getButton() == MouseButton.MIDDLE) {
                    if (showCloseButton()) {
                        if (behavior.canCloseTab(tab)) {
                            removeListeners();
                            behavior.closeTab(tab);
                        }
                    }
                } else if (event.getButton() == MouseButton.PRIMARY) {
                    setOpacity(1);
                    behavior.selectTab(tab);
                }
            });

            addEventHandler(ContextMenuEvent.CONTEXT_MENU_REQUESTED, event -> {
                ContextMenu contextMenu = tab.getContextMenu();
                if (contextMenu != null) {
                    contextMenu.show(tabLabel, event.getScreenX(), event.getScreenY());
                    event.consume();
                }
            });

            // initialize pseudo-class state
            pseudoClassStateChanged(SELECTED_PSEUDOCLASS_STATE, tab.isSelected());
            pseudoClassStateChanged(DISABLED_PSEUDOCLASS_STATE, tab.isDisable());
            final Side side = getSkinnable().getSide();
            pseudoClassStateChanged(TOP_PSEUDOCLASS_STATE, (side == Side.TOP));
            pseudoClassStateChanged(RIGHT_PSEUDOCLASS_STATE, (side == Side.RIGHT));
            pseudoClassStateChanged(BOTTOM_PSEUDOCLASS_STATE, (side == Side.BOTTOM));
            pseudoClassStateChanged(LEFT_PSEUDOCLASS_STATE, (side == Side.LEFT));
            pseudoClassStateChanged(CLOSABLE, showCloseButton());
        }

        private void updateInnerUI() {
            inner.requestLayout();
            requestLayout();
        }

        private void updateSkinnableUI() {
            requestLayout();
            getSkinnable().requestLayout();
        }

        private boolean showCloseButton() {
            return tab.isClosable() &&
                    (getSkinnable().getTabClosingPolicy().equals(TabPane.TabClosingPolicy.ALL_TABS));
//                   ||
//                    getSkinnable().getTabClosingPolicy().equals(TabPane.TabClosingPolicy.SELECTED_TAB) && tab.isSelected());
        }

        private void removeListeners() {
            listener.dispose();
            inner.getChildren().clear();
            getChildren().clear();
        }

        @Override
        protected double computePrefWidth(double height) {
            double minWidth = snapSize(getSkinnable().getTabMinWidth());
            double maxWidth = snapSize(getSkinnable().getTabMaxWidth());
            double paddingRight = snappedRightInset();
            double paddingLeft = snappedLeftInset();
            double tmpPrefWidth = snapSize(tabLabel.prefWidth(-1));

            if (showCloseButton()) {
                tmpPrefWidth += snapSize(closeButton.prefWidth(-1));
            }

            if (tmpPrefWidth > maxWidth) {
                tmpPrefWidth = maxWidth;
            } else if (tmpPrefWidth < minWidth) {
                tmpPrefWidth = minWidth;
            }
            tmpPrefWidth += paddingRight + paddingLeft;
            return tmpPrefWidth;
        }

        @Override
        protected double computePrefHeight(double width) {
            double minHeight = snapSize(getSkinnable().getTabMinHeight());
            double maxHeight = snapSize(getSkinnable().getTabMaxHeight());
            double paddingTop = snappedTopInset();
            double paddingBottom = snappedBottomInset();
            double tmpPrefHeight = snapSize(tabLabel.prefHeight(width));

            if (tmpPrefHeight > maxHeight) {
                tmpPrefHeight = maxHeight;
            } else if (tmpPrefHeight < minHeight) {
                tmpPrefHeight = minHeight;
            }
            tmpPrefHeight += paddingTop + paddingBottom;
            return tmpPrefHeight;
        }

        @Override
        protected void layoutChildren() {
            final boolean showClose = showCloseButton();
            closeButton.setManaged(showClose);
            closeButton.setVisible(showClose);
            pseudoClassStateChanged(CLOSABLE, showClose);
            rippler.resizeRelocate(0, 0, getWidth(), getHeight());
        }
    }

    private static final PseudoClass SELECTED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass DISABLED_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("disabled");
    private static final PseudoClass TOP_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("top");
    private static final PseudoClass BOTTOM_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("bottom");
    private static final PseudoClass LEFT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("left");
    private static final PseudoClass RIGHT_PSEUDOCLASS_STATE = PseudoClass.getPseudoClass("right");
    private static final PseudoClass CLOSABLE = PseudoClass.getPseudoClass("closable");

    /**************************************************************************
     *																		  *
     * TabContentHolder: each tab content container						      *
     *																		  *
     **************************************************************************/
    protected class TabContentHolder extends StackPane {
        private Tab tab;
        private InvalidationListener tabContentListener = valueModel -> updateContent();
        private InvalidationListener tabSelectedListener = valueModel -> setVisible(tab.isSelected());
        private WeakInvalidationListener weakTabContentListener = new WeakInvalidationListener(tabContentListener);
        private WeakInvalidationListener weakTabSelectedListener = new WeakInvalidationListener(tabSelectedListener);

        public TabContentHolder(Tab tab) {
            this.tab = tab;
            getStyleClass().setAll("tab-content-area");
            setManaged(false);
            updateContent();
            setVisible(tab.isSelected());
            tab.selectedProperty().addListener(weakTabSelectedListener);
            tab.contentProperty().addListener(weakTabContentListener);
        }

        private void updateContent() {
            Node newContent = tab.getContent();
            if (newContent == null) {
                getChildren().clear();
            } else {
                getChildren().setAll(newContent);
            }
        }

        private void removeListeners(Tab tab) {
            tab.selectedProperty().removeListener(weakTabSelectedListener);
            tab.contentProperty().removeListener(weakTabContentListener);
        }
    }

    private enum ArrowPosition {
        RIGHT, LEFT
    }

    /**************************************************************************
     *																		  *
     * HeaderControl: left/right controls to interact with HeaderContainer*
     *																		  *
     **************************************************************************/
    protected class HeaderControl extends StackPane {
        private StackPane inner;
        private boolean showControlButtons, isLeftArrow;
        private Timeline arrowAnimation;
        private SVGGlyph arrowButton;
        private StackPane container;

        private PseudoClass left = PseudoClass.getPseudoClass("left");
        private PseudoClass right = PseudoClass.getPseudoClass("right");

        public HeaderControl(ArrowPosition pos) {
            getStyleClass().setAll("control-buttons-tab");
            isLeftArrow = pos == ArrowPosition.LEFT;
            arrowButton = new SVGGlyph();
            arrowButton.pseudoClassStateChanged(isLeftArrow ? left : right, true);
            arrowButton.getStyleClass().setAll("tab-down-button");
            arrowButton.setVisible(isControlButtonShown());
            StackPane.setMargin(arrowButton, new Insets(0, 0, 0, isLeftArrow ? -4 : 4));

            DoubleProperty offsetProperty = new SimpleDoubleProperty(0);
            offsetProperty.addListener((o, oldVal, newVal) -> header.updateScrollOffset(newVal.doubleValue()));

            container = new StackPane(arrowButton);
            container.getStyleClass().add("container");
            container.setPadding(new Insets(7));
            container.setCursor(Cursor.HAND);
            container.setOnMousePressed(press -> {
                offsetProperty.set(header.scrollOffset);
                double offset = isLeftArrow ? header.scrollOffset + header.headersRegion.getWidth() : header.scrollOffset - header.headersRegion
                        .getWidth();
                arrowAnimation = new Timeline(new KeyFrame(Duration.seconds(1),
                        new KeyValue(offsetProperty, offset, Interpolator.LINEAR)));
                arrowAnimation.play();
            });
            container.setOnMouseReleased(release -> arrowAnimation.stop());
            JFXRipple arrowRipple = new JFXRipple(container, JFXRipple.RippleMask.CIRCLE, JFXRipple.RipplePos.BACK);
            arrowRipple.setPadding(new Insets(0, 5, 0, 5));

            inner = new StackPane() {
                @Override
                protected double computePrefWidth(double height) {
                    double preferWidth = 0.0d;
                    double maxArrowWidth = !isControlButtonShown() ? 0 : snapSize(arrowRipple.prefWidth(getHeight()));
                    preferWidth += isControlButtonShown() ? maxArrowWidth : 0;
                    preferWidth += (preferWidth > 0) ? snappedLeftInset() + snappedRightInset() : 0;
                    return preferWidth;
                }

                @Override
                protected double computePrefHeight(double width) {
                    double prefHeight = 0.0d;
                    prefHeight = isControlButtonShown() ? Math.max(prefHeight,
                            snapSize(arrowRipple.prefHeight(width))) : 0;
                    prefHeight += prefHeight > 0 ? snappedTopInset() + snappedBottomInset() : 0;
                    return prefHeight;
                }

                @Override
                protected void layoutChildren() {
                    if (isControlButtonShown()) {
                        double x = 0;
                        double y = snappedTopInset();
                        double width = snapSize(getWidth()) - x + snappedLeftInset();
                        double height = snapSize(getHeight()) - y + snappedBottomInset();
                        positionArrow(arrowRipple, x, y, width, height);
                    }
                }

                private void positionArrow(JFXRipple rippler, double x, double y, double width, double height) {
                    rippler.resize(width, height);
                    positionInArea(rippler, x, y, width, height, 0, HPos.CENTER, VPos.CENTER);
                }
            };

            inner.getChildren().add(arrowRipple);
            getChildren().add(inner);

            showControlButtons = false;
            if (isControlButtonShown()) {
                showControlButtons = true;
                requestLayout();
            }
        }

        private boolean showTabsHeaderControls = false;

        private void showTabsMenu(boolean value) {

            final boolean wasTabsMenuShowing = isControlButtonShown();
            this.showTabsHeaderControls = value;

            // need to show & it was not showing
            if (showTabsHeaderControls && !wasTabsMenuShowing) {
                arrowButton.setVisible(true);
                showControlButtons = true;
                inner.requestLayout();
                header.layoutChildren();
            } else {
                // need to hide & was showing
                if (!showTabsHeaderControls && wasTabsMenuShowing) {
                    container.setPrefWidth(0);
                    // hide control button
                    if (isControlButtonShown()) {
                        showControlButtons = true;
                    } else {
                        setVisible(false);
                    }
                    requestLayout();
                }
            }
        }

        private boolean isControlButtonShown() {
            return showTabsHeaderControls;
        }

        @Override
        protected double computePrefWidth(double height) {
            double prefWidth = snapSize(inner.prefWidth(height));
            if (prefWidth > 0) {
                prefWidth += snappedLeftInset() + snappedRightInset();
            }
            return prefWidth;
        }

        @Override
        protected double computePrefHeight(double width) {
            return Math.max(getSkinnable().getTabMinHeight(),
                    snapSize(inner.prefHeight(width))) + snappedTopInset() + snappedBottomInset();
        }

        @Override
        protected void layoutChildren() {
            double x = snappedLeftInset();
            double y = snappedTopInset();
            double width = snapSize(getWidth()) - x + snappedRightInset();
            double height = snapSize(getHeight()) - y + snappedBottomInset();
            if (showControlButtons) {
                setVisible(true);
                showControlButtons = false;
            }
            inner.resize(width, height);
            positionInArea(inner, x, y, width, height, 0, HPos.CENTER, VPos.BOTTOM);
        }
    }

}
