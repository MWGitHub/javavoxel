package com.halboom.pgt.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/31/13
 * Time: 12:09 PM
 * Shows a grid in the scene.
 */
public class Grid {
    /**
     * Dimensions of the grid.
     */
    private static final int GRID_DIMENSION = 200;

    /**
     * Asset manager to load resources from.
     */
    private AssetManager assetManager;

    /**
     * Geometry of the grid.
     */
    private Geometry grid;

    /**
     * Number of lines on the grid.
     */
    private int size = GRID_DIMENSION;

    /**
     * Distance between each grid line.
     */
    private float distance = 1.0f;

    /**
     * Color of the grid lines.
     */
    private ColorRGBA color = ColorRGBA.Gray;

    /**
     * Initializes the grid.
     * @param assetManager the asset manager to use.
     */
    public Grid(AssetManager assetManager) {
        this.assetManager = assetManager;
        createGeometry();
    }

    /**
     * Creates the grid.
     */
    private void createGeometry() {
        grid = new Geometry("wireframe grid", new com.jme3.scene.debug.Grid(size, size, distance));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        grid.setMaterial(mat);
        grid.center().setLocalTranslation(- size * distance / 2, 0, - size * distance / 2);
    }

    /**
     * Attaches the grid to a node.
     * @param parent the parent to attach to.
     */
    public void attachTo(Node parent) {
        parent.attachChild(grid);
    }

    /**
     * Removes the grid.
     */
    public void detach() {
        grid.removeFromParent();
    }

    /**
     * Toggles the grid on or off.
     * @param parent the node to attach to when toggling on.
     */
    public void toggle(Node parent) {
        if (grid.getParent() == null) {
            attachTo(parent);
        } else {
            detach();
        }
    }

    /**
     * @param position the position to set the grid.
     */
    public void setPosition(Vector3f position) {
        Vector3f alignedPosition = new Vector3f(position);
        alignedPosition.x = position.x - size * distance / 2 - position.x % distance;
        alignedPosition.z = position.z - size * distance / 2 - position.z % distance;
        grid.setLocalTranslation(alignedPosition);
    }

    /**
     * @return true if the grid is currently attached to a parent node.
     */
    public boolean isAttached() {
        return grid.getParent() != null;
    }

    /**
     * Recreates the geometry and reattaches it if needed.
     */
    private void recreateGeometry() {
        Vector3f originalPosition = grid.getLocalTranslation().add(size * distance / 2, 0, size * distance / 2);
        Node parent = grid.getParent();
        detach();
        createGeometry();
        setPosition(originalPosition);
        if (parent != null) {
            attachTo(parent);
        }
    }

    /**
     * @param color the color to set.
     */
    public void setColor(ColorRGBA color) {
        this.color = color;
        recreateGeometry();
    }

    /**
     * @param size the number of squares to set.
     */
    public void setSize(int size) {
        this.size = size;
        recreateGeometry();
    }

    /**
     * @param distance the distance between lines to set.
     */
    public void setDistance(float distance) {
        this.distance = distance;
        recreateGeometry();
    }
}
