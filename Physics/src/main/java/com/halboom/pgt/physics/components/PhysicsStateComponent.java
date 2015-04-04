package com.halboom.pgt.physics.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/20/13
 * Time: 12:36 PM
 * Holds information for each physics collider.
 */
public class PhysicsStateComponent implements Component {
    /**
     * Grounded flags to know if any of the colliders have a grounded entity.
     */
    public boolean isGroundedBullet, isGroundedBounds = false, isGroundedGrid = false;

    @Override
    public Component copy() {
        PhysicsStateComponent output = new PhysicsStateComponent();
        output.isGroundedBullet = isGroundedBullet;
        output.isGroundedBounds = isGroundedBounds;
        output.isGroundedGrid = isGroundedGrid;

        return output;
    }
}
