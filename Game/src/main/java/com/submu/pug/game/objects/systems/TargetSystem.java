package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.TargetComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/16/13
 * Time: 2:30 PM
 * Targeting system that can be used for homing or head tracking.
 */
public class TargetSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the targeting system.
     * @param entitySystem the entity system to use.
     */
    public TargetSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanupSubsystem() {
        // Remove targets that are no longer in the entity system.
        EntitySet set = entitySystem.getEntities(TargetComponent.class);
        for (Entity entity : set.getEntities()) {
            TargetComponent targetComponent = entitySystem.getComponent(entity, TargetComponent.class);
            if (targetComponent == null || targetComponent.target == null) {
                continue;
            }
            if (!entitySystem.hasEntity(targetComponent.target)) {
                targetComponent.target = null;
            }
        }
    }

    @Override
    public void destroy() {
    }
}
