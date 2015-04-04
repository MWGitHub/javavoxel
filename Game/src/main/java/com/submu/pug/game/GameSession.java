package com.submu.pug.game;


import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.debug.DebugTools;
import com.halboom.pgt.entityspatial.SpatialSystem;
import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.pgutil.threading.Threading;
import com.halboom.pgt.physics.PhysicsSystem;
import com.halboom.pgt.physics.debug.DebugBoundsSystem;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.halboom.pgt.terrainsystem.Terrain;
import com.halboom.pgt.terrainsystem.TileAtlas;
import com.halboom.pgt.terrainsystem.TileBank;
import com.halboom.pgt.terrainsystem.generator.Noise;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.submu.pug.camera.CameraState;
import com.submu.pug.camera.CameraSystem;
import com.submu.pug.data.Data;
import com.submu.pug.data.MapData;
import com.submu.pug.data.MapTilesData;
import com.submu.pug.data.PathNames;
import com.submu.pug.data.TerrainData;
import com.submu.pug.data.WorldData;
import com.submu.pug.game.actions.ChaseSystem;
import com.submu.pug.game.gui.GameGUI;
import com.submu.pug.game.objects.DecorationFactory;
import com.submu.pug.game.objects.GameObjectFactory;
import com.submu.pug.game.objects.ItemFactory;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.systems.AISystem;
import com.submu.pug.game.objects.systems.AbilitySystem;
import com.submu.pug.game.objects.systems.ActionSystem;
import com.submu.pug.game.objects.systems.ActorAnimationSystem;
import com.submu.pug.game.objects.systems.ExperienceSystem;
import com.submu.pug.game.objects.systems.HeadSystem;
import com.submu.pug.game.objects.systems.ItemSystem;
import com.submu.pug.game.objects.systems.LookSystem;
import com.submu.pug.game.objects.systems.MoveCommandSystem;
import com.submu.pug.game.objects.systems.StatSystem;
import com.submu.pug.game.objects.systems.TargetSystem;
import com.submu.pug.game.objects.systems.TimedLifeSystem;
import com.submu.pug.game.objects.systems.WalkSystem;
import com.submu.pug.game.world.AtmosphereSystem;
import com.submu.pug.game.world.GravitySystem;
import com.submu.pug.game.world.Regions;
import com.submu.pug.processors.FogState;
import com.submu.pug.processors.ShadowState;
import com.submu.pug.resources.map.MapFile;
import com.submu.pug.resources.map.MapUtils;
import com.submu.pug.scripting.GroovyRunner;
import com.submu.pug.scripting.ScriptAPI;
import com.submu.pug.scripting.ScriptGlobals;
import com.submu.pug.scripting.events.EventAI;
import com.submu.pug.scripting.events.EventAbilities;
import com.submu.pug.scripting.events.EventActor;
import com.submu.pug.scripting.events.EventAtmosphere;
import com.submu.pug.scripting.events.EventCameraMoves;
import com.submu.pug.scripting.events.EventCollider;
import com.submu.pug.scripting.events.EventGUI;
import com.submu.pug.scripting.events.EventHook;
import com.submu.pug.scripting.events.EventItem;
import com.submu.pug.scripting.events.EventKey;
import com.submu.pug.scripting.events.EventPlayers;
import com.submu.pug.scripting.events.EventRegions;
import com.submu.pug.scripting.events.EventTime;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/26/13
 * Time: 12:40 PM
 * Creates and updates all that is needed to start a game.
 * Do not modify app states with this.
 * Do not attach a non-new game session state.
 * TODO: Remove dependency on requiring a controller for the camera.
 */
public class GameSession {
    /**
     * Simple application cast of the app.
     */
    private SimpleApplication application;

    /**
     * Asset manager of the application.
     */
    private AssetManager assetManager;

    /**
     * Actions for debugging.
     */
    private DebugTools debugTools;

    /**
     * System to use for chase movement.
     */
    private ChaseSystem chaseSystem;

    /**
     * Root node for the session.
     */
    private Node sessionRoot;

    /**
     * Root gui node for the session.
     */
    private Node sessionGUI;

