package com.halboom.pgt.terrainsystem.generator;

import com.halboom.pgt.pgutil.math.Vector3Int;
import com.jme3.math.FastMath;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/16/13
 * Time: 1:24 PM
 * Generates a simple dungeon using walkers.
 */
public class Dungeon implements Generator {
    /**
     * Sizes to use for each room.
     */
    private int sizeX, sizeZ;

    /**
     * Initializes the generator.
     * @param sizeX the x size of each room.
     * @param sizeZ the z size of each room.
     */
    public Dungeon(int sizeX, int sizeZ) {
        this.sizeX = sizeX;
        this.sizeZ = sizeZ;
    }

    /**
     * Checks if the position is out of bounds.
     * @param array the array to check with.
     * @param x the x location.
     * @param y the y location.
     * @return true if out of bounds.
     */
    private boolean isOutOfBounds(byte[][] array, int x, int y) {
        if (x < 0 || x >= array.length) {
            return true;
        }
        if (y < 0 || y >= array[0].length) {
            return true;
        }
        return false;
    }

    /**
     * Creates a room at the given index location.
     * @param tiles the tiles to create the room in.
     * @param x the x location of the room.
     * @param z the y location of the room.
     */
    private void createRoom(byte[][][] tiles, int x, int z) {
        int minX = x * sizeX - sizeX / 2;
        int maxX = x * sizeX + sizeX / 2;
        int minZ = z * sizeZ - sizeZ / 2;
        int maxZ = z * sizeZ + sizeZ / 2;

        for (int rx = minX; rx < maxX; rx++) {
            for (int rz = minZ; rz < maxZ; rz++) {
                tiles[rx][1][rz] = 0;
                tiles[rx][2][rz] = 0;
                tiles[rx][3][rz] = 0;
                tiles[rx][4][rz] = 0;
            }
        }
    }

    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        // Tiles for the map.
        byte[][][] tiles = new byte[xLength][yLength][zLength];
        // Create the floor.
        for (int x = 0; x < xLength; x++) {
            for (int z = 0; z < zLength; z++) {
                tiles[x][0][z] = 1;
                tiles[x][1][z] = 1;
                tiles[x][2][z] = 1;
                tiles[x][3][z] = 1;
                tiles[x][4][z] = 1;
            }
        }

        // Set the properties of each room.
        final int directionsPerRoom = 4;
        final int buffer = 2;
        int roomsX = xLength / (int) Math.ceil(sizeX + buffer);
        int roomsZ = zLength / (int) Math.ceil(sizeZ + buffer);

        // Tiles for a floor.
        byte[][] floor = new byte[roomsX][roomsZ];

        Vector3Int location = new Vector3Int(FastMath.nextRandomInt(0, roomsX - 1), 0, FastMath.nextRandomInt(0, roomsZ - 1));
        boolean isWalkerDone = false;
        while (!isWalkerDone) {
            floor[location.x][location.z] = 1;
            int direction = FastMath.nextRandomInt(0, directionsPerRoom - 1);
            // Forward, Backward, East, West for directions.
            switch (direction) {
                case 0:
                    location.z++;
                    break;
                case 1:
                    location.z--;
                    break;
                case 2:
                    location.x++;
                    break;
                case 3:
                    location.x--;
                    break;
                default:
                    isWalkerDone = true;
                    break;
            }
            if (isOutOfBounds(floor, location.x, location.z)) {
                isWalkerDone = true;
            }
        }

        // Print out the floor.
        /*
        for (int x = 0; x < floor.length; x++) {
            for (int y = 0; y < floor[x].length; y++) {
                System.out.print(floor[x][y] + " ");
            }
            System.out.println();
        }
        System.out.println();
        */

        // Translate the floor to the map.
        for (int x = 0; x < floor.length; x++) {
            for (int y = 0; y < floor[x].length; y++) {
                if (floor[x][y] != 0) {
                    createRoom(tiles, x, y);
                }
            }
        }

        return tiles;
    }
}
