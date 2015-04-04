package com.submu.pug.editor.tools;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.terrainsystem.Terrain;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/16/13
 * Time: 3:46 PM
 * Tool used for drilling down to the lowest block.
 */
public class DrillTool implements Tool {
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
     * @param terrain the terrain to remove the blocks from.
     * @param gridColliderSystem the collider system to use for checking which tile is selected.
     */
    public DrillTool(Terrain terrain, GridColliderSystem gridColliderSystem) {
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
    }

    @Override
    public void removeBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch) {
        if (selectedIndex != null) {
            try {
                int y = selectedIndex.y;
                for (int i = y; i > 1; i--) {
                    int type = gridColliderSystem.getTileAtIndex(selectedIndex.x, i, selectedIndex.z);
                    if (type != Terrain.TILE_EMPTY) {
                        terrain.removeTile(selectedIndex.x, i, selectedIndex.z, isBatch);
                    }
                }
            } catch (IndexOutOfBoundsException error) {
                // Ignore removal when index is out of bounds.
                LoggerFactory.getLogger(DrillTool.class).debug("Block to remove is out of bounds.");
            }
        }
    }
}
