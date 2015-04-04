package com.halboom.pgt.physics.filters;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/11/13
 * Time: 8:55 PM
 * Component to handle pulses in colliders.
 */
public class PulseCollisionComponent implements Component {
    /**
     * Period to turn off and on the pulse in seconds.
     */
    public float period = 0f;

    /**
     * Current time for keeping track of the period.
     */
    public float currentTime = 0f;

    /**
     * Previous target before turning off the collision.
     */
    public long previousTarget = 0;

    @Override
    public Component copy() {
        PulseCollisionComponent output = new PulseCollisionComponent();
        output.period = period;
        output.currentTime = currentTime;
        output.previousTarget = previousTarget;

        return output;
    }
}
