package com.halboom.pgt.terrainsystem.generator;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/20/13
 * Time: 10:19 AM
 */
public interface Generator {
    /**
     * Generates tile indices to use with the map.
     * @param xLength the x length of the map.
     * @param yLength the y length of the map.
     * @param zLength the z length of the map.
     * @return the generated tile indices.
     */
    byte[][][] generate(int xLength, int yLength, int zLength);
}
