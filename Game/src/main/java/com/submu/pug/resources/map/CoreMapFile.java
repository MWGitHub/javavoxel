package com.submu.pug.resources.map;

import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.submu.pug.data.AssetsData;
import com.submu.pug.data.Data;
import com.submu.pug.data.MapData;
import com.submu.pug.data.MapTilesData;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.data.ScriptsData;
import com.submu.pug.data.TerrainData;
import com.submu.pug.data.WorldData;
import com.submu.pug.game.objects.GameObjectFactory;
import com.submu.pug.resources.Resources;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/22/13
 * Time: 2:23 PM
 * Loads the resources used for the map and allows the retrieval of data through it.
 * Each ExtractedMapFile will be unique to the loaded map and will not be shared throughout maps.
 * This map file uses local files to read from.
 */
public class CoreMapFile implements MapFile {
    /**
     * Status for saving and loading files.
     */
    public enum FileStatus {
        /**
         * Flags for the status.
         */
        SUCCESS, FAILED;
    }

    /**
     * Directory of the data files.
     */
    public static final String DIRECTORY_DATA = "Data/";

    /**
     * Directory for the scripts.
     */
    public static final String DIRECTORY_SCRIPTS = "Scripts/";

    /**
     * Directory for the assets.
     */
    public static final String DIRECTORY_ASSETS = "Assets/";

    /**
     * AssetManager used to load the resources.
     */
    private DesktopAssetManager assetManager;

    /**
     * Map data of the map.
     */
    private MapData mapData;

    /**
     * Terrain data of the map.
     */
    private TerrainData terrainData;

    /**
     * Assets of the map.
     */
    private AssetsData assetsData;

    /**
     * Data for scripts.
     */
    private ScriptsData scriptsData;

    /**
     * Data for the world.
     */
    private WorldData worldData;

    /**
     * Data for objects.
     */
    private ObjectsData objectsData;

    /**
     * Map tiles data.
     */
    private MapTilesData mapTilesData;

    /**
     * Initializes the class without an asset manager.
     * Resources will not be loaded.
     */
    public CoreMapFile() {
    }

    /**
     * Initializes the class.
     * @param assetManager the asset manager to load resources from.
     */
    public CoreMapFile(AssetManager assetManager) {
        this();
        this.assetManager = (DesktopAssetManager) assetManager;
        loadDefaults();
    }

    /**
     * Creates a default map.
     */
    public void loadDefaults() {
        mapData = Resources.loadJson((String) assetManager.loadAsset(
                Data.getInstance().getConfigData().assets.defaults.mapData), MapData.class);
        terrainData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.terrainData), TerrainData.class);
        assetsData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.assetsData), AssetsData.class);
        scriptsData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.scriptsData), ScriptsData.class);
        worldData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.worldData), WorldData.class);
        mapTilesData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.mapTilesData), MapTilesData.class);

        objectsData = GameObjectFactory.parseObjectsData((String) assetManager.loadAsset(mapData.dataPaths.objectsData));
    }

    /**
     * @return the map data.
     */
    public MapData getMapData() {
        return mapData;
    }

    /**
     * @param mapData the map data to set.
     */
    public void setMapData(MapData mapData) {
        this.mapData = mapData;
    }

    /**
     * @return the terrain data.
     */
    public TerrainData getTerrainData() {
        return terrainData;
    }

    /**
     * @param terrainData the terrain data to set.
     */
    public void setTerrainData(TerrainData terrainData) {
        this.terrainData = terrainData;
    }

    /**
     * @return the assets data.
     */
    public AssetsData getAssetsData() {
        return assetsData;
    }

    /**
     * @param assetsData the assets data to set.
     */
    public void setAssetsData(AssetsData assetsData) {
        this.assetsData = assetsData;
    }

    /**
     * @return the scripts data.
     */
    public ScriptsData getScriptsData() {
        return scriptsData;
    }

    /**
     * @param scriptsData the scripts data to set.
     */
    public void setScriptsData(ScriptsData scriptsData) {
        this.scriptsData = scriptsData;
    }

    /**
     * @return the world data.
     */
    public WorldData getWorldData() {
        return worldData;
    }

    /**
     * @param worldData the world data to set.
     */
    public void setWorldData(WorldData worldData) {
        this.worldData = worldData;
    }

    /**
     * @return the objects data.
     */
    public ObjectsData getObjectsData() {
        return objectsData;
    }

    /**
     * @param objectsData the objects data to set.
     */
    public void setObjectsData(ObjectsData objectsData) {
        this.objectsData = objectsData;
    }

    @Override
    public MapTilesData getMapTilesData() {
        return mapTilesData;
    }

    @Override
    public void setMapTilesData(MapTilesData mapTilesData) {
        this.mapTilesData = mapTilesData;
    }

    /**
     * Destroys the pointed data to prevent use within classes.
     */
    public void destroy() {
        mapData = null;
        terrainData = null;
        assetsData = null;
        scriptsData = null;
        assetManager = null;
    }
}
