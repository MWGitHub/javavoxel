package com.halboom.pgt.physics.debug;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/1/13
 * Time: 2:18 PM
 * Debug functions related to the grid collider system.
 */
public class DebugGrid {
    /**
     * Asset manager to load debug materials from.
     */
    private AssetManager assetManager;

    /**
     * Root to attach the grid objects to.
     */
    private Node root;

    /**
     * Debug root to attach to the root.
     */
    private Node debugRoot = new Node();

    /**
     * Tiles the grid uses.
     */
    private byte[][][] tiles;

    /**
     * Size of a tile.
     */
    private float size = 1.0f;

    /**
     * Initializes the debug grid.
     * @param assetManager the asset manager to use.
     */
    public DebugGrid(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * Generates the geometry for the tiles and attaches them to the debug root.
     */
    private void generateDisplay() {
        if (tiles == null) {
            return;
        }
        debugRoot = new Node();
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                for (int z = 0; z < tiles[x][y].length; z++) {
                    if (tiles[x][y][z] != 0) {
                        Box box = new Box(size / 2, size / 2, size / 2);
                        Geometry boxGeometry = new Geometry("grid", box);
                        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                        material.setTexture("ColorMap", assetManager.loadTexture("com/halboom/pgt/physics/Textures/RedFloorGrid.png"));
                        boxGeometry.setMaterial(material);
                        boxGeometry.setLocalTranslation(x * size + size / 2, y * size + size / 2, z * size + size / 2);
                        debugRoot.attachChild(boxGeometry);
                    }
                }
            }
        }
    }

    /**
     * @param tiles the tiles to set for debugging.
     */
    public void setTiles(byte[][][] tiles) {
        this.tiles = tiles;
        disable();
        generateDisplay();
        enable(root);
    }

    /**
     * @param size the size of a tile.
     */
    public void setTileSize(float size) {
        this.size = size;
        disable();
        generateDisplay();
        enable(root);
    }

    /**
     * @param position the offset position for the grid to display from.
     */
    public void setPosition(Vector3f position) {
        debugRoot.setLocalTranslation(position);
    }

    /**
     * Enables the debug grid.
     * @param root the root to attach to.
     */
    public void enable(Node root) {
        if (root == null) {
            return;
        }
        this.root = root;
        root.attachChild(debugRoot);
    }

    public void disable() {
        debugRoot.removeFromParent();
    }
}
