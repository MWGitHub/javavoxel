package com.halboom.pgt.terrainsystem.generator;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/16/13
 * Time: 3:03 PM
 * Generates flat terrain.
 */
public class Flat implements Generator {
    /**
     * Height of the terrain.
     */
    private int height;

    /**
     * Initializes the class.
     * @param height the height to generate the tiles up to.
     */
    public Flat(int height) {
        this.height = height;
    }

    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        byte[][][] tiles = new byte[xLength][yLength][zLength];

        for (int x = 0; x < xLength; x++) {
            for (int z = 0; z < zLength; z++) {
                for (int y = 0; y < height; y++) {
                    if (y == height - 1) {
                        tiles[x][y][z] = 1;
                    } else {
                        tiles[x][y][z] = 2;
                    }
                }
            }
        }

        return tiles;
    }
}
