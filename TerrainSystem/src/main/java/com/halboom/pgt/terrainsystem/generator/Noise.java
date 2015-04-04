package com.halboom.pgt.terrainsystem.generator;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/20/13
 * Time: 10:19 AM
 * Generates terrain randomly using the Diamond Square algorithm.
 */
public class Noise implements Generator {
    /**
     * Maximum height of the generator.
     */
    private int maxHeight;

    /**
     * Initializes the generator.
     */
    public Noise() {
    }

    /**
     * Initializes the generator.
     * @param maxHeight the maximum height to allow.
     */
    public Noise(int maxHeight) {
        this.maxHeight = maxHeight;
    }

    /**
     * Generates the map given the dimensions.
     * Size of grid to generate, note this must be a value 2^n+1.
     * @param dimensions the dimensions of the map which must be in the power of 2^n + 1.
     * @return the generated map.
     */
    private double[][] generateMap(int dimensions) {
        // An initial seed value for the corners of the data.
        final double seed = 1000.0;
        final double points = 4.0;
        double[][] data = new double[dimensions][dimensions];
        // Seed the data.
        data[0][0] = seed;
        data[0][dimensions - 1] = seed;
        data[dimensions - 1][0] = seed;
        data[dimensions - 1][dimensions - 1] = seed;

        // The range (-h -> +h) for the average offset.
        final double startingRange = 500.0;
        double range = startingRange;
        // Used for the new value in range of h.
        Random r = new Random();
        // Side length is distance of a single square side or distance of diagonal in diamond.
        // Side length must be >= 2 so we always have a new value (if its 1 we overwrite existing values on the last iteration).
        // Each iteration we are looking at smaller squares diamonds, and we decrease the variation of the offset.
        for (int sideLength = dimensions - 1; sideLength >= 2; sideLength /= 2, range /= 2.0) {
            // Half the length of the side of a square or distance from diamond center to one corner.
            int halfSide = sideLength / 2;
            // Generate the new square values.
            // X, Y is the upper left corner of square.
            for (int x = 0; x < dimensions - 1; x += sideLength) {
                for (int y = 0; y < dimensions - 1; y += sideLength) {
                    // Calculate average of existing corners.
                    // Top left, top right, lower left, then lower right.
                    double avg = data[x][y] + data[x + sideLength][y] + data[x][y + sideLength] + data[x + sideLength][y + sideLength];
                    avg /= points;

                    // Center is average plus random offset.
                    // We calculate random value in range of 2h and then subtract h so the end value is in the range (-h, +h).
                    data[x + halfSide][y + halfSide] = avg + (r.nextDouble() * 2 * range) - range;
                }
            }

            // Generate the diamond values since the diamonds are staggered we only move x by half side.
            // NOTE: if the data shouldn't wrap then x < dimensions to generate the far edge values.
            for (int x = 0; x < dimensions - 1; x += halfSide) {
                // Y is X offset by half a side, but moved by the full side length.
                // NOTE: if the data shouldn't wrap then y < dimensions to generate the far edge values.
                for (int y = (x + halfSide) % sideLength; y < dimensions - 1; y += sideLength) {
                    // X, Y is center of diamond.
                    // NOTE: we must use mod and add dimensions for subtraction so that we can wrap around the array to find the corners.
                    // Left of center, right of center, below center, and above center.
                    double avg = data[(x - halfSide + dimensions - 1) % (dimensions - 1)][y]
                            + data[(x + halfSide) % (dimensions - 1)][y]
                            + data[x][(y + halfSide) % (dimensions - 1)]
                            + data[x][(y - halfSide + dimensions - 1) % (dimensions - 1)];
                    avg /= points;

                    // New value = average plus random offset.
                    // We calculate random value in range of 2h and then subtract h so the end value is in the range (-h, +h).
                    avg = avg + (r.nextDouble() * 2 * range) - range;
                    // Update value for center of diamond
                    data[x][y] = avg;

                    // Wrap values on the edges, remove this and adjust loop condition above for non-wrapping values.
                    if (x == 0) {
                        data[dimensions - 1][y] = avg;
                    }
                    if (y == 0) {
                        data[x][dimensions - 1] = avg;
                    }
                }
            }
        }

        return data;
    }

    @Override
    public byte[][][] generate(int xLength, int yLength, int zLength) {
        if (maxHeight == 0) {
            maxHeight = yLength;
        }

        double[][] map = generateMap(xLength + 1);

        //print out the data
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (double[] row : map) {
            for (double d : row) {
                if (d < min) {
                    min = d;
                }
                if (d > max) {
                    max = d;
                }
            }
        }
        // Difference of the max value and min value which will be used for normalization.
        double difference = max - min;

        byte[][][] tiles = new byte[xLength][yLength][zLength];

        for (int x = 0; x < tiles.length; x++) {
            for (int z = 0; z < tiles[x][0].length; z++) {
                int height = (int) ((map[x][z] - min) / difference * maxHeight);
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
