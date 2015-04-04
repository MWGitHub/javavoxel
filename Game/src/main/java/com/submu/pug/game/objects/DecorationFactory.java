package com.submu.pug.game.objects;

import com.halboom.pgt.entityspatial.SpatialComponent;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.submu.pug.data.ObjectsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/28/13
 * Time: 11:31 AM
 * Handles the creation of decorations.
 */
public class DecorationFactory {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Decorations data.
     */
    private Map<String, String> decorations = new HashMap<String, String>();

    /**
     * Initializes the factory and loads the decorations.
     * @param entitySystem the entity system to use.
     * @param objectsData the object data to load the decorations from.
     */
    public DecorationFactory(EntitySystem entitySystem, ObjectsData objectsData) {
        this.entitySystem = entitySystem;
        decorations.putAll(objectsData.decorations);
    }

    /**
     * Creates a decoration matching the key.
     * @param key the key of the decoration to load.
     * @return the decoration.
     */
    public Entity createDecoration(String key) {
        Entity entity = entitySystem.createEntity();
        entitySystem.setComponent(entity, new TransformComponent());
        SpatialComponent spatialComponent = new SpatialComponent();
        spatialComponent.model = decorations.get(key);
        entitySystem.setComponent(entity, spatialComponent);

        return entity;
    }
}
