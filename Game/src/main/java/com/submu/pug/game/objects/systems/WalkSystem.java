package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.jme3.math.Vector3f;
import com.submu.pug.game.objects.components.ActionComponent;
import com.submu.pug.game.objects.components.WalkComponent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/23/13
 * Time: 3:31 PM
 * Handles the walking actions of an entity.
 */
public class WalkSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the system to use.
     */
    public WalkSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    /**
     * Retrieves the walking acceleration for an axis given the maximum speeds.
     * @param speed the current speed.
     * @param acceleration the current acceleration.
     * @param maxWalkSpeed the maximum walking speed.
     * @param walkAcceleration the walking acceleration.
     * @param direction the normalized movement direction.
     * @return the amount to add acceleration by.
     */
    private float calculateWalkAcceleration(float speed, float acceleration, float maxWalkSpeed, float walkAcceleration, float direction) {
        // Do not need to find limits if there is no walk speed cap.
        if (maxWalkSpeed == 0) {
            return direction * walkAcceleration;
        } else {
            // Check if the speed will go over the limit in the specified direction.
            int walkDirection = direction < 0 ? -1 : 1;
            float nextSpeedCurrent = speed + acceleration;
            // Do nothing if the speed is in the same direction of the walk and also greater than the walk cap.
            float absMax = Math.abs(maxWalkSpeed);
            if (walkDirection > 0 && nextSpeedCurrent > absMax) {
                return 0.0f;
            }
            if (walkDirection < 0 && nextSpeedCurrent < -absMax) {
                return 0.0f;
            }
            // If the speed after walking isn't greater than the max walk then allow normally.
            if (Math.abs(nextSpeedCurrent + walkAcceleration * direction) <= absMax) {
                return walkAcceleration * direction;
            } else {
                // Find the limit to make the speed the same as the max walking speed.
                // Note that the current speed will not be over the max walking speed.
                return walkDirection * absMax - speed - acceleration;
            }
        }
    }

    /**
     * Adds the horizontal ground acceleration.
     * @param speedComponent the speed component of the entity.
     * @param walkComponent the walk component of the entity.
     * @param direction the direction of movement.
     */
    private void addHorizontalAcceleration(SpeedComponent speedComponent, WalkComponent walkComponent, Vector3f direction) {
        // Get the acceleration speed.
        float walkAccel = walkComponent.horizontalAcceleration;
        // Calculate the acceleration in each direction.
        float accelX = walkAccel * direction.x;
        float accelZ = walkAccel * direction.z;

        speedComponent.accelX += accelX;
        speedComponent.accelZ += accelZ;
    }

    /**
     * Adds the horizontal air acceleration.
     * @param speedComponent the speed component of the entity.
     * @param walkComponent the walk component of the entity.
     * @param direction the direction of movement.
     */
    private void addAirAcceleration(SpeedComponent speedComponent, WalkComponent walkComponent, Vector3f direction) {
        // Get the acceleration speed.
        float airAccel = walkComponent.airAcceleration;
        // Calculate the acceleration in each direction.
        float accelX = airAccel * direction.x;
        float accelZ = airAccel * direction.z;

        // Flying entities accelerate normally in the air.
        if (walkComponent.isYMovementAllowed) {
            speedComponent.accelX += accelX;
            speedComponent.accelZ += accelZ;
            return;
        }

        // Get the walking speed when walking.
        float x = speedComponent.speedX + speedComponent.accelX + accelX;
        float z = speedComponent.speedZ + speedComponent.accelZ + accelZ;
        float walkSpeed = (float) Math.sqrt(x * x + z * z);

        // Get the walking speed without walk acceleration.
        x = speedComponent.speedX + speedComponent.accelX;
        z = speedComponent.speedZ + speedComponent.accelZ;
        float speed = (float) Math.sqrt(x * x + z * z);

        // Allow normal movement if the next speed is not as fast as the maximum.
        if (walkSpeed < walkComponent.maxHorizontalWalkSpeed) {
            speedComponent.accelX += accelX;
            speedComponent.accelZ += accelZ;
            return;
        }

        // Allow if the new speed is lower than the current speed.
        if (walkSpeed < speed) {
            speedComponent.accelX += accelX;
            speedComponent.accelZ += accelZ;
        }
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(WalkComponent.class);
        for (Entity entity : set.getEntities()) {
            ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
            if (actionComponent == null) {
                continue;
            }
            SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);
            if (speedComponent == null) {
                continue;
            }
            if (!actionComponent.isWalking) {
                continue;
            }

            Vector3f direction = new Vector3f(actionComponent.walkDirectionX, actionComponent.walkDirectionY, actionComponent.walkDirectionZ);
            WalkComponent walkComponent = entitySystem.getComponent(entity, WalkComponent.class);
            if (!walkComponent.isYMovementAllowed) {
                direction.y = 0;
            }
            direction.normalizeLocal();
            // Add the acceleration for entities that can fly.
            if (walkComponent.isYMovementAllowed) {
                speedComponent.accelY += calculateWalkAcceleration(speedComponent.speedY, speedComponent.accelY, walkComponent.maxVerticalWalkSpeed, walkComponent.verticalAcceleration, direction.y);
            }
            // Add the acceleration horizontally.
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            boolean isOnFloor = collisionComponent == null || collisionComponent.isOnFloor;
            if (isOnFloor) {
                addHorizontalAcceleration(speedComponent, walkComponent, direction);
            } else {
                addAirAcceleration(speedComponent, walkComponent, direction);
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
