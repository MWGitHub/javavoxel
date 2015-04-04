package com.halboom.pgt.physics.filters;

import com.exploringlines.entitysystem.Entity;
import com.halboom.pgt.physics.simple.shapes.Bounds;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/2/13
 * Time: 11:51 AM
 * Filter that can be used to filter collisions before they occur.
 */
public abstract class Filter {
    /**
     * Runs before a collision is checked for custom collision filters.
     * @param bounds1 the bounds of one of the colliders.
     * @param bounds2 the bounds of another collider.
     * @return true if the collision should be checked.
     */
    public boolean filterBounds(Bounds bounds1, Bounds bounds2) {
        return true;
    }

    /**
     * Allows the user to check for a property on the entity before collision.
     * Useful for ray checks which does not require bounds.
     * @param entity the entity to check.
     * @param entityBounds the bounds of the checked entity.
     * @return true if the collision should be checked.
     */
    public boolean filterEntity(Entity entity, Bounds entityBounds) {
        return true;
    }
}
