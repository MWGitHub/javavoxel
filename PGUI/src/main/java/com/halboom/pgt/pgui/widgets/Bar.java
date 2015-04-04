package com.halboom.pgt.pgui.widgets;

import com.halboom.pgt.pgui.Color;
import com.halboom.pgt.pgui.GUIObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/3/13
 * Time: 12:22 PM
 * A bar that can shrink and expand.
 */
public class Bar extends GUIObject {
    /**
     * Type of cooldown bar.
     */
    public enum Type {
        LEFT_RIGHT,
        DOWN_UP
    }

    /**
     * Asset manager to load images from.
     */
    private AssetManager assetManager;

    /**
     * Bar geometry.
     */
    private Geometry bar;

    /**
     * Width and height of the bar.
     */
    private float width, height;

    /**
     * Type of cooldown bar.
     */
    private Type type = Type.LEFT_RIGHT;

    /**
     * Initializes the bar with a parent.
     * @param assetManager asset manager to use.
     * @param width the width of the bar.
     * @param height the height of the bar.
     */
    public Bar(AssetManager assetManager, float width, float height) {
        this.assetManager = assetManager;
        this.width = width;
        this.height = height;
        Quad quad = new Quad(width, height);
        Material material = new Material(assetManager, "Common/MatDefs/Gui/Gui.j3md");
        material.setColor("Color", ColorRGBA.Red);
        bar = new Geometry("Bar", quad);
        bar.setMaterial(material);
        addToNode(bar);

        // Keep the bar centered with the node's center.
        bar.setLocalTranslation(-width / 2, -height / 2, 0.0f);
    }

    /**
     * Set the color overlay of the bar.
     * @param red the red amount.
     * @param green the green amount.
     * @param blue the blue amount.
     * @param alpha the alpha.
     */
    public void setColor(int red, int green, int blue, int alpha) {
        ColorRGBA color = new ColorRGBA(red / Color.MAX_COLOR, green / Color.MAX_COLOR, blue / Color.MAX_COLOR,
                alpha / Color.MAX_COLOR);
        bar.getMaterial().setColor("Color", color);
    }

    /**
     * @param path the path to set the image from.
     */
    public void setImage(String path) {
        if (path != null) {
            bar.getMaterial().setTexture("Texture", assetManager.loadTexture(path));
        }
    }

    /**
     * Sets how much the bar is filled.
     * @param amount the amount to fill the bar to.
     */
    public void setAmount(float amount) {
        if (type == Type.LEFT_RIGHT) {
            bar.setLocalScale(amount, 1, 1);
        } else if (type == Type.DOWN_UP) {
            bar.setLocalScale(1, amount, 1);
        }
    }

    /**
     * @param type the type of cooldown bar.
     */
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public float getExtentX() {
        return width / 2.0f;
    }

    @Override
    public float getExtentY() {
        return height / 2.0f;
    }
}
