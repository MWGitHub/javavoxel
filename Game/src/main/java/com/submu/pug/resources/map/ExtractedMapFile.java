package com.submu.pug.resources.map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halboom.pgt.asseteditor.Asset;
import com.jme3.asset.AssetManager;
import com.jme3.asset.DesktopAssetManager;
import com.submu.pug.data.AssetsData;
import com.submu.pug.data.Data;
import com.submu.pug.data.MapData;
import com.submu.pug.data.MapTilesData;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.data.PathNames;
import com.submu.pug.data.ScriptsData;
import com.submu.pug.data.TerrainData;
import com.submu.pug.data.WorldData;
import com.submu.pug.game.objects.GameObjectFactory;
import com.submu.pug.resources.Resources;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileOutputStream;
import de.schlichtherle.truezip.file.TVFS;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOError;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/22/13
 * Time: 2:23 PM
 * Loads the resources used for the map and allows the retrieval of data through it.
 * Each ExtractedMapFile will be unique to the loaded map and will not be shared throughout maps.
 * MapFiles only contain data used for maps and nothing else and should not be stored as a
 * member variable.
 */
public class ExtractedMapFile implements MapFile {
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
     * Data for the map tiles.
     */
    private MapTilesData mapTilesData;

    /**
     * List of assets the resources use.
     */
    private List<Asset> assets;

    /**
     * Initializes the class without an asset manager.
     * Resources will not be loaded.
     */
    public ExtractedMapFile() {
    }

    /**
     * Initializes the class.
     * @param assetManager the asset manager to load resources from.
     */
    public ExtractedMapFile(AssetManager assetManager) {
        this();
        this.assetManager = (DesktopAssetManager) assetManager;
        this.assetManager.clearCache();
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
        //objectsData = Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.objectsData), ObjectsData.class);


        // Change the defaults to the default map data structure after loading.
        redirectDirectories();
    }

    /**
     * Redirects directories in the map data to use custom ones instead of the default.
     * This can be run safely after initial map loading.
     */
    private void redirectDirectories() {
        // Add default data to the temporary directory.
        String tempPath = Data.getInstance().getResourcePaths().getPath(PathNames.MAP_DIRECTORY) + DIRECTORY_DATA;
        try {
            File toPath = new File(tempPath);
            File fromPath = new File(Data.getInstance().getResourcePaths().getPath(PathNames.CORE_ASSETS_DIRECTORY) + DIRECTORY_DATA);
            FileUtils.deleteDirectory(toPath);
            FileUtils.copyDirectory(fromPath, toPath);
        } catch (IOException e) {
            throw new IOError(e);
        }

        // Redirect the data directories.
        String path = Data.getInstance().getConfigData().assets.subfolders.map + DIRECTORY_DATA;
        mapData.dataPaths.mapData = path + "MapData.json";
        mapData.dataPaths.terrainData = path + "TerrainData.json";
        mapData.dataPaths.assetsData = path + "AssetsData.json";
        mapData.dataPaths.scriptsData = path + "ScriptsData.json";
        mapData.dataPaths.worldData = path + "WorldData.json";
        mapData.dataPaths.objectsData = path + "ObjectsData.json";
        mapData.dataPaths.mapTilesData = path + "MapTiles.json";

        // Add default scripts to a temporary directory.
        tempPath = Data.getInstance().getResourcePaths().getPath(PathNames.MAP_DIRECTORY) + DIRECTORY_SCRIPTS;
        try {
            File toPath = new File(tempPath);
            File fromPath = new File(scriptsData.root);
            FileUtils.deleteDirectory(toPath);
            FileUtils.copyDirectory(fromPath, toPath);
        } catch (IOException e) {
            throw new IOError(e);
        }

        // Set the redirected root.
        path = Data.getInstance().getConfigData().assets.subfolders.map + DIRECTORY_SCRIPTS;
        scriptsData.root = path;
    }

