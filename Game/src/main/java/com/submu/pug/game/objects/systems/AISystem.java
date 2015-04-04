package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.BoundsSystem;
import com.halboom.pgt.physics.simple.shapes.BoundsSphere;
import com.jme3.math.Vector3f;
import com.submu.pug.game.CollisionFilter;
import com.submu.pug.game.objects.components.AIComponent;
import com.submu.pug.game.objects.components.FilterFlagComponent;
import com.submu.pug.game.objects.components.MoveCommandComponent;
import com.submu.pug.game.objects.components.OwnerComponent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/26/13
 * Time: 4:56 PM
 * Handles general AI triggering.
 */
public class AISystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * System to use for getting entities within bounds.
     */
    private BoundsSystem boundsSystem;

    /**
     * Collision filter to use for finding which entities can be acted upon.
     */
    private CollisionFilter collisionFilter;

    /**
     * Location of the volume.
     */
    private Vector3f center = new Vector3f();

    /**
     * Sphere used for bounds checks.
     */
    private BoundsSphere sphere = new BoundsSphere(1.0f, center);

    /**
     * Callbacks for the AI system.
     */
    private Callbacks callbacks;

    /**
     * Initializes the AI system.
     * @param entitySystem the entity system to use.
     * @param boundsSystem the system used for bounds.
     * @param collisionFilter filtering system to use for finding which entities that can be collided with.
     */
    public AISystem(EntitySystem entitySystem, BoundsSystem boundsSystem, CollisionFilter collisionFilter) {
        this.entitySystem = entitySystem;
        this.boundsSystem = boundsSystem;
        this.collisionFilter = collisionFilter;
        sphere.setTargets(CollisionFilter.COLLISION_UNIT);
        Entity sphereEntity = entitySystem.createEntity();
        sphere.setEntity(sphereEntity);

        FilterFlagComponent filterFlagComponent = new FilterFlagComponent();
        filterFlagComponent.collidesEnemy = true;
        entitySystem.setComponent(sphereEntity, filterFlagComponent);
        entitySystem.setComponent(sphereEntity, new OwnerComponent());
    }

    /**
     * Retrieves the closest entity to a point.
     * @param position the position to check from.
     * @param entities the entities to check with.
     * @return the closest entity or null if none.
     */
    private Entity getClosestEntity(Vector3f position, List<Entity> entities) {
        Entity closest = null;
        float minDistance = Float.MAX_VALUE;
        for (Entity entity : entities) {
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent != null) {
                float distance = (transformComponent.positionX - position.x) * (transformComponent.positionX - position.x)
                        + (transformComponent.positionY - position.y) * (transformComponent.positionY - position.y)
                        + (transformComponent.positionZ - position.z) * (transformComponent.positionZ - position.z);
                if (distance < minDistance) {
                    minDistance = distance;
                    closest = entity;
                }
            }
        }

        return closest;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(AIComponent.class);
        for (Entity entity : set.getEntities()) {
            // Follow hostile entities within range.
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent == null) {
                continue;
            }
            AIComponent aiComponent = entitySystem.getComponent(entity, AIComponent.class);
            sphere.setRadius(aiComponent.aggroRange);
            center.x = transformComponent.positionX;
            center.y = transformComponent.positionY;
            center.z = transformComponent.positionZ;
            sphere.setCenter(center);
            // Update sphere properties to match the entity for filtering.
            Entity sphereEntity = sphere.getEntity();
            OwnerComponent ownerComponent = entitySystem.getComponent(entity, OwnerComponent.class);
            if (ownerComponent != null) {
                OwnerComponent sphereOwner = entitySystem.getComponent(sphereEntity, OwnerComponent.class);
                sphereOwner.playerID = ownerComponent.playerID;
            } else {
                entitySystem.removeComponent(sphereEntity, OwnerComponent.class);
            }
            // Get the closest enemy.
            List<Entity> inRange = boundsSystem.getEntitiesInBounds(sphere, collisionFilter);
            Entity closest = getClosestEntity(center, inRange);
            if (closest != null) {
                MoveCommandComponent moveCommandComponent = entitySystem.getComponent(entity, MoveCommandComponent.class);
                // Check if within combat range, else aggro.
                TransformComponent enemyPosition = entitySystem.getComponent(closest, TransformComponent.class);
                float distance = Float.MAX_VALUE;
                // Only calculate distance if the entity has a combat script.
                if (aiComponent.combatScript != null && enemyPosition != null) {
                    distance = (enemyPosition.positionX - center.x) * (enemyPosition.positionX - center.x)
                        + (enemyPosition.positionY - center.y) * (enemyPosition.positionY - center.y)
                        + (enemyPosition.positionZ - center.z) * (enemyPosition.positionZ - center.z);
                }
                if (distance < aiComponent.combatRange * aiComponent.combatRange) {
                    aiComponent.isInCombat = true;
                    // Run the combat script.
                    if (aiComponent.combatScript != null && callbacks != null) {
                        callbacks.onCombat(entity, closest, aiComponent.combatScript);
                    }
                } else {
                    if (moveCommandComponent == null) {
                        moveCommandComponent = new MoveCommandComponent();
                        entitySystem.setComponent(entity, moveCommandComponent);
                    }
                    moveCommandComponent.entity = closest;
                }
            } else if (aiComponent.isInCombat) {
                // Leave combat.
                aiComponent.isInCombat = false;
                if (callbacks != null) {
                    callbacks.onLeaveCombat(entity);
                }
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * @param callbacks the callbacks to set.
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void destroy() {
    }

    /**
     * Callbacks for the AI system.
     */
    public interface Callbacks {
        /**
         * Called when entering combat.
         * @param entity the entity entering combat.
         * @param target the target that triggered the combat.
         * @param script the function to run for the AI.
         */
        void onCombat(Entity entity, Entity target, String script);

        /**
         * Called when the entity leaves combat.
         * @param entity the entity leaving combat.
         */
        void onLeaveCombat(Entity entity);
    }
}
