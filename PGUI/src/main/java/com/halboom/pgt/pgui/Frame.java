package com.halboom.pgt.pgui;

import com.jme3.scene.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/19/13
 * Time: 3:48 PM
 * Holds UI components and containers.
 * The width and height of a frame has little to do with the elements within the frame.
 * The main purpose of the dimensions are to allow sub components to decide where to position
 * their elements based on the width and height of a frame.
 * Frame dimensions are generally based from a corner.
 * Generic GUI objects are based from the center.
 */
public class Frame extends GUIObject {
    /**
     * Key listener the frame can use for standard actions.
     */
    private GUIKeyListener guiKeyListener;

    /**
     * Width and height of the frame.
     */
    private float width = 0, height = 0;

    /**
     * Initializes the frame.
     */
    public Frame() {
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (guiKeyListener != null) {
            if (guiKeyListener.isKeyReleased(GUIKeys.closeWindow.name)) {
                detachFromParent();
            }
        }
    }

    /**
     * Attaches the GUI object's node to a displayable node.
     * @param root the root to attach to.
     */
    public void attachRoot(Node root) {
        detachFromParent();
        root.attachChild(getNode());
    }

    /**
     * Detaches from a node.
     */
    public void detachRoot() {
        detachFromParent();
        getNode().removeFromParent();
    }

    /**
     * Sets the gui key listener.
     * Setting the listener will allow the frame to automatically handle some actions.
     * @param guiKeyListener the gui key listener to use.
     */
    public void setGuiKeyListener(GUIKeyListener guiKeyListener) {
        this.guiKeyListener = guiKeyListener;
    }

    /**
     * @param width the width to set.
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * @param height the height to set.
     */
    public void setHeight(float height) {
        this.height = height;
    }

    @Override
    public float getExtentX() {
        return width / 2;
    }

    @Override
    public float getExtentY() {
        return height / 2;
    }
}
