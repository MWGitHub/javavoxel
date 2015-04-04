package com.submu.pug.editor.tools;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.terrainsystem.Terrain;
import com.jme3.renderer.Camera;
import com.submu.pug.data.Data;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/16/13
 * Time: 3:46 PM
 * Tool used for placing and removing single blocks.
 */
public class PlacementTool implements Tool {
    /**
     * Camera to use.
     */
    private Camera camera;

    /**
     * Terrain to use.
     */
    private Terrain terrain;

    /**
     * Grid collider to use.
     */
    private GridColliderSystem gridColliderSystem;

    /**
     * Initializes the tool.
     * @param camera the camera to use to aim the tool.
     * @param terrain the terrain to remove the blocks from.
     * @param gridColliderSystem the collider system to use for checking which tile is selected.
     */
    public PlacementTool(Camera camera, Terrain terrain, GridColliderSystem gridColliderSystem) {
        this.camera = camera;
        this.terrain = terrain;
        this.gridColliderSystem = gridColliderSystem;
    }

    @Override
    public void onSelected() {
    }

    @Override
    public void onDeselected() {
    }

    @Override
    public void addBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch) {
        // Get the closest empty grid.
        Vector3Int closestTile = selectedIndex;
        if (!isBatch) {
            closestTile = gridColliderSystem.getClosestEmptyGridFromRay(camera.getLocation(),
                    camera.getDirection(), Data.getInstance().getConfigData().controls.editor.maxPlaceDistance);
        }
        if (closestTile != null) {
            try {
                int type = gridColliderSystem.getTileAtIndex(closestTile.x, closestTile.y, closestTile.z);
                if (type == Terrain.TILE_EMPTY || isBatch) {
                    terrain.addTile(selectedType, closestTile.x, closestTile.y, closestTile.z, isBatch);
                }
            } catch (IndexOutOfBoundsException error) {
                // Ignore adding blocks when index is out of bounds.
                LoggerFactory.getLogger(PlacementTool.class).debug("Block to add is out of bounds.");
            }
        }
    }

    @Override
    public void removeBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch) {
        if (selectedIndex != null) {
            try {
                int type = gridColliderSystem.getTileAtIndex(selectedIndex.x, selectedIndex.y, selectedIndex.z);
                if (type != Terrain.TILE_EMPTY) {
                    terrain.removeTile(selectedIndex.x, selectedIndex.y, selectedIndex.z, isBatch);
                }
            } catch (IndexOutOfBoundsException error) {
                // Ignore removal when index is out of bounds.
                LoggerFactory.getLogger(PlacementTool.class).debug("Block to remove is out of bounds.");
            }
        }
    }
}
