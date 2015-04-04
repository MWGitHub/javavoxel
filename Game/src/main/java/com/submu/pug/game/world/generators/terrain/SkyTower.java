package com.submu.pug.game.world.generators.terrain;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.terrainsystem.generator.Generator;
import com.jme3.math.FastMath;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/14/13
 * Time: 2:19 PM
 * Generates a sky tower.
 * A sky tower is a circular tower that starts from the sky and goes downwards.
 * The top of the sky tower will be a floating island.
 */
public class SkyTower implements Generator {
    /**
     * Base level of the sky tower (entrance of the tower).
     */
    private int entranceLevel;

    /**
     * Radius of the tower.
     */
    private float radius;

    /**
     * Height of the floors.
     */
    private int floorHeight;

    /**
     * Step size to draw the tower tiles from.
     */
    private float stepSize = 0.1f;

    /**
     * Initializes the generator.
     * @param radius the radius of the tower.
     * @param entranceLevel the height to place the tower entrance.
     * @param floorHeight the height of the floors.
     */
    public SkyTower(float radius, int entranceLevel, int floorHeight) {
        this.radius = radius;
        this.entranceLevel = entranceLevel;
        this.floorHeight = floorHeight;
    }

    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        byte[][][] tiles = new byte[xLength][yLength][zLength];
        float cx = xLength / 2;
        float cz  = zLength / 2;
        // Create the floors.
        int floors = entranceLevel / floorHeight;
        for (int x = 0; x < xLength; x++) {
            for (int z = 0; z < zLength; z++) {
                float xd = x - cx;
                float zd = z - cz;
                if (xd * xd + zd * zd <= radius * radius) {
                    for (int y = 0; y < floors; y++) {
                        tiles[x][y * floorHeight][z] = 1;
                    }
                }
            }
        }
        // Create the walls.
        for (float angle = 0; angle < 360; angle += stepSize) {
            int tx = (int) (cx + radius * Math.cos(angle * FastMath.DEG_TO_RAD));
            int tz = (int) (cz + radius * Math.sin(angle * FastMath.DEG_TO_RAD));
            for (int y = 0; y < entranceLevel; y++) {
                if (tx < 0 || tz < 0 || tx >= xLength || tz >= zLength) {
                    continue;
                }
                tiles[tx][y][tz] = 1;
            }
        }
        // Create the top platform.
        for (int x = 0; x < xLength; x++) {
            for (int z = 0; z < zLength; z++) {
                tiles[x][entranceLevel][z] = 1;
            }
        }

        return tiles;
    }
}
