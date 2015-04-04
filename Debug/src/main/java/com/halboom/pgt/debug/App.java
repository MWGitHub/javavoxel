package com.halboom.pgt.debug;

import com.halboom.pgt.input.InputActions;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/22/13
 * Time: 10:08 AM
 */
public class App extends SimpleApplication {
    /**
     * Starts the application.
     * @param args the arguments to pass in.
     */
    public static void main(String[] args) {
        App app = new App();
        app.setShowSettings(false);
        AppSettings settings = new AppSettings(true);
        settings.setWidth(1280);
        settings.setHeight(720);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        this.getStateManager().attach(new TestState());
    }

    private class TestState extends AbstractAppState {
        /**
         * Simple application.
         */
        private SimpleApplication simpleApp;

        /**
         * Debug actions to test.
         */
        private DebugTools debugTools;

        /**
         * Inputs to drive the test.
         */
        private TestInput testInput;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            simpleApp = (SimpleApplication) app;
            simpleApp.getFlyByCamera().setMoveSpeed(10);
            simpleApp.getCamera().setLocation(new Vector3f(0, 0, 5f));

            assetManager.registerLocator("assets", FileLocator.class);

            // Create a light so models can be seen.
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            rootNode.addLight(sun);

            AmbientLight globalLight = new AmbientLight();
            globalLight.setColor(ColorRGBA.White.mult(0.3f));
            rootNode.addLight(globalLight);

            debugTools = new DebugTools(simpleApp);
            testInput = new TestInput(inputManager);
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

            debugTools.update(tpf);
            testInput.update(tpf);
            debugTools.getGrid().setColor(ColorRGBA.randomColor());
        }


        /**
         * Inputs for testing the debug state.
         */
        private class TestInput extends InputActions {
            /**
             * Actions for the tests.
             */
            private static final String KEY_RANDOM_DISTANCE = "random distance",
            KEY_RANDOM_SIZE = "random size",
            KEY_RANDOM_COLOR = "random color";

            /**
             * Flag to set size when updating.
             */
            private boolean wasDistanceChanged = false,
            wasSizeChanged = false,
            wasColorChanged = false;


            /**
             * Initialize the input.
             * @param inputManager the input manager to use.
             */
            private TestInput(InputManager inputManager) {
                super(inputManager);

                registerAction(KEY_RANDOM_DISTANCE, new KeyTrigger(KeyInput.KEY_I));
                registerAction(KEY_RANDOM_SIZE, new KeyTrigger(KeyInput.KEY_O));
                registerAction(KEY_RANDOM_COLOR, new KeyTrigger(KeyInput.KEY_P));
            }

            @Override
            protected void onActionInput(String name, boolean isPressed, float tpf) {
                if (name.equals(KEY_RANDOM_DISTANCE)) {
                    wasDistanceChanged = isPressed;
                }
                if (name.equals(KEY_RANDOM_SIZE)) {
                    wasSizeChanged = isPressed;
                }
                if (name.equals(KEY_RANDOM_COLOR)) {
                    wasColorChanged = isPressed;
                }
            }

            @Override
            protected void onAnalogInput(String name, float value, float tpf) {
            }

            @Override
            protected void onUpdate(float tpf) {
                if (wasDistanceChanged) {
                    debugTools.getGrid().setDistance((float) Math.random() * 3.0f + 0.01f);
                }
                if (wasSizeChanged) {
                    debugTools.getGrid().setSize((int) (Math.random() * 100 + 1));
                }
                if (wasColorChanged) {
                    debugTools.getGrid().setColor(ColorRGBA.randomColor());
                }
                wasDistanceChanged = false;
                wasSizeChanged = false;
                wasColorChanged = false;
            }

            @Override
            protected void onActivated() {
            }

            @Override
            protected void onDeactivated() {
            }

            @Override
            protected void cleanupAction() {
            }
        }
    }
}
