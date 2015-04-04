package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.ExperienceComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/31/13
 * Time: 1:22 PM
 * Handles the leveling of entities.
 */
public class ExperienceSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public ExperienceSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(ExperienceComponent.class);
        // Level up an entity.
        for (Entity entity : set.getChangedEntities()) {
            ExperienceComponent experienceComponent = entitySystem.getComponent(entity, ExperienceComponent.class);
            if (experienceComponent.experience >= experienceComponent.level * experienceComponent.experiencePerLevel) {
                // TODO: Add leveling callback.
                experienceComponent.level++;
                experienceComponent.skillPoints++;
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
