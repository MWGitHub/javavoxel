package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/7/13
 * Time: 5:27 PM
 * Updates chunks and sets their visibilities.
 */
public class ChunkUpdater {
    /**
     * Default size of a chunk.
     */
    private static final int DEFAULT_CHUNK_SIZE = 16;

    /**
     * Default viewing distance.
     */
    private static final float DEFAULT_VIEW_DISTANCE = 96f;

    /**
     * Chunks for holding the map tiles.
     */
    private Chunk[][][] chunks;

    /**
     * Future that is used to check chunk updating status.
     */
    private Future updatedChunkFuture;

    /**
     * Tiles of the chunk.
     */
    private byte[][][] tiles;

    /**
     * Atlas to use for the meshing material.
     */
    private TileAtlas tileAtlas;

    /**
     * Data to use for the tiles.
     */
    private TileBank tileBank;

    /**
     * Dimensions of the map in tiles.
     */
    private Vector3Int mapDimensions = new Vector3Int();

    /**
     * Dimensions of the chunk in tiles per chunk.
     */
    private Vector3Int chunkDimensions = new Vector3Int(DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_SIZE, DEFAULT_CHUNK_SIZE);

    /**
     * Viewing distance to determine if a chunk should be visible.
     */
    private float viewDistance = DEFAULT_VIEW_DISTANCE;

    /**
     * Scale of a tile.
     */
    private float scale = 1f;

    /**
     * Callbacks for the chunk states.
     */
    private TerrainCallbacks callbacks;

    /**
     * Creates the chunks for the map.
     * @param tiles the tiles of the map.
     * @param mapDimensions the dimensions of the map in tiles.
     */
    public ChunkUpdater(byte[][][] tiles, Vector3Int mapDimensions) {
        this.tiles = tiles;
        this.mapDimensions.set(mapDimensions);

        recreateChunks();
    }

    /**
     * Recreates the chunks or creates them if chunks do not already exist.
     */
    private void recreateChunks() {
        destroy();

        // Create the empty chunks and map them to the tile segments.
        chunks = new Chunk[mapDimensions.x / chunkDimensions.x]
                [mapDimensions.y / chunkDimensions.y]
                [mapDimensions.z / chunkDimensions.z];
        Vector3Int startingIndex = new Vector3Int();
        Vector3Int endIndex = new Vector3Int();
        for (int x = 0; x < chunks.length; x++) {
            for (int y = 0; y < chunks[x].length; y++) {
                for (int z = 0; z < chunks[x][y].length; z++) {
                    startingIndex.x = x * chunkDimensions.x;
                    startingIndex.y = y * chunkDimensions.y;
                    startingIndex.z = z * chunkDimensions.z;
                    endIndex.x = x * chunkDimensions.x + chunkDimensions.x - 1;
                    endIndex.y = y * chunkDimensions.y + chunkDimensions.y - 1;
                    endIndex.z = z * chunkDimensions.z + chunkDimensions.z - 1;
                    chunks[x][y][z] = new Chunk(startingIndex, endIndex, scale);
                }
            }
        }
    }

    /**
     * Updates the visibility of chunks depending on the camera orientation.
     * @param executorService the executor service to create threads with.
     * @param center the center point to update from.
     * @param attachedNode the node to attach a visible chunk to.
     */
    public void updateVisibility(ExecutorService executorService, Vector3f center, Node attachedNode) {
        float diagonal = FastMath.sqrt(2);
        float maxDistance = viewDistance + chunkDimensions.x * scale * diagonal;
        // Find the closest chunk that has not been optimized.
        float smallestDistance = Float.MAX_VALUE;
        // Index of the smallest distance chunk.
        int sx = -1, sy = -1, sz = -1;
        // Go through each chunk and check if it is within view distance.
        for (int x = 0; x < chunks.length; x++) {
            for (int y = 0; y < chunks[x].length; y++) {
                for (int z = 0; z < chunks[x][y].length; z++) {
                    // Show chunks that are within the view distance from the center of the chunk.
                    float distance = chunks[x][y][z].getCenter().distance(center);
                    if (distance < maxDistance) {
                        // Find the smallest distance chunk that isn't merged.
                        if (!chunks[x][y][z].getIsMerged() && distance < smallestDistance) {
                            smallestDistance = distance;
                            sx = x;
                            sy = y;
                            sz = z;
                        }
                        // Show the chunk when merged otherwise merge the chunk as long as no other chunks are being merged.
                        if (chunks[x][y][z].getIsMerged()) {
                            if (chunks[x][y][z].getBatchedGeometry().getParent() == null) {
                                if (callbacks != null) {
                                    callbacks.onChunkAttached(chunks[x][y][z]);
                                }
                            }
                            chunks[x][y][z].attach(attachedNode);
                        }
                    } else {
                        // Hide
                        chunks[x][y][z].detach();
                        if (callbacks != null) {
                            callbacks.onChunkRemoved(chunks[x][y][z]);
                        }
                    }
                }
            }
        }
        // Merge the closest chunk when no merging threads are running.
        // Only merge if a tile atlas is being used as merging is mainly used for rendering.
        if (tileAtlas != null && tileBank != null && (updatedChunkFuture == null || updatedChunkFuture.isDone()) && sx != -1) {
            final int tx = sx;
            final int ty = sy;
            final int tz = sz;
            updatedChunkFuture = executorService.submit(new Runnable() {
                @Override
                public void run() {
                    chunks[tx][ty][tz].createAttachable(tiles, scale, tileAtlas, tileBank);
                }
            });
            try {
                updatedChunkFuture.get();
            } catch (InterruptedException e) {
                Logger.getLogger(ChunkUpdater.class.getName()).log(Level.SEVERE, "Unable to create the chunk mesh.", e);
            } catch (ExecutionException e) {
                Logger.getLogger(ChunkUpdater.class.getName()).log(Level.SEVERE, "Unable to create the chunk mesh.", e);
            }
        }
    }

