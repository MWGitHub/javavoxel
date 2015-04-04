package com.halboom.pgt.physics.simple.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/19/13
 * Time: 12:26 PM
 * Component to store the speed and acceleration of an object.
 */
public class SpeedComponent implements Component {
    /**
     * Default damping of the speed.
     */
    private static final float DEFAULT_DAMPING = 1.0f;

    /**
     * Amount to reduce the speed by per frame.
     */
    public float damping = DEFAULT_DAMPING;

    /**
     * Friction to apply per frame but only when touching the ground.
     */
    public float friction = 0.0f;

    /**
     * Friction for the self object.
     */
    public float selfFriction = 0.0f;

    /**
     * Minimum and maximum friction to limit the object by.
     */
    public float minFriction = 0.0f, maxFriction = 1.0f;

    /**
     * Acceleration amounts.
     */
    public float accelX = 0, accelY = 0, accelZ = 0;

    /**
     * Speed of the object.
     */
    public float speedX = 0, speedY = 0, speedZ = 0;

    /**
     * Maximum horizontal speed for the entity.
     */
    public float maxSpeedHorizontal = 0;
    /**
     * Maximum vertical speed for the entity.
     */
    public float maxSpeedVertical = 0;

    /**
     * True to damp the vertical direction.
     */
    public boolean isVerticalDamped = true;

    /**
     * True to damp the horizontal direction.
     */
    public boolean isHorizontalDamped = true;

    @Override
    public Component copy() {
        SpeedComponent output = new SpeedComponent();
        output.damping = damping;
        output.friction = friction;
        output.accelX = accelX;
        output.accelY = accelY;
        output.accelZ = accelZ;
        output.speedX = speedX;
        output.speedY = speedY;
        output.speedZ = speedZ;
        output.maxSpeedHorizontal = maxSpeedHorizontal;
        output.maxSpeedVertical = maxSpeedVertical;
        output.isVerticalDamped = isVerticalDamped;
        output.isHorizontalDamped = isHorizontalDamped;

        return output;
    }
}
