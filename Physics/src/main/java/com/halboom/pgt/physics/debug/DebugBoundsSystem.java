package com.halboom.pgt.physics.debug;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.physics.simple.components.AABBComponent;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/2/13
 * Time: 1:40 PM
 * System to display debug AABB and collision bounds.
 */
public class DebugBoundsSystem implements Subsystem {
    /**
     * Debug color the the bounds.
     */
    private static final ColorRGBA DEBUG_COLOR = new ColorRGBA(1f, 1f, 0.15f, 1f);

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Asset manager to load materials from.
     */
    private AssetManager assetManager;

    /**
     * Root node to attach to.
     */
    private Node root;

    /**
     * Entity to geometry relation map.
     */
    private Map<Entity, Geometry> entityGeometryMap = new HashMap<Entity, Geometry>();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param assetManager the asset manager to load materials from.
     * @param root the node to attach to.
     */
    public DebugBoundsSystem(EntitySystem entitySystem, AssetManager assetManager, Node root) {
        this.entitySystem = entitySystem;
        this.assetManager = assetManager;
        this.root = root;
    }

    /**
     * Removes bounds from the entity.
     * @param entity the entity to remove bounds from.
     */
    private void removeBounds(Entity entity) {
        Geometry geometry = entityGeometryMap.get(entity);
        if (geometry != null) {
            geometry.removeFromParent();
            entityGeometryMap.remove(entity);
        }
    }

    /**
     * Creates bounds for the given entity if possible.
     * @param entity the entity to create the bounds for.
     */
    private void createBounds(Entity entity) {
        if (entityGeometryMap.containsKey(entity)) {
            removeBounds(entity);
        }

        // Create the bounds and attach it if it exists.
        AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
        if (aabbComponent != null) {
            Geometry debugBounds = new Geometry("Debug Bounds");
            Box box = new Box(aabbComponent.localExtentX, aabbComponent.localExtentY, aabbComponent.localExtentZ);
            debugBounds.setMesh(box);
            Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.getAdditionalRenderState().setWireframe(true);
            material.setColor("Color", DEBUG_COLOR);
            debugBounds.setMaterial(material);
            root.attachChild(debugBounds);
            entityGeometryMap.put(entity, debugBounds);
        }
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(DebugBoundsComponent.class);
        for (Entity entity : set.getAddedEntities()) {
            createBounds(entity);
        }
        for (Entity entity : set.getChangedEntities()) {
            removeBounds(entity);
            createBounds(entity);
        }
        for (Entity entity : set.getRemovedEntities()) {
            removeBounds(entity);
        }
        // Update the bounds.
        for (Entity entity : set.getEntities()) {
            Geometry geometry = entityGeometryMap.get(entity);
            if (geometry != null) {
                AABBComponent aabbComponent = entitySystem.getComponent(entity, AABBComponent.class);
                if (aabbComponent != null) {
                    geometry.setLocalTranslation(aabbComponent.centerX, aabbComponent.centerY, aabbComponent.centerZ);
                    geometry.setLocalScale(aabbComponent.worldExtentX / aabbComponent.localExtentX,
                            aabbComponent.worldExtentY / aabbComponent.localExtentY,
                            aabbComponent.worldExtentZ / aabbComponent.localExtentZ);
                }
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
        EntitySet set = entitySystem.getEntities(DebugBoundsComponent.class);
        for (Entity entity : set.getEntities()) {
            removeBounds(entity);
        }
    }
}
