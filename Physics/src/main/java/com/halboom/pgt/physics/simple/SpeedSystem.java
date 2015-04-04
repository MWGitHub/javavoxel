package com.halboom.pgt.physics.simple;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/19/13
 * Time: 12:29 PM
 * Updates all entities with speed.
 */
public class SpeedSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Vector to use for temporary calculations.
     */
    private Vector3f speed = new Vector3f();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public SpeedSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    /**
     * Clamps the speed.
     * @param speedComponent the speed to clamp.
     */
    private void clampSpeed(SpeedComponent speedComponent) {
        // Clamp the Y speed if too fast.
        if (speedComponent.maxSpeedVertical != 0 && FastMath.abs(speedComponent.speedY) > speedComponent.maxSpeedVertical) {
            speedComponent.speedY = FastMath.clamp(speedComponent.speedY, -speedComponent.maxSpeedVertical, speedComponent.maxSpeedVertical);
        }
        // Clamp the X and Z speed if too fast.
        float magnitude = (float) Math.sqrt(speedComponent.speedX * speedComponent.speedX + speedComponent.speedZ * speedComponent.speedZ);
        if (speedComponent.maxSpeedHorizontal != 0 && magnitude > speedComponent.maxSpeedHorizontal) {
            speed.set(speedComponent.speedX, 0, speedComponent.speedZ);
            speed.normalizeLocal().multLocal(speedComponent.maxSpeedHorizontal);
            speedComponent.speedX = speed.x;
            speedComponent.speedZ = speed.z;
        }
    }

    /**
     * Applies damping to the speed.
     * @param speedComponent the speed to apply damping to.
     */
    private void applyDamping(SpeedComponent speedComponent) {
        // Apply damping to speed in the x and z directions only.
        if (speedComponent.isHorizontalDamped) {
            speedComponent.speedX *= speedComponent.damping;
            speedComponent.speedZ *= speedComponent.damping;
        }
        // Damp the vertical speed.
        if (speedComponent.isVerticalDamped) {
            speedComponent.speedY *= speedComponent.damping;
        }
    }

    /**
     * Applies friction to the speed.
     * @param speedComponent the speed to apply friction to.
     */
    private void applyFriction(SpeedComponent speedComponent) {
        // Apply friction to the object.
        float friction = 1 - (speedComponent.friction + speedComponent.selfFriction) / 2;
        friction = FastMath.clamp(friction, speedComponent.minFriction, speedComponent.maxFriction);
        speedComponent.speedX *= friction;
        speedComponent.speedZ *= friction;

        // Reset friction when done
        speedComponent.friction = 0.0f;
    }

    /**
     * Updates the speed to add acceleration.
     * The acceleration should already be in units of ticks as it is added every update no matter the tickrate.
     * @param tpf the time step.
     */
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(SpeedComponent.class);

        for (Entity entity : set.getEntities()) {
            SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);

            // Add the acceleration to the speed where acceleration has been converted to the tickrate.
            speedComponent.speedX += speedComponent.accelX;
            speedComponent.speedY += speedComponent.accelY;
            speedComponent.speedZ += speedComponent.accelZ;

            // Apply damping.
            applyDamping(speedComponent);
            // Apply friction only if the entity is on the floor.
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            boolean isOnFloor = collisionComponent == null || collisionComponent.isOnFloor;
            if (isOnFloor) {
                applyFriction(speedComponent);
            }

            // Clamp the speed if needed.
            clampSpeed(speedComponent);

            // Move the entity by the set speed using the movement component if available.
            MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
            if (movementComponent != null) {
                movementComponent.moveX += speedComponent.speedX * tpf;
                movementComponent.moveY += speedComponent.speedY * tpf;
                movementComponent.moveZ += speedComponent.speedZ * tpf;
            } else {
                TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
                if (transformComponent != null) {
                    transformComponent.positionX += speedComponent.speedX * tpf;
                    transformComponent.positionY += speedComponent.speedY * tpf;
                    transformComponent.positionZ += speedComponent.speedZ * tpf;
                }
            }

            // Reset the acceleration.
            speedComponent.accelX = 0;
            speedComponent.accelY = 0;
            speedComponent.accelZ = 0;
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
