package com.jfoenix.transition;

import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.layout.Region;

import java.util.concurrent.atomic.AtomicBoolean;

public class CacheMemento {
    private boolean cache;
    private boolean cacheShape;
    private boolean snapToPixel;
    private CacheHint cacheHint = CacheHint.DEFAULT;
    private Node node;
    private AtomicBoolean isCached = new AtomicBoolean(false);

    public CacheMemento(Node node) {
        this.node = node;
    }

    /**
     * this method will cache the node only if it wasn't cached before
     */
    public void cache() {
        if (!isCached.getAndSet(true)) {
            this.cache = node.isCache();
            this.cacheHint = node.getCacheHint();
            node.setCache(true);
            node.setCacheHint(CacheHint.SPEED);
            if (node instanceof Region) {
                this.cacheShape = ((Region) node).isCacheShape();
                this.snapToPixel = ((Region) node).isSnapToPixel();
                ((Region) node).setCacheShape(true);
                ((Region) node).setSnapToPixel(true);
            }
        }
    }

    public void restore() {
        if (isCached.getAndSet(false)) {
            node.setCache(cache);
            node.setCacheHint(cacheHint);
            if (node instanceof Region) {
                ((Region) node).setCacheShape(cacheShape);
                ((Region) node).setSnapToPixel(snapToPixel);
            }
        }
    }
}
