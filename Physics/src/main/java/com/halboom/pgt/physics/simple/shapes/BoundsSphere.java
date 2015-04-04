package com.halboom.pgt.physics.simple.shapes;

import com.exploringlines.entitysystem.Entity;
import com.jme3.bounding.BoundingSphere;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/23/13
 * Time: 7:53 PM
 * An bounding sphere.
 */
public class BoundsSphere extends BoundingSphere implements Bounds {
    /**
     * Collision group the bounds is in.
     */
    private long groups = 1;

    /**
     * Collision group the bounds can collide with.
     */
    private long targets = 1;

    /**
     * Entity that the bounds is attached to.
     */
    private Entity entity;

    /**
     * Creates a bounding box.
     */
    public BoundsSphere() {
    }

    /**
     * Creates a bounding box given the center and the extents from the center.
     * @param center the center of the bounding box.
     * @param radius the radius of the sphere.
     */
    public BoundsSphere(float radius, Vector3f center) {
        super(radius, center);
    }

    /**
     * @return the collision group of the bounds.
     */
    public long getGroups() {
        return groups;
    }

    /**
     * @param groups the group to set for the bounds.
     */
    public void setGroups(long groups) {
        this.groups = groups;
    }

    /**
     * @return the targets the bounds can collide with.
     */
    public long getTargets() {
        return targets;
    }

    /**
     * @param targets the group to set for the targets.
     */
    public void setTargets(long targets) {
        this.targets = targets;
    }

    /**
     * @return the entity the bounds is attached to.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * @param entity the entity to set the bounds to be attached to.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public BoundingVolume getBounds() {
        return this;
    }
}
