package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.ModelComponent;
import com.halboom.pgt.entityspatial.SpatialSystem;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.submu.pug.data.Data;
import com.submu.pug.data.ModelData;
import com.submu.pug.game.objects.components.ActionComponent;
import com.submu.pug.game.objects.components.HeadComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/17/13
 * Time: 3:21 PM
 * System for handling the head of an entity.
 * This system is somewhat similar to the spatial system only more specialized.
 * A head without a parent spatial will not be created.
 */
public class HeadSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Spatial system to retrieve attachment spatials.
     */
    private SpatialSystem spatialSystem;

    /**
     * Asset manager to load resources fom.
     */
    private AssetManager assetManager;

    /**
     * Map for entities and heads.
     */
    private Map<Entity, Spatial> entityHeadMap = new HashMap<Entity, Spatial>();

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param spatialSystem the spatial system to use.
     * @param assetManager the asset manager to load from.
     */
    public HeadSystem(EntitySystem entitySystem, SpatialSystem spatialSystem, AssetManager assetManager) {
        this.entitySystem = entitySystem;
        this.spatialSystem = spatialSystem;
        this.assetManager = assetManager;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(HeadComponent.class);
        // Create a head model for each valid spatial.
        for (Entity entity : set.getAddedEntities()) {
            Spatial spatial = spatialSystem.getSpatial(entity);
            if (spatial != null && spatial instanceof Node) {
                Node node = (Node) spatial;
                HeadComponent headComponent = entitySystem.getComponent(entity, HeadComponent.class);
                Spatial model = assetManager.loadModel(headComponent.model);
                Vector3f originalScale = new Vector3f(model.getLocalScale());
                entityHeadMap.put(entity, model);
                node.attachChild(model);

                // Set the scale so that it does not get rescaled by the model.
                TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
                float scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;
                if (transformComponent != null) {
                    scaleX = transformComponent.scaleX;
                    scaleY = transformComponent.scaleY;
                    scaleZ = transformComponent.scaleZ;
                }
                ModelComponent modelComponent = entitySystem.getComponent(entity, ModelComponent.class);
                if (modelComponent != null) {
                    Vector3f spatialScale = spatial.getLocalScale();
                    Vector3f resizedScale = spatialScale.subtract(modelComponent.scaleX * scaleX,
                            modelComponent.scaleY * scaleY, modelComponent.scaleZ * scaleZ);
                    resizedScale.x = resizedScale.x <= 0 ? 1 + resizedScale.x : resizedScale.x;
                    resizedScale.y = resizedScale.y <= 0 ? 1 + resizedScale.y : resizedScale.y;
                    resizedScale.z = resizedScale.z <= 0 ? 1 + resizedScale.z : resizedScale.z;
                    model.setLocalScale(originalScale.multLocal(1 / spatialScale.x * resizedScale.x * headComponent.scaleX,
                            1 / spatialScale.y * resizedScale.y * headComponent.scaleY,
                            1 / spatialScale.z * resizedScale.z * headComponent.scaleZ));
                }

                // Attach to the specified model data.
                ModelData modelData = Data.getInstance().getModelData();
                Node rootNode = (Node) node.getChild(modelData.dataNodes.attachRootNode);
                if (rootNode != null) {
                    // Calculate the offset of the head.
                    float offX = headComponent.offsetX * scaleX;
                    float offY = headComponent.offsetY * scaleY;
                    float offZ = headComponent.offsetZ * scaleZ;
                    Spatial head = rootNode.getChild(modelData.dataNodes.attachedNodes.head);
                    if (head != null) {
                        model.setLocalTranslation(head.getLocalTranslation().add(offX, offY, offZ));
                    }
                    // Offset to the base.
                    Node modelRoot = (Node) ((Node) model).getChild(modelData.dataNodes.attachRootNode);
                    if (modelRoot != null) {
                        Spatial base = modelRoot.getChild(modelData.dataNodes.attachedNodes.base);
                        if (base != null) {
                            model.setLocalTranslation(base.getLocalTranslation().add(offX, offY, offZ));
                        }
                    }
                }
            }
        }

        // Set the looking direction of the head.
        for (Entity entity : set.getEntities()) {
            ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
            if (actionComponent != null) {
                Spatial head = entityHeadMap.get(entity);
                if (head != null) {
                    head.lookAt(head.getWorldTranslation().add(actionComponent.headDirectionX,
                            actionComponent.headDirectionY,
                            actionComponent.headDirectionZ),
                            Vector3f.UNIT_Y);
                    // Show the body but hide the head if the body is hidden.
                    HeadComponent headComponent = entitySystem.getComponent(entity, HeadComponent.class);
                    if (headComponent.isBodyShown) {
                        Spatial body = spatialSystem.getSpatial(entity);
                        if (body != null) {
                            if (body.getCullHint().equals(Spatial.CullHint.Always)) {
                                body.setCullHint(Spatial.CullHint.Inherit);
                                head.setCullHint(Spatial.CullHint.Always);
                            } else {
                                head.setCullHint(Spatial.CullHint.Inherit);
                            }
                        }
                    }
                }
            }
        }

        // Removed unused spatials.
        for (Entity entity : set.getRemovedEntities()) {
            Spatial spatial = entityHeadMap.get(entity);
            spatial.removeFromParent();
            entityHeadMap.remove(entity);
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
