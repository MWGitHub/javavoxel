package com.halboom.pgt.debug;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Quad;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/31/13
 * Time: 3:02 PM
 * Shows directions on the screen or at a location.
 */
public class Compass {
    /**
     * Default line width.
     */
    private static final float DEFAULT_LINE_WIDTH = 4f;

    /**
     * Default text size.
     */
    private static final float DEFAULT_TEXT_SIZE = 13.0f;

    /**
     * Default compass colors.
     */
    private static final ColorRGBA DEFAULT_RED = ColorRGBA.Red,
    DEFAULT_GREEN = ColorRGBA.Green,
    DEFAULT_BLUE = new ColorRGBA(0.7f, 0.7f, 1.0f, 1.0f),
    DEFAULT_BACKGROUND = new ColorRGBA(0.0f, 0.0f, 0.0f, 0.4f);

    /**
     * Asset manager to load the materials from.
     */
    private AssetManager assetManager;

    /**
     * Geometry of the compass.
     */
    private Node compass;

    /**
     * Labels that are attached to the compass but are not effected by scaling nor rotation.
     */
    private Node attachedLabels;
    /**
     * Labels for the axis and rotations.
     */
    private BitmapText xLabel, yLabel, zLabel,
    rxLabel, ryLabel, rzLabel;

    /**
     * Line width of the arrows.
     */
    private float lineWidth = DEFAULT_LINE_WIDTH;

    /**
     * Initializes the compass.
     * @param assetManager the asset manager to use.
     */
    public Compass(AssetManager assetManager) {
        this.assetManager = assetManager;

        createCompass();
    }

    /**
     * Creates the compass.
     */
    private void createCompass() {
        compass = new Node();
        attachedLabels = new Node();

        final float yOffset = -DEFAULT_TEXT_SIZE;
        // Create the background.
        final float defaultWidth = 85.0f;
        final float paddingX = 5.0f;
        final float paddingY = 8.0f;
        Quad quad = new Quad(defaultWidth, yOffset * 7);
        Geometry background = new Geometry("Background", quad);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", DEFAULT_BACKGROUND);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        background.setMaterial(material);
        background.setLocalTranslation(new Vector3f(-paddingX, -paddingY, -1));
        attachedLabels.attachChild(background);

        // Create the arrows.
        // Flip the X arrow around to point in the positive direction.
        Geometry arrow = createArrow(Vector3f.UNIT_X, DEFAULT_RED, lineWidth);
        arrow.setLocalRotation(new Quaternion().fromAngles(0, FastMath.PI, 0));
        compass.attachChild(arrow);
        compass.attachChild(createArrow(Vector3f.UNIT_Y, DEFAULT_GREEN, lineWidth));
        compass.attachChild(createArrow(Vector3f.UNIT_Z, DEFAULT_BLUE, lineWidth));

        // Create the labels.
        xLabel = createLabel("X: 0", DEFAULT_RED);
        xLabel.setLocalTranslation(new Vector3f(0, yOffset, 0));
        attachedLabels.attachChild(xLabel);
        rxLabel = createLabel("RX: 0", DEFAULT_RED);
        rxLabel.setLocalTranslation(new Vector3f(0, yOffset * 2, 0));
        attachedLabels.attachChild(rxLabel);

        yLabel = createLabel("Y: 0", DEFAULT_GREEN);
        yLabel.setLocalTranslation(new Vector3f(0, yOffset * 3, 0));
        attachedLabels.attachChild(yLabel);
        ryLabel = createLabel("RY: 0", DEFAULT_GREEN);
        ryLabel.setLocalTranslation(new Vector3f(0, yOffset * 4, 0));
        attachedLabels.attachChild(ryLabel);

        zLabel = createLabel("Z: 0", DEFAULT_BLUE);
        zLabel.setLocalTranslation(new Vector3f(0, yOffset * 5, 0));
        attachedLabels.attachChild(zLabel);
        rzLabel = createLabel("RZ: 0", DEFAULT_BLUE);
        rzLabel.setLocalTranslation(new Vector3f(0, yOffset * 6, 0));
        attachedLabels.attachChild(rzLabel);
    }

