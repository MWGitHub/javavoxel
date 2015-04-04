package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/14/13
 * Time: 12:01 PM
 * Data for map specific configurations but not the map itself.
 */
public class MapData {
    /**
     * Size of a tile relative to the other objects in world units.
     */
    public float tileScale = 1f;

    /**
     * Dimensions of the terrain.
     */
    public Dimensions dimensions = new Dimensions();

    /**
     * Path for the sky box texture.
     */
    public Skybox skybox = new Skybox();

    /**
     * Fog parameters.
     */
    public Fog fog = new Fog();

    /**
     * Physics parameters.
     */
    public Physics physics = new Physics();

    /**
     * True to enable shadows on the whole map.
     */
    public boolean areShadowsEnabled = false;

    /**
     * Sun to use for the world.
     */
    public Sun sun = new Sun();

    /**
     * Ambient light to use for the world.
     */
    public AmbientLight ambientLight = new AmbientLight();

    /**
     * Paths to load the map data files from.
     */
    public DataPaths dataPaths = new DataPaths();

    /**
     * Dimensions of the world with x going left to right, z going back to forward, and y going up and down.
     */
    public static class Dimensions {
        // X axis length of the world.
        public int xLength = 32;
        // Z axis length of the world.
        public int zLength = 32;
        // Y axis yLength of the world.
        public int yLength = 32;
    }

    /**
     * Skybox properties.
     */
    public static class Skybox {
        public String west = "Core/Textures/Sky/BackgroundDay.jpg";
        public String east = "Core/Textures/Sky/BackgroundDay.jpg";
        public String north = "Core/Textures/Sky/BackgroundDay.jpg";
        public String south = "Core/Textures/Sky/BackgroundDay.jpg";
        public String up = "Core/Textures/Sky/BackgroundDayTop.jpg";
        public String down = "Core/Textures/Sky/BackgroundDayTop.jpg";
    }

    /**
     * Fog parameters.
     */
    public static class Fog {
        public float red = 0.145f;
        public float green = 0.211f;
        public float blue = 0.329f;
        public float alpha = 1.0f;

        /**
         * Density of the fog
         */
        public float density = 1.0f;

        /**
         * Distance of the fog
         */
        public float distance = 6400f;
    }

    /**
     * Physics parameters for the map.
     */
    public static class Physics {
        /**
         * Gravity to apply to all objects.
         */
        public float gravity = 9.8f;
    }

    /**
     * Data for the world's sun.
     */
    public static class Sun {
        /**
         * Color for the sun's light.
         */
        public Color color = new Color();

        /**
         * Direction of the sun.
         */
        public Direction direction = new Direction();

        /**
         * Color for the sun's light.
         */
        public static class Color {
            /**
             * RGBA color values to set the light as.
             */
            public float red = 1f, green = 1f, blue = 1f, alpha = 1f;
        }

        /**
         * Direction of the light.
         */
        public static class Direction {
            /**
             * XYZ values of the direction.
             */
            public float x = 0f, y = 0f, z = 0f;
        }
    }

    /**
     * Data for the ambient light..
     */
    public static class AmbientLight {
        /**
         * Color for the ambient light.
         */
        public Color color = new Color();

        /**
         * Color for the ambient light.
         */
        public static class Color {
            /**
             * RGBA color values to set the light as.
             */
            public float red = 0.1f, green = 0.1f, blue = 0.1f, alpha = 1f;
        }
    }

    /**
     * Paths for the map data
     */
    public static class DataPaths {
        /**
         * Map Data path.
         */
        public String mapData = "Core/Data/MapData.json";

        /**
         * Terrain data path.
         */
        public String terrainData = "Core/Data/TerrainData.json";

        /**
         * Assets data path.
         */
        public String assetsData = "Core/Data/AssetsData.json";

        /**
         * Scripts data path.
         */
        public String scriptsData = "Core/Data/ScriptsData.json";

        /**
         * World data path.
         */
        public String worldData = "Core/Data/WorldData.json";

        /**
         * Objects data path.
         */
        public String objectsData = "Core/Data/ObjectsData.json";

        /**
         * Map tiles path.
         */
        public String mapTilesData = "Core/Data/MapTiles.json";
    }
}
