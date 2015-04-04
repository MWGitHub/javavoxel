package com.submu.pug.data;

import com.exploringlines.entitysystem.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/21/13
 * Time: 4:55 PM
 * Data for object properties, abilities, items, things, upgrades, and buffs.
 */
public class ObjectsData {
    /**
     * Default actor to use when none provided.
     */
    public String defaultActor;

    /**
     * Map of the actor data.
     */
    public Map<String, ActorData> actors;

    /**
     * Ability map for the data.
     */
    public Map<String, AbilityData> abilities;

    /**
     * Map for the item data.
     */
    public Map<String, ItemData> items;

    /**
     * Decorations are just models in the world with collision as model bounds.
     */
    public Map<String, String> decorations;

    /**
     * Actors are the main interacting objects in the game.
     */
    public static class ActorData {
        /**
         * Components of the actor.
         */
        public Map<String, Map<String, Object>> components;

        /**
         * Parsed components for the actor.
         */
        public List<Component> parsedComponents = new ArrayList<Component>();
    }

    /**
     * Abilities are the data to handle ability cooldowns and costs.
     */
    public static class AbilityData {
        /**
         * Name of the ability for use when displayed.
         */
        public String name;

        /**
         * True to show the ability in the GUI.
         */
        public boolean isShown = true;

        /**
         * Description of the ability.
         */
        public String description = "None";

        /**
         * Cooldown in milliseconds.
         */
        public float cooldown = 0;

        /**
         * Time it takes to cast the ability in seconds.
         */
        public float castTime = 0;

        /**
         * Cost of purchasing the ability.
         */
        public float cost = 0;

        /**
         * Amount of energy the ability costs to use.
         */
        public float energy = 0;

        /**
         * Icon of the ability in the ability bar.
         */
        public String icon;

        /**
         * True to set the ability as an analog ability.
         */
        public boolean isAnalog = false;

        /**
         * True to set the ability as a channeled ability.
         */
        public boolean isChanneled = false;

        /**
         * Attachment point for the ability spawn.
         */
        public String attachPoint;

        /**
         * Offset to cast the ability from.
         */
        public float offsetX = 0, offsetY = 0, offsetZ = 0;

        /**
         * Upgrades of the ability.
         */
        public Map<String, UpgradeData> upgrades;

        /**
         * User data for the ability.
         * Only strings are allowed to keep data from getting too complicated.
         */
        public Map<String, String> userData;

        /**
         * Holds data for the ability upgrades.
         */
        public static class UpgradeData {
            /**
             * Name of the upgrade for use when displaying it.
             */
            public String name;

            /**
             * Description of the upgrade.
             */
            public String description = "None";

            /**
             * Cost to purchase the upgrade.
             */
            public float[] costs;
        }
    }

    /**
     * Data for the items.
     */
    public static class ItemData {
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
    }
}
