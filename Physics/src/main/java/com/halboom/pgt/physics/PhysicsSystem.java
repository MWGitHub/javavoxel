package com.halboom.pgt.physics;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.bullet.BulletSystem;
import com.halboom.pgt.physics.components.PhysicsStateComponent;
import com.halboom.pgt.physics.debug.DebugBoundsSystem;
import com.halboom.pgt.physics.filters.ColliderHistorySystem;
import com.halboom.pgt.physics.filters.Filter;
import com.halboom.pgt.physics.filters.PulseCollisionSystem;
import com.halboom.pgt.physics.simple.BoundsColliderSystem;
import com.halboom.pgt.physics.simple.BoundsSystem;
import com.halboom.pgt.physics.simple.CollisionCallbacks;
import com.halboom.pgt.physics.simple.CollisionResolver;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.physics.simple.MovementSystem;
import com.halboom.pgt.physics.simple.SpeedSystem;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/31/13
 * Time: 9:17 PM
 * System that controls and updates the other physics systems in the preferred order.
 */
public class PhysicsSystem implements Subsystem {
    /**
     * Entity system to use for getting entity information.
     */
    private EntitySystem entitySystem;

    /**
     * Subsystems to update in order.
     */
    private List<Subsystem> subsystems = new ArrayList<Subsystem>();

    /**
     * Collision resolver can be used externally for manual collision.
     */
    private CollisionResolver collisionResolver;

    /**
     * System used for entity collisions with non grid terrain.
     */
    private BulletSystem bulletSystem;

    /**
     * Bound system that can be used externally for manual collision.
     */
    private BoundsSystem boundsSystem;

    /**
     * Grid collider system that can be used externally for manual collision.
     */
    private GridColliderSystem gridColliderSystem;

    /**
     * Initializes the physics system.
     * @param entitySystem the entity system to use.
     */
    public PhysicsSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;

        collisionResolver = new CollisionResolver();
        boundsSystem = new BoundsSystem(entitySystem, collisionResolver);
        bulletSystem = new BulletSystem(entitySystem);
        BoundsColliderSystem boundsColliderSystem =
                new BoundsColliderSystem(entitySystem, collisionResolver, boundsSystem);
        gridColliderSystem = new GridColliderSystem(entitySystem, collisionResolver);

        subsystems.add(new SpeedSystem(entitySystem));
        subsystems.add(new ColliderHistorySystem(entitySystem, boundsColliderSystem));
        subsystems.add(new PulseCollisionSystem(entitySystem));
        subsystems.add(bulletSystem);
        subsystems.add(gridColliderSystem);
        subsystems.add(boundsColliderSystem);
        subsystems.add(new MovementSystem(entitySystem));
        subsystems.add(boundsSystem);
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(CollisionComponent.class);
        for (Entity entity : set.getAddedEntities()) {
            entitySystem.setComponent(entity, new PhysicsStateComponent());
        }

        for (Subsystem subsystem : subsystems) {
            subsystem.update(tpf);

            // Keep track of the original movement before physics to know if an entity should be on the floor.
            if (subsystem instanceof SpeedSystem) {
                for (Entity entity : set.getEntities()) {
                    MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
                    if (movementComponent == null) {
                        continue;
                    }
                    CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
                    collisionComponent.prePhysicsMoveX = movementComponent.moveX;
                    collisionComponent.prePhysicsMoveY = movementComponent.moveY;
                    collisionComponent.prePhysicsMoveZ = movementComponent.moveZ;
                }
            }
        }

