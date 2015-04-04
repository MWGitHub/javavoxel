package com.halboom.pgt.physics.filters;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.BoundsColliderSystem;
import com.halboom.pgt.physics.simple.CollisionCallbacks;
import com.halboom.pgt.physics.simple.CollisionInformation;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/10/13
 * Time: 1:31 PM
 * Handles lists of previous colliders.
 * Make sure the collider system runs before the colliders or else the previous colliders
 * will update before testing for first additions.
 */
public class ColliderHistorySystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param boundsCollider the bounds collider to hook to.
     */
    public ColliderHistorySystem(EntitySystem entitySystem, BoundsColliderSystem boundsCollider) {
        this.entitySystem = entitySystem;
        final EntitySystem system = entitySystem;
        boundsCollider.addCollisionCallbacks(new CollisionCallbacks() {
            @Override
            public void onSensorCollide(CollisionInformation collisionInformation) {
                // Add colliders that were not colliding before into the new colliders.
                Entity collider = collisionInformation.getCollider();
                Entity target = collisionInformation.getCollidee();
                ColliderHistoryComponent colliderHistoryComponent = system.getComponent(collider, ColliderHistoryComponent.class);
                if (colliderHistoryComponent != null && !colliderHistoryComponent.colliders.contains(target)) {
                    colliderHistoryComponent.newColliders.add(target);
                }
            }

            @Override
            public void onBlockerCollide(CollisionInformation collisionInformation) {
                // Add colliders that were not colliding before into the new colliders.
                Entity collider = collisionInformation.getCollider();
                Entity target = collisionInformation.getCollidee();
                ColliderHistoryComponent colliderHistoryComponent = system.getComponent(collider, ColliderHistoryComponent.class);
                if (colliderHistoryComponent != null && !colliderHistoryComponent.colliders.contains(target)) {
                    colliderHistoryComponent.newColliders.add(target);
                }
            }
        });
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(ColliderHistoryComponent.class);
        // Clear previous colliders if a certain time has passed.
        for (Entity entity : set.getEntities()) {
            ColliderHistoryComponent colliderHistoryComponent = entitySystem.getComponent(entity, ColliderHistoryComponent.class);
            if (colliderHistoryComponent.clearTime != 0) {
                colliderHistoryComponent.currentClearTime += tpf;
                if (colliderHistoryComponent.currentClearTime >= colliderHistoryComponent.clearTime) {
                    colliderHistoryComponent.currentClearTime = 0;
                    colliderHistoryComponent.colliders.clear();
                }
            }

            // Add the colliders from the previous frame.
            colliderHistoryComponent.colliders.addAll(colliderHistoryComponent.newColliders);
            colliderHistoryComponent.newColliders.clear();
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
