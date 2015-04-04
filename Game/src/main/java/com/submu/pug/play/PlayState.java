package com.submu.pug.play;

import com.halboom.pgt.debug.DebugGlobals;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import com.submu.pug.camera.CameraState;
import com.submu.pug.game.GameSession;
import com.submu.pug.resources.map.CoreMapFile;
import com.submu.pug.resources.map.MapFile;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/27/12
 * Time: 12:16 PM
 * Represents the state for the game.
 */
public class PlayState extends AbstractAppState {
    /**
     * Simple application cast of the app.
     */
    private SimpleApplication application;

    /**
     * Asset manager of the application.
     */
    private AssetManager assetManager;

    /**
     * State to return to when exiting a map.
     */
    private AbstractAppState returnState;

    /**
     * Path of the map to use.
     */
    private String mapPath;

    /**
     * Resources for the map.
     */
    //private ExtractedMapFile mapFile;

    /**
     * Root node for the state.
     */
    private Node stateRoot;

    /**
     * Root gui node for the state.
     */
    private Node stateGui;

    /**
     * Session for the game.
     */
    private GameSession gameSession;

    /**
     * Actions for the play state.
     */
    private PlayActions playActions;

    /**
     * Creates the state without a default map.
     */
    public PlayState() {
        super();
    }

    /**
     * Creates the state.
     * @param returnState the state to return to when exiting the game.
     * @param mapPath the path for the map to load.
     */
    public PlayState(AbstractAppState returnState, String mapPath) {
        this();

        this.returnState = returnState;
        this.mapPath = mapPath;
    }

    @Override
    public final void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

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

        // Load the map.
        /*
        ExtractedMapFile mapFile = new ExtractedMapFile(assetManager);
        if (mapPath != null) {
            try {
                mapFile.fromMapFile(mapPath);
            } catch (IOException ex) {
                mapFile = null;
            }
        } else {
            mapFile = null;
        }
        if (mapFile == null) {
            mapFile = new ExtractedMapFile(assetManager);
            // Load the map resources.
            mapFile.loadDefaults();

            LoggerFactory.getLogger(PlayState.class).info("Loading from the default map.");
        }
        */
        MapFile mapFile = new CoreMapFile(assetManager);

        // Start the game session.
        gameSession = new GameSession(app, mapFile, stateRoot, stateGui);

        // Initialize the editor keys
        initKeys();

        CameraState cameraState = app.getStateManager().getState(CameraState.class);
        if (cameraState != null) {
            cameraState.setDragToRotate(false);
        }

        mapFile.destroy();
    }

    /**
     * Initialize editor movement.
     */
    private void initKeys() {
        playActions = new PlayActions(application.getInputManager());
        playActions.setQuitCallback(new PlayActions.PlayerActionCallback() {
            @Override
            public void execute() {
                onQuit();
            }
        });
    }

    @Override
    public final void update(float tpf) {
        super.update(tpf);

        gameSession.update(tpf);
    }

    /**
     * Quits the play state and returns to the previous state.
     */
    private void onQuit() {
        application.getStateManager().detach(this);
        if (returnState != null) {
            application.getStateManager().attach(returnState);
        }
    }

    @Override
    public final void cleanup() {
        super.cleanup();

        stateRoot.removeFromParent();
        stateGui.removeFromParent();

        playActions.cleanup();

        gameSession.destroy();

        //mapFile.clear();

        returnState = null;

        System.gc();
    }
}
