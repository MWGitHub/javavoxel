package com.halboom.pgt.physics.simple.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/11/13
 * Time: 12:21 PM
 * Component with special flags when colliding with the grid.
 */
public class GridColliderComponent implements Component {
    /**
     * Collision group for grid collisions.
     */
    public long collisionGroup = 1;

    /**
     * True to make the entity resolve collisions with the grid as a sensor.
     */
    public boolean isSensor = false;

    /**
     * True to make the entity completely stop on collision.
     */
    public boolean stopsOnCollide = false;

    @Override
    public Component copy() {
        GridColliderComponent output = new GridColliderComponent();
        output.isSensor = isSensor;
        output.stopsOnCollide = stopsOnCollide;

        return output;
    }
}
