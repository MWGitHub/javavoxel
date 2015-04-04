package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/28/13
 * Time: 11:16 AM
 * Represents the inventory of an entity.
 */
public class InventoryComponent implements Component {
    /**
     * Size of the inventory.
     */
    public int size = 1;

    /**
     * IDs of items in the inventory.
     */
    public List<Long> itemIDs = new LinkedList<Long>();


    @Override
    public Component copy() {
        InventoryComponent output = new InventoryComponent();
        output.size = size;
        output.itemIDs.addAll(itemIDs);

        return output;
    }
}
