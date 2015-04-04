package com.submu.pug.game.objects;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.game.objects.components.ItemComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/28/13
 * Time: 11:26 AM
 * Handles the items in the game.
 */
public class ItemFactory {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Items of the map.
     */
    private Map<String, ObjectsData.ItemData> items = new HashMap<String, ObjectsData.ItemData>();

    /**
     * Initializes the factory and loads the item data.
     * @param entitySystem the entity system to use.
     * @param objectsData the object data to use to get the items.
     */
    public ItemFactory(EntitySystem entitySystem, ObjectsData objectsData) {
        this.entitySystem = entitySystem;

        items.putAll(objectsData.items);
    }

    /**
     * Creates an item given a key and name.
     * @param key the key of the item.
     * @param name the unique name of the item.
     * @return the item.
     */
    public Entity createItem(String key, String name) {
        ObjectsData.ItemData data = items.get(key);
        if (data == null) {
            return null;
        }

        Entity entity = entitySystem.createEntity(name);
        ItemComponent itemComponent = new ItemComponent();
        itemComponent.name = data.name;
        itemComponent.description = data.description;
        itemComponent.icon = data.icon;
        itemComponent.model = data.model;
        itemComponent.abilities.addAll(data.abilities);
        entitySystem.setComponent(entity, itemComponent);
        entitySystem.setComponent(entity, new TransformComponent());

        return entity;
    }

    /**
     * Creates an item given a key.
     * @param key the key of the item.
     * @return the item.
     */
    public Entity createItem(String key) {
        return createItem(key, null);
    }
}
