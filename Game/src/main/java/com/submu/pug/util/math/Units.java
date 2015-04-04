package com.submu.pug.util.math;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/14/13
 * Time: 3:15 PM
 * Unit conversions used in the game to match the world.
 */
public final class Units {
    /**
     * AtmosphereSystem units per meter.
     */
    public static final float WORLD_UNIT_PER_METER = 1;

    /**
     * Nanoseconds per second.
     */
    public static final float NANOSECONDS_PER_SECOND = 1000000000;

    /**
     * Milliseconds per nanosecond.
     */
    public static final float MILLISECONDS_PER_NANOSECOND = 1000000;

    /**
     * Default tickrate of the game.
     */
    private static final float DEFAULT_TICKRATE = 32;

    /**
     * Tolerance allowed for floating point calculations.
     */
    public static final float TOLERANCE = 0.00001f;

    /**
     * FPS of the fixed timestep for the game logic.
     */
    private static float tickrate = DEFAULT_TICKRATE;

    /**
     * Time per frame for the game logic.
     */
    private static float timestep = 1 / tickrate;

    /**
     * Do not allow initialization.
     */
    private Units() {
    }

    /**
     * Converts speed in meters per second to world units per second.
     * @param speedPerSecond the speed in meters per second.
     * @return the speed in world units per second.
     */
    public static float speedToWorld(float speedPerSecond) {
        // Equation for milliseconds if needed: X:m/s * 1:s / 1000:ms * Y:wu / 1:m = Z:wu/ms
        return speedPerSecond * WORLD_UNIT_PER_METER;
    }

    /**
     * Converts acceleration in meters per second squared to world units per tick.
     * @param accelerationPerSecondSquared the acceleration in meters per second squared.
     * @return the acceleration in world units per second squared.
     */
    public static float accelerationToWorld(float accelerationPerSecondSquared) {
        // Equation for milliseconds if needed: X:m/s^2 * 1:s / 1000:ms * 1:s / 1000:ms * Y:wu / 1:m = Z:wu/ms^2
        return accelerationPerSecondSquared * WORLD_UNIT_PER_METER / tickrate;
    }

    /**
     * Sets the timestep used for the game.
     * @param tpf the timestep of the game.
     */
    public static void setTimestep(float tpf) {
        timestep = tpf;
        tickrate = 1 / tpf;
    }

    /**
     * Sets the tickrate used for the game.
     * @param tickrate the tickrate of the game.
     */
    public static void setTickrate(float tickrate) {
        Units.tickrate = tickrate;
        timestep = 1 / tickrate;
    }
}
