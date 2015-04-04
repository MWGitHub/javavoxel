package com.submu.pug.editor;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.halboom.pgt.asseteditor.Asset;
import com.halboom.pgt.asseteditor.ImportListener;
import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.pgutil.math.Vector3Int;
import com.halboom.pgt.physics.simple.GridColliderSystem;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.submu.pug.camera.CameraState;
import com.submu.pug.data.Data;
import com.submu.pug.data.MapTilesData;
import com.submu.pug.data.PathNames;
import com.submu.pug.editor.menu.EditorMainMenu;
import com.submu.pug.game.GameSession;
import com.submu.pug.play.PlayState;
import com.submu.pug.resources.map.ExtractedMapFile;
import com.submu.pug.resources.map.MapFile;
import com.submu.pug.resources.map.MapUtils;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.EventQueue;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/27/12
 * Time: 12:19 PM
 * Adds editor controls to the PlayState and changes the playerActor character to an editor.
 * This must not implement any game logic, only ui and placement.
 */
public class EditorState extends AbstractAppState {
    /**
     * Name of the main segment which will always be equal to the whole map tiles.
     */
    private static final String MAIN_SEGMENT_NAME = "Main Segment";

    /**
     * Main frame of the editor.
     */
    private JFrame frame;

    /**
     * Frames the editor uses.
     */
    private EditorFrames editorFrames;

    /**
     * Simple application cast of the app.
     */
    private SimpleApplication application;

    /**
     * Asset manager of the application.
     */
    private AssetManager assetManager;

    /**
     * Actions for the editor.
     */
    private EditorActions editorActions;

    /**
     * Path to the map data to load.
     */
    private String mapPath;

    /**
     * Root node for the state.
     */
    private Node stateRoot;

    /**
     * Root gui node for the state.
     */
    private Node stateGui;

    /**
     * Cube graphics for the targeted cube.
     */
    private Geometry targetCube;

    /**
     * Session for the game.
     */
    private GameSession gameSession;

    /**
     * Segments in the last loaded map file.
     */
    private List<MapTilesData.TileSegment> loadedSegments;

    /**
     * Creates the state but does not initialize it.
     * @param frame the main frame of the editor.
     */
    public EditorState(JFrame frame) {
        super();

        this.frame = frame;
        editorFrames = new EditorFrames(frame);
        createMenuCallbacks();
        createAssetImporterCallbacks();
    }

    /**
     * Creates the callbacks for the menu.
     */
    private void createMenuCallbacks() {
        EditorMainMenu editorMainMenu = editorFrames.getMainMenu();
        editorMainMenu.setNewMapCallback(new EditorCallbacks() {
            @Override
            public void onAction() {
                newMap();
            }
        });
        editorMainMenu.setLoadFileCallback(new EditorCallbacks() {
            @Override
            public void onFileSelected(String path) {
                openMap(path);
            }
        });
        editorMainMenu.setSaveFileCallback(new EditorCallbacks() {
            @Override
            public void onFileSelected(String path) {
                if (path != null) {
                    saveMap(path);
                }
            }
        });
        editorMainMenu.setTestMapCallback(new EditorCallbacks() {
            @Override
            public void onAction() {
                testMap();
            }
        });
    }

    /**
     * Creates callbacks for the asset importer.
     */
    private void createAssetImporterCallbacks() {
        editorFrames.getAssetImporter().addImportListener(new ImportListener() {
            @Override
            public void onAssetEdited(Asset asset) {
                //mapFile.setAssets(editorFrames.getAssetImporter().getAllAssets());
            }

            @Override
            public void onAssetDeleted(Asset asset) {
                //mapFile.setAssets(editorFrames.getAssetImporter().getAllAssets());
            }

            @Override
            public void onAssetImported(Asset asset) {
                //mapFile.setAssets(editorFrames.getAssetImporter().getAllAssets());
            }
        });
    }

    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        // Show the editor menu.
        editorFrames.show();

