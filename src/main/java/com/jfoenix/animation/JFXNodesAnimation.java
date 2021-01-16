package com.jfoenix.animation;

import javafx.animation.Animation;
import javafx.scene.Node;

public abstract class JFXNodesAnimation<S extends Node, T extends Node> {

    protected S fromNode;
    protected T toNode;

    public JFXNodesAnimation(S fromNode, T toNode) {
        this.fromNode = fromNode;
        this.toNode = toNode;
    }

    public void animate() {
        init();
        Animation exitAnimation = animateExit();
        Animation sharedAnimation = animateSharedNodes();
        Animation entranceAnimation = animateEntrance();
        exitAnimation.setOnFinished(finish -> sharedAnimation.play());
        sharedAnimation.setOnFinished(finish -> entranceAnimation.play());
        entranceAnimation.setOnFinished(finish -> end());
        exitAnimation.play();
    }

    public abstract Animation animateExit();

    public abstract Animation animateSharedNodes();

    public abstract Animation animateEntrance();

    public abstract void init();

    public abstract void end();

}
