package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.jme3.scene.Spatial;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/17/13
 * Time: 4:07 PM
 */
public interface TileMesher {
    /**
     * Merges the tiles in a chunk.
     * @param tiles the tiles to load from.
     * @param scale the scale of a tile.
     * @param atlas the atlas to get the material and coordinates from.
     * @param tileBank the data to use for the tiles.
     * @param startIndex the starting index of the chunk.
     * @param endIndex the end index of the chunk.
     * @return the optimized mesh based on the tiles.
     */
    Spatial merge(byte[][][] tiles, float scale, TileAtlas atlas, TileBank tileBank, Vector3Int startIndex, Vector3Int endIndex);

    /**
     * Tells the mesher that the information is out of date and should be regenerated.
     */
    void setDirty();

    /**
     * Destroys data used by the mesh.
     * @param spatial the spatial the mesh outputted to be destroyed.
     */
    void destroy(Spatial spatial);
}
