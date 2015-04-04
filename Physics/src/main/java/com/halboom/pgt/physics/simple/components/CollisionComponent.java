package com.halboom.pgt.physics.simple.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/9/13
 * Time: 2:19 PM
 * Entities with this component will be collidable with other collidable components.
 */
public class CollisionComponent implements Component {
    /**
     * When true the component will not be used for static collision against other entities.
     */
    public boolean isSensor = false;

    /**
     * Group the collision component belongs to.
     */
    public long groups = 1;

    /**
     * Group the collision can target.
     */
    public long targets = 1;

    /**
     * Friction to apply when an object is standing on another object.
     */
    public float friction = 1.0f;

    /**
     * True if the component is touching the floor.
     */
    public boolean isOnFloor = false;

    /**
     * Movement amounts before the physics system begins.
     */
    public float prePhysicsMoveX = 0, prePhysicsMoveY = 0, prePhysicsMoveZ = 0;

    @Override
    public Component copy() {
        CollisionComponent output = new CollisionComponent();
        output.isSensor = isSensor;
        output.groups = groups;
        output.targets = targets;
        output.friction = friction;
        output.isOnFloor = isOnFloor;
        output.prePhysicsMoveX = prePhysicsMoveX;
        output.prePhysicsMoveY = prePhysicsMoveY;
        output.prePhysicsMoveZ = prePhysicsMoveZ;

        return output;
    }
}
