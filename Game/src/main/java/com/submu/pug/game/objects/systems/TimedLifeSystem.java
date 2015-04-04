package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.TimedLifeComponent;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/10/13
 * Time: 2:21 PM
 * System to handle timed life.
 */
public class TimedLifeSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public TimedLifeSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(TimedLifeComponent.class);
        // Remove entities whose life time has expired.
        List<Entity> removedEntities = new LinkedList<Entity>();
        for (Entity entity : set.getEntities()) {
            TimedLifeComponent timedLifeComponent = entitySystem.getComponent(entity, TimedLifeComponent.class);
            timedLifeComponent.currentLifeTime += tpf;
            if (timedLifeComponent.currentLifeTime >= timedLifeComponent.lifeTime) {
                removedEntities.add(entity);
            }
        }
        for (Entity entity : removedEntities) {
            entitySystem.removeEntity(entity);
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
