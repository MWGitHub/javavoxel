package com.halboom.pgt.physics.simple.shapes;

import com.exploringlines.entitysystem.Entity;
import com.jme3.bounding.BoundingVolume;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/10/13
 * Time: 3:08 PM
 * Bounds interface for shapes.
 */
public interface Bounds {
    /**
     * @return the entity the bounds is attached to.
     */
    Entity getEntity();

    /**
     * This method is used for colliding bounds.
     * @return the bounding volume of the bounds.
     */
    BoundingVolume getBounds();

    /**
     * @return the group of the bounds.
     */
    long getGroups();

    /**
     * @param groups the group to set.
     */
    void setGroups(long groups);

    /**
     * @return the targets of the bounds.
     */
    long getTargets();

    /**
     * @param targets the targets of the bounds.
     */
    void setTargets(long targets);
}
