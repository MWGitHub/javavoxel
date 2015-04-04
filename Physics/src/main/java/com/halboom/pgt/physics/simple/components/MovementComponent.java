package com.halboom.pgt.physics.simple.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/19/13
 * Time: 3:52 PM
 * Holds how much to move by.
 */
public class MovementComponent implements Component {
    /**
     * Amount to move by.
     */
    public float moveX = 0, moveY = 0, moveZ = 0;

    @Override
    public Component copy() {
        MovementComponent output = new MovementComponent();
        output.moveX = moveX;
        output.moveY = moveY;
        output.moveZ = moveZ;

        return output;
    }
}
