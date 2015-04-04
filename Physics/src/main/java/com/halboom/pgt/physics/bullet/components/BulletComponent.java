package com.halboom.pgt.physics.bullet.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/8/13
 * Time: 12:34 PM
 * Component for bullet bodies.
 */
public class BulletComponent implements Component {
    /**
     * Set to true to use the physics object as a sensor.
     */
    public boolean isGhost = false;

    /**
     * Mass of the object.
     * A zero mass makes the object static.
     */
    public float mass = 1f;

    /**
     * Friction of the object.
     */
    public float friction = 1f;

    /**
     * How bouncy the object is.
     */
    public float restitution = 0.0f;

    @Override
    public Component copy() {
        BulletComponent output = new BulletComponent();
        output.isGhost = isGhost;
        output.mass = mass;
        output.friction = friction;
        output.restitution = restitution;

        return output;
    }
}
