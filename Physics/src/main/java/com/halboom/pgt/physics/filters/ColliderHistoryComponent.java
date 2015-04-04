package com.halboom.pgt.physics.filters;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/10/13
 * Time: 1:14 PM
 * Holds which colliders have previously collided with the entity.
 */
public class ColliderHistoryComponent implements Component {
    /**
     * Colliders that have collided with the entity before.
     */
    public List<Entity> colliders = new LinkedList<Entity>();

    /**
     * New colliders that have collided with the entity.
     */
    public List<Entity> newColliders = new LinkedList<Entity>();

    /**
     * Time before clearing the previous colliders list.
     */
    public float clearTime = 0.0f;

    /**
     * Current clearing time; clears when clearTime is reached.
     */
    public float currentClearTime = 0.0f;

    @Override
    public Component copy() {
        ColliderHistoryComponent output = new ColliderHistoryComponent();
        output.colliders.addAll(colliders);
        output.clearTime = clearTime;
        output.currentClearTime = currentClearTime;

        return output;
    }
}
