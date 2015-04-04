package com.submu.pug.game.world;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.SpeedComponent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/2/13
 * Time: 2:00 PM
 * Calculates gravity for objects.
 */
public class GravitySystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public GravitySystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(GravityComponent.class);
        for (Entity entity : set.getEntities()) {
            SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);
            if (speedComponent != null) {
                GravityComponent gravityComponent = entitySystem.getComponent(entity, GravityComponent.class);
                speedComponent.accelX += gravityComponent.x * tpf;
                speedComponent.accelY += gravityComponent.y * tpf;
                speedComponent.accelZ += gravityComponent.z * tpf;
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
