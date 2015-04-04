package com.halboom.pgt.physics.simple;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/10/13
 * Time: 2:15 PM
 * Callbacks for collision systems.
 */
public abstract class CollisionCallbacks {
    /**
     * Runs when a collider is blocked by something.
     * @param collisionInformation the collision information with only resolution details and the entity.
     */
    public void onBlockerCollide(CollisionInformation collisionInformation) {}

    /**
     * Runs when a collider collides with a sensor.
     * @param collisionInformation the collision information without resolution.
     */
    public void onSensorCollide(CollisionInformation collisionInformation) {}

    /**
     * Runs when a collider collides with a tile.
     * @param collisionInformation the collision information with only resolution details and the entity.
     */
    public void onTileCollide(CollisionInformation collisionInformation) {}
}
