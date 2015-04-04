package com.halboom.pgt.pgui;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/3/13
 * Time: 11:04 PM
 * Base GUI object that contains some standard methods.
 */
public abstract class GUIObject {
    /**
     * Node of the GUI object.
     * Attach additional spatials to the node.
     */
    private Node node = new Node();

    /**
     * Parent of the object.
     */
    private GUIObject parent;

    /**
     * Children of the object.
     */
    private List<GUIObject> children = new LinkedList<GUIObject>();

    /**
     * @return the parent of the object or null if none found.
     */
    public GUIObject getParent() {
        return parent;
    }

    /**
     * @return the children of the GUI object.
     */
    public List<GUIObject> getChildren() {
        return children;
    }

    /**
     * Attaches the child and sets the child's parent as this.
     * @param child the child to attach.
     */
    public void attach(GUIObject child) {
        if (child == null) {
            return;
        }
        if (child.parent == this) {
            return;
        }
        child.detachFromParent();
        child.parent = this;
        children.add(child);
        node.attachChild(child.node);
        child.onAttached();
    }

    /**
     * Detaches the child.
     * @param child the child to detach.
     */
    public void detach(GUIObject child) {
        if (children.contains(child)) {
            child.onDetached();
            node.detachChild(child.node);
            children.remove(child);
            child.parent = null;
        }
    }

    /**
     * Runs when the object has been attached to a parent.
     */
    public void onAttached() {

    }

    /**
     * Runs when the object has been detached by the parent.
     */
    public void onDetached() {

    }

    /**
     * Attaches an object to the gui object's node.
     * @param spatial the spatial to add.
     */
    public void addToNode(Spatial spatial) {
        node.attachChild(spatial);
    }

    /**
     * Removes a spatial from the gui object's node.
     * @param spatial the spatial to remove.
     */
    public void removeFromNode(Spatial spatial) {
        node.detachChild(spatial);
    }

    /**
     * Detaches from the parent and the root node if it is connected to one.
     */
    public void detachFromParent() {
        if (parent != null) {
            parent.detach(this);
        }
        // Useful for when the child has no GUI object parent but has a node parent.
        node.removeFromParent();
    }

    /**
     * Updates the object.
     * @param tpf the time per frame.
     */
    public void update(float tpf) {
        // Allow adding new elements when iterating.
        List<GUIObject> nextList = new ArrayList<GUIObject>();
        while (!children.isEmpty()) {
            GUIObject child = children.get(0);
            child.update(tpf);
            children.remove(child);
            nextList.add(child);
        }
        children = nextList;
    }

    /**
     * @param position the position to set.
     */
    public void setPosition(Vector3f position) {
        node.setLocalTranslation(position);
    }

    /**
     * @return the position of the gui object.
     */
    public Vector3f getPosition() {
        return node.getWorldTranslation();
    }

    /**
     * @return the local position of the gui object.
     */
    public Vector3f getLocalPosition() {
        return node.getLocalTranslation();
    }

    /**
     * @param scale the scale to set.
     */
    public void setScale(Vector3f scale) {
        node.setLocalScale(scale);
    }

    /**
     * @return the half X length.
     */
    public float getExtentX() {
        BoundingBox bounds = (BoundingBox) node.getWorldBound();
        if (bounds == null) {
            return 0;
        }
        return bounds.getXExtent();
    }

    /**
     * @return the half Y length.
     */
    public float getExtentY() {
        BoundingBox bounds = (BoundingBox) node.getWorldBound();
        if (bounds == null) {
            return 0;
        }
        return bounds.getYExtent();
    }

    /**
     * @return the half Z length.
     */
    public float getExtentZ() {
        BoundingBox bounds = (BoundingBox) node.getWorldBound();
        if (bounds == null) {
            return 0;
        }
        return bounds.getZExtent();
    }

    /**
     * Retrieves the node of the GUI object.
     * This should only be used for special cases.
     * @return the node of the gui object.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Clears all children but keeps the object on the parent.
     */
    public void clear() {
        for (GUIObject child : children) {
            child.destroy();
        }
        node.detachAllChildren();
    }

    /**
     * Destroys the gui object.
     */
    public void destroy() {
        for (GUIObject child : children) {
            child.destroy();
        }

        if (parent != null) {
            detach(this);
        }
        node.removeFromParent();
    }
}
