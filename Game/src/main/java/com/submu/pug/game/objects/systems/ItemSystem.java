package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.SpatialComponent;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.InventoryComponent;
import com.submu.pug.game.objects.components.ItemComponent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/28/13
 * Time: 11:46 AM
 * Handles the display and equipping of items and the inventory.
 */
public class ItemSystem implements Subsystem {
    /**
     * Range for picking up items.
     */
    private static final float PICKUP_RANGE = 1;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Callbacks for items.
     */
    private Callbacks callbacks;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public ItemSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    /**
     * Updates an item's display.
     * @param entity the entity to update the display of.
     */
    private void updateItemDisplay(Entity entity) {
        ItemComponent itemComponent = entitySystem.getComponent(entity, ItemComponent.class);
        if (itemComponent.holder != null) {
            entitySystem.removeComponent(entity, SpatialComponent.class);
        } else {
            SpatialComponent spatialComponent = new SpatialComponent();
            spatialComponent.model = itemComponent.model;
            entitySystem.setComponent(entity, spatialComponent);
        }
    }

    /**
     * Checks if any entities are within pickup range.
     */
    private void checkForPickUp() {
        // Find valid items that can be picked up.
        EntitySet items = entitySystem.getEntities(ItemComponent.class);
        Map<Entity, TransformComponent> validItems = new HashMap<Entity, TransformComponent>();
        for (Entity item : items.getEntities()) {
            ItemComponent itemComponent = entitySystem.getComponent(item, ItemComponent.class);
            if (itemComponent.holder != null) {
                continue;
            }
            TransformComponent transformComponent = entitySystem.getComponent(item, TransformComponent.class);
            if (transformComponent == null) {
                continue;
            }
            validItems.put(item, transformComponent);
        }
        // Check each entity for distance between items.
        EntitySet set = entitySystem.getEntities(InventoryComponent.class);
        for (Entity entity : set.getEntities()) {
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent == null) {
                continue;
            }
            for (Map.Entry<Entity, TransformComponent> item : validItems.entrySet()) {
                // Calculate the distance between the entity and the item.
                float dx = transformComponent.positionX - item.getValue().positionX;
                float dy = transformComponent.positionY - item.getValue().positionY;
                float dz = transformComponent.positionZ - item.getValue().positionZ;
                // Valid for pickup.
                if (dx * dx + dy * dy + dz * dz <= PICKUP_RANGE * PICKUP_RANGE) {
                    pickUpItem(entity, item.getKey());
                }
            }
        }
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(ItemComponent.class);
        // Add spatials to an item if it isn't in an inventory.
        for (Entity entity : set.getAddedEntities()) {
            updateItemDisplay(entity);
        }

        checkForPickUp();

        // Check spatials to add or remove based on the holder status.
        for (Entity entity : set.getChangedEntities()) {
            updateItemDisplay(entity);
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * Picks up an item.
     * @param holder the holder that is equipping the item.
     * @param item the item to equip.
     */
    public void pickUpItem(Entity holder, Entity item) {
        if (holder == null || item == null) {
            return;
        }
        // Only entities with inventories can hold items.
        InventoryComponent inventoryComponent = entitySystem.getComponent(holder, InventoryComponent.class);
        if (inventoryComponent == null) {
            return;
        }
        // Only items that aren't held can be equipped.
        ItemComponent itemComponent = entitySystem.getComponent(item, ItemComponent.class);
        if (itemComponent == null || itemComponent.holder != null) {
            return;
        }
        // Do not allow equipping of the same exact item.
        if (inventoryComponent.itemIDs.contains(item.getId())) {
            return;
        }
        // Do not allow full inventories.
        if (inventoryComponent.itemIDs.size() >= inventoryComponent.size) {
            return;
        }
        // Add the item.
        itemComponent.holder = holder;
        inventoryComponent.itemIDs.add(item.getId());

        entitySystem.setComponent(holder, inventoryComponent);
        entitySystem.setComponent(item, itemComponent);

        if (callbacks != null) {
            callbacks.onItemPickup(holder, item);
        }
    }

    /**
     * Drops an item.
     * @param holder the holder of the item.
     * @param item the item to take off.
     */
    public void dropItem(Entity holder, Entity item) {
        if (holder == null || item == null) {
            return;
        }
        // Only entities with inventories can take off items.
        InventoryComponent inventoryComponent = entitySystem.getComponent(holder, InventoryComponent.class);
        if (inventoryComponent == null) {
            return;
        }
        // Only items that are held can be taken off.
        ItemComponent itemComponent = entitySystem.getComponent(item, ItemComponent.class);
        if (itemComponent == null || itemComponent.holder == null) {
            return;
        }
        // Only items that the entity is holding can be taken off.
        if (!inventoryComponent.itemIDs.contains(item.getId())) {
            return;
        }

        inventoryComponent.itemIDs.remove(item.getId());
        itemComponent.holder = null;

        entitySystem.setComponent(holder, inventoryComponent);
        entitySystem.setComponent(item, itemComponent);

        if (callbacks != null) {
            callbacks.onItemDrop(holder, item);
        }
    }

    /**
     * @param callbacks the callbacks to use.
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void destroy() {
    }

    /**
     * Callbacks for the item system.
     */
    public interface Callbacks {
        /**
         * Runs when an entity picks up an item.
         * @param entity the entity that picked up an item.
         * @param item the item that is picked up.
         */
        void onItemPickup(Entity entity, Entity item);

        /**
         * Runs when an entity drops an item.
         * @param entity the entity that dropped an item.
         * @param item the item that is dropped.
         */
        void onItemDrop(Entity entity, Entity item);
    }
}
