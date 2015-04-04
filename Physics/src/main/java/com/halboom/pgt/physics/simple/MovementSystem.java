package com.halboom.pgt.physics.simple;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.MovementComponent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/19/13
 * Time: 3:54 PM
 * Moves all objects with a movement component.
 */
public class MovementSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the movement system.
     * @param entitySystem the entity system to use.
     */
    public MovementSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    /**
     * Moves the transforms of entities and resets the movement amount.
     * @param tpf the time passed per frame.
     */
    public void update(float tpf) {
        EntitySet entities = entitySystem.getEntities(MovementComponent.class);

        // Move all the entities.
        for (Entity entity : entities.getEntities()) {
            MovementComponent component = entitySystem.getComponent(entity, MovementComponent.class);
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent != null) {
                transformComponent.positionX += component.moveX;
                transformComponent.positionY += component.moveY;
                transformComponent.positionZ += component.moveZ;
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
        EntitySet entities = entitySystem.getEntities(MovementComponent.class);
        for (Entity entity : entities.getEntities()) {
            MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
            movementComponent.moveX = 0;
            movementComponent.moveY = 0;
            movementComponent.moveZ = 0;
        }
    }

    @Override
    public void destroy() {
    }
}
