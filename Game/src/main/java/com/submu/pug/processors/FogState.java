package com.submu.pug.processors;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.math.ColorRGBA;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/18/13
 * Time: 5:15 PM
 * Creates fog.
 */
public class FogState extends AbstractAppState {
    /**
     * Application that the state is attached to.
     */
    private Application app;

    /**
     * Filter post processor to add for the fog.
     */
    private FilterPostProcessor filter;

    /**
     * Fog filter used for the fog.
     */
    private CustomFogFilter fogFilter;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        // Add fog
        generateFog(app.getAssetManager());
    }

    /**
     * Generates the fog.
     * @param assetManager the assetManager to load the shaders from.
     */
    private void generateFog(AssetManager assetManager) {
        filter = new FilterPostProcessor(assetManager);
        fogFilter = new CustomFogFilter();
        fogFilter.setFogMode(CustomFogFilter.FOG_MODE.EXP2_DISTANCE_TO_INFINITY);
        fogFilter.setExcludeSky(true);
        filter.addFilter(fogFilter);

        FXAAFilter fxaaFilter = new FXAAFilter();
        fxaaFilter.setReduceMul(0.0f);
        fxaaFilter.setSubPixelShift(0.0f);
        filter.addFilter(fxaaFilter);

        app.getViewPort().addProcessor(filter);
    }

    /**
     * @param color the color of the fog to set.
     */
    public void setFogColor(ColorRGBA color) {
        fogFilter.setFogColor(color);
    }

    /**
     * @param density the density of the fog to set.
     */
    public void setFogDensity(float density) {
        fogFilter.setFogDensity(density);
    }

    /**
     * @param distance the distance of the fog to set.
     */
    public void setFogStartDistance(float distance) {
        fogFilter.setFogStartDistance(distance);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        app.getViewPort().removeProcessor(filter);
    }
}
