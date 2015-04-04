package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.terrainsystem.generator.Generator;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import java.util.concurrent.ExecutorService;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/31/12
 * Time: 11:51 AM
 * Creates and manages the tiles used in the world but does not manage the chunks themselves.
 * The terrain should be the only class that changes the map tiles array directly.
 */
public class Terrain {
    /**
     * Empty tile index.
     */
    public static final int TILE_EMPTY = 0;

    /**
     * Default size of the terrain.
     */
    private static final int DEFAULT_SIZE = 64;

    /**
     * Updates chunks and sets their visibility.
     */
    private ChunkUpdater chunkUpdater;

    /**
     * Atlas to use for the specific map.
     */
    private TileAtlas tileAtlas;

    /**
     * Data for the tiles.
     */
    private TileBank tileBank;

    /**
     * Dimensions of the map.
     */
    private Vector3Int dimensions = new Vector3Int(DEFAULT_SIZE, DEFAULT_SIZE, DEFAULT_SIZE);

    /**
     * Tiles of the map.
     */
    private byte[][][] tiles = new byte[dimensions.x][dimensions.y][dimensions.z];

    /**
     * Main node for the terrain.
     */
    private Node terrainNode = new Node();

    /**
     * Node to attach tiles to.
     */
    private Node tileNode = new Node();

    /**
     * Callbacks for the terrain.
     */
    private TerrainCallbacks callbacks;

    /**
     * Initializes parameters and creates the floor.
     * @param tileAtlas the tile atlas to use for displaying the terrain.
     * @param tileBank the tile data to use.
     */
    public Terrain(TileAtlas tileAtlas, TileBank tileBank) {
        // Create the texture used for tiles.
        this.tileAtlas = tileAtlas;
        this.tileBank = tileBank;

        // Create the chunk updater.
        chunkUpdater = new ChunkUpdater(tiles, dimensions);
        chunkUpdater.setTileAtlas(tileAtlas);
        chunkUpdater.setTileBank(tileBank);

        // Generate default terrain.
        generate(null);

        terrainNode.attachChild(tileNode);
    }

    /**
     * Updates which chunks are visible.
     * @param executorService the executor service to create the update thread with.
     * @param center the center of where chunks should be updated.
     */
    public void cull(ExecutorService executorService, Vector3f center) {
        chunkUpdater.updateVisibility(executorService, center, tileNode);
    }

    /**
     * Attaches the terrain to a node.
     * @param node  Node to attach the terrain to.
     */
    public void attach(Node node) {
        detach();
        node.attachChild(terrainNode);
    }

    /**
     * Detaches the terrain from an attached node.
     */
    public void detach() {
        terrainNode.removeFromParent();
    }

    /**
     * Generates a terrain using the given generator.
     * @param generator the generator to generate with.
     */
    public void generate(Generator generator) {
        if (generator != null) {
            tiles = generator.generate(dimensions.x, dimensions.y, dimensions.z);
        } else {
            // Generate empty terrain if no generator is given.
            tiles = new byte[dimensions.x][dimensions.y][dimensions.z];
        }

        chunkUpdater.setTilesReference(tiles);
    }

    /**
     * Removes the tile at the specified index.
     * @param x the x index.
     * @param y the y index.
     * @param z the z index.
     * @param isQueued true to queue the update or false to instantly update.
     */
    public void removeTile(int x, int y, int z, boolean isQueued) {
        // Set the modified tile to empty
        tiles[x][y][z] = TILE_EMPTY;
        chunkUpdater.updateTouchedChunks(x, y, z, tileNode, isQueued);
    }

    /**
     * Adds a tile to the terrain if none exist at that location.
     * @param type the type of tile to add.
     * @param x the X index to add to.
     * @param y the Y index to add to.
     * @param z the Z index to add to.
     * @param isQueued true to queue the update or false to instantly update.
     */
    public void addTile(byte type, int x, int y, int z, boolean isQueued) {
        if (isIndexInBounds(x, y, z)) {
            tiles[x][y][z] = type;
            chunkUpdater.updateTouchedChunks(x, y, z, tileNode, isQueued);
        }
    }

    /**
     * Sets the tiles of the terrain by reference.
     * @param tiles the tiles to set.
     */
    public void setTiles(byte[][][] tiles) {
        this.tiles = tiles;
        chunkUpdater.setTilesReference(tiles);
    }

    /**
     * Adds tiles to the current tiles.
     * Tiles with the same indices will be overwritten.
     * @param tiles the tiles to add.
     * @param offsetX the X offset of the tiles to add.
     * @param offsetY the Y offset ot the tiles to add.
     * @param offsetZ the Z offset of the tiles to add.
     */
    public void addTiles(byte[][][] tiles, int offsetX, int offsetY, int offsetZ) {
        for (int x = 0; x < tiles.length; x++) {
            for (int y = 0; y < tiles[x].length; y++) {
                for (int z = 0; z < tiles[x][y].length; z++) {
                    int oX = x + offsetX;
                    int oY = y + offsetY;
                    int oZ = z + offsetZ;
                    if (!isIndexOutOfBounds(oX, oY, oZ) && tiles[x][y][z] != 0) {
                        this.tiles[oX][oY][oZ] = tiles[x][y][z];
                    }
                }
            }
        }
    }

    /**
     * @return the tiles of the terrain.
     */
    public byte[][][] getTiles() {
        return tiles;
    }

    /**
     * Resize the terrain.
     * @param xLength the x length.
     * @param yLength the y length.
     * @param zLength the z length.
     */
    public void setDimensions(int xLength, int yLength, int zLength) {
        dimensions.x = xLength;
        dimensions.y = yLength;
        dimensions.z = zLength;
        generate(null);
        chunkUpdater.setMapDimensions(dimensions);
    }

    /**
     * @param scale the scale of the terrain to set as.
     */
    public void setScale(float scale) {
        chunkUpdater.setScale(scale);
    }

    /**
     * Refreshes all the chunks in the terrain.
     */
    public void refreshChunks() {
        chunkUpdater.setTilesReference(tiles);
        chunkUpdater.setTileAtlas(tileAtlas);
    }

    /**
     * Checks if an index is not within the bounds of the grid.
     * @param x the X index.
     * @param y the Y index.
     * @param z the Z index.
     * @return true if the index is outside of the grid, false otherwise.
     */
    public final boolean isIndexOutOfBounds(int x, int y, int z) {
        return x < 0 || y < 0 || z < 0 || x >= tiles.length || y >= tiles[x].length || z >= tiles[x][y].length;
    }

    /**
     * Checks if an index is within the bounds of the grid.
     * @param x the X index.
     * @param y the Y index.
     * @param z the Z index.
     * @return true if the index is within the grid, false otherwise.
     */
    public final boolean isIndexInBounds(int x, int y, int z) {
        boolean isOutsideMinimum = (x < 0 || y < 0 || z < 0);
        if (isOutsideMinimum) {
            return false;
        }
        boolean isOutsideMaximum = (x >= tiles.length || y >= tiles[0].length || z >= tiles[0][0].length);
        if (isOutsideMaximum) {
            return false;
        }

        return true;
    }

    /**
     * @param distance the distance before chunks get culled.
     */
    public void setCullDistance(float distance) {
        chunkUpdater.setViewDistance(distance);
    }

    /**
     * @param callbacks the terrain callbacks to set.
     */
    public void setCallbacks(TerrainCallbacks callbacks) {
        this.callbacks = callbacks;
        chunkUpdater.setCallbacks(callbacks);
    }

    /**
     * Destroys the terrain.
     */
    public void destroy() {
        chunkUpdater.destroy();
    }
}
