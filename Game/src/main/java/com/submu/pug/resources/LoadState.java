package com.submu.pug.resources;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.resources.ResourcePaths;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.FileLocator;
import com.submu.pug.camera.CameraState;
import com.submu.pug.data.ConfigData;
import com.submu.pug.data.Data;
import com.submu.pug.data.GUIElementsData;
import com.submu.pug.data.GameData;
import com.submu.pug.data.ModelData;
import com.submu.pug.data.PathNames;
import com.submu.pug.processors.FogState;
import com.submu.pug.processors.ShadowState;
import com.submu.pug.util.math.Units;
import de.schlichtherle.truezip.file.TArchiveDetector;
import de.schlichtherle.truezip.file.TConfig;
import de.schlichtherle.truezip.fs.archive.zip.ZipDriver;
import de.schlichtherle.truezip.socket.sl.IOPoolLocator;
import org.apache.commons.io.FileUtils;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/28/12
 * Time: 3:11 PM
 * Loads configuration files.
 */
public class LoadState extends AbstractAppState {
    /**
     * State to switch to when loading is complete.
     */
    private AbstractAppState nextState;

    /**
     * Creates the class.
     * @param nextState the state to switch to when finished loading.
     */
    public LoadState(AbstractAppState nextState) {
        super();
        this.nextState = nextState;
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);

        Data data = Data.getInstance();
        // Read the capabilities of the graphics card.
        data.readCapabilities(app.getRenderer());

        // Set the resources paths
        ConfigData configData = data.getConfigData();
        ResourcePaths resourcePaths = data.getResourcePaths();
        // Set custom paths for assets
        AssetManager assetManager = app.getAssetManager();
        assetManager.registerLocator(resourcePaths.getPath(PathNames.ROOT_ASSETS_DIRECTORY), FileLocator.class);
        // Set a valid user path.
        assetManager.registerLocator(resourcePaths.getPath(PathNames.USER_DIRECTORY), FileLocator.class);

        // Register loader for text files.
        assetManager.registerLoader(TextLoader.class, "json", "txt", "cfg", configData.assets.mapExtension);

        // Load the data files.
        GameData gameData = Resources.loadJson((String) assetManager.loadAsset(configData.assets.defaults.gameData),
                GameData.class);
        data.setGameData(gameData);
        ModelData modelData = Resources.loadJson((String) assetManager.loadAsset(configData.assets.defaults.modelData),
                ModelData.class);
        data.setModelData(modelData);
        GUIElementsData guiElementsData = Resources.loadJson((String) assetManager.loadAsset(configData.assets.defaults.guiElementsData),
                GUIElementsData.class);
        data.setGuiElementsData(guiElementsData);

        // Set a path path for temporary files.
        assetManager.registerLocator(resourcePaths.getPath(PathNames.TEMP_DIRECTORY), FileLocator.class);

        // TODO: This does not work properly most of the time.
        try {
            FileUtils.forceDeleteOnExit(new File(resourcePaths.getPath(PathNames.TEMP_DIRECTORY)));
        } catch (IOException e) {
            LoggerFactory.getLogger(LoadState.class).warn("Unable to delete the temporary directory.", e);
        }

        // Set up the zip library
        TConfig tConfig = TConfig.get();
        tConfig.setArchiveDetector(new TArchiveDetector(configData.assets.mapExtension, new ZipDriver(IOPoolLocator.SINGLETON)));

        // Set game units
        Units.setTickrate(configData.logicRate);

        // Set debugging globals
        DebugGlobals.getInstance().setAssetManager(assetManager);

        // Attach states that are used by the other states.
        attachCommonStates(app);

        // Move to the next state and remove the load state.
        app.getStateManager().attach(nextState);
        app.getStateManager().detach(this);
    }

    /**
     * Attaches common states.
     * The states added here are guaranteed to be initialized before the main logic states are attached.
     * Do not reference other states within the initialize method of these states.
     * @param app the application to attach states to.
     */
    private void attachCommonStates(Application app) {
        // Attach the shadow state before the fog state.
        app.getStateManager().attach(new ShadowState());
        // Attach the fog state.
        app.getStateManager().attach(new FogState());
        // Attach the camera state.
        app.getStateManager().attach(new CameraState());
    }
}
