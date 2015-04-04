package com.halboom.pgt.physics.simple;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.components.PhysicsStateComponent;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.halboom.pgt.physics.simple.components.SpeedComponent;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.math.Vector3f;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/9/13
 * Time: 2:19 PM
 * Collides objects with other objects.
 */
public class BoundsColliderSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Used to resolve and test for collisions.
     */
    private CollisionResolver resolver;

    /**
     * Used to retrieve bounds.
     */
    private BoundsSystem boundsSystem;

    /**
     * Callbacks for collisions.
     */
    private List<CollisionCallbacks> collisionCallbacks = new LinkedList<CollisionCallbacks>();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param resolver the collision resolver to use.
     * @param boundsSystem the bounds system.
     */
    public BoundsColliderSystem(EntitySystem entitySystem, CollisionResolver resolver, BoundsSystem boundsSystem) {
        this.entitySystem = entitySystem;
        this.resolver = resolver;
        this.boundsSystem = boundsSystem;
    }

    /**
     * Checks sensor collisions.
     */
    private void checkSensorCollisions() {
        // Check collisions of sensors.
        EntitySet sensorSet = entitySystem.getEntities(CollisionComponent.class);
        List<ColliderSet> sensorColliders = new LinkedList<ColliderSet>();
        // Check sensor collisions.
        for (Entity entity : sensorSet.getEntities()) {
            Bounds bounds = boundsSystem.getBounds(entity);
            if (bounds != null) {
                sensorColliders.add(new ColliderSet(bounds, entity));
            }
        }

        // Check for sensor collisions against any collider.
        EntitySet aabbSet = entitySystem.getEntities(CollisionComponent.class);
        for (Entity entity : aabbSet.getEntities()) {
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (!collisionComponent.isSensor) {
                continue;
            }
            Bounds bounds = boundsSystem.getBounds(entity);
            if (bounds == null) {
                continue;
            }
            for (ColliderSet collidee : sensorColliders) {
                CollisionInformation information = resolver.collideBounds(bounds, collidee.getBounds());
                if (information != null) {
                    information.setCollider(entity);
                    information.setCollidee(collidee.getEntity());
                    for (CollisionCallbacks callback : collisionCallbacks) {
                        callback.onSensorCollide(information);
                    }
                }
            }
        }
    }

    /**
     * Checks collisions with static objects.
     */
    private void checkStaticCollisions() {
        EntitySet set = entitySystem.getEntities(CollisionComponent.class);
        // Create a list of all colliders and add movement bounds if needed.
        List<BoundsBox> colliders = new LinkedList<BoundsBox>();
        for (Entity entity : set.getEntities()) {
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (!collisionComponent.isSensor) {
                Bounds bounds = boundsSystem.getBounds(entity);
                if (bounds != null && bounds instanceof BoundsBox) {
                    colliders.add((BoundsBox) bounds);
                }
            }
        }

        // Check for collisions.
        for (Entity entity : set.getEntities()) {
            // Check if the entity is valid for static collisions.
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (collisionComponent.isSensor) {
                continue;
            }
            MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
            if (movementComponent == null) {
                continue;
            }
            AABBComponent component = entitySystem.getComponent(entity, AABBComponent.class);
            if (component == null) {
                continue;
            }
            Bounds bounds = boundsSystem.getBounds(entity);
            if (bounds == null || !(bounds instanceof BoundsBox)) {
                continue;
            }
            BoundsBox boundsBox = (BoundsBox) bounds;
            CollisionInformation information = resolver.resolveCollision(boundsBox,
                    new Vector3f(movementComponent.moveX, movementComponent.moveY, movementComponent.moveZ),
                    colliders);
            information.setCollider(entity);
            Vector3f moveAmount = information.getMoveAmount();
            // Trigger callbacks only if the collider cannot move fully.
            boolean hasMoved = movementComponent.moveX != moveAmount.x || movementComponent.moveY != moveAmount.y
                    || movementComponent.moveZ != moveAmount.z;
            if (hasMoved) {
                for (CollisionCallbacks callback : collisionCallbacks) {
                    callback.onBlockerCollide(information);
                }
            }
            // Handle Y flags and speed changes.
            if (movementComponent.moveY != moveAmount.y) {
                // Set the floor flag.
                if (movementComponent.moveY < 0) {
                    PhysicsStateComponent physicsStateComponent = entitySystem.getComponent(entity, PhysicsStateComponent.class);
                    if (physicsStateComponent != null) {
                        physicsStateComponent.isGroundedBounds = true;
                    }
                    collisionComponent.isOnFloor = true;
                }
                // Set friction of the collider if the collider is above.
                SpeedComponent speedComponent = entitySystem.getComponent(entity, SpeedComponent.class);
                if (speedComponent != null) {
                    speedComponent.speedY = 0;
                    if (moveAmount.y > movementComponent.moveY) {
                        speedComponent.friction = entitySystem.getComponent(information.getCollider(), CollisionComponent.class).friction;
                    }
                }
            }
            movementComponent.moveX = moveAmount.x;
            movementComponent.moveY = moveAmount.y;
            movementComponent.moveZ = moveAmount.z;
            boundsBox.setCenter(boundsBox.getCenter().add(movementComponent.moveX, movementComponent.moveY, movementComponent.moveZ));
        }
    }

    @Override
    public void update(float tpf) {
        checkSensorCollisions();
        checkStaticCollisions();
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * @param collisionCallbacks the collision callbacks to set.
     */
    public void addCollisionCallbacks(CollisionCallbacks collisionCallbacks) {
        if (!this.collisionCallbacks.contains(collisionCallbacks)) {
            this.collisionCallbacks.add(collisionCallbacks);
        }
    }

    @Override
    public void destroy() {
    }
}
