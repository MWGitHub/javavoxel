package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/28/13
 * Time: 11:43 AM
 * Having this component allows the entity to be picked up and equipped.
 */
public class ItemComponent implements Component {
    /**
     * Name of the item to display.
     */
    public String name;

    /**
     * Description of the item.
     */
    public String description;

    /**
     * Abilities of the item.
     */
    public List<String> abilities = new LinkedList<String>();

    /**
     * Model to use for the item when on the floor.
     */
    public String model;

    /**
     * Icon to use for the item when in the inventory.
     */
    public String icon;

    /**
     * Entity that is holding the item.
     */
    public Entity holder;

    @Override
    public Component copy() {
        ItemComponent output = new ItemComponent();
        output.name = name;
        output.description = description;
        output.abilities.addAll(abilities);
        output.model = model;
        output.icon = icon;
        output.holder = holder;

        return output;
    }
}
