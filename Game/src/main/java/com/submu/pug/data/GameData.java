package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/28/12
 * Time: 6:20 PM
 * Game data stores engine data and controls data.
 */
public class GameData {
    /**
     * Chunk dimensions.
     */
    public Chunk chunk = new Chunk();

    @Override
    public String toString() {
        return chunk.toString();
    }

    /**
     * Dimensions of a chunk of blocks.
     */
    public static class Chunk {
        // X axis length of the world.
        public int xLength = 16;
        // Z axis length of the world.
        public int zLength = 16;
        // Y axis yLength of the world.
        public int yLength = 16;

        @Override
        public String toString() {
            return xLength + "x" + yLength + "x" + zLength;
        }
    }
}