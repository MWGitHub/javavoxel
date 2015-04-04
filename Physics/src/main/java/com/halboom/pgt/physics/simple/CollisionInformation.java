package com.halboom.pgt.physics.simple;

import com.exploringlines.entitysystem.Entity;
import com.jme3.math.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/6/13
 * Time: 1:51 PM
 * Information regarding a collision.
 */
public class CollisionInformation {
    /**
     * Amount that can be moved without colliding.
     */
    private Vector3f moveAmount = new Vector3f();

    /**
     * Collider entity.
     */
    private Entity collider;

    /**
     * Collidee entity.
     */
    private Entity collidee;

    /**
     * Point of collision.
     */
    private Vector3f collisionPoint = new Vector3f();

    /**
     * Initializes an empty collision information.
     */
    public CollisionInformation() {
    }

    /**
     * @param moveAmount the move amount to set.
     */
    public void setMoveAmount(Vector3f moveAmount) {
        this.moveAmount.set(moveAmount);
    }

    /**
     * @return the amount that can be moved without colliding.
     */
    public Vector3f getMoveAmount() {
        return moveAmount;
    }

    /**
     * @return the collider entity.
     */
    public Entity getCollider() {
        return collider;
    }

    /**
     * @param collider the collider entity to set.
     */
    public void setCollider(Entity collider) {
        this.collider = collider;
    }

    /**
     * @return the collidee entity.
     */
    public Entity getCollidee() {
        return collidee;
    }

    /**
     * @param collidee the collidee entity to set.
     */
    public void setCollidee(Entity collidee) {
        this.collidee = collidee;
    }

    /**
     * @return the distance between each axis.
     */
    public Vector3f getCollisionPoint() {
        return collisionPoint;
    }

    /**
     * Sets the point of collision.
     * @param x the x amount.
     * @param y the y amount.
     * @param z the z amount.
     */
    public void setCollisionPoint(float x, float y, float z) {
        collisionPoint.set(x, y, z);
    }
}
