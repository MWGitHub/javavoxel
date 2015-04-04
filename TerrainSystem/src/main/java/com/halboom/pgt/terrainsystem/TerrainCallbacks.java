package com.halboom.pgt.terrainsystem;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/9/13
 * Time: 12:03 AM
 * Callbacks for the terrain.
 */
public interface TerrainCallbacks {
    /**
     * Runs when a chunk is attached.
     * @param chunk the attached chunk.
     */
    void onChunkAttached(Chunk chunk);

    /**
     * Runs when a chunk is changed.
     * @param chunk the changed chunk.
     */
    void onChunkChanged(Chunk chunk);

    /**
     * Runs when a chunk is removed.
     * @param chunk the removed chunk.
     */
    void onChunkRemoved(Chunk chunk);
}
