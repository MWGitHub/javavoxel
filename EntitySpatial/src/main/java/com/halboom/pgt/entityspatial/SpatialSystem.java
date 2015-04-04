package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/22/13
 * Time: 11:21 AM
 * System to create Nodes and Geoms using an entity system.
 */
public class SpatialSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * AssetManager to load assets from.
     */
    private AssetManager assetManager;

    /**
     * Node to attach all spatials to.
     */
    private Node root;

    /**
     * Map for entities to spatials.
     */
    private Map<Entity, Spatial> entitySpatialMap = new HashMap<Entity, Spatial>();

    /**
     * Queued spatial changes for entities that have not been added yet.
     */
    private Map<Entity, Spatial> queuedSpatials = new HashMap<Entity, Spatial>();

    /**
     * Entities with the spatial component but with a removed parent.
     * Orphans are cleared once removal is complete.
     */
    private List<Entity> orphans = new LinkedList<Entity>();

    /**
     * Map for entities to animations.
     */
    private Map<Entity, SpatialAnimation> entityAnimationMap = new HashMap<Entity, SpatialAnimation>();

    /**
     * Quaternion used for rotation.
     */
    private Quaternion rotation = new Quaternion();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param assetManager the asset manager to load resources from.
     * @param root the node to use as a root to attach all orphan spatials to.
     */
    public SpatialSystem(EntitySystem entitySystem, AssetManager assetManager, Node root) {
        this.entitySystem = entitySystem;
        this.assetManager = assetManager;
        this.root = root;
    }

    /**
     * Transforms the given spatial.
     * @param spatial the spatial to transform.
     * @param transformComponent the component to use to transform the spatial.
     * @param modelComponent the component used for the model's original data if applicable.
     */
    private void transformSpatial(Spatial spatial, TransformComponent transformComponent, ModelComponent modelComponent) {
        if (spatial != null && transformComponent != null) {
            // Update the position.
            spatial.setLocalTranslation(transformComponent.positionX, transformComponent.positionY, transformComponent.positionZ);
            // Update the rotation.
            rotation.set(transformComponent.rotationX, transformComponent.rotationY, transformComponent.rotationZ,
                    transformComponent.rotationW);
            spatial.setLocalRotation(rotation);
            // Update the scale.
            if (modelComponent != null) {
                spatial.setLocalScale(transformComponent.scaleX * modelComponent.scaleX,
                        transformComponent.scaleY * modelComponent.scaleY,
                        transformComponent.scaleZ * modelComponent.scaleZ);
            } else {
                spatial.setLocalScale(transformComponent.scaleX, transformComponent.scaleY, transformComponent.scaleZ);
            }
        }
    }

    /**
     * Updates the spatials.
     */
    private void updateSpatials() {
        EntitySet spatials = entitySystem.getEntities(SpatialComponent.class);

        for (Entity entity : spatials.getAddedEntities()) {
            addEntity(entity);
        }

        // Remove and re-add changed entities.
        for (Entity entity : spatials.getChangedEntities()) {
            removeEntity(entity);
            addEntity(entity);
        }

        // Remove spatials along with their children.
        for (Entity entity : spatials.getRemovedEntities()) {
            removeEntity(entity);
        }

        // Remove orphans with parents that have been removed.
        // The orphans may not be in the removed entities list as the entity system may be flushed before the update.
        for (Entity entity : orphans) {
            // Removed entities will no longer have the spatial component.
            entitySystem.removeComponent(entity, SpatialComponent.class);
        }

        // Update all spatials.
        for (Entity entity : spatials.getEntities()) {
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent != null) {
                Spatial spatial = getSpatial(entity);
                if (spatial != null) {
                    ModelComponent modelComponent = entitySystem.getComponent(entity, ModelComponent.class);
                    transformSpatial(spatial, transformComponent, modelComponent);
                }
            }
        }
    }

    /**
     * Plays an animation if needed.
     * @param spatial the spatial to play the animation on.
     * @param animationComponent the animation component.
     */
    private void playAnimation(Spatial spatial, AnimationComponent animationComponent) {
        int index = 0;
        for (AnimationComponent.AnimationElement element : animationComponent.animations) {
            if (element.playAnimation != null && !element.playAnimation.equals("")) {
                AnimControl control = spatial.getControl(AnimControl.class);
                if (control != null) {
                    // Create channels until the given index is reached.
                    while (control.getNumChannels() <= index) {
                        control.createChannel();
                    }
                    AnimChannel channel = control.getChannel(index);
                    channel.setAnim(element.playAnimation, element.blendTime);
                    channel.setSpeed(element.speed);
                    channel.setLoopMode(LoopMode.values()[element.loopMode]);
                    element.currentAnimation = element.playAnimation;
                    element.playAnimation = null;
                }
            }
            index++;
        }
    }

    /**
     * Updates the animations.
     */
    private void updateAnimations() {
        // Update animations.
        EntitySet spatials = entitySystem.getEntities(AnimationComponent.class);
        // Add new animations
        for (Entity entity : spatials.getAddedEntities()) {
            Spatial spatial = getSpatial(entity);
            if (spatial != null) {
                // Put the new animation into the map.
                AnimControl control = spatial.getControl(AnimControl.class);
                if (control != null) {
                    AnimationComponent animationComponent = entitySystem.getComponent(entity, AnimationComponent.class);
                    SpatialAnimation spatialAnimation = new SpatialAnimation(animationComponent);
                    control.addListener(spatialAnimation);
                    entityAnimationMap.put(entity, spatialAnimation);

                    // Play the animation if one is set.
                    playAnimation(spatial, animationComponent);
                }
            }
        }

        // Update changed animations.
        for (Entity entity : spatials.getChangedEntities()) {
            Spatial spatial = entitySpatialMap.get(entity);
            playAnimation(spatial, entitySystem.getComponent(entity, AnimationComponent.class));
        }

        // Remove animations.
        for (Entity entity : spatials.getRemovedEntities()) {
            Spatial spatial = getSpatial(entity);
            if (spatial != null) {
                AnimControl control = spatial.getControl(AnimControl.class);
                if (control != null) {
                    control.removeListener(entityAnimationMap.get(entity));
                    control.clearChannels();
                }
                entityAnimationMap.remove(entity);
            }
        }
    }

    /**
     * Updates all the spatials to match the transforms.
     * @param tpf the time per frame.
     */
    @Override
    public void update(float tpf) {
        updateSpatials();
        updateAnimations();

        queuedSpatials.clear();
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * Creates the spatial depending on the given components.
     * @param entity the entity with the components.
     * @return the created spatial.
     */
    private Spatial createSpatial(Entity entity) {
        Spatial spatial = null;
        // Create a node if it contains the node component or a geometry if it contains a geometry component.
        SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
        if (spatialComponent != null) {
            if (spatialComponent.model != null && !spatialComponent.model.equals("")) {
                spatial = assetManager.loadModel(spatialComponent.model);
                ModelComponent modelComponent = new ModelComponent();
                Vector3f originalScale = spatial.getLocalScale();
                modelComponent.scaleX = originalScale.x;
                modelComponent.scaleY = originalScale.y;
                modelComponent.scaleZ = originalScale.z;
                entitySystem.setComponent(entity, modelComponent);
            } else {
                spatial = new Node();
            }
        }

        return spatial;
    }

    /**
     * Adds the given entity to the scene.
     * @param entity the entity to add.
     */
    public void addEntity(Entity entity) {
        // Ignore if the entity already exists.
        if (!entitySpatialMap.containsKey(entity)) {
            Spatial spatial;
            // Set the spatial if one has already been set for it otherwise create a new one.
            if (queuedSpatials.containsKey(entity)) {
                spatial = queuedSpatials.get(entity);
            } else {
                spatial = createSpatial(entity);
            }
            // Set spatial properties if the entity is valid.
            if (spatial != null) {
                // Remove the spatial from existing parents and attach the spatial.
                spatial.removeFromParent();
                entitySpatialMap.put(entity, spatial);
                SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
                // Add the spatial to the parent if possible else add it to the root.
                if (spatialComponent.parent != null) {
                    Spatial parentSpatial = getSpatial(spatialComponent.parent);
                    if (parentSpatial instanceof Node) {
                        ((Node) parentSpatial).attachChild(spatial);
                    }
                } else {
                    root.attachChild(spatial);
                }
            }
        }
    }

    /**
     * Removes an entity from the system and from the scene.
     * @param entity the entity to remove.
     */
    private void removeEntity(Entity entity) {
        // Remove from the scene and spatial map.
        Spatial spatial = getSpatial(entity);
        spatial.removeFromParent();
        entitySpatialMap.remove(entity);
        // Remove animations for the entity.
        if (entityAnimationMap.containsKey(entity)) {
            AnimControl control = spatial.getControl(AnimControl.class);
            if (control != null) {
                control.removeListener(entityAnimationMap.get(entity));
            }
            entityAnimationMap.remove(entity);
        }

        // Remove children with the spatial as the parent from the map too.
        EntitySet entitySet = entitySystem.getEntities(SpatialComponent.class);
        for (Entity checkedEntity : entitySet.getEntities()) {
            SpatialComponent spatialComponent = entitySystem.getComponent(checkedEntity, SpatialComponent.class);
            if (spatialComponent.parent != null && spatialComponent.parent.equals(entity)) {
                removeEntity(checkedEntity);
            }
        }
    }

    /**
     * Retrieves the entity attached to a spatial.
     * @param spatial the spatial attached to the entity.
     * @return the entity that is attached to the spatial.
     */
    public Entity getEntity(Spatial spatial) {
        for (Map.Entry<Entity, Spatial> entry : entitySpatialMap.entrySet()) {
            if (entry.getValue().equals(spatial)) {
                return entry.getKey();
            }
        }

        return null;
    }

    /**
     * Retrieves a spatial given an entity.
     * @param entity the entity the spatial is attache dto.
     * @return the spatial attached to the given entity.
     */
    public Spatial getSpatial(Entity entity) {
        return entitySpatialMap.get(entity);
    }

    /**
     * Sets a spatial for the given entity and removes the currently mapped one.
     * @param entity the entity to map to the spatial.
     * @param spatial the spatial to set.
     */
    public void setSpatial(Entity entity, Spatial spatial) {
        // Do not allow null spatials to be set.
        if (spatial == null) {
            return;
        }

        // Check if the entity spatial mapping already exists and put it in queue if it doesn't.
        if (!entitySpatialMap.containsKey(entity)) {
            queuedSpatials.put(entity, spatial);
        } else {
            // Check if the entity is valid.
            SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
            if (spatialComponent != null) {
                Spatial currentSpatial = entitySpatialMap.get(entity);
                currentSpatial.removeFromParent();
                entitySpatialMap.put(entity, spatial);
                // Add the new spatial to the parent.
                if (spatialComponent.parent != null) {
                    Spatial parent = getSpatial(spatialComponent.parent);
                    if (parent instanceof Node) {
                        ((Node) parent).attachChild(spatial);
                    }
                } else {
                    root.attachChild(spatial);
                }
            }
        }
    }

    /**
     * Sets the model of an entity given the path.
     * @param entity the entity to change the model of.
     * @param path the path of the entity.
     */
    public void changeModel(Entity entity, String path) {
        if (!entitySpatialMap.containsKey(entity)) {
            return;
        }

        SpatialComponent spatialComponent = entitySystem.getComponent(entity, SpatialComponent.class);
        if (spatialComponent != null) {
            spatialComponent.model = path;
            removeEntity(entity);
            addEntity(entity);
        }
    }

    @Override
    public void destroy() {
    }
}