    /**
     * Handles the creation and response for GUI objects.
     */
    private GameGUI gameGUI;

    /**
     * Terrain of the game to be used with the atmosphereSystem.
     */
    private Terrain terrain;

    /**
     * Tile atlas to use for terrain rendering.
     */
    private TileAtlas tileAtlas;

    /**
     * Factory used to create objects.
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
     * Runs groovy scripts.
     */
    private GroovyRunner groovyRunner;

    /**
     * True to enable scripts.
     */
    private boolean areScriptsEnabled = true;

    /**
     * List of events to hook.
     */
    private List<EventHook> eventHooks = new ArrayList<EventHook>();

    /**
     * Handle the toggling of the key presses manually.
     */
    private EventKey eventKey;

    /**
     * Entity system to create entities with.
     */
    private EntitySystem entitySystem;

    /**
     * Systems the game uses.
     */
    private List<Subsystem> systems;

    /**
     * Regions within the map.
     */
    private Regions regions;

    /**
     * Assigns players.
     */
    private PlayerAssigner playerAssigner = new PlayerAssigner();

    /**
     * Initializes the session.
     * @param app the application to use.
     * @param mapFile the map data to load from.
     * @param rootNode the node to attach all other spatials to.
     * @param rootGUI the node to attach all gui elements to.
     */
    public GameSession(Application app, MapFile mapFile, Node rootNode, Node rootGUI) {
        // Sets up the initial variables.
        if (app instanceof SimpleApplication) {
            application = (SimpleApplication) app;
        }
        assetManager = app.getAssetManager();

        // Create the root nodes.
        sessionRoot = new Node();
        rootNode.attachChild(sessionRoot);
        sessionGUI = new Node();
        rootGUI.attachChild(sessionGUI);

        // Create the entity system.
        entitySystem = new EntitySystem();

        // Debug data used only when needed.
        DebugGlobals.getInstance().setRootNode(sessionRoot);

        // Create the terrain.
        MapData mapData = mapFile.getMapData();
        TerrainData terrainData = mapFile.getTerrainData();
        MapTilesData mapTilesData = mapFile.getMapTilesData();
        // Load the terrain tiles if it exists.
        if (!mapTilesData.segments.isEmpty()) {
            for (MapTilesData.TileSegment segment : mapTilesData.segments) {
                try {
                    segment.rawTiles = MapUtils.decompressTiles(segment.tiles,
                            segment.dimensionX, segment.dimensionY, segment.dimensionZ);
                } catch (IOException e) {
                    LoggerFactory.getLogger(GameSession.class).error("Unable to decompress the tiles.", e);
                    segment.tiles = null;
                    segment.rawTiles = null;
                }
            }
        } else {
            LoggerFactory.getLogger(GameSession.class).info("No terrain data provided.");
        }
        // Create the terrain classes.
        TileBank tileBank = new TileBank();
        tileBank.clear();
        float[] tileFriction = new float[terrainData.tileTypes.properties.length];
        long[] tileCollisionGroups = new long[terrainData.tileTypes.properties.length];
        for (int i = 0; i < terrainData.tileTypes.properties.length; i++) {
            tileBank.addTile(terrainData.tileTypes.properties[i]);
            tileFriction[i] = terrainData.tileTypes.properties[i].friction;
            tileCollisionGroups[i] = terrainData.tileTypes.properties[i].collisionGroup;
        }
        tileAtlas = new TileAtlas(assetManager, tileBank, terrainData.tileTypes.tileSheet);
        tileAtlas.setShadingEnabled(terrainData.shading.isEnabled);
        tileAtlas.setShininess(terrainData.shading.shininess);
        terrain = new Terrain(tileAtlas, tileBank);
        terrain.setDimensions(mapData.dimensions.xLength, mapData.dimensions.yLength, mapData.dimensions.zLength);
        terrain.setCullDistance(Data.getInstance().getConfigData().graphics.viewDistance);
        terrain.setScale(mapData.tileScale);
        // Generate a terrain if none is given.
        if (mapFile.getMapTilesData().segments.isEmpty()) {
            terrain.generate(new Noise((int) (mapData.dimensions.yLength / 3.0f)));
            //terrain.generate(new SkyIsland((int) (mapData.dimensions.yLength / 3.0f)));
            /*
            terrain.generate(new SkyTower(mapData.dimensions.xLength / 2, mapData.dimensions.yLength - 16, 16));
            terrain.generate(new Noise());
            */
        } else {
            // Add each segment to the terrain.
            for (MapTilesData.TileSegment segment : mapFile.getMapTilesData().segments) {
                terrain.addTiles(segment.rawTiles, segment.startX, segment.startY, segment.startZ);
            }
        }
        terrain.attach(this.sessionRoot);
        /**
         * Set up systems.
         * Systems that remove entities should be updated earlier.
         */
        // Create the physics system.
        PhysicsSystem physicsSystem = new PhysicsSystem(entitySystem);
        physicsSystem.setTileScale(mapData.tileScale);
        physicsSystem.setTiles(terrain.getTiles());
        physicsSystem.setTileFriction(tileFriction);
        physicsSystem.setTileCollisionGroups(tileCollisionGroups);
        CollisionFilter collisionFilter = new CollisionFilter(entitySystem, playerAssigner);
        physicsSystem.addFilter(collisionFilter);
        physicsSystem.getBulletSystem().enableDebug(app.getStateManager());

        chaseSystem = new ChaseSystem(entitySystem, app.getInputManager(), app.getCamera());
        AbilitySystem abilitySystem = new AbilitySystem(entitySystem, mapFile.getObjectsData());
        // Add the systems in order.
        systems = new LinkedList<Subsystem>();
        systems.add(new StatSystem(entitySystem));
        systems.add(new TimedLifeSystem(entitySystem));
        systems.add(chaseSystem);
        systems.add(new GravitySystem(entitySystem));
        systems.add(new AISystem(entitySystem, physicsSystem.getBoundsSystem(), collisionFilter));
        systems.add(new MoveCommandSystem(entitySystem));
        systems.add(abilitySystem);
        systems.add(new WalkSystem(entitySystem));
        systems.add(physicsSystem);
        systems.add(new ActorAnimationSystem(entitySystem));
        systems.add(new LookSystem(entitySystem));
        systems.add(new ItemSystem(entitySystem));
        SpatialSystem spatialSystem = new SpatialSystem(entitySystem, assetManager, this.sessionRoot);
        systems.add(spatialSystem);
        systems.add(new CameraSystem(app.getStateManager().getState(CameraState.class), entitySystem, spatialSystem));
        systems.add(new HeadSystem(entitySystem, spatialSystem, assetManager));
        systems.add(new DebugBoundsSystem(entitySystem, assetManager, this.sessionRoot));
        AtmosphereSystem atmosphereSystem = new AtmosphereSystem(assetManager, entitySystem, this.sessionRoot);
        systems.add(atmosphereSystem);
        // These systems just reset themselves so order does not matter as much.
        systems.add(new ActionSystem(entitySystem));
        systems.add(new TargetSystem(entitySystem));
        systems.add(new ExperienceSystem(entitySystem));

        // Create the factory that composes objects.
        gameObjectFactory = new GameObjectFactory(entitySystem, spatialSystem, mapFile.getObjectsData());
        decorationFactory = new DecorationFactory(entitySystem, mapFile.getObjectsData());
        itemFactory = new ItemFactory(entitySystem, mapFile.getObjectsData());

        // Initialize the regions.
        regions = new Regions(physicsSystem.getBoundsSystem());
        regions.enableDebug();

        setupAtmosphere(mapFile);

        // Set the shadow properties.
        ShadowState shadowState = app.getStateManager().getState(ShadowState.class);
        if (shadowState != null) {
            if (mapData.areShadowsEnabled) {
                shadowState.setShadowDirection(atmosphereSystem.getSun().getDirection().normalizeLocal());
            } else {
                shadowState.disable();
            }
        }

        // Set fog properties
        FogState fogState = app.getStateManager().getState(FogState.class);
        if (fogState != null) {
            fogState.setFogColor(new ColorRGBA(mapData.fog.red, mapData.fog.green, mapData.fog.blue, mapData.fog.alpha));
            fogState.setFogDensity(mapData.fog.density);
            fogState.setFogStartDistance(Data.getInstance().getConfigData().graphics.viewDistance * mapData.fog.distance);
        }

        // Create the crosshair
        createCrossHair();

        // Create the local player and give it a default civilian.
        Player localPlayer = playerAssigner.createPlayer(1);
        playerAssigner.setLocalPlayer(localPlayer);
        getSystem(LookSystem.class).setLocalPlayer(playerAssigner.getLocalPlayer());
        Player ally = playerAssigner.createPlayer(2);
        playerAssigner.setMutualAlliance(localPlayer, ally, PlayerAssigner.AllianceStatus.ALLIED);
        Player enemy = playerAssigner.createPlayer(3);
        playerAssigner.setMutualAlliance(enemy, localPlayer, PlayerAssigner.AllianceStatus.HOSTILE);
        playerAssigner.setMutualAlliance(enemy, ally, PlayerAssigner.AllianceStatus.HOSTILE);

        // Set the camera.
        CameraState cameraState = app.getStateManager().getState(CameraState.class);
        if (cameraState != null) {
            cameraState.setTileScale(mapData.tileScale);
            cameraState.addMouseToggleCallback(new CameraState.MouseToggleCallback() {
                @Override
                public void execute(boolean isDragToRotate) {
                    setMouseLockedActions(isDragToRotate);
                }
            });
        }

        // Initialize the editor keys.
        initKeys();

        // Create the initial objects.
        createInitialObjects(mapFile.getWorldData());

        // Initialize the GUI.
        gameGUI = new GameGUI(assetManager, app.getInputManager(), this.sessionGUI,
                entitySystem, getSystem(AbilitySystem.class), playerAssigner, cameraState,
                new Vector2f(app.getCamera().getWidth(), app.getCamera().getHeight()));

        // Initialize the scripts and event hooks.
        // TODO: Refactor to use just one script root.
        //initScripts(Data.getInstance().getResourcePaths().getPath(PathNames.TEMP_DIRECTORY) + mapFile.getScriptsData().root);
        ScriptAPI scriptAPI = new ScriptAPI.ScriptAPIBuilder().entitySystem(entitySystem)
                .gameObjectFactory(gameObjectFactory).decorationFactory(decorationFactory).itemFactory(itemFactory)
                .playerAssigner(playerAssigner).regions(regions).terrain(terrain).spatialSystem(spatialSystem)
                .abilitySystem(getSystem(AbilitySystem.class)).boundsSystem(physicsSystem.getBoundsSystem()).
                gameGUI(gameGUI).itemSystem(getSystem(ItemSystem.class)).build();
        initScripts(scriptAPI, mapFile.getScriptsData().root,
                Data.getInstance().getResourcePaths().getPath(PathNames.TEMP_DIRECTORY) + mapFile.getScriptsData().root);
    }

