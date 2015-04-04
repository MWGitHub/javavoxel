package com.halboom.pgt.physics.filters;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.CollisionComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/11/13
 * Time: 8:59 PM
 * System for turning off and on collision.
 */
public class PulseCollisionSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the entity system.
     * @param entitySystem the entity system to use.
     */
    public PulseCollisionSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(PulseCollisionComponent.class);
        // Turn on and off the targets for the bounds.
        for (Entity entity : set.getEntities()) {
            CollisionComponent bounds = entitySystem.getComponent(entity, CollisionComponent.class);
            if (bounds == null) {
                continue;
            }
            PulseCollisionComponent pulse = entitySystem.getComponent(entity, PulseCollisionComponent.class);
            pulse.currentTime += tpf;
            if (pulse.currentTime >= pulse.period) {
                if (pulse.previousTarget == 0) {
                    pulse.previousTarget = bounds.targets;
                    bounds.targets = 0;
                } else {
                    bounds.targets = pulse.previousTarget;
                    pulse.previousTarget = 0;
                }
                pulse.currentTime = 0;
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
