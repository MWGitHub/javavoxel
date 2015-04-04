package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.ActionComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/5/13
 * Time: 4:12 PM
 * Resets actions and handles animations for some actions.
 */
public class ActionSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public ActionSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void cleanupSubsystem() {
        // Reset all actions.
        EntitySet set = entitySystem.getEntities(ActionComponent.class);
        for (Entity entity : set.getEntities()) {
            ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
            actionComponent.isWalking = false;
            actionComponent.walkDirectionX = 0;
            actionComponent.walkDirectionY = 0;
            actionComponent.walkDirectionZ = 0;
            actionComponent.isCasting = false;
            actionComponent.castDirectionX = 0;
            actionComponent.castDirectionY = 0;
            actionComponent.castDirectionZ = 0;
        }
    }

    @Override
    public void destroy() {
    }
}
