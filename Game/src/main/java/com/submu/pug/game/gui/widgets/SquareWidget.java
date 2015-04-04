package com.submu.pug.game.gui.widgets;

import com.google.common.base.Strings;
import com.halboom.pgt.pgui.GUIObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 9/3/13
 * Time: 1:26 PM
 * Widget of a square image.
 */
public class SquareWidget extends GUIObject {
    /**
     * Asset manager to use.
     */
    private AssetManager assetManager;

    /**
     * Icon of the widget.
     */
    private Geometry icon;

    /**
     * Initializes the widget.
     * @param assetManager the asset manager to use.
     * @param width the width of the widget.
     * @param height the height of the widget.
     */
    public SquareWidget(AssetManager assetManager, float width, float height) {
        this.assetManager = assetManager;

        Quad iconQuad = new Quad(width, height);
        icon = new Geometry("Icon", iconQuad);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        icon.setMaterial(material);
        icon.setLocalTranslation(-width / 2.0f, -height / 2.0f, 0.0f);
        addToNode(icon);
    }

    /**
     * @param icon the icon image to set.
     */
    public void setIcon(String icon) {
        if (!Strings.isNullOrEmpty(icon)) {
            this.icon.getMaterial().setTexture("ColorMap", assetManager.loadTexture(icon));
        }
    }
}
