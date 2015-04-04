package com.halboom.pgt.pgui;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.WireBox;
import com.jme3.scene.shape.Sphere;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/10/13
 * Time: 3:15 PM
 * Shows debug information about GUI windows.
 */
public class GUIDebug {
    /**
     * Debug name for the debug geometry.
     */
    private static final String DEBUG_NAME = "com.halboom.pgt.pgui - GUI Debug Node";

    /**
     * Asset manager to use.
     */
    private static AssetManager assetManager;

    /**
     * Asset manager to use for showing the object bounds.
     * @param assetManager the asset manager to use.
     */
    public static void setAssetManager(AssetManager assetManager) {
        GUIDebug.assetManager = assetManager;
    }

    /**
     * Checks if the debug has been intialized.
     */
    private static void checkInitialization() {
        if (assetManager == null) {
            throw new InstantiationError("The asset manager has not been set.");
        }
    }

    /**
     * Creates an object bounds.
     * @param object the object to create the bounds of.
     * @return the geometry for the bounds.
     */
    private static Node createDebugNode(GUIObject object) {
        Node node = new Node(DEBUG_NAME);
        Geometry wire = new Geometry("Wire", new WireBox(object.getExtentX(), object.getExtentY(), object.getExtentZ()));
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        if (object instanceof Frame) {
            material.setColor("Color", ColorRGBA.Yellow);
        } else {
            material.setColor("Color", ColorRGBA.Magenta);
        }
        wire.setMaterial(material);
        // Frame bounds are considered to be started from the bottom left to the top right of the screen.
        if (object instanceof Frame) {
            wire.setLocalTranslation(object.getExtentX(), object.getExtentY(), 0);
        }
        node.attachChild(wire);

        final int samples = 15;
        final float radius = 2.0f;
        Geometry center = new Geometry("Center", new Sphere(samples, samples, radius));
        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        if (object instanceof Frame) {
            material.setColor("Color", ColorRGBA.Yellow);
        } else {
            material.setColor("Color", ColorRGBA.Magenta);
        }
        center.setMaterial(material);
        node.attachChild(center);

        return node;
    }

    /**
     * Shows the bounds of the object.
     * @param object the object to show the bounds of.
     */
    public static void showObjectBounds(GUIObject object) {
        checkInitialization();
        if (object.getNode().getChild(DEBUG_NAME) == null) {
            object.getNode().attachChild(createDebugNode(object));
        }
        for (GUIObject child : object.getChildren()) {
            showObjectBounds(child);
        }
    }

    /**
     * Hides the object bounds.
     * @param object the object to disable the object bounds.
     */
    public static void hideObjectBounds(GUIObject object) {
        Spatial spatial = object.getNode().getChild(DEBUG_NAME);
        if (spatial != null) {
            spatial.removeFromParent();
        }
        for (GUIObject child : object.getChildren()) {
            spatial = child.getNode().getChild(DEBUG_NAME);
            if (spatial != null) {
                spatial.removeFromParent();
            }
        }
    }
}
