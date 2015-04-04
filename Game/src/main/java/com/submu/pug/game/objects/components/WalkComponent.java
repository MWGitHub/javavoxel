package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/22/13
 * Time: 3:37 PM
 * Has data on the movement acceleration and movement direction.
 */
public class WalkComponent implements Component {
    /**
     * True to allow movement in the Y axis.
     */
    public boolean isYMovementAllowed = false;

    /**
     * Horizontal acceleration when moving.
     */
    public float horizontalAcceleration = 0f;

    /**
     * Vertical acceleration when moving.
     */
    public float verticalAcceleration = 0f;

    /**
     * Acceleration amount in the horizontal direction when in the air.
     */
    public float airAcceleration = 0f;

    /**
     * Maximum speed for horizontal movement when walking in the air without flying.
     */
    public float maxHorizontalWalkSpeed = 0f;

    /**
     * Maximum speed for vertical movement when walking.
     */
    public float maxVerticalWalkSpeed = 0f;

    @Override
    public Component copy() {
        WalkComponent output = new WalkComponent();
        output.isYMovementAllowed = isYMovementAllowed;
        output.horizontalAcceleration = horizontalAcceleration;
        output.verticalAcceleration = verticalAcceleration;
        output.airAcceleration = airAcceleration;
        output.maxHorizontalWalkSpeed = maxHorizontalWalkSpeed;
        output.maxVerticalWalkSpeed = maxVerticalWalkSpeed;

        return output;
    }
}