    /**
     * Retrieves a system given the class.
     * @param cls the class of the system.
     * @param <T> the type of subsystem.
     * @return the first system matching the class or null if none found.
     */
    private <T extends Subsystem> T getSystem(Class<T> cls) {
        for (Subsystem subsystem : systems) {
            if (subsystem.getClass().equals(cls)) {
                return cls.cast(subsystem);
            }
        }
        return null;
    }

    /**
     * Sets up the atmosphere.
     * @param mapFile the map file to load the settings of the atmosphere.
     */
    private void setupAtmosphere(MapFile mapFile) {
        MapData mapData = mapFile.getMapData();
        // Create the atmosphereSystem and set it up.
        MapData.Skybox skybox = mapData.skybox;
        AtmosphereSystem atmosphereSystem = getSystem(AtmosphereSystem.class);
        atmosphereSystem.setSkybox(skybox.west, skybox.east, skybox.north, skybox.south, skybox.up, skybox.down);
        atmosphereSystem.setSunColor(new ColorRGBA(mapData.sun.color.red, mapData.sun.color.green, mapData.sun.color.blue, mapData.sun.color.alpha));
        atmosphereSystem.setSunDirection(new Vector3f(mapData.sun.direction.x, mapData.sun.direction.y, mapData.sun.direction.z));
        atmosphereSystem.setAmbientLightColor(new ColorRGBA(mapData.ambientLight.color.red, mapData.ambientLight.color.green,
                mapData.ambientLight.color.blue, mapData.ambientLight.color.alpha));
        atmosphereSystem.createClouds(10, 10, 5.0f);
    }

