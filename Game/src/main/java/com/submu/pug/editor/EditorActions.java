package com.submu.pug.editor;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.input.InputActions;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.terrainsystem.Terrain;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.submu.pug.data.Data;
import com.submu.pug.data.KeyMap;
import com.submu.pug.editor.tools.DrillTool;
import com.submu.pug.editor.tools.PlacementTool;
import com.submu.pug.editor.tools.SelectTool;
import com.submu.pug.editor.tools.Tool;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/31/12
 * Time: 4:14 PM
 */
public class EditorActions extends InputActions {
    /**
     * Tools for the editor.
     */
    private List<Tool> tools = new ArrayList<Tool>();

    /**
     * Terrain to use for tiles.
     */
    private Terrain terrain;

    /**
     * Grid collision system to check for block collisions.
     */
    private GridColliderSystem collider;

    /**
     * Camera to use when deciding placement locations.
     */
    private Camera camera;

    /**
     * Currently selected tile index.
     */
    private byte selectedTileIndex = 0;

    /**
     * Selected tool.
     */
    private Tool selectedTool;

    /**
     * Scale of each tile.
     */
    private float scale = 1.0f;

    /**
     * Initializes the editor movement.
     * @param assetManager the asset manager to use for loading assets.
     * @param inputManager the input manager to attach to.
     * @param camera the camera to use when placing objects.
     * @param terrain the terrain of the game.
     * @param collider the grid collider system to use.
     * @param root the root node to attach objects to.
     * @param scale the scale of each tile.
     */
    public EditorActions(AssetManager assetManager, InputManager inputManager, Camera camera,
                         Terrain terrain, GridColliderSystem collider, Node root, float scale) {
        super(inputManager);
        this.camera = camera;
        this.terrain = terrain;
        this.collider = collider;
        this.scale = scale;

        // Create the tools.
        tools.add(new PlacementTool(camera, terrain, collider));
        tools.add(new DrillTool(terrain, collider));
        tools.add(new SelectTool(inputManager, assetManager, root, collider, scale));

        selectedTool = tools.get(0);

        registerAction(KeyMap.editorRemoveBlock);
        registerAction(KeyMap.editorAddBlock);
        registerAction(KeyMap.editorIncrementBlock);
        registerAction(KeyMap.editorDecrementBlock);
        registerAction(KeyMap.editorToolNormal);
        registerAction(KeyMap.editorToolDrill);
        registerAction(KeyMap.editorToolSelect);
    }

    @Override
    protected final void onActionInput(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            // Remove a block that the user is looking at.
            if (name.equals(KeyMap.editorRemoveBlock.name)) {
                removeBlock();
            } else if (name.equals(KeyMap.editorAddBlock.name)) {
                addBlock();
            } else if (name.equals(KeyMap.editorIncrementBlock.name)) {
                selectedTileIndex++;
                if (selectedTileIndex > Byte.MAX_VALUE + Math.abs(Byte.MIN_VALUE)) {
                    selectedTileIndex = (byte) (Byte.MAX_VALUE + Math.abs(Byte.MIN_VALUE));
                }
                DebugGlobals.println(selectedTileIndex);
            } else if (name.equals(KeyMap.editorDecrementBlock.name)) {
                selectedTileIndex--;
                if (selectedTileIndex < 0) {
                    selectedTileIndex = 0;
                }
                DebugGlobals.println(selectedTileIndex);
            } else if (name.equals(KeyMap.editorToolNormal.name)) {
                selectedTool.onDeselected();
                selectedTool = tools.get(0);
                selectedTool.onSelected();
                DebugGlobals.println("Selected tool: 1");
            } else if (name.equals(KeyMap.editorToolDrill.name)) {
                selectedTool.onDeselected();
                selectedTool = tools.get(1);
                selectedTool.onSelected();
                DebugGlobals.println("Selected tool: 2");
            } else if (name.equals(KeyMap.editorToolSelect.name)) {
                selectedTool.onDeselected();
                selectedTool = tools.get(2);
                selectedTool.onSelected();
                DebugGlobals.println("Selected tool: 3");
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
     * Removes a block from the terrain.
     */
    private void removeBlock() {
        Vector3Int closestTile = collider.getClosestUsedGridFromRay(camera.getLocation(),
                camera.getDirection(), Data.getInstance().getConfigData().controls.editor.maxPlaceDistance);
        if (closestTile == null) {
            return;
        }
        // Select tool is always single block retrieval.
        SelectTool selectTool = getTool(SelectTool.class);
        if (selectedTool.equals(selectTool)) {
            selectedTool.removeBlock(closestTile, selectedTileIndex, false);
            return;
        }
        // If no select tool exists then use single tile operations.
        if (selectTool == null) {
            selectedTool.removeBlock(closestTile, selectedTileIndex, false);
        } else {
            // If there is no selection then treat it as a single tile operation.
            if (selectTool.getSelectStart().x == SelectTool.CLEARED_SELECTION) {
                selectedTool.removeBlock(closestTile, selectedTileIndex, false);
            } else {
                Vector3Int start = selectTool.getSelectStart();
                Vector3Int end = selectTool.getSelectEnd();
                for (int x = start.x; x <= end.x; x++) {
                    for (int y = start.y; y <= end.y; y++) {
                        for (int z = start.z; z <= end.z; z++) {
                            closestTile.set(x, y, z);
                            selectedTool.removeBlock(closestTile, selectedTileIndex, true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adds a block to the terrain next to a tile or floor.
     */
    private void addBlock() {
        Vector3Int closestTile = collider.getClosestUsedGridFromRay(camera.getLocation(),
                camera.getDirection(), Data.getInstance().getConfigData().controls.editor.maxPlaceDistance);
        if (closestTile == null) {
            return;
        }
        // Select tool is always single block retrieval.
        SelectTool selectTool = getTool(SelectTool.class);
        if (selectedTool.equals(selectTool)) {
            selectedTool.addBlock(closestTile, selectedTileIndex, false);
            return;
        }
        // If no select tool exists then use single tile operations.
        if (selectTool == null) {
            selectedTool.addBlock(closestTile, selectedTileIndex, false);
        } else {
            // If there is no selection then treat it as a single tile operation.
            if (selectTool.getSelectStart().x == SelectTool.CLEARED_SELECTION) {
                selectedTool.addBlock(closestTile, selectedTileIndex, false);
            } else {
                Vector3Int start = selectTool.getSelectStart();
                Vector3Int end = selectTool.getSelectEnd();
                for (int x = start.x; x <= end.x; x++) {
                    for (int y = start.y; y <= end.y; y++) {
                        for (int z = start.z; z <= end.z; z++) {
                            closestTile.set(x, y, z);
                            selectedTool.addBlock(closestTile, selectedTileIndex, true);
                        }
                    }
                }
            }
        }
    }

    /**
     * Retrieves a tool given the class.
     * @param cls the class of the tool.
     * @param <T> the type of the tool.
     * @return the tool or null if none found.
     */
    private <T extends Tool> T getTool(Class<T> cls) {
        for (Tool tool : tools) {
            if (tool.getClass().equals(cls)) {
                return cls.cast(tool);
            }
        }
        return null;
    }
}
