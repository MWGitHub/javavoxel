package com.submu.pug.scripting;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.entityspatial.SpatialSystem;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.physics.simple.BoundsSystem;
import com.halboom.pgt.terrainsystem.Terrain;
import com.submu.pug.data.Data;
import com.submu.pug.data.ModelData;
import com.submu.pug.game.gui.GameGUI;
import com.submu.pug.game.objects.DecorationFactory;
import com.submu.pug.game.objects.GameObjectFactory;
import com.submu.pug.game.objects.ItemFactory;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.systems.AbilitySystem;
import com.submu.pug.game.objects.systems.ItemSystem;
import com.submu.pug.game.world.Regions;
import org.codehaus.groovy.runtime.StackTraceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/15/13
 * Time: 12:15 PM
 * Functions and variables that the scripts can import and call.
 */
public final class ScriptAPI {
    /**
     * Name of the variable to hold when the script should really start executing.
     * This allows for setting up the systems in the game first.
     */
    public static final String IS_FIRST_PASS = "isFirstPass";

    /**
     * Name of the variable in the binding that holds all the triggered events.
     */
    public static final String TRIGGERED_EVENTS = "triggeredEvents";

    /**
     * Name of the variable to use to store the API in the wrapper.
     */
    public static final String WRAPPER_NAME = "groovyScriptBindings";

    /**
     * Name for the variables within the binding.
     */
    public static final String VARIABLE_NAME = "variables";

    /**
     * Variables to set and store.
     */
    private final Map<String, Object> variables;

    /**
     * Entity system used for the game.
     */
    private EntitySystem entitySystem;

    /**
     * Factory used to create game objects.
     */
    private GameObjectFactory gameObjectFactory;

    /**
     * Factory used to create decorations.
     */
    private DecorationFactory decorationFactory;

    /**
     * Factory used to create items.
     */
    private ItemFactory itemFactory;

    /**
     * Terrain used for the game.
     */
    private Terrain terrain;

    /**
     * Player assigner to retrieve players from.
     */
    private PlayerAssigner playerAssigner;

    /**
     * Regions to retrieve from the game.
     */
    private Regions regions;

    /**
     * Spatial system used for entities.
     */
    private SpatialSystem spatialSystem;

    /**
     * System for the abilities.
     */
    private AbilitySystem abilitySystem;

    /**
     * System to use for retrieving bounds.
     */
    private BoundsSystem boundsSystem;

    /**
     * Game GUI to use.
     */
    private GameGUI gameGUI;

    /**
     * System that handles the items and inventory.
     */
    private ItemSystem itemSystem;

    /**
     * Do not allow construction of the class.
     * @param builder the builder to create the API from.
     */
    public ScriptAPI(ScriptAPIBuilder builder) {
        entitySystem = builder.entitySystem;
        gameObjectFactory = builder.gameObjectFactory;
        terrain = builder.terrain;
        playerAssigner = builder.playerAssigner;
        regions = builder.regions;
        spatialSystem = builder.spatialSystem;
        abilitySystem = builder.abilitySystem;
        boundsSystem = builder.boundsSystem;
        gameGUI = builder.gameGUI;
        itemSystem = builder.itemSystem;
        variables = new HashMap<String, Object>();
    }

    /**
     * @return the table of variables.
     */
    public Map<String, Object> getVariables() {
        return variables;
    }

    /**
     * Stores a variable into the binding.
     * @param key the key to store the variable as.
     * @param value the value of the variable.
     */
    public void setVariable(String key, Object value) {
        variables.put(key, value);
    }

    /**
     * Retrieves a variable from the binding.
     * @param key the of the variable to retrieve.
     * @return the variable to retrieve.
     */
    public Object getVariable(String key) {
        return variables.get(key);
    }

    /**
     * Adds an event to the next script update.
     * @param scriptEvent the script event to add.
     */
    public void addEvent(ScriptEvent scriptEvent) {
        ScriptGlobals.getInstance().addEvent(scriptEvent);
    }

    /**
     * Logs an error and displays it in the console.
     * @param errors the errors to log.
     */
    public void logError(Object... errors) {
        /*
        for (StackTraceElement trace : Thread.currentThread().getStackTrace()) {
            DebugGlobals.println(trace, errors);
        }
        */

        StackTraceElement trace = StackTraceUtils.sanitize(new Throwable()).getStackTrace()[1];
        DebugGlobals.println(trace, errors);
    }

    /**
     * @return the entity system used for the game.
     */
    public EntitySystem getEntitySystem() {
        return entitySystem;
    }

    /**
     * @return the game object factory.
     */
    public GameObjectFactory getGameObjectFactory() {
        return gameObjectFactory;
    }

    /**
     * @return the decorations factory.
     */
    public DecorationFactory getDecorationFactory() {
        return decorationFactory;
    }