    /**
     * Creates a label on the compass.
     * @param text the label text.
     * @param color the color of the text.
     * @return the geometry of the label.
     */
    private BitmapText createLabel(String text, ColorRGBA color) {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText label = new BitmapText(font, false);
        label.setSize(DEFAULT_TEXT_SIZE);
        label.setColor(color);
        label.setText(text);

        return label;
    }

    /**
     * Create the arrow geometry.
     * @param direction the shape of the arrow.
     * @param color the color of the arrow.
     * @param lineWidth the width of the arrow line.
     * @return the arrow geometry.
     */
    private Geometry createArrow(Vector3f direction, ColorRGBA color, float lineWidth) {
        Arrow arrow = new Arrow(direction);
        arrow.setLineWidth(lineWidth);
        Geometry arrowGeometry = new Geometry("compass arrow", arrow);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.getAdditionalRenderState().setWireframe(true);
        mat.setColor("Color", color);
        arrowGeometry.setMaterial(mat);

        return arrowGeometry;
    }

    /**
     * Converts a float to a fixed float with a position.
     * @param input the input to convert.
     * @param precision the precision in 10s.
     * @return the fixed float.
     */
    private float toFixed(float input, int precision) {
        float fixedAmount = (float) Math.pow(10, precision);
        return (int) (input * fixedAmount) / fixedAmount;
    }

    /**
     * Sets the position of the compass.
     */
    public void setPosition(Vector3f position) {
        compass.setLocalTranslation(position);
        attachedLabels.setLocalTranslation(position);
    }

    /**
     * @param quaternion the rotation of the compass to set.
     */
    public void setRotation(Quaternion quaternion) {
        compass.setLocalRotation(quaternion);

        float[] angles = quaternion.toAngles(null);
        final int precision = 2;
        rxLabel.setText("RX: " + toFixed(angles[0] * FastMath.RAD_TO_DEG, precision) + " d");
        ryLabel.setText("RY: " + toFixed(angles[1] * FastMath.RAD_TO_DEG, precision) + " d");
        rzLabel.setText("RZ: " + toFixed(angles[2] * FastMath.RAD_TO_DEG, precision) + " d");
    }

    /**
     * @param scale the scale to set the arrows.
     */
    public void setScale(float scale) {
        compass.setLocalScale(scale);

        BoundingBox compassBounds = (BoundingBox) compass.getWorldBound();
        Vector3f compassPosition = compass.getLocalTranslation();
        attachedLabels.setLocalTranslation(compassPosition.subtract(compassBounds.getXExtent() * 2, 0, 0));
    }

    /**
     * Sets the coordinate labels of the compass.
     * @param x the x amount to set.
     * @param y the y amount to set.
     * @param z the z amount to set.
     */
    public void setCoordinateLabels(float x, float y, float z) {
        final int precision = 2;
        xLabel.setText("X: " + toFixed(x, precision));
        yLabel.setText("Y: " + toFixed(y, precision));
        zLabel.setText("Z: " + toFixed(z, precision));
    }

    /**
     * Attaches the compass to the node.
     * @param parent the node to attach the compass to.
     */
    public void attachTo(Node parent) {
        parent.attachChild(compass);
        parent.attachChild(attachedLabels);
    }

    /**
     * Detaches the compass.
     */
    public void detach() {
        compass.removeFromParent();
        attachedLabels.removeFromParent();
    }

    /**
     * Toggles the compass of and on.
     * @param parent the node to attach to if toggled on.
     */
    public void toggle(Node parent) {
        if (isAttached()) {
            detach();
        } else {
            attachTo(parent);
        }
    }

    /**
     * @return true if the compass is attached to a node.
     */
    public boolean isAttached() {
        return compass.getParent() != null;
    }
}
