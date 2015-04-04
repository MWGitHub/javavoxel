package com.submu.pug.editor.tools;

import com.halboom.pgt.input.InputActions;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.submu.pug.data.KeyMap;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/20/13
 * Time: 3:17 PM
 * Selects multiple tiles.
 */
public class SelectTool extends InputActions implements Tool {
    /**
     * Amount of space outside of the selection.
     */
    private static final float CUBE_BUFFER = 0.1f;

    /**
     * Index to signify a cleared selection.
     */
    public static final int CLEARED_SELECTION = -1;

    /**
     * Collider to use.
     */
    private GridColliderSystem collider;

    /**
     * Scale of each tile.
     */
    private float scale = 1.0f;

    /**
     * Block used for the tile selection display.
     */
    private Geometry selectionBlock;

    /**
     * Index of the add action,
     */
    private Vector3Int leftClickIndex = new Vector3Int(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);

    /**
     * Index of the remove action.
     */
    private Vector3Int rightClickIndex = new Vector3Int(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);

    /**
     * Starting selection index.
     */
    private Vector3Int selectStart = new Vector3Int(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);

    /**
     * Ending selection index which will always be greater than starting.
     */
    private Vector3Int selectEnd = new Vector3Int(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);

    /**
     * Initializes the tool.
     * @param inputManager the input manager to attach input events to.
     * @param assetManager the asset manager to use to load the tool selector.
     * @param parent the parent node to attach to.
     * @param collider the collider to use.
     * @param scale the scale of each tile.
     */
    public SelectTool(InputManager inputManager, AssetManager assetManager, Node parent, GridColliderSystem collider, float scale) {
        super(inputManager);
        this.collider = collider;
        this.scale = scale;

        // Create the targeted cube graphics.
        float cubeScale = scale / 2f;
        Box cube = new Box(cubeScale, cubeScale, cubeScale);
        selectionBlock = new Geometry("Targeting Cube", cube);
        Material targetMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        targetMaterial.setColor("Color", new ColorRGBA(0.0f, 0.0f, 1.0f, 0.3f));
        targetMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        selectionBlock.setMaterial(targetMaterial);
        selectionBlock.setQueueBucket(RenderQueue.Bucket.Transparent);
        selectionBlock.setCullHint(Spatial.CullHint.Always);
        parent.attachChild(selectionBlock);

        registerAction(KeyMap.editorCancel);
    }

    /**
     * Updates the selection model.
     */
    private void updateSelectionModel() {
        if (leftClickIndex.x == -1) {
            selectionBlock.setCullHint(Spatial.CullHint.Always);
            return;
        } else {
            selectionBlock.setCullHint(Spatial.CullHint.Inherit);
        }
        // Calculate the scales with a consistent buffer amount.
        float xLength = Math.abs(leftClickIndex.x - rightClickIndex.x) + 1;
        float actualLengthX = xLength * scale;
        float yLength = Math.abs(leftClickIndex.y - rightClickIndex.y) + 1;
        float actualLengthY = yLength * scale;
        float zLength = Math.abs(leftClickIndex.z - rightClickIndex.z) + 1;
        float actualLengthZ = zLength * scale;
        selectionBlock.setLocalScale(
                actualLengthX * (actualLengthX + CUBE_BUFFER * 2) / actualLengthX,
                actualLengthY * (actualLengthY + CUBE_BUFFER * 2) / actualLengthY,
                actualLengthZ * (actualLengthZ + CUBE_BUFFER * 2) / actualLengthZ);

        Vector3f startPosition = collider.getPositionFromGrid(selectStart.x, selectStart.y, selectStart.z);
        selectionBlock.setLocalTranslation(
                startPosition.x + (xLength - 1) * scale / 2f,
                startPosition.y + (yLength - 1) * scale / 2f,
                startPosition.z + (zLength - 1) * scale / 2f);
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onDeselected() {
    }

    /**
     * Keep the end indices greater than the start indices.
     */
    private void cleanIndices() {
        int swap;
        selectStart.set(leftClickIndex);
        selectEnd.set(rightClickIndex);
        if (selectStart.x > selectEnd.x) {
            swap = selectStart.x;
            selectStart.x = selectEnd.x;
            selectEnd.x = swap;
        }
        if (selectStart.y > selectEnd.y) {
            swap = selectStart.y;
            selectStart.y = selectEnd.y;
            selectEnd.y = swap;
        }
        if (selectStart.z > selectEnd.z) {
            swap = selectStart.z;
            selectStart.z = selectEnd.z;
            selectEnd.z = swap;
        }
    }

    @Override
    public void addBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch) {
        if (selectedIndex != null) {
            leftClickIndex.set(selectedIndex);
            if (rightClickIndex.x == CLEARED_SELECTION) {
                rightClickIndex.set(selectedIndex);
            }
            cleanIndices();
            updateSelectionModel();
        }
    }

    @Override
    public void removeBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch) {
        if (selectedIndex != null) {
            rightClickIndex.set(selectedIndex);
            if (leftClickIndex.x == CLEARED_SELECTION) {
                leftClickIndex.set(selectedIndex);
            }
            cleanIndices();
            updateSelectionModel();
        }
    }

    @Override
    protected void onActionInput(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (name.equals(KeyMap.editorCancel.name)) {
                leftClickIndex.set(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);
                rightClickIndex.set(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);
                selectStart.set(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);
                selectEnd.set(CLEARED_SELECTION, CLEARED_SELECTION, CLEARED_SELECTION);
                cleanIndices();
                updateSelectionModel();
            }
        }
    }

    @Override
    protected void onAnalogInput(String name, float value, float tpf) {
    }

    @Override
    protected void onUpdate(float tpf) {
    }

    @Override
    protected void onActivated() {
    }

    @Override
    protected void onDeactivated() {
    }

    @Override
    protected void cleanupAction() {
    }

    /**
     * @return the start of the selection.
     */
    public Vector3Int getSelectStart() {
        return selectStart;
    }

    /**
     * @return the end of the selection.
     */
    public Vector3Int getSelectEnd() {
        return selectEnd;
    }
}