    /**
     * Writes the given data to the archive.
     * This does not close the file system, an unmount will need to be called manually.
     * @param path the path to write to.
     * @param jsonData the data object to convert to json and write.
     * @return success if complete, failed otherwise.
     */
    private FileStatus writeDataToArchive(String path, Object jsonData) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File entry = new TFile(path);
        String output = gson.toJson(jsonData);
        TFileOutputStream stream = null;
        try {
            stream = new TFileOutputStream(entry);
            stream.write(output.getBytes());
        } catch (IOException ex) {
            LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to write " + path + ".", ex);
            return FileStatus.FAILED;
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to close the writer for " + path + ".", e);
                return FileStatus.FAILED;
            }
        }
        return FileStatus.SUCCESS;
    }

    /**
     * Writes all the scripts the map uses into the archive.
     * @param path the path of the archive file.
     * @throws IOException thrown when the data cannot be written.
     */
    private void writeScriptsToArchive(String path) throws IOException {
        TFile tempDir = new TFile(Data.getInstance().getResourcePaths().getPath(PathNames.MAP_DIRECTORY) + DIRECTORY_SCRIPTS);
        TFile entry = new TFile(path + "/" + scriptsData.root);

        tempDir.cp_rp(entry);
    }

    /**
     * Writes all the assets to the archive.
     * @param path the path to write into.
     * @throws IOException thrown when the data cannot be written.
     */
    private void writeAssetsToArchive(String path) throws IOException {
        if (assets != null) {
            for (Asset asset : assets) {
                TFile data = new TFile(Data.getInstance().getResourcePaths().getPath(PathNames.MAP_DIRECTORY) + asset.getPath());
                TFile entry = new TFile(path + "/" + Data.getInstance().getConfigData().assets.subfolders.map + asset.getPath());
                data.cp_rp(entry);
            }
        }
    }

    /**
     * Outputs the data of the resources to a map file and saves it to disk.
     * @param path the path to save the file as.
     * @return success if successfully saved.
     */
    public FileStatus toMapFile(String path) {
        FileStatus status = FileStatus.SUCCESS;

        try {
            TVFS.umount();
        } catch (IOException e) {
            LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to unmount the map file.", e);
        }

        // Set the temporary file path to create a backup when saving.
        TFile oldFile = new TFile(path, TArchiveDetector.NULL);
        TFile tempFile = new TFile(path + ".temp", TArchiveDetector.NULL);
        boolean doesOldFileExist = oldFile.exists();

        // Make a copy of the map in case of a save error.
        try {
            // Remove files that are being overwritten and copy it to a temp file.
            if (doesOldFileExist) {
                TVFS.umount();

                FileUtils.copyFile(oldFile, tempFile);
                FileUtils.forceDelete(oldFile);
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to create a backup map file.", e);
            status = FileStatus.FAILED;
        }

        // Write the map data
        try {
            // Compress the tiles.
            List<byte[][][]> rawTilesStore = new LinkedList<byte[][][]>();
            for (MapTilesData.TileSegment segment : mapTilesData.segments) {
                byte[][][] rawTiles = segment.rawTiles;
                rawTilesStore.add(rawTiles);
                segment.dimensionX = rawTiles.length;
                segment.dimensionY = rawTiles[0].length;
                segment.dimensionZ = rawTiles[0][0].length;
                segment.tiles = MapUtils.compressTiles(rawTiles);
                segment.rawTiles = null;
            }

            // Write the individual data items.
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.mapData, mapData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.terrainData, terrainData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.assetsData, assetsData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.scriptsData, scriptsData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.worldData, worldData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.objectsData, objectsData));
            status = getFileStatus(status, writeDataToArchive(path + "/" + mapData.dataPaths.mapTilesData, mapTilesData));

            // Replace the terrain tiles.
            for (int i = 0; i < mapTilesData.segments.size(); i++) {
                mapTilesData.segments.get(i).rawTiles = rawTilesStore.get(i);
            }

            // Write the scripts.
            writeScriptsToArchive(path);
            // Write the assets.
            writeAssetsToArchive(path);
        } catch (IOException ex) {
            LoggerFactory.getLogger(ExtractedMapFile.class).error("Error on map save.", ex);
            status = FileStatus.FAILED;
            // Move the temp file back if it exists.
            try {
                if (doesOldFileExist) {
                    TVFS.umount();
                    oldFile.rm();
                    tempFile.cp(oldFile);
                    tempFile.rm();
                }
            } catch (IOException e) {
                LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to move the temporary save file back on save error.", e);
            }
        }

        // Clean up the temporary file once everything is successful.
        if (FileStatus.SUCCESS == status && doesOldFileExist) {
            try {
                tempFile.rm();
            } catch (IOException e) {
                LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to clean up the temporary save file.", e);
            }
        }

        try {
            TVFS.umount();
        } catch (IOException e) {
            LoggerFactory.getLogger(ExtractedMapFile.class).error("Unable to unmount the save file.", e);
        }

        return status;
    }



    /**
     * Adds map assets to the asset manager.
     * @param mapData the map data to use to determine what files to load.
     * @throws IOException thrown when a resource cannot be read.
     */
    private void addMapAssetsToManager(MapData mapData) throws IOException {
        // Load all the data files
        try {
            terrainData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.terrainData),
                    TerrainData.class));
            assetsData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.assetsData),
                    AssetsData.class));
            scriptsData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.scriptsData),
                    ScriptsData.class));
            worldData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.worldData),
                    WorldData.class));
            objectsData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.objectsData),
                    ObjectsData.class));
            mapTilesData = Objects.requireNonNull(Resources.loadJson((String) assetManager.loadAsset(mapData.dataPaths.mapTilesData),
                    MapTilesData.class));

            // Decompress the tiles.
            for (MapTilesData.TileSegment segment : mapTilesData.segments) {
                segment.rawTiles = MapUtils.decompressTiles(segment.tiles,
                        mapData.dimensions.xLength, mapData.dimensions.yLength, mapData.dimensions.zLength);
            }

        } catch (NullPointerException e) {
            throw new IOException(e);
        }
    }

    /**
     * Loads a map file from a path.
     * @param path the path of the map file.
     * @throws IOException thrown when the file could not be loaded.
     */
    public void fromMapFile(String path) throws IOException {
        TVFS.umount();

        // Unzip the directory.
        TFile file = new TFile(path);
        TFile unpacked = new TFile(Data.getInstance().getResourcePaths().getPath(PathNames.TEMP_DIRECTORY));
        TFile.cp_rp(file, unpacked, TArchiveDetector.NULL, TArchiveDetector.NULL);
        TVFS.umount();

        // Load the map data first and then load everything from there.
        String mapFolder = Data.getInstance().getConfigData().assets.subfolders.map;
        mapData = Resources.loadJson((String) assetManager.loadAsset(mapFolder + DIRECTORY_DATA + "MapData.json"), MapData.class);

        // Add the map data to the asset manager.
        addMapAssetsToManager(mapData);

        TVFS.umount();
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
     * Gets the status for the file on failure.
     * @param status the current status of the file.
     * @param checkedStatus the checked status.
     */
    private FileStatus getFileStatus(FileStatus status, FileStatus checkedStatus) {
        if (checkedStatus == FileStatus.FAILED) {
            return FileStatus.FAILED;
        }
        return status;
    }

    /**
     * Removes map assets from the asset manager.
     */
    private void removeMapAssetsFromManager() {
        assetManager.clearCache();
    }

    /**
     * Sets the list of assets the map uses.
     * @param assets the assets the map uses.
     */
    public void setAssets(List<Asset> assets) {
        this.assets = assets;
        List<String> assetPaths = new ArrayList<String>();
        for (Asset asset : assets) {
            assetPaths.add(asset.getPath());
        }
        assetsData.assets = assetPaths.toArray(assetsData.assets);
    }

    /**
     * Clears the assets loaded and the map data.
     */
    public void clear() {
        // Clear all the cached assets from the map.
        removeMapAssetsFromManager();

        // Delete the temporary directory.
        String tempPath = Data.getInstance().getResourcePaths().getPath(PathNames.TEMP_DIRECTORY);
        try {
            TVFS.umount();
            FileUtils.deleteDirectory(new File(tempPath));
        } catch (IOException e) {
            LoggerFactory.getLogger(ExtractedMapFile.class).warn("Unable to delete the temporary directory.", e);
        }

        mapData = null;
        terrainData = null;
        assetsData = null;
        scriptsData = null;
        assetManager = null;
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