        // Sets up the initial variables.
        if (app instanceof SimpleApplication) {
            application = (SimpleApplication) app;
        }
        assetManager = app.getAssetManager();

        // Create the root nodes.
        stateRoot = new Node();
        application.getRootNode().attachChild(stateRoot);
        stateGui = new Node();
        application.getGuiNode().attachChild(stateGui);

        // Debug data used only when needed.
        DebugGlobals.getInstance().setRootNode(stateRoot);

        // Load the map (should not be a MapData file later on.
        ExtractedMapFile mapFile = null;
        if (mapPath != null) {
            mapFile = loadMap(mapPath);
            loadedSegments = mapFile.getMapTilesData().segments;
            for (String asset : mapFile.getAssetsData().assets) {
                editorFrames.getAssetImporter().addFile(asset);
            }
        }
        if (mapFile == null) {
            mapFile = new ExtractedMapFile(assetManager);
            // Load the map resources.
            mapFile.loadDefaults();
            loadedSegments = mapFile.getMapTilesData().segments;

            LoggerFactory.getLogger(EditorState.class).info("Loading from the default map.");
        }

        // Start the game session.
        gameSession = new GameSession(app, mapFile, stateRoot, stateGui);
        gameSession.setAreScriptsEnabled(false);

        // Initialize the editor keys
        initKeys(mapFile);

        // Set the camera.
        CameraState cameraState = app.getStateManager().getState(CameraState.class);
        if (cameraState != null) {
            cameraState.setFlySpeed(Data.getInstance().getConfigData().controls.editor.speed);
            cameraState.addMouseToggleCallback(new CameraState.MouseToggleCallback() {
                @Override
                public void execute(boolean isDragToRotate) {
                    setMouseLockedActions(isDragToRotate);
                }
            });
            cameraState.setDragToRotate(true);
        }

        createTargetCube(mapFile.getMapData().tileScale);

