package com.submu.pug.game.world.generators.terrain;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.terrainsystem.generator.Generator;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/21/13
 * Time: 11:31 AM
 * Creates a sky island.
 */
public class SkyIsland implements Generator {
    /**
     * Chances to continue generating in the given direction.
     */
    private static final float CHANCE_LEFT = 0.5f,
                               CHANCE_RIGHT = 0.5f,
                               CHANCE_FORWARD = 0.5f,
                               CHANCE_BACKWARD = 0.5f,
                               CHANCE_UP = 0f,
                               CHANCE_DOWN = 0f;

    private static final float CHANCE_DOWNLEVEL = 0.01f,
                               CHANCE_UPLEVEL = 0.01f;

    /**
     * Height of the island.
     */
    private int islandHeight;

    /**
     * Initialize the generator.
     * @param islandHeight the height of the island.
     */
    public SkyIsland(int islandHeight) {
        this.islandHeight = islandHeight;
    }

    /**
     * Recursively generates the tiles.
     * @param tiles the tiles to place the data into.
     * @param startX the x location.
     * @param startY the y location.
     * @param startZ the z location.
     */
    private void generate(byte[][][] tiles, int startX, int startY, int startZ) {
        //DebugGlobals.println(x, y, z);
        //DebugGlobals.println(tiles.length);
        //DebugGlobals.println(tiles[x].length);
        //DebugGlobals.println(tiles[x][y].length);

        // Recursion will overflow so use a stack instead.
        Stack<Vector3Int> stack = new Stack<Vector3Int>();
        int height = (int) (Math.random() * islandHeight);
        for (int h = 0; h < height; h++) {
            int y = startY - h;
            stack.push(new Vector3Int(startX, y, startZ));
            while (!stack.isEmpty()) {
                Vector3Int index = stack.pop();
                int x = index.x;
                int z = index.z;
                // Stop generating when outside the bounds.
                if (x < 0 || y < 0 || z < 0 || x >= tiles.length || y >= tiles[x].length || z >= tiles[x][y].length) {
                    continue;
                }

                // Make the tile solid.
                tiles[x][y][z] = 1;
                // Expand to other areas.
                double random = Math.random();
                if (random <= CHANCE_LEFT && x > 0 && tiles[x - 1][y][z] != 1) {
                    stack.push(new Vector3Int(x - 1, y, z));
                }
                random = Math.random();
                if (random <= CHANCE_RIGHT && x < tiles.length - 1 && tiles[x + 1][y][z] != 1) {
                    stack.push(new Vector3Int(x + 1, y, z));
                }
                random = Math.random();
                if (random <= CHANCE_BACKWARD && z > 0 && tiles[x][y][z - 1] != 1) {
                    stack.push(new Vector3Int(x, y, z - 1));
                }
                random = Math.random();
                if (random <= CHANCE_FORWARD && z < tiles[x][y].length - 1 && tiles[x][y][z + 1] != 1) {
                    stack.push(new Vector3Int(x, y, z + 1));
                }
                random = Math.random();
                if (random <= CHANCE_DOWN && y > 0 && tiles[x][y - 1][z] != 1) {
                    stack.push(new Vector3Int(x, y - 1, z));
                }
                random = Math.random();
                if (random <= CHANCE_UP && y < tiles[x].length - 1 && tiles[x][y + 1][z] != 1) {
                    stack.push(new Vector3Int(x, y + 1, z));
                }
            }
        }
    }

    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        byte[][][] tiles = new byte[xLength][yLength][zLength];

        // First generate the top slice of the island.
        int centerX = (int) (xLength / 2.0f);
        int centerZ = (int) (zLength / 2.0f);
        generate(tiles, centerX, islandHeight, centerZ);

        return tiles;
    }
}
