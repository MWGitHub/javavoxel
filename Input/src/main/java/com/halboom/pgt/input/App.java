package com.halboom.pgt.input;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;
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

    /**
     * Test state.
     */
    private class TestState extends AbstractAppState {
        /**
         * Simple application.
         */
        private SimpleApplication simpleApp;

        /**
         * Actions to test.
         */
        private Actions actions;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            simpleApp = (SimpleApplication) app;
            simpleApp.getFlyByCamera().setMoveSpeed(5);
            simpleApp.getCamera().setLocation(new Vector3f(0, 0, 5f));

            actions = new Actions(inputManager);
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

        }
    }

    /**
     * Actions to test with.
     */
    private class Actions extends InputActions {
        private static final String SPACE = "space",
        G = "g", REMOVE = "remove", REMAP = "remap";

        private Actions(InputManager inputManager) {
            super(inputManager);
            HotKey space = new HotKey(SPACE, new KeyTrigger(KeyInput.KEY_SPACE), new KeyTrigger(KeyInput.KEY_0));
            registerAction(space);
            registerAction(G, new KeyTrigger(KeyInput.KEY_G), new KeyTrigger(KeyInput.KEY_LSHIFT));
            registerAction(G, new KeyTrigger(KeyInput.KEY_G), new KeyTrigger(KeyInput.KEY_LSHIFT));
            registerAction(REMOVE, new KeyTrigger(KeyInput.KEY_R));
            registerAction(REMAP, new KeyTrigger(KeyInput.KEY_B));
        }

        @Override
        protected void onActionInput(String name, boolean isPressed, float tpf) {
            if (isPressed) {
                if (name.equals(SPACE)) {
                    System.out.println(SPACE + " Pressed");
                }
                if (name.equals(G)) {
                    System.out.println(G + " Pressed");
                }
                if (name.equals(REMOVE)) {
                    System.out.println(REMOVE + " Pressed");
                    removeAction(G);
                }
                if (name.equals(REMAP)) {
                    System.out.println(REMAP + " Pressed");
                    remapAction(G, new KeyTrigger(KeyInput.KEY_N));
                }
            }
        }

        @Override
        protected void onAnalogInput(String name, float value, float tpf) {
        }

        @Override
        protected void onUpdate(float tpf) {
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
