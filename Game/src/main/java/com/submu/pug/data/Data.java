package com.submu.pug.data;

import com.halboom.pgt.resources.ResourcePaths;
import com.jme3.renderer.Caps;
import com.jme3.renderer.Renderer;
import com.submu.pug.resources.Resources;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 12/28/12
 * Time: 3:32 PM
 * Holds data used across the application and provides utility methods for data loading.
 * No map specific data should be here.
 */
public class Data {
    private static Data ourInstance = new Data();

    // Capabilities of the renderer.
    Collection<Caps> capabilities;

    // Resource loader used to load other resources.
    private Resources resources;
    // Data used for the window configuration.
    private ConfigData configData;
    // Data used for the game configuration.
    private GameData gameData;
    // Data fields that are common to all models.
    private ModelData modelData;
    // Data fields for gui positioning.
    private GUIElementsData guiElementsData;

    /**
     * Paths for resources.
     */
    private ResourcePaths resourcePaths;

    public static Data getInstance() {
        return ourInstance;
    }

    private Data() {
        resources = new Resources();
        resourcePaths = new ResourcePaths();
    }

    /**
     * Reads the capabilities and stores them.
     * @param renderer the renderer to get the capabilities from.
     */
    public void readCapabilities(Renderer renderer) {
        capabilities = renderer.getCaps();
    }

    /**
     * Checks if a capability is supported.
     * @param cap the capability to check with.
     * @return true if supported.
     */
    public boolean hasCapability(Caps cap) {
        for (Caps capability : capabilities) {
            if (cap == capability) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return the resources for the application.
     */
    public Resources getResources() {
        return resources;
    }

    /**
     * @return the resource paths for the application.
     */
    public ResourcePaths getResourcePaths() {
        return resourcePaths;
    }

    /**
     * @return the configuration data for the application.
     */
    public ConfigData getConfigData() {
        return configData;
    }

    /**
     * Sets the config data and updates the resource paths.
     * @param configData the configuration data to set.
     */
    public void setConfigData(ConfigData configData) {
        resourcePaths.addPath(PathNames.ROOT_ASSETS_DIRECTORY, configData.assets.path);
        resourcePaths.addPath(PathNames.USER_DIRECTORY, configData.assets.user);
        resourcePaths.addPath(PathNames.CORE_ASSETS_DIRECTORY, resourcePaths.getPath(PathNames.ROOT_ASSETS_DIRECTORY)
                + configData.assets.subfolders.core);
        resourcePaths.addPath(PathNames.TEMP_DIRECTORY, resourcePaths.getPath(PathNames.USER_DIRECTORY)
                + configData.assets.subfolders.temp);
        resourcePaths.addPath(PathNames.MAP_DIRECTORY, resourcePaths.getPath(PathNames.TEMP_DIRECTORY)
                + configData.assets.subfolders.map);
        this.configData = configData;
    }

    /**
     * @return the game data for the world.
     */
    public GameData getGameData() {
        return gameData;
    }

    /**
     * @param gameData the game data to set.
     */
    public void setGameData(GameData gameData) {
        this.gameData = gameData;
    }

    /**
     * @return the model data format for the models.
     */
    public ModelData getModelData() {
        return modelData;
    }

    /**
     * @param modelData the model data to set.
     */
    public void setModelData(ModelData modelData) {
        this.modelData = modelData;
    }

    /**
     * @return the gui elements data.
     */
    public GUIElementsData getGuiElementsData() {
        return guiElementsData;
    }

    /**
     * @param guiElementsData the gui elements data to set.
     */
    public void setGuiElementsData(GUIElementsData guiElementsData) {
        this.guiElementsData = guiElementsData;
    }
}
