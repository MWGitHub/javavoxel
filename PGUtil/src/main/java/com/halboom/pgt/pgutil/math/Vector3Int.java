package com.halboom.pgt.pgutil.math;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/16/13
 * Time: 4:12 PM
 * A 3D vector for integers.
 */
public class Vector3Int {
    /**
     * Index of the vector.
     */
    public int x, y, z;

    /**
     * Initializes the vector with default values.
     */
    public Vector3Int() {
        x = 0;
        y = 0;
        z = 0;
    }

    /**
     * Initializes the vector.
     * @param x the x amount to set.
     * @param y the y amount to set.
     * @param z the z amount to set.
     */
    public Vector3Int(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * Sets the values of the vector to those of the input.
     * @param input the input values to set.
     */
    public void set(Vector3Int input) {
        x = input.x;
        y = input.y;
        z = input.z;
    }

    /**
     * Sets the values for the vector.
     * @param x the x value.
     * @param y the y value.
     * @param z the z value.
     */
    public void set(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }
}
