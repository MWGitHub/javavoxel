package com.halboom.pgt.pgui;

import com.halboom.pgt.pgui.widgets.Bar;
import com.halboom.pgt.pgui.widgets.Button;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;

import java.util.LinkedList;
import java.util.List;

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
         * Listener for GUI events.
         */
        private GUIKeyListener guiKeyListener;

        /**
         * Frame of the GUI.
         */
        private Frame frame;

        /**
         * Bars to test with.
         */
        private List<Bar> bars = new LinkedList<Bar>();
        /**
         * Bar amount to test with.
         */
        private float amount = 0.0f;

        /**
         * Button to test with.
         */
        private Button button;

        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);
            simpleApp = (SimpleApplication) app;
            simpleApp.getFlyByCamera().setMoveSpeed(5);
            simpleApp.getCamera().setLocation(new Vector3f(0, 0, 5f));
            simpleApp.getFlyByCamera().setEnabled(false);
            GUIDebug.setAssetManager(app.getAssetManager());

            assetManager.registerLocator("assets", FileLocator.class);

            // Create a light so models can be seen.
            DirectionalLight sun = new DirectionalLight();
            sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
            sun.setColor(ColorRGBA.White);
            rootNode.addLight(sun);

            AmbientLight globalLight = new AmbientLight();
            globalLight.setColor(ColorRGBA.White.mult(0.3f));
            rootNode.addLight(globalLight);

            guiKeyListener = new GUIKeyListener(inputManager);

            float width = cam.getWidth();
            float height = cam.getHeight();
            frame = new Frame();
            frame.setPosition(new Vector3f(width / 2, height / 2, 0));
            frame.attachRoot(guiNode);

             // Create the test bars.
            Bar bar = new Bar(assetManager, 30, 30);
            bars.add(bar);
            frame.attach(bar);

            // Textured bar.
            bar = new Bar(assetManager, 30, 30);
            bar.setPosition(new Vector3f(0, 50.0f, 0f));
            bar.setImage("Textures/FloorGrid.png");
            bar.setColor(255, 255, 255, 255);
            bars.add(bar);
            frame.attach(bar);

            // Long bar.
            bar = new Bar(assetManager, 30, 90);
            bar.setPosition(new Vector3f(50.0f, 0, 0f));
            bar.setType(Bar.Type.DOWN_UP);
            bar.setImage("Textures/FloorGrid.png");
            bar.setColor(255, 255, 255, 255);
            bars.add(bar);
            frame.attach(bar);

            // Create test buttons.
            button = new Button(assetManager, 30, 30);
            button.setPosition(new Vector3f(0, -50.0f, 0f));
            button.setGUIKeyListener(guiKeyListener);
            frame.attach(button);

            GUIDebug.showObjectBounds(frame);
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

            frame.update(tpf);

            amount += 0.0001f;
            if (amount > 1) {
                amount = 0;
            }
            for (Bar bar : bars) {
                bar.setAmount(amount);
            }

            guiKeyListener.update(tpf);
        }
    }
}
