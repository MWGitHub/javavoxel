package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.math.Vector3f;
import com.submu.pug.game.objects.components.ActionComponent;
import com.submu.pug.game.objects.components.MoveCommandComponent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/23/13
 * Time: 8:30 PM
 */
public class MoveCommandSystem implements Subsystem {
    /**
     * System to use for the entities.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public MoveCommandSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        // Move entities that have the move command.
        EntitySet set = entitySystem.getEntities(MoveCommandComponent.class);
        for (Entity entity : set.getEntities()) {
            // Check that the entity is valid.
            ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
            if (actionComponent == null) {
                continue;
            }
            TransformComponent transform = entitySystem.getComponent(entity, TransformComponent.class);
            if (transform == null) {
                continue;
            }
            MoveCommandComponent moveCommand = entitySystem.getComponent(entity, MoveCommandComponent.class);
            // Check if any of the movement command values are assigned.
            boolean shouldMove = (moveCommand.destinationX != 0 || moveCommand.destinationY != 0 || moveCommand.destinationZ != 0)
                    || (moveCommand.directionX != 0 || moveCommand.directionY != 0 || moveCommand.directionZ != 0)
                    || moveCommand.entity != null;
            if (!shouldMove) {
                continue;
            }
            // Get the destination from an entity else from the move command.
            Vector3f destination = null;
            // Follow an entity.
            if (moveCommand.entity != null) {
                TransformComponent destinationTransform = entitySystem.getComponent(moveCommand.entity, TransformComponent.class);
                if (destinationTransform != null) {
                    destination = new Vector3f(destinationTransform.positionX, destinationTransform.positionY, destinationTransform.positionZ);
                }
            }
            // Move towards a destination point.
            if (destination == null && (moveCommand.destinationX != 0 || moveCommand.destinationY != 0 || moveCommand.destinationZ != 0)) {
                destination = new Vector3f(moveCommand.destinationX, moveCommand.destinationY, moveCommand.destinationZ);
            }
            // Move towards the destination if there is one or the direction if not.
            if (destination != null) {
                // Find the angle to reach the destination.
                Vector3f currentLocation = new Vector3f(transform.positionX, transform.positionY, transform.positionZ);
                // Calculate the direction, the Y axis does not need to be checked against as the walk system will handle that.
                destination.subtractLocal(currentLocation);
                destination.normalizeLocal();
                // Set the action component.
                actionComponent.isWalking = true;
                actionComponent.walkDirectionX = destination.x;
                actionComponent.walkDirectionY = destination.y;
                actionComponent.walkDirectionZ = destination.z;
            } else if (moveCommand.directionX != 0 || moveCommand.directionY != 0 || moveCommand.directionZ != 0) {
                // Set the angle to walk.
                actionComponent.isWalking = true;
                actionComponent.walkDirectionX = moveCommand.directionX;
                actionComponent.walkDirectionY = moveCommand.directionY;
                actionComponent.walkDirectionZ = moveCommand.directionZ;
            }
            // Look in the walking direction.
            actionComponent.lookDirectionX = actionComponent.walkDirectionX;
            actionComponent.lookDirectionY = actionComponent.walkDirectionY;
            actionComponent.lookDirectionZ = actionComponent.walkDirectionZ;
            // Reset the movement for directional move, if the user wants to simulate directional move
            // just move to point of a direction and keep changing it to ahead.
            moveCommand.directionX = 0;
            moveCommand.directionY = 0;
            moveCommand.directionZ = 0;
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
