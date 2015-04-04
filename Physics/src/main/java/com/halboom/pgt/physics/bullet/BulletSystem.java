package com.halboom.pgt.physics.bullet;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.bullet.components.BulletComponent;
import com.halboom.pgt.physics.components.PhysicsStateComponent;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.halboom.pgt.physics.simple.components.CollisionComponent;
import com.halboom.pgt.physics.simple.components.MovementComponent;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.PhysicsTickListener;
import com.jme3.bullet.collision.PhysicsCollisionEvent;
import com.jme3.bullet.collision.PhysicsCollisionListener;
import com.jme3.bullet.collision.PhysicsCollisionObject;
import com.jme3.bullet.collision.PhysicsRayTestResult;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.debug.BulletDebugAppState;
import com.jme3.bullet.objects.PhysicsGhostObject;
import com.jme3.bullet.objects.PhysicsRigidBody;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/7/13
 * Time: 4:02 PM
 */
public class BulletSystem implements Subsystem, PhysicsTickListener, PhysicsCollisionListener {
    /**
     * Default world sizes.
     */
    private static final Vector3f DEFAULT_MIN_SIZE = new Vector3f(-10000f, -10000f, -10000f),
    DEFAULT_MAX_SIZE = new Vector3f(10000f, 10000f, 10000f);

    /**
     * Groups for physics objects.
     */
    private static final int GROUP_STATIC = PhysicsCollisionObject.COLLISION_GROUP_01,
                             GROUP_DYNAMIC = PhysicsCollisionObject.COLLISION_GROUP_02;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Physics space to use.
     */
    private PhysicsSpace physicsSpace;

    /**
     * Entity to physics object map.
     */
    private Map<Entity, PhysicsCollisionObject> entityPhysicsMap = new HashMap<Entity, PhysicsCollisionObject>();

    /**
     * Debug app state.
     */
    BulletDebugAppState bulletDebugAppState;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public BulletSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;