    /**
     * @return the item factory.
     */
    public ItemFactory getItemFactory() {
        return itemFactory;
    }

    /**
     * @return the terrain.
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * @return the player assigner.
     */
    public PlayerAssigner getPlayerAssigner() {
        return playerAssigner;
    }

    /**
     * @return the regions in the game.
     */
    public Regions getRegions() {
        return regions;
    }

    /**
     * @return the spatial system used in the game.
     */
    public SpatialSystem getSpatialSystem() {
        return spatialSystem;
    }

    /**
     * @return the ability system for the game.
     */
    public AbilitySystem getAbilitySystem() {
        return abilitySystem;
    }

    /**
     * @return the name of properties models use.
     */
    public ModelData getModelData() {
        return Data.getInstance().getModelData();
    }

    /**
     * @return the bounds system.
     */
    public BoundsSystem getBoundsSystem() {
        return boundsSystem;
    }

    /**
     * @return the game gui.
     */
    public GameGUI getGameGUI() {
        return gameGUI;
    }

    /**
     * @return the item system.
     */
    public ItemSystem getItemSystem() {
        return itemSystem;
    }

    /**
     * Builds the script API.
     */
    public static class ScriptAPIBuilder {
        private EntitySystem entitySystem;
        private GameObjectFactory gameObjectFactory;
        private DecorationFactory decorationFactory;
        private ItemFactory itemFactory;
        private Terrain terrain;
        private PlayerAssigner playerAssigner;
        private Regions regions;
        private SpatialSystem spatialSystem;
        private AbilitySystem abilitySystem;
        private BoundsSystem boundsSystem;
        private GameGUI gameGUI;
        private ItemSystem itemSystem;

        public ScriptAPIBuilder() {
        }

        /**
         * @param entitySystem the entity system the scripts will use.
         */
        public ScriptAPIBuilder entitySystem(EntitySystem entitySystem) {
            this.entitySystem = entitySystem;
            return this;
        }

        /**
         * @param gameObjectFactory the object factory the scripts will create actors from.
         */
        public ScriptAPIBuilder gameObjectFactory(GameObjectFactory gameObjectFactory) {
            this.gameObjectFactory = gameObjectFactory;
            return this;
        }

        /**
         * @param decorationFactory the decoration factory.
         */
        public ScriptAPIBuilder decorationFactory(DecorationFactory decorationFactory) {
            this.decorationFactory = decorationFactory;
            return this;
        }

        /**
         * @param itemFactory the item factory.
         */
        public ScriptAPIBuilder itemFactory(ItemFactory itemFactory) {
            this.itemFactory = itemFactory;
            return this;
        }

        /**
         * @param terrain the terrain to retrieve the grid from.
         */
        public ScriptAPIBuilder terrain(Terrain terrain) {
            this.terrain = terrain;
            return this;
        }

        /**
         * @param playerAssigner the players in the game.
         */
        public ScriptAPIBuilder playerAssigner(PlayerAssigner playerAssigner) {
            this.playerAssigner = playerAssigner;
            return this;
        }

        /**
         * @param regions the regions in the game.
         */
        public ScriptAPIBuilder regions(Regions regions) {
            this.regions = regions;
            return this;
        }

        /**
         * @param spatialSystem the spatial system the game uses.
         */
        public ScriptAPIBuilder spatialSystem(SpatialSystem spatialSystem) {
            this.spatialSystem = spatialSystem;
            return this;
        }

        /**
         * @param abilitySystem the ability system to use.
         */
        public ScriptAPIBuilder abilitySystem(AbilitySystem abilitySystem) {
            this.abilitySystem = abilitySystem;
            return this;
        }

        /**
         * @param boundsSystem the bounds system to use.
         */
        public ScriptAPIBuilder boundsSystem(BoundsSystem boundsSystem) {
            this.boundsSystem = boundsSystem;
            return this;
        }

        /**
         * @param gameGUI the game gui to use.
         */
        public ScriptAPIBuilder gameGUI(GameGUI gameGUI) {
            this.gameGUI = gameGUI;
            return this;
        }

        /**
         * @param itemSystem the item system to use.
         */
        public ScriptAPIBuilder itemSystem(ItemSystem itemSystem) {
            this.itemSystem = itemSystem;
            return this;
        }

        /**
         * @return the built script API or null if the API is invalid..
         */
        public ScriptAPI build() {
            if (entitySystem == null || gameObjectFactory == null || decorationFactory == null || itemFactory == null
                    || terrain == null || playerAssigner == null || regions == null
                    || spatialSystem == null || abilitySystem == null || boundsSystem == null || gameGUI == null
                    || itemSystem == null) {
                throw new InstantiationError("Not all parameters are set.");
            }
            return new ScriptAPI(this);
        }
    }
}
