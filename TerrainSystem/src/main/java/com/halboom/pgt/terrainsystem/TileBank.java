package com.halboom.pgt.terrainsystem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/31/13
 * Time: 1:14 PM
 * Holds the data of the tile faces and tile types.
 */
public class TileBank {
    /**
     * Data of the tile faces.
     */
    private List<Tile> tiles = new ArrayList<Tile>();

    /**
     * Initializes the data.
     */
    public TileBank() {
        // Add some default data.
        tiles.add(new Tile());
        tiles.add(new Tile(0.3f, 0.6f, 0.3f, 0.6f, -0.1f, 0.1f, 0.95f, false, 1, new int[]{2, 2, 2, 2, 1, 3}));
        tiles.add(new Tile(0.3f, 0.6f, 0.3f, 0.6f, -0.3f, 0.3f, 0.95f, false, 1, new int[]{3, 3, 3, 3, 3, 3}));
    }

    /**
     * Adds a tile.
     * @param tile the tile to add.
     */
    public void addTile(Tile tile) {
        tiles.add(tile);
    }

    /**
     * Sets a tile data into the tiles.
     * @param index the index to set the tile as.
     * @param tile the tile face to set.
     */
    public void setTile(int index, Tile tile) {
        tiles.set(index, tile);
    }

    /**
     * @param index the index of the tile to retrieve.
     * @return the tile data or the first data if none found.
     */
    public Tile getTile(int index) {
        Tile tile;
        try {
            tile = tiles.get(index);
        } catch (IndexOutOfBoundsException e) {
            return tiles.get(0);
        }
        return tile;
    }

    /**
     * Clears the tile list.
     */
    public void clear() {
        tiles.clear();
    }

    /**
     * Data of a tile.
     */
    public class Tile {
        /**
         * Optional name for the tile for editor use only.
         */
        public String name;

        /**
         * Midpoint ranges for the tile.
         */
        public float mx1 = 0, mx2 = 0, my1 = 0, my2 = 0, mz1 = 0, mz2 = 0;

        /**
         * Friction of the tile.
         */
        public float friction = 1f;

        /**
         * Types of the directions of the tile.
         */
        public int[] type;

        /**
         * True to flag the tile as having aligned textures.
         */
        public boolean isAligned = false;

        /**
         * Collision groups for the tiles.
         */
        public long collisionGroup = 1;

        /**
         * Initializes with the default values.
         */
        public Tile() {
            type = new int[]{0, 0, 0, 0, 0, 0};
        }

        /**
         * Initialize with set values.
         * @param mx1 the start X midpoint range.
         * @param mx2 the end X midpoint range.
         * @param my1 the start Y midpoint range.
         * @param my2 the end Y midpoint range.
         * @param mz1 the start Z midpoint range.
         * @param mz2 the end Z midpoint range.
         * @param friction the friction of the tile.
         * @param isAligned true to align textures.
         * @param type the type of faces of the tile.
         */
        public Tile(float mx1, float mx2, float my1, float my2, float mz1, float mz2, float friction, boolean isAligned, long collisionGroup, int[] type) {
            this.mx1 = mx1;
            this.mx2 = mx2;
            this.my1 = my1;
            this.my2 = my2;
            this.mz1 = mz1;
            this.mz2 = mz2;
            this.friction = friction;
            this.isAligned = isAligned;
            this.collisionGroup = collisionGroup;
            if (type != null) {
                this.type = type;
            } else {
                this.type = new int[]{0, 0, 0, 0, 0, 0};
            }
        }
    }
}
