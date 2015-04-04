package com.halboom.pgt.physics.simple;

import com.exploringlines.entitysystem.Entity;
import com.halboom.pgt.physics.simple.shapes.Bounds;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/10/13
 * Time: 9:08 PM
 * Used for collider information.
 */
public class ColliderSet {
    /**
     * Bounds of the collider.
     */
    private Bounds bounds;

    /**
     * Entity of the collider.
     */
    private Entity entity;

    /**
     * Creates the collider set.
     * @param bounds the bounds of the collider.
     * @param entity the entity of the collider.
     */
    public ColliderSet(Bounds bounds, Entity entity) {
        this.bounds = bounds;
        this.entity = entity;
    }

    /**
     * @return the bounds.
     */
    public Bounds getBounds() {
        return bounds;
    }

    /**
     * @return the entity.
     */
    public Entity getEntity() {
        return entity;
    }
}