    /**
     * Updates chunks touching a particular tile index and the chunk containing the index.
     * @param x the x index of the tile.
     * @param y the y index of the tile.
     * @param z the z index of the tile.
     * @param attachedNode the node to attach to.
     * @param isQueued true to queue the updated chunks or false to instantly update.
     */
    public void updateTouchedChunks(int x, int y, int z, Node attachedNode, boolean isQueued) {
        // Add the modified chunk to a changed chunk list.
        Chunk modifiedChunk = getChunkAtTileIndex(x, y, z);
        List<Chunk> updatedChunks = new LinkedList<Chunk>();
        updatedChunks.add(modifiedChunk);
        // Find other chunks that border the modified tile
        Chunk borderingChunk = getChunkAtTileIndex(x + 1, y, z);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }
        borderingChunk = getChunkAtTileIndex(x - 1, y, z);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }
        borderingChunk = getChunkAtTileIndex(x, y + 1, z);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }
        borderingChunk = getChunkAtTileIndex(x, y - 1, z);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }
        borderingChunk = getChunkAtTileIndex(x, y, z + 1);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }
        borderingChunk = getChunkAtTileIndex(x, y, z - 1);
        if (borderingChunk != null && !modifiedChunk.equals(borderingChunk)) {
            updatedChunks.add(borderingChunk);
        }

        for (Chunk chunk : updatedChunks) {
            if (isQueued) {
                chunk.setDirty();
            } else {
                chunk.updateTiles(tiles, scale, tileAtlas, tileBank, attachedNode);
            }
            if (callbacks != null) {
                callbacks.onChunkChanged(chunk);
            }
        }
    }

    /**
     * Retrieves the chunk at a specified tile index.
     * @param x the x index of the tiles.
     * @param y the y index of the tiles.
     * @param z the z index of the tiles.
     * @return the chunk that contains the index.
     */
    public Chunk getChunkAtTileIndex(int x, int y, int z) {
        int chunkX = x / chunkDimensions.x;
        int chunkY = y / chunkDimensions.y;
        int chunkZ = z / chunkDimensions.z;

        boolean isOutsideMinimum = (chunkX < 0 || chunkY < 0 || chunkZ < 0);
        if (isOutsideMinimum) {
            return null;
        }
        boolean isOutsideMaximum = (chunkX >= chunks.length || chunkY >= chunks[0].length || chunkZ >= chunks[0][0].length);
        if (isOutsideMaximum) {
            return null;
        }

        return chunks[chunkX][chunkY][chunkZ];
    }

    /**
     * Marks all chunks as dirty.
     */
    private void makeChunksDirty() {
        for (int x = 0; x < chunks.length; x++) {
            for (int y = 0; y < chunks[x].length; y++) {
                for (int z = 0; z < chunks[x][y].length; z++) {
                    chunks[x][y][z].setDirty();
                }
            }
        }
    }

    /**
     * Set the tiles to use as a reference.
     * This is normally used when the tile map reference is changed.
     * @param tiles the tiles to use for chunk generation.
     */
    public void setTilesReference(byte[][][] tiles) {
        this.tiles = tiles;
        makeChunksDirty();
    }

    /**
     * @param tileAtlas the tile atlas to use.
     */
    public void setTileAtlas(TileAtlas tileAtlas) {
        this.tileAtlas = tileAtlas;
        makeChunksDirty();
    }

    /**
     * @param tileBank the tile data to use.
     */
    public void setTileBank(TileBank tileBank) {
        this.tileBank = tileBank;
        makeChunksDirty();
    }

    /**
     * @param mapDimensions the dimension of the map to set as.
     */
    public void setMapDimensions(Vector3Int mapDimensions) {
        this.mapDimensions.set(mapDimensions);
        recreateChunks();
    }

    /**
     * @param chunkDimensions the dimensions of the chunk to set as.
     */
    public void setChunkDimensions(Vector3Int chunkDimensions) {
        this.chunkDimensions.set(chunkDimensions);
        recreateChunks();
    }

    /**
     * @return the viewing distance before chunks are culled.
     */
    public float getViewDistance() {
        return viewDistance;
    }

    /**
     * @param viewDistance the distance before chunks are no longer rendered.
     */
    public void setViewDistance(float viewDistance) {
        this.viewDistance = viewDistance;
    }

    /**
     * @param scale the scale of each tile to set as.
     */
    public void setScale(float scale) {
        this.scale = scale;
        destroy();
        recreateChunks();
    }

    /**
     * @param callbacks the callbacks to set for the chunk states.
     */
    public void setCallbacks(TerrainCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * Destroys the chunks.
     */
    public void destroy() {
        if (chunks != null) {
            for (int x = 0; x < chunks.length; x++) {
                for (int y = 0; y < chunks[x].length; y++) {
                    for (int z = 0; z < chunks[x][y].length; z++) {
                        chunks[x][y][z].destroy();
                    }
                }
            }
        }
    }
}