        // If any of the systems placed the entity on the floor then set the floor flag.
        set = entitySystem.getEntities(PhysicsStateComponent.class);
        for (Entity entity : set.getEntities()) {
            CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
            if (collisionComponent == null) {
                continue;
            }
            PhysicsStateComponent physicsStateComponent = entitySystem.getComponent(entity, PhysicsStateComponent.class);
            collisionComponent.isOnFloor = physicsStateComponent.isGroundedBullet
                    || physicsStateComponent.isGroundedBounds
                    || physicsStateComponent.isGroundedGrid;
        }
    }

    @Override
    public void cleanupSubsystem() {
        for (Subsystem subsystem : subsystems) {
            subsystem.cleanupSubsystem();
        }

        // Reset the shared physics states.
        EntitySet set = entitySystem.getEntities(PhysicsStateComponent.class);
        for (Entity entity : set.getEntities()) {
            PhysicsStateComponent physicsStateComponent = entitySystem.getComponent(entity, PhysicsStateComponent.class);
            physicsStateComponent.isGroundedBullet = false;
            physicsStateComponent.isGroundedBounds = false;
            physicsStateComponent.isGroundedGrid = false;
        }
    }

    /**
     * Adds a static object to the bullet physics system.
     * @param object the object to add.
     */
    public void addStaticObject(Spatial object) {
        if (object instanceof Node) {
            bulletSystem.addStaticObject((Node) object);
        }
    }

    /**
     * Removes a static object from the bullet physics system.
     * @param object the object to remove.
     */
    public void removeStaticObject(Spatial object) {
        bulletSystem.removeStaticObject(object);
    }

    /**
     * Sets the tiles of the grid collider.
     * @param tiles the tiles to set.
     */
    public void setTiles(byte[][][] tiles) {
        gridColliderSystem.setTiles(tiles);
    }

    /**
     * Sets the tile frictions for the grid collider system.
     * @param frictions the tile frictions to set.
     */
    public void setTileFriction(float[] frictions) {
        gridColliderSystem.setTileFriction(frictions);
    }

    /**
     * Sets the tile collision groups for the grid collider system.
     * @param groups the groups to set.
     */
    public void setTileCollisionGroups(long[] groups) {
        gridColliderSystem.setCollisionGroups(groups);
    }

    /**
     * @param scale the size of the tiles to set for use with the grid collider.
     */
    public void setTileScale(float scale) {
        gridColliderSystem.setScale(scale);
    }

    /**
     * Adds collision callbacks to both the grid and bounds system.
     * @param callbacks the callbacks to add.
     */
    public void addCollisionCallbacks(CollisionCallbacks callbacks) {
        gridColliderSystem.addCollisionCallbacks(callbacks);
        getSystem(BoundsColliderSystem.class).addCollisionCallbacks(callbacks);
    }

    /**
     * Adds a filter to use for checking if an object should be collidable with another.
     * @param filter the filter to add.
     */
    public void addFilter(Filter filter) {
        collisionResolver.addFilter(filter);
    }

    /**
     * Enable debugging.
     * @param assetManager the asset manager to use for debug materials.
     * @param root the node to attach the debugging displays to.
     */
    public void enableDebug(AssetManager assetManager, Node root) {
        DebugBoundsSystem debugBoundsSystem = getSystem(DebugBoundsSystem.class);
        if (debugBoundsSystem != null) {
            subsystems.remove(debugBoundsSystem);
            debugBoundsSystem.destroy();
        }
        debugBoundsSystem = new DebugBoundsSystem(entitySystem, assetManager, root);
        subsystems.add(debugBoundsSystem);
    }

    /**
     * Disable debugging.
     */
    public void disableDebug() {
        DebugBoundsSystem debugBoundsSystem = getSystem(DebugBoundsSystem.class);
        if (debugBoundsSystem != null) {
            subsystems.remove(debugBoundsSystem);
            debugBoundsSystem.destroy();
        }
    }

    /**
     * @return the collision resolver.
     */
    public CollisionResolver getCollisionResolver() {
        return collisionResolver;
    }

    /**
     * @return the bullet physics system.
     */
    public BulletSystem getBulletSystem() {
        return bulletSystem;
    }

    /**
     * @return the bounds updating system.
     */
    public BoundsSystem getBoundsSystem() {
        return boundsSystem;
    }

    /**
     * @return the grid collider.
     */
    public GridColliderSystem getGridColliderSystem() {
        return gridColliderSystem;
    }

    @Override
    public void destroy() {
        for (Subsystem subsystem : subsystems) {
            subsystem.destroy();
        }
    }

    /**
     * Retrieves a system given the class.
     * @param cls the class of the system.
     * @param <T> the type of subsystem.
     * @return the first system matching the class or null if none found.
     */
    private <T extends Subsystem> T getSystem(Class<T> cls) {
        for (Subsystem subsystem : subsystems) {
            if (subsystem.getClass().equals(cls)) {
                return cls.cast(subsystem);
            }
        }
        return null;
    }
}
