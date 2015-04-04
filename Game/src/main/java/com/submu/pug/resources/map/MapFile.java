package com.submu.pug.resources.map;

import com.submu.pug.data.AssetsData;
import com.submu.pug.data.MapData;
import com.submu.pug.data.MapTilesData;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.data.ScriptsData;
import com.submu.pug.data.TerrainData;
import com.submu.pug.data.WorldData;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/11/13
 * Time: 8:09 PM
 * MapFile interface for handling the loading and saving of a map.
 */
public interface MapFile {
    /**
     * @return the map data.
     */
    MapData getMapData();

    /**
     * @param mapData the map data to set.
     */
    void setMapData(MapData mapData);

    /**
     * @return the terrain data.
     */
    TerrainData getTerrainData();

    /**
     * @param terrainData the terrain data to set.
     */
    void setTerrainData(TerrainData terrainData);

    /**
     * @return the assets data.
     */
    AssetsData getAssetsData();

    /**
     * @param assetsData the assets data to set.
     */
    void setAssetsData(AssetsData assetsData);

    /**
     * @return the scripts data.
     */
    ScriptsData getScriptsData();

    /**
     * @param scriptsData the scripts data to set.
     */
    void setScriptsData(ScriptsData scriptsData);

    /**
     * @return the world data.
     */
    WorldData getWorldData();

    /**
     * @param worldData the world data to set.
     */
    void setWorldData(WorldData worldData);

    /**
     * @return the objects data.
     */
    ObjectsData getObjectsData();

    /**
     * @param objectsData the objects data to set.
     */
    void setObjectsData(ObjectsData objectsData);

    /**
     * @return the map tiles data.
     */
    MapTilesData getMapTilesData();

    /**
     * @param mapTilesData the map tiles data to set.
     */
    void setMapTilesData(MapTilesData mapTilesData);

    /**
     * Destroys the map file so that no other class can use it afterwards.
     */
    void destroy();
}
