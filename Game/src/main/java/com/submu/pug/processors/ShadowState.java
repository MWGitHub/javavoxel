package com.submu.pug.processors;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.math.Vector3f;
import com.jme3.shadow.PssmShadowRenderer;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/26/13
 * Time: 12:40 AM
 * Adds shadows to the state.
 */
public class ShadowState extends AbstractAppState {
    /**
     * Application to add the processor to.
     */
    private Application app;

    /**
     * Shadow renderer for graphic cards that support it (Intel HD 4000).
     */
    private PssmShadowRenderer shadowRenderer;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;

        final int size = 1024;
        final int splits = 2;
        // Use this renderer for now for graphics card compatibility.
        shadowRenderer = new PssmShadowRenderer(app.getAssetManager(), size, splits);
        enable();
    }

    /**
     * Enables the shadows.
     */
    public void enable() {
        app.getViewPort().addProcessor(shadowRenderer);
    }

    /**
     * Disables the shadows.
     */
    public void disable() {
        app.getViewPort().removeProcessor(shadowRenderer);
    }

    /**
     * Sets the direction of the shadow.
     * @param direction the direction to set.
     */
    public void setShadowDirection(Vector3f direction) {
        shadowRenderer.setDirection(direction);
    }

    @Override
    public void cleanup() {
        super.cleanup();

        app.getViewPort().removeProcessor(shadowRenderer);
    }
}
