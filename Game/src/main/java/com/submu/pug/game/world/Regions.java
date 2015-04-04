package com.submu.pug.game.world;

import com.halboom.pgt.debug.DebugGlobals;
import com.exploringlines.entitysystem.Entity;
import com.halboom.pgt.physics.simple.BoundsSystem;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.halboom.pgt.physics.simple.shapes.BoundsBox;
import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/20/13
 * Time: 5:06 PM
 * Manages the properties of regions and stores objects within each one from the previous update.
 */
public class Regions {
    /**
     * Debug color the the bounds.
     */
    private static final ColorRGBA DEBUG_COLOR = new ColorRGBA(1f, 1f, 0.15f, 1f);

    /**
     * Bounds system to use.
     */
    private BoundsSystem boundsSystem;

    /**
     * Bounds and entity map to track entering and leaving.
     */
    private Map<RegionInfo, List<Entity>> regionMap = new HashMap<RegionInfo, List<Entity>>();

    /**
     * Callbacks for the region events.
     */
    private Callbacks callbacks;

    /**
     * True to enable debug display.
     */
    private boolean isDebugEnabled = false;

    /**
     * Debug spatials.
     */
    private List<Spatial> debugBounds = new LinkedList<Spatial>();

    /**
     * Initializes the class.
     * @param boundsSystem the bounds system to use for retrieving bounds.
     */
    public Regions(BoundsSystem boundsSystem) {
        this.boundsSystem = boundsSystem;
    }

    /**
     * Add the region to the region list.
     * @param name the name of the region.
     * @param center the center of the region.
     * @param extents the half extents of each side.
     */
    public void addRegion(String name, Vector3f center, Vector3f extents) {
        Bounds bounds = new BoundsBox(center, extents.x, extents.y, extents.z);
        bounds.setGroups(Long.MAX_VALUE);
        bounds.setTargets(Long.MAX_VALUE);
        RegionInfo regionInfo = new RegionInfo(name, bounds);
        regionMap.put(regionInfo, new LinkedList<Entity>());

        if (isDebugEnabled) {
            createDebugBound(bounds);
        }
    }

    /**
     * Updates the regions.
     */
    public void update() {
        if (callbacks != null) {
            for (Map.Entry<RegionInfo, List<Entity>> entry : regionMap.entrySet()) {
                String name = entry.getKey().name;
                Bounds bounds = entry.getKey().bounds;
                List<Entity> collided = boundsSystem.getEntitiesInBounds(bounds, null);
                List<Entity> previousColliders = entry.getValue();
                // Trigger callbacks on entities within the region.
                for (Entity collidee : collided) {
                    callbacks.onEntityInside(name, bounds, collidee);
                    // Trigger callbacks on newly entered entities.
                    if (!previousColliders.remove(collidee)) {
                        callbacks.onEntityEnters(name, bounds, collidee);
                    }
                }
                // Trigger callbacks on entities that are no longer in the region.
                for (Entity collidee : previousColliders) {
                    callbacks.onEntityLeaves(name, bounds, collidee);
                }
                // Add the new entities into the list.
                previousColliders.clear();
                for (Entity collidee : collided) {
                    previousColliders.add(collidee);
                }
            }
        }
    }

    /**
     * Creates a debug bound.
     * @param bound the bound to create the debug bounds of.
     */
    private void createDebugBound(Bounds bound) {
        AssetManager assetManager = DebugGlobals.getInstance().getAssetManager();
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", DEBUG_COLOR);

        // Create the bounds and attach it if it exists.
        if (bound.getBounds() instanceof BoundingBox) {
            BoundingBox boundingBox = (BoundingBox) bound.getBounds();
            Geometry boundsGeometry = new Geometry("Debug Bounds");
            Box box = new Box(boundingBox.getXExtent(), boundingBox.getYExtent(), boundingBox.getZExtent());
            boundsGeometry.setMesh(box);
            boundsGeometry.setMaterial(material);
            DebugGlobals.getInstance().getRootNode().attachChild(boundsGeometry);
            boundsGeometry.setLocalTranslation(boundingBox.getCenter());
        }
    }

    /**
     * Retrieves a region by name.
     * @param name the name of the region.
     * @return the region matching the name or null if none found.
     */
    public Bounds getRegion(String name) {
        for (RegionInfo region : regionMap.keySet()) {
            if (region.name.equals(name)) {
                return region.bounds;
            }
        }
        return null;
    }

    /**
     * Enables the debug bounds.
     */
    public void enableDebug() {
        disableDebug();
        isDebugEnabled = true;
        for (RegionInfo regionInfo : regionMap.keySet()) {
            createDebugBound(regionInfo.bounds);
        }
    }

    /**
     * Disables the debug bounds.
     */
    public void disableDebug() {
        isDebugEnabled = false;
        for (Spatial spatial : debugBounds) {
            spatial.removeFromParent();
        }
        debugBounds.clear();
    }

    /**
     * @param callbacks the callbacks to set.
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * Destroys the system.
     */
    public void destroy() {
        disableDebug();
    }


    /**
     * Info for a region.
     */
    private class RegionInfo {
        /**
         * Name of the region.
         */
        private String name;

        /**
         * Bounds of the region.
         */
        private Bounds bounds;

        /**
         * Creates the region info.
         * @param name the name of the region.
         * @param bounds the bounds.
         */
        private RegionInfo(String name, Bounds bounds) {
            this.name = name;
            this.bounds = bounds;
        }
    }

    /**
     * Callbacks for the region events.
     */
    public interface Callbacks {
        /**
         * Runs when an entity enters a region.
         * @param name the name of the region.
         * @param bounds the bounds of the region.
         * @param entity the entity that entered the region.
         */
        void onEntityEnters(String name, Bounds bounds, Entity entity);

        /**
         * Runs when an entity is within a region.
         * @param name the name of the region.
         * @param bounds the bounds of the region.
         * @param entity the entity that is within the region.
         */
        void onEntityInside(String name, Bounds bounds, Entity entity);

        /**
         * Runs when an entity leaves a region.
         * @param name the name of the region.
         * @param bounds the bounds of the region.
         * @param entity the entity that leaves the region.
         */
        void onEntityLeaves(String name, Bounds bounds, Entity entity);
    }
}