        mapFile.destroy();
    }

    /**
     * Initialize editor movement.
     * @param mapFile the map file to retrieve data from.
     */
    private void initKeys(MapFile mapFile) {
        editorActions = new EditorActions(application.getAssetManager(), application.getInputManager(), application.getCamera(),
                gameSession.getTerrain(), gameSession.getGridColliderSystem(), stateRoot, mapFile.getMapData().tileScale);
    }

    @Override
    public final void update(float tpf) {
        super.update(tpf);

        gameSession.update(tpf);

        // Update the targeting cube.
        updateTargetCube();
    }

    /**
     * Creates the targeting cube.
     * @param dimension the dimension of the cube.
     */
    private void createTargetCube(float dimension) {
        if (targetCube != null) {
            targetCube.removeFromParent();
        }

        // Create the targeted cube graphics.
        final float cubeBuffer = 0.01f;
        float cubeScale = dimension / 2f + dimension * cubeBuffer;
        Box cube = new Box(cubeScale, cubeScale, cubeScale);
        targetCube = new Geometry("Targeting Cube", cube);
        Material targetMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        Texture texture = assetManager.loadTexture("Core/Textures/TilePlacement.png");
        texture.setAnisotropicFilter(Data.getInstance().getConfigData().graphics.anisotropic);
        targetMaterial.setTexture("ColorMap", texture);
        targetMaterial.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        targetCube.setMaterial(targetMaterial);
        targetCube.setQueueBucket(RenderQueue.Bucket.Transparent);
        stateRoot.attachChild(targetCube);
    }

    /**
     * Updates the position of the targetting cube.
     */
    private void updateTargetCube() {
        GridColliderSystem collider = gameSession.getGridColliderSystem();
        Vector3Int index = collider.getClosestUsedGridFromRay(application.getCamera().getLocation(),
                application.getCamera().getDirection(), Data.getInstance().getConfigData().controls.editor.maxPlaceDistance);
        if (index == null) {
            targetCube.setCullHint(Spatial.CullHint.Always);
        } else {
            Vector3f gridPosition = collider.getPositionFromGrid(index.x, index.y, index.z);
            targetCube.setLocalTranslation(gridPosition.x, gridPosition.y, gridPosition.z);
            targetCube.setCullHint(Spatial.CullHint.Inherit);
        }
    }

    /**
     * Creates a new map.
     */
    private void newMap() {
        mapPath = null;
        application.getStateManager().detach(this);
        application.getStateManager().attach(this);
    }

    /**
     * Opens the map at the given path.
     * @param path the path to of the file to load.
     */
    private void openMap(String path) {
        if (!Strings.isNullOrEmpty(path)) {
            application.getStateManager().detach(this);
            application.getStateManager().attach(this);
            setMapPath(path);
        }
    }

    /**
     * Saves the map given the path.
     * Saves only the terrain for now.
     * @param path the path to save as.
     */
    private void saveMap(String path) {
        // Create the main segment which is the whole map.
        MapTilesData mapTilesData = new MapTilesData();
        MapTilesData.TileSegment mainSegment = new MapTilesData.TileSegment();
        byte[][][] tiles = gameSession.getTerrain().getTiles();
        mainSegment.dimensionX = tiles.length;
        mainSegment.dimensionY = tiles[0].length;
        mainSegment.dimensionZ = tiles[0][0].length;
        mainSegment.name = MAIN_SEGMENT_NAME;
        mainSegment.rawTiles = tiles;
        mapTilesData.segments.add(mainSegment);

        // Create the sub segments based on the dimensions.
        if (loadedSegments != null) {
            for (MapTilesData.TileSegment segment : loadedSegments) {
                if (segment.name.equals(MAIN_SEGMENT_NAME)) {
                    continue;
                }
                MapTilesData.TileSegment savedSegment = new MapTilesData.TileSegment();
                savedSegment.name = segment.name;
                savedSegment.dimensionX = segment.dimensionX;
                savedSegment.dimensionY = segment.dimensionY;
                savedSegment.dimensionZ = segment.dimensionZ;
                savedSegment.startX = segment.startX;
                savedSegment.startY = segment.startY;
                savedSegment.startZ = segment.startZ;
                // Set the tiles for the segment.
                savedSegment.rawTiles = new byte[segment.dimensionX][segment.dimensionY][segment.dimensionZ];
                for (int x = segment.startX; x < segment.dimensionX; x++) {
                    for (int y = segment.startY; y < segment.dimensionY; y++) {
                        for (int z = segment.startZ; z < segment.dimensionZ; z++) {
                            // Make sure the indices are within the main tile grid.
                            if (gameSession.getTerrain().isIndexOutOfBounds(x, y, z)) {
                                continue;
                            }
                            // Make sure the indices are within the segment grid.
                            int sX = x - segment.startX;
                            int sY = y - segment.startY;
                            int sZ = z - segment.startZ;
                            if (sX < 0 || sY < 0 || sZ < 0
                                    || sX >= segment.dimensionX || sY >= segment.dimensionY || sZ >= segment.dimensionZ) {
                                continue;
                            }
                            savedSegment.rawTiles[sX][sY][sZ] = tiles[x][y][z];
                        }
                    }
                }
                mapTilesData.segments.add(savedSegment);
            }
        }

        //TerrainData terrainData = gameSession.getTerrain().generateTerrainData();
            try {
                for (MapTilesData.TileSegment segment : mapTilesData.segments) {
                    segment.tiles = MapUtils.compressTiles(segment.rawTiles);
                    segment.rawTiles = null;
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String output = gson.toJson(mapTilesData, MapTilesData.class);
                FileUtils.write(new File(path), output);
                LoggerFactory.getLogger(EditorState.class).info("Save completed.");
            } catch (IOException e) {
                LoggerFactory.getLogger(EditorState.class).error("Unable to compress the tiles.", e);
            }

        /*
        // Create the terrain data.
        TerrainData terrainData = new TerrainData();
        terrainData.shading.isEnabled = gameSession.getTileAtlas().isShadingEnabled();
        terrainData.shading.shininess = gameSession.getTileAtlas().getShininess();
        terrainData.tileTypes.tileSheet = gameSession.getTileAtlas().getTileSheet();
        //terrainData.tileTypes.types = gameSession.getTileAtlas().getTileTypes();
        terrainData.rawTiles = gameSession.getTerrain().getTiles();

        //TerrainData terrainData = gameSession.getTerrain().generateTerrainData();
        try {
            terrainData.tiles = MapUtils.compressTiles(terrainData.rawTiles);
            byte[][][] decompress = MapUtils.decompressTiles(terrainData.tiles,
                    terrainData.rawTiles.length, terrainData.rawTiles[0].length, terrainData.rawTiles[0][0].length);
            byte[][][] rawTiles = terrainData.rawTiles;
            terrainData.rawTiles = null;
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String output = gson.toJson(terrainData, TerrainData.class);
            FileUtils.write(new File(path), output);
            LoggerFactory.getLogger(EditorState.class).info("Save completed.");
            for (int x = 0; x < rawTiles.length; x++) {
                for (int y = 0; y < rawTiles[x].length; y++) {
                    for (int z = 0; z < rawTiles[x][y].length; z++) {
                        if (rawTiles[x][y][z] != decompress[x][y][z]) {
                            DebugGlobals.println(rawTiles[x][y][z] + "  " + decompress[x][y][z]);
                        }
                    }
                }
            }
        } catch (IOException e) {
            LoggerFactory.getLogger(EditorState.class).error("Unable to compress the tiles.", e);
        }
        */

        /*
        if (!Strings.isNullOrEmpty(path) && mapFile != null) {
            mapFile.setTerrainData(gameSession.getTerrain().generateTerrainData());
            ExtractedMapFile.FileStatus status = mapFile.toMapFile(path);
            if (status == ExtractedMapFile.FileStatus.FAILED) {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        JOptionPane.showMessageDialog(frame, "Map not saved!\nAn error occurred when saving the file to disk.",
                                "Save Map Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
                LoggerFactory.getLogger(EditorState.class).error("Error occured when saving map.");
            }
        }
        */
    }

    /**
     * Loads a map file given the path.
     * @param mapPath the path of the map file.
     * @return the ExtractedMapFile or null if unsuccessful.
     */
    private ExtractedMapFile loadMap(String mapPath) {
        ExtractedMapFile loadedResources = new ExtractedMapFile(assetManager);
        try {
            loadedResources.fromMapFile(mapPath);
        } catch (IOException ex) {
            // Do not use the current map path as a save location when loading fails.
            editorFrames.getMainMenu().setCurrentFilePath(null);
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    JOptionPane.showMessageDialog(frame, "Unable to load map!\nAn error occurred when reading a file within the map.",
                            "Load Map Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            LoggerFactory.getLogger(EditorState.class).error("Error occurred when loading map.", ex);
            return null;
        }

        return loadedResources;
    }

    /**
     * Tests the current map.
     */
    private void testMap() {
        editorFrames.hide();
        String tempPath = Data.getInstance().getResourcePaths().getPath(PathNames.USER_DIRECTORY) + "/temp.pgm";
        mapPath = tempPath;
        saveMap(tempPath);
        application.getStateManager().detach(this);
        application.getStateManager().attach(new PlayState(this, tempPath));
    }

    /**
     * Toggles movement related to mouse locking.
     * @param isDragToRotate true to set it as locked.
     */
    private void setMouseLockedActions(boolean isDragToRotate) {
        if (!isDragToRotate) {
            editorActions.activate();
        } else {
            editorActions.deactivate();
        }
    }

    /**
     * Set the map file path to load from on initialization.
     * @param mapPath the path to load from.
     */
    public void setMapPath(String mapPath) {
        this.mapPath = mapPath;
    }

    @Override
    public final void cleanup() {
        super.cleanup();

        stateRoot.removeFromParent();
        stateGui.removeFromParent();

        editorActions.cleanup();

        gameSession.destroy();

        //mapFile.clear();
        editorFrames.getAssetImporter().deleteAllImports();

        System.gc();
    }
}