    /**
     * Initialize editor movement.
     */
    private void initKeys() {
        debugTools = new DebugTools(application);

        // Activate the chase movement system.
        chaseSystem.activate();
    }

    /**
     * Place the initial objects.
     * @param worldData the data for the initial world objects.
     */
    private void createInitialObjects(WorldData worldData) {
        // Create actors.
        List<WorldData.ActorPlacement> actorPlacements = worldData.actors;
        for (WorldData.ActorPlacement actorPlacement : actorPlacements) {
            Entity entity = gameObjectFactory.createActor(actorPlacement.type, actorPlacement.owner, actorPlacement.name);
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent == null) {
                transformComponent = new TransformComponent();
            }
            transformComponent.positionX = actorPlacement.location.x;
            transformComponent.positionY = actorPlacement.location.y;
            transformComponent.positionZ = actorPlacement.location.z;
            entitySystem.setComponent(entity, transformComponent);
        }

        // Create decorations.
        List<WorldData.ThingPlacement> decorationPlacements = worldData.decorations;
        for (WorldData.ThingPlacement decorationPlacement : decorationPlacements) {
            Entity entity = decorationFactory.createDecoration(decorationPlacement.type);
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent == null) {
                transformComponent = new TransformComponent();
            }
            transformComponent.positionX = decorationPlacement.location.x;
            transformComponent.positionY = decorationPlacement.location.y;
            transformComponent.positionZ = decorationPlacement.location.z;
            entitySystem.setComponent(entity, transformComponent);
        }

        // Create items.
        List<WorldData.ThingPlacement> itemPlacements = worldData.items;
        for (WorldData.ThingPlacement itemPlacement : itemPlacements) {
            Entity entity = itemFactory.createItem(itemPlacement.type, itemPlacement.name);
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent == null) {
                transformComponent = new TransformComponent();
            }
            transformComponent.positionX = itemPlacement.location.x;
            transformComponent.positionY = itemPlacement.location.y;
            transformComponent.positionZ = itemPlacement.location.z;
            entitySystem.setComponent(entity, transformComponent);
        }

        // Create regions.
        List<WorldData.Region> regionPlacement = worldData.regions;
        for (WorldData.Region region : regionPlacement) {
            Vector3f center = new Vector3f(region.location.x, region.location.y, region.location.z);
            Vector3f extents = new Vector3f(region.extentX, region.extentY, region.extentZ);
            regions.addRegion(region.name, center, extents);
        }
    }

    /**
     * Initializes the scripts.
     * @param scriptAPI the api to use for the scripts.
     * @param path the root path for the scripts.
     */
    private void initScripts(ScriptAPI scriptAPI, String... path) {
        // Create the groovy script runner.
        groovyRunner = new GroovyRunner(scriptAPI, path);

        // Create every event used.
        eventKey = new EventKey(application.getInputManager(), playerAssigner.getLocalPlayer(), getSystem(AbilitySystem.class));
        eventHooks.add(eventKey);
        eventHooks.add(new EventGUI(playerAssigner.getLocalPlayer()));
        eventHooks.add(new EventCameraMoves(application.getCamera()));
        eventHooks.add(new EventTime());
        eventHooks.add(new EventActor(gameObjectFactory));
        eventHooks.add(new EventAtmosphere(getSystem(AtmosphereSystem.class)));
        eventHooks.add(new EventPlayers(playerAssigner));
        eventHooks.add(new EventCollider(getSystem(PhysicsSystem.class)));
        eventHooks.add(new EventRegions(regions));
        eventHooks.add(new EventAbilities(getSystem(AbilitySystem.class)));
        eventHooks.add(new EventAI(getSystem(AISystem.class)));
        eventHooks.add(new EventItem(getSystem(ItemSystem.class)));
    }

    /**
     * Updates the session.
     * @param tpf the time passed per frame.
     */
    public final void update(float tpf) {
        // Updates properties
        playerAssigner.getLocalPlayer().setCameraDirection(application.getCamera().getDirection());
        playerAssigner.getLocalPlayer().setCameraLocation(application.getCamera().getLocation());

        // Update the regions used for scripts.
        regions.update();
        // Updates script events.
        for (EventHook event : eventHooks) {
            event.updateEvent(tpf);
        }

        // Updates the scripts.
        if (areScriptsEnabled) {
            groovyRunner.update(tpf);
        } else {
            groovyRunner.flushEvents();
        }

        // Update the systems.
        long t1 = System.nanoTime();
        for (Subsystem system : systems) {
            system.update(tpf);
        }
        //DebugGlobals.println((System.nanoTime() - t1) / Units.MILLISECONDS_PER_NANOSECOND);

        // Cull the terrain.
        terrain.cull(Threading.getInstance().getExecutor(), application.getCamera().getLocation());

        // Set new GUI targets.
        final Entity controlledEntity = playerAssigner.getLocalPlayer().getControlledEntity();
        gameGUI.setControlledEntity(controlledEntity);
        gameGUI.setTargetedEntity(getSystem(PhysicsSystem.class).getBoundsSystem().getClosestIntersect(
                new Ray(application.getCamera().getLocation(), application.getCamera().getDirection()), controlledEntity, gameGUI.getTargetFilter()));
        // Update the GUI.
        gameGUI.update(tpf);

        // Clean up the systems.
        for (Subsystem system : systems) {
            system.cleanupSubsystem();
        }

        // Flush changes in the system.
        entitySystem.flushSetChanges();

        debugTools.update(tpf);
    }

    /**
     * Creates the crosshair.
     */
    private void createCrossHair() {
        BitmapFont font = assetManager.loadFont("Interface/Fonts/Default.fnt");
        BitmapText crossHair = new BitmapText(font, false);
        crossHair.setSize(font.getCharSet().getRenderedSize() * 2);
        crossHair.setText("+");
        crossHair.setLocalTranslation(application.getCamera().getWidth() / 2f - font.getCharSet().getRenderedSize() / 3f * 2f,
                application.getCamera().getHeight() / 2f + crossHair.getLineHeight() / 2f, 0f);
        sessionGUI.attachChild(crossHair);
    }

    /**
     * Toggles movement related to mouse locking.
     * @param isDragToRotate true to set it as locked.
     */
    private void setMouseLockedActions(boolean isDragToRotate) {
        if (!isDragToRotate) {
            chaseSystem.activate();
            debugTools.activate();
            eventKey.isGUIActivated(false);
        } else {
            chaseSystem.deactivate();
            debugTools.deactivate();
            eventKey.isGUIActivated(true);
        }
    }

    /**
     * @return the terrain.
     */
    public Terrain getTerrain() {
        return terrain;
    }

    /**
     * @return tile atlas used for the terrain.
     */
    public TileAtlas getTileAtlas() {
        return tileAtlas;
    }

    /**
     * @return the grid collision system.
     */
    public GridColliderSystem getGridColliderSystem() {
        return getSystem(PhysicsSystem.class).getGridColliderSystem();
    }

    /**
     * @return the factory to create objects with.
     */
    public GameObjectFactory getGameObjectFactory() {
        return gameObjectFactory;
    }

    /**
     * @param areScriptsEnabled true to enable scripts.
     */
    public void setAreScriptsEnabled(boolean areScriptsEnabled) {
        this.areScriptsEnabled = areScriptsEnabled;
    }

    /**
     * Destroys the session.
     */
    public void destroy() {
        sessionRoot.removeFromParent();
        sessionGUI.removeFromParent();

        terrain.destroy();

        debugTools.cleanup();

        for (Subsystem system : systems) {
            system.destroy();
        }

        regions.destroy();

        // Clean up the event hooks.
        for (EventHook event : eventHooks) {
            event.destroyEvent();
        }

        groovyRunner.destroy();

        // Reset the camera.
        CameraState cameraState = application.getStateManager().getState(CameraState.class);
        if (cameraState != null) {
            cameraState.removeCallbacks();
        }

        ScriptGlobals.getInstance().destroy();

        System.gc();
    }
}
