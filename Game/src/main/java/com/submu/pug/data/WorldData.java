package com.submu.pug.data;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/21/13
 * Time: 4:54 PM
 * Data for object placements in the world excluding trigger regions.
 */
public class WorldData {
    /**
     * Actors in the world.
     */
    public List<ActorPlacement> actors;

    /**
     * Decorations in the world.
     */
    public List<ThingPlacement> decorations;

    /**
     * Items in the world.
     */
    public List<ThingPlacement> items;

    /**
     * Regions in the world.
     */
    public List<Region> regions;

    /**
     * Represents a location in the world.
     */
    public static class Location {
        public float x = 0f;
        public float y = 0f;
        public float z = 0f;
    }

    /**
     * Represents an actor.
     */
    public static class ActorPlacement {
        /**
         * Type of actor that corresponds to data.
         */
        public String type;

        /**
         * Name of the actor.
         */
        public String name;

        /**
         * Location to place the actor.
         */
        public Location location;

        /**
         * Index of the owning player.
         */
        public int owner;
    }

    /**
     * Reprsents a region for sensing objects.
     */
    public static class Region {
        /**
         * Name of the region.
         */
        public String name;

        /**
         * Center location of the region.
         */
        public Location location;

        /**
         * Half extents of the region.
         */
        public float extentX, extentY, extentZ;
    }

    /**
     * Represents a decoration or an item.
     */
    public static class ThingPlacement {
        /**
         * Name of the item entity.
         */
        public String name;

        /**
         * Type of decoration.
         */
        public String type;

        /**
         * Location to place the decoration.
         */
        public Location location;
    }
}
