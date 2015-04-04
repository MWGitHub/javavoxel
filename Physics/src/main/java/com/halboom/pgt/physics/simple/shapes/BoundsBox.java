package com.halboom.pgt.physics.simple.shapes;

import com.exploringlines.entitysystem.Entity;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/23/13
 * Time: 7:53 PM
 * An AABB bounding box.
 */
public class BoundsBox extends BoundingBox implements Bounds {
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
    public BoundsBox() {
    }

    /**
     * Creates a bounding box given the center and the extents from the center.
     * @param center the center of the bounding box.
     * @param extentX the x extent.
     * @param extentY the y extent.
     * @param extentZ the z extent.
     */
    public BoundsBox(Vector3f center, float extentX, float extentY, float extentZ) {
        super(center, extentX, extentY, extentZ);
    }

    /**
     * Creates a bounding box given another bounding box.
     * @param source the bounding box to copy from.
     */
    public BoundsBox(BoundingBox source) {
        super(source);
    }

    /**
     * Creates a bounding box given the minimum and the maximum points.
     * @param min the minimum points.
     * @param max the maximum points.
     */
    public BoundsBox(Vector3f min, Vector3f max) {
        super(min, max);
    }

    /**
     * Creates a new bound and adds to it while retaining the position of the unchanged sides.
     * A negative value means added bounds to the minimum while a positive adds to the maximum.
     * @param x the amount in the x direction to add.
     * @param y the amount in the y direction to add.
     * @param z the amount in the z direction to add.
     * @return the new bounds.
     */
    public final BoundsBox addBounds(float x, float y, float z) {
        Vector3f newCenter = new Vector3f(center);
        newCenter.x += x / 2;
        newCenter.y += y / 2;
        newCenter.z += z / 2;

        // Create the extended bounds.
        return new BoundsBox(newCenter, getXExtent() + FastMath.abs(x / 2),
                getYExtent() + FastMath.abs(y / 2), getZExtent() + FastMath.abs(z / 2));
    }

    /**
     * Creates a new bound and adds to it while retaining the position of the unchanged sides.
     * A negative value means added bounds to the minimum while a positive adds to the maximum.
     * @param x the amount in the x direction to add.
     * @param y the amount in the y direction to add.
     * @param z the amount in the z direction to add.
     * @return the added bounds.
     */
    public final BoundsBox addBoundsLocal(float x, float y, float z) {
        center.set(center.x + x / 2, center.y + y / 2, center.z + z / 2);
        setXExtent(getXExtent() + FastMath.abs(x / 2));
        setYExtent(getYExtent() + FastMath.abs(y / 2));
        setZExtent(getZExtent() + FastMath.abs(z / 2));
        return this;
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
