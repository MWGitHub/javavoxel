package com.halboom.pgt.terrainsystem.generator;

import com.jme3.math.FastMath;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/20/13
 * Time: 10:24 AM
 * Generates hilly terrain.
 */
public class Hills implements Generator {
    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        byte[][][] tiles = new byte[xLength][yLength][zLength];
        int[][] heightMap = new int[xLength][zLength];
        int height = yLength / 2;
        for (int x = 0; x < heightMap.length; x++) {
            for (int z = 0; z < heightMap[x].length; z++) {
                heightMap[x][z] = FastMath.nextRandomInt(height - height / 5, height + height / 5);
            }
        }
        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x][0].length; z++) {
                // Get average yLength around the tile.
                height = 0;
                if (x > 0) {
                    height += heightMap[x - 1][z];
                } else {
                    height += yLength / 2;
                }
                if (x < heightMap.length - 1) {
                    height += heightMap[x + 1][z];
                } else {
                    height += yLength / 2;
                }
                if (z > 0) {
                    height += heightMap[x][z - 1];
                } else {
                    height += yLength / 2;
                }
                if (z < heightMap[x].length -1) {
                    height += heightMap[x][z + 1];
                } else {
                    height += yLength / 2;
                }
                height = height / 4;

                height += FastMath.nextRandomInt(-1, 1);
                if (height < 0) {
                    height = 0;
                } else if (height > yLength - 1) {
                    height = yLength - 1;
                }
                for (int h = 0; h < height; h++) {
                    tiles[x][h][z] = (byte) (FastMath.rand.nextInt(2) + 1);
                }
            }
        }

        return tiles;
    }
}
