package com.halboom.pgt.physics.simple;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.filters.Filter;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/26/13
 * Time: 2:02 PM
 * Updates bounds to match the transform component.
 * The bounds system is special as collision resolution will need to be able to change
 * the bounds before or after this system updates.
 */
public class BoundsSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Collision resolver to use.
     */
    private CollisionResolver collisionResolver;

    /**
     * Entity to bounds map.
     */
    private Map<Entity, Bounds> entityBoundsMap = new HashMap<Entity, Bounds>();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param collisionResolver used for broad phase collision and region queries.
     */
    public BoundsSystem(EntitySystem entitySystem, CollisionResolver collisionResolver) {
        this.entitySystem = entitySystem;
        this.collisionResolver = collisionResolver;
    }

    @Override
    public void update(float tpf) {
        EntitySet entitySet = entitySystem.getEntities(AABBComponent.class);

        // Add entities to the map.
        for (Entity entity : entitySet.getAddedEntities()) {
            AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
            aabbComponent.worldExtentX = aabbComponent.localExtentX;
            aabbComponent.worldExtentY = aabbComponent.localExtentY;
            aabbComponent.worldExtentZ = aabbComponent.localExtentZ;
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent != null) {
                aabbComponent.worldExtentX *= transformComponent.scaleX;
                aabbComponent.worldExtentY *= transformComponent.scaleY;
                aabbComponent.worldExtentZ *= transformComponent.scaleZ;
            }
            BoundsBox bounds = new BoundsBox(new Vector3f(aabbComponent.centerX, aabbComponent.centerY, aabbComponent.centerZ),
                    aabbComponent.worldExtentX, aabbComponent.worldExtentY, aabbComponent.worldExtentZ);
            bounds.setEntity(entity);
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (collisionComponent != null) {
                bounds.setGroups(collisionComponent.groups);
                bounds.setTargets(collisionComponent.targets);
            }
            entityBoundsMap.put(entity, bounds);
        }

        // Remove entities from the map.
        for (Entity entity : entitySet.getRemovedEntities()) {
            entityBoundsMap.remove(entity);
        }

        updateAllBounds();
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * Retrieves the bounds of an entity.
     * @param entity the entity to retrieve from.
     * @return the bounds of the entity or null if none exists.
     */
    public Bounds getBounds(Entity entity) {
        return entityBoundsMap.get(entity);
    }

    /**
     * Updates all bounds.
     */
    public void updateAllBounds() {
        EntitySet entitySet = entitySystem.getEntities(AABBComponent.class);
        // Update the bounds component and the internal bounds.
        Vector3f center = new Vector3f();
        for (Entity entity : entitySet.getEntities()) {
            AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            // Update the AABB component information.
            float scaleX = 1, scaleY = 1, scaleZ = 1;
            if (transformComponent != null) {
                scaleX = transformComponent.scaleX;
                scaleY = transformComponent.scaleY;
                scaleZ = transformComponent.scaleZ;
                aabbComponent.worldOffsetX = aabbComponent.localOffsetX * scaleX;
                aabbComponent.worldOffsetY = aabbComponent.localOffsetY * scaleY;
                aabbComponent.worldOffsetZ = aabbComponent.localOffsetZ * scaleZ;
                aabbComponent.centerX = transformComponent.positionX + aabbComponent.worldOffsetX;
                aabbComponent.centerY = transformComponent.positionY + aabbComponent.worldOffsetY;
                aabbComponent.centerZ = transformComponent.positionZ + aabbComponent.worldOffsetZ;
            }
            aabbComponent.worldExtentX = aabbComponent.localExtentX * scaleX;
            aabbComponent.worldExtentY = aabbComponent.localExtentY * scaleY;
            aabbComponent.worldExtentZ = aabbComponent.localExtentZ * scaleZ;
            // Update the bounds to match the AABB data.
            center.set(aabbComponent.centerX, aabbComponent.centerY, aabbComponent.centerZ);
            BoundsBox bounds = (BoundsBox) entityBoundsMap.get(entity);
            bounds.setCenter(center);
            bounds.setXExtent(aabbComponent.worldExtentX);
            bounds.setYExtent(aabbComponent.worldExtentY);
            bounds.setZExtent(aabbComponent.worldExtentZ);
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (collisionComponent != null) {
                bounds.setGroups(collisionComponent.groups);
                bounds.setTargets(collisionComponent.targets);
            }
        }
    }

    /**
     * Retrieves all entities within the given bounds.
     * @param bounds the bounds to retrieve from.
     * @param filter the filter to check valid entities with, null to not use any.
     * @return the entities within the bounds matching the filters.
     */
    public List<Entity> getEntitiesInBounds(Bounds bounds, Filter filter) {
        List<Entity> colliders = new ArrayList<Entity>();
        EntitySet entitySet = entitySystem.getEntities(CollisionComponent.class);
        for (Entity entity : entitySet.getEntities()) {
            Bounds entityBounds = getBounds(entity);
            boolean isValid = true;
            if (filter != null && bounds != null && entityBounds != null) {
                isValid = filter.filterBounds(bounds, entityBounds);
            }
            if (isValid && entityBounds != null) {
                CollisionInformation collisionInformation = collisionResolver.collideBounds(bounds, entityBounds);
                if (collisionInformation != null) {
                    colliders.add(entity);
                }
            }
        }

        return colliders;
    }

    /**
     * Retrieves the closest intersection of a ray.
     * @param ray the ray to test for intersections.
     * @param self the entity to ignore when casting the ray.
     * @param filter the filter to check valid entities with.
     * @return the closest intersecting entity or null if none found.
     */
    public Entity getClosestIntersect(Ray ray, Entity self, Filter filter) {
        EntitySet entitySet = entitySystem.getEntities(CollisionComponent.class);
        Entity closestEntity = null;
        float closest = Float.MAX_VALUE;
        Vector3f origin = ray.getOrigin();
        for (Entity entity : entitySet.getEntities()) {
            Bounds entityBounds = getBounds(entity);
            boolean isValid = true;
            if (filter != null) {
                isValid = filter.filterEntity(entity, entityBounds);
            }
            if (isValid && entity != self) {
                if (collisionResolver.intersectsWithBounds(ray, entityBounds)) {
                    // Calculate the partial distance.
                    Vector3f center = entityBounds.getBounds().getCenter();
                    float dx = origin.x - center.x;
                    float dy = origin.y - center.y;
                    float dz = origin.z - center.z;
                    float distance = dx * dx + dy * dy + dz * dz;
                    if (closest > distance) {
                        closest = distance;
                        closestEntity = entity;
                    }
                }
            }
        }

        return closestEntity;
    }

    @Override
    public void destroy() {
    }
}
