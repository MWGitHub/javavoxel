package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/14/13
 * Time: 1:55 PM
 * Application configuration, provides default values if none loaded.
 */
public class ConfigData {
    /**
     * True to show the settings dialog.
     */
    public boolean showSettings = false;

    /**
     * Settings for the graphics.
     */
    public Graphics graphics = new Graphics();

    /**
     * Settings for the assets.
     */
    public Assets assets = new Assets();

    /**
     * Settings for the controls.
     */
    public Controls controls = new Controls();

    /**
     * Number of logic updates per second.
     */
    public int logicRate = 32;

    /**
     * Graphic settings for the game.
     */
    public static class Graphics {
        /**
         * Window fullscreen flags.
         */
        public static final int SCREEN_WINDOW = 0,
        SCREEN_FULL = 1,
        SCREEN_BORDERLESS = 2;


        /**
         * Resolution of the window.
         */
        public Resolution resolution = new Resolution();

        /**
         * Screen is set to full when true.
         */
        public int fullscreen = SCREEN_FULL;

        /**
         * Turns vsync on when true.
         */
        public boolean vsync = false;

        /**
         * Framerate of the window.
         */
        public int framerate = 500;

        /**
         * Bits to use per pixel.
         */
        public int bitsPerPixel = 24;

        /**
         * Samples to use.
         */
        public int samples = 0;

        /**
         * Anisotropic filter level.
         */
        public int anisotropic = 4;

        /**
         * Viewing distance of the game.
         */
        public float viewDistance = 128f;

        /**
         * Resolution of the game window.
         */
        public static class Resolution {
            public int width = 1280;
            public int height = 720;
        }
    }

    /**
     * Asset settings.
     */
    public static class Assets {
        /**
         * Folder where the assets root resides.
         */
        public String path = "$user.dir$/assets/";

        /**
         * Folder where user assets reside.
         */
        public String user = "$APPDATA$/PugGame/";

        /**
         * Subfolders for the assets main roots.
         */
        public Subfolders subfolders = new Subfolders();

        /**
         * Default paths to data files.
         */
        public Defaults defaults = new Defaults();

        /**
         * Extension used for maps.
         */
        public String mapExtension = "pgm";

        /**
         * Subfolders for the assets.
         */
        public static class Subfolders {
            /**
             * Cores subfolders will go inside the path.
             */
            public String core = "Core/";

            /**
             * Development subfolders will go inside the user directory.
             */
            public String temp = "Temp/";

            /**
             * Subfolder for the map files.
             */
            public String map = "Map/";
        }

        /**
         * Default files for data.
         */
        public static class Defaults {
            /**
             * Default game data path.
             */
            public String gameData = "Core/Data/GameData.json";

            /**
             * Default model data path.
             */
            public String modelData = "Core/Data/ModelData.json";

            /**
             * Default gui elements path.
             */
            public String guiElementsData = "Core/Data/GUIElements.json";

            /**
             * Default map data path.
             */
            public String mapData = "Core/Data/MapData.json";
        }
    }

    /**
     * Controls for the game.
     */
    public static class Controls {
        /**
         * True to invert Y axis when looking around shoulder of fps view.
         */
        public boolean invertY = false;

        /**
         * Sensitivity of the mouse.
         */
        public float mouseSensitivity = 1.0f;

        /**
         * Speed to zoom in and out at.
         */
        public float mouseZoomSpeed = 0.5f;

        /**
         * Editor settings for the game.
         */
        public Editor editor = new Editor();

        /**
         * Editor controls and settings.
         */
        public static class Editor {
            /**
             * Maximum placement distance for the editor.
             */
            public float maxPlaceDistance = 64;

            /**
             * Movement speed of the editor camera.
             */
            public float speed = 10f;
        }
    }
}