        physicsSpace = new PhysicsSpace(DEFAULT_MIN_SIZE, DEFAULT_MAX_SIZE, PhysicsSpace.BroadphaseType.DBVT);
        physicsSpace.setGravity(new Vector3f());
        physicsSpace.addTickListener(this);
        physicsSpace.addCollisionListener(this);
    }

    /**
     * Enables debugging.
     * @param appStateManager state manager to attach to.
     */
    public void enableDebug(AppStateManager appStateManager) {
        bulletDebugAppState = new BulletDebugAppState(physicsSpace);
        appStateManager.attach(bulletDebugAppState);
    }

    /**
     * Disables debugging.
     * @param appStateManager the state manager to detach from.
     */
    public void disableDebug(AppStateManager appStateManager) {
        appStateManager.detach(bulletDebugAppState);
    }

    /**
     * Adds a static object to the system and automatically generates the collision shape.
     * @param object the object to add.
     */
    public void addStaticObject(Node object) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(object);
        RigidBodyControl body = new RigidBodyControl(shape, 0);
        body.setCollisionGroup(GROUP_STATIC);
        body.setCollideWithGroups(GROUP_DYNAMIC);
        object.addControl(body);
        physicsSpace.add(object);
    }

    /**
     * Removes a static object from the system.
     * @param object the object to remove.
     */
    public void removeStaticObject(Spatial object) {
        physicsSpace.remove(object);
    }

    /**
     * Creates a collision shape given the entity.
     * @param entity the entity to check for the collision shape of.
     * @return the collision shape of the entity.
     */
    private CollisionShape createCollisionShape(Entity entity) {
        AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
        if (aabbComponent == null) {
            return null;
        }
        CapsuleCollisionShape capsuleCollisionShape = new CapsuleCollisionShape(
                (aabbComponent.localExtentX + aabbComponent.localExtentZ) / 2,
                aabbComponent.localExtentY);
        return capsuleCollisionShape;
    }

    /**
     * Adds the physics object into the world.
     * @param entity the entity to get the components from.
     */
    private void addPhysicsObject(Entity entity) {
        removePhysicsObject(entity);
        // Add the physics object if a shape is found.
        CollisionShape shape = createCollisionShape(entity);
        if (shape != null) {
            PhysicsCollisionObject physicsCollisionObject;
            BulletComponent bulletComponent = entitySystem.getComponent(entity, BulletComponent.class);
            if (bulletComponent.isGhost) {
                physicsCollisionObject = new PhysicsGhostObject(shape);
            } else {
                physicsCollisionObject = new PhysicsRigidBody(shape, bulletComponent.mass);
                PhysicsRigidBody body = (PhysicsRigidBody) physicsCollisionObject;
                body.setFriction(bulletComponent.friction);
                body.setRestitution(bulletComponent.restitution);
                body.setKinematic(false);
                body.setAngularFactor(0);
                body.setLinearDamping(0);
            }
            physicsCollisionObject.setUserObject(entity);
            physicsCollisionObject.setCollisionGroup(GROUP_DYNAMIC);
            physicsCollisionObject.setCollideWithGroups(GROUP_STATIC);
            updatePhysicalObject(Float.MAX_VALUE, entity);
            physicsSpace.addCollisionObject(physicsCollisionObject);
            entityPhysicsMap.put(entity, physicsCollisionObject);
        }
    }

    /**
     * Removes a physics object given the entity.
     * @param entity the entity to remove.
     */
    private void removePhysicsObject(Entity entity) {
        // Remove an entity that has already been added and add it again.
        if (entityPhysicsMap.containsKey(entity)) {
            physicsSpace.removeCollisionObject(entityPhysicsMap.get(entity));
            entityPhysicsMap.remove(entity);
        }
    }

    /**
     * Updates the physical object.
     * @param tpf the time that passed since the last frame.
     * @param entity the entity to update from.
     */
    private void updatePhysicalObject(float tpf, Entity entity) {
        PhysicsCollisionObject object = entityPhysicsMap.get(entity);
        if (object != null) {
            // Set the starting transform.
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (object instanceof PhysicsGhostObject) {
                PhysicsGhostObject body = (PhysicsGhostObject) object;
                if (transformComponent != null) {
                    body.setPhysicsLocation(new Vector3f(
                            transformComponent.positionX,
                            transformComponent.positionY,
                            transformComponent.positionZ));
                    body.setPhysicsRotation(new Quaternion(
                            transformComponent.rotationX,
                            transformComponent.rotationY,
                            transformComponent.rotationZ,
                            transformComponent.rotationW));
                }
            } else {
                PhysicsRigidBody body = (PhysicsRigidBody) object;
                if (transformComponent != null) {
                    body.setPhysicsLocation(new Vector3f(
                            transformComponent.positionX,
                            transformComponent.positionY,
                            transformComponent.positionZ));
                    body.setPhysicsRotation(new Quaternion(
                            transformComponent.rotationX,
                            transformComponent.rotationY,
                            transformComponent.rotationZ,
                            transformComponent.rotationW));
                    // Set the speed to equal the movement after one tick.
                    MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
                    if (movementComponent != null) {
                        body.setLinearVelocity(new Vector3f(movementComponent.moveX / tpf,
                                movementComponent.moveY / tpf,
                                movementComponent.moveZ / tpf));
                    }
                    body.setAngularVelocity(Vector3f.ZERO);
                }
            }
        }
    }

    /**
     * Updates the move amount for an entity.
     * @param entity the entity to update the move amount for.
     */
    private void updateMoveAmount(Entity entity) {
        TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
        if (transformComponent == null) {
            return;
        }
        MovementComponent movementComponent = entitySystem.getComponent(entity, MovementComponent.class);
        if (movementComponent == null) {
            return;
        }
        PhysicsCollisionObject physicsCollisionObject = entityPhysicsMap.get(entity);
        if (physicsCollisionObject == null || !(physicsCollisionObject instanceof PhysicsRigidBody)) {
            return;
        }
        PhysicsRigidBody body = (PhysicsRigidBody) physicsCollisionObject;
        Vector3f moveAmount = new Vector3f();
        moveAmount.x = body.getPhysicsLocation().x - transformComponent.positionX;
        moveAmount.y = body.getPhysicsLocation().y - transformComponent.positionY;
        moveAmount.z = body.getPhysicsLocation().z - transformComponent.positionZ;

        // Check if the floor is right underneath the object.
        if (physicsCollisionObject.getCollisionShape() instanceof CapsuleCollisionShape) {
            CapsuleCollisionShape shape = (CapsuleCollisionShape) physicsCollisionObject.getCollisionShape();
            List<PhysicsRayTestResult> results = physicsSpace.rayTest(
                    body.getPhysicsLocation().subtract(0, shape.getHeight() + 0.01f, 0),
                    body.getPhysicsLocation().subtract(0, shape.getHeight() + 0.1f, 0));
            for (PhysicsRayTestResult result : results) {
                if (result.getCollisionObject() != physicsCollisionObject) {
                    PhysicsStateComponent physicsStateComponent = entitySystem.getComponent(entity, PhysicsStateComponent.class);
                    if (physicsStateComponent != null) {
                        physicsStateComponent.isGroundedBullet = true;
                    }
                    CollisionComponent collisionComponent = entitySystem.getComponent(entity, CollisionComponent.class);
                    if (collisionComponent != null) {
                        collisionComponent.isOnFloor = true;
                    }
                    break;
                }
            }
        }

        movementComponent.moveX = moveAmount.x;
        movementComponent.moveY = moveAmount.y;
        movementComponent.moveZ = moveAmount.z;
    }

    @Override
    public void collision(PhysicsCollisionEvent physicsCollisionEvent) {
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(BulletComponent.class);
        for (Entity entity : set.getAddedEntities()) {
            addPhysicsObject(entity);
        }

        for (Entity entity : set.getChangedEntities()) {
            addPhysicsObject(entity);
        }

        for (Entity entity : set.getRemovedEntities()) {
            removePhysicsObject(entity);
        }

        // Update all the entities.
        for (Entity entity : set.getEntities()) {
            updatePhysicalObject(tpf, entity);
        }

        physicsSpace.update(tpf);
        physicsSpace.distributeEvents();

        // Get the amount moved for the entities.
        for (Entity entity : set.getEntities()) {
            updateMoveAmount(entity);
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void prePhysicsTick(PhysicsSpace physicsSpace, float v) {
    }

    @Override
    public void physicsTick(PhysicsSpace physicsSpace, float v) {
    }

    /**
     * @param accuracy sets the accuracy with higher being slower but reduces glitches.
     */
    public void setAccuracy(float accuracy) {
        physicsSpace.setAccuracy(accuracy);
    }

    @Override
    public void destroy() {
        physicsSpace.removeTickListener(this);
        physicsSpace.destroy();
    }
}
