package com.halboom.pgt.terrainsystem;

import com.halboom.pgt.pgutil.threading.Threading;
import com.halboom.pgt.terrainsystem.generator.Noise;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/9/13
 * Time: 6:02 PM
 * Class to test the manager with.
 */
public final class App extends SimpleApplication {
    /**
     * Starts the test application.
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
        this.getStateManager().attach(new TerrainTest());
    }

    @Override
    public void destroy() {
        super.destroy();
        Threading.getInstance().destroy();
    }

    /**
     * Test state for the terrain.
     */
    private class TerrainTest extends AbstractAppState implements ActionListener {
        /**
         * Terrain of the application.
         */
        private Terrain terrain;

        /**
         * Testing quint.
         */
        private Quint quint;

        /**
         * True to enable wireframe.
         */
        private boolean isWireframe = false;


        @Override
        public void initialize(AppStateManager stateManager, Application app) {
            super.initialize(stateManager, app);

            Box b = new Box(Vector3f.ZERO, 1, 1, 1); // create a 1x1x1 box shape at the origin
            Geometry geom = new Geometry("Box", b);  // create a cube geometry from the box shape
            //Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
            //mat.setColor("Color", ColorRGBA.Yellow);   // set color of material to blue
            //mat.setTexture("ColorMap", assetManager.loadTexture("com.halboom.pgt.terrainsystem.assets/None.png"));
            //mat.getAdditionalRenderState().setWireframe(true);
            geom.setMaterial(mat);                   // set the cube geometry 's material
            rootNode.attachChild(geom);              // make the cube geometry appear in the scene

            Quad quad = new Quad(1, 1);
            geom = new Geometry("qg", quad);
            geom.setMaterial(mat);
            geom.setLocalTranslation(-3, 0, 0);
            rootNode.attachChild(geom);

            quint = new Quint(1, 1, new Vector3f(0.5f, 0.5f, 0.5f));
            geom = new Geometry("quint", quint);
            geom.setMaterial(mat);
            geom.setLocalTranslation(-6, 0, 0);
            rootNode.attachChild(geom);

            DirectionalLight sun = new DirectionalLight();
            sun.setColor(ColorRGBA.White);
            sun.setDirection(new Vector3f(1, -0.5f, -0.25f));
            rootNode.addLight(sun);

            AmbientLight light = new AmbientLight();
            light.setColor(ColorRGBA.White);
            rootNode.addLight(light);

            TileBank tileBank = new TileBank();
            TileAtlas tileAtlas = new TileAtlas(assetManager, tileBank, null);
            tileAtlas.setShadingEnabled(true);
            tileAtlas.setShininess(0.3f);
            terrain = new Terrain(tileAtlas, tileBank);
            terrain.generate(new Noise());
            terrain.attach(rootNode);

            getFlyByCamera().setMoveSpeed(10f);

            inputManager.addMapping("Mesh View", new KeyTrigger(KeyInput.KEY_T));
            inputManager.addListener(this, "Mesh View");
        }

        @Override
        public void update(float tpf) {
            super.update(tpf);

            //quint.setMidpoint(new Vector3f(FastMath.nextRandomFloat(), FastMath.nextRandomFloat(), FastMath.nextRandomFloat()));

            terrain.cull(Threading.getInstance().getExecutor(), getCamera().getLocation());
        }

        @Override
        public void onAction(String name, boolean isPressed, float tpf) {
            if (isPressed && name.equals("Mesh View")) {
                isWireframe = !isWireframe;
                rootNode.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial spatial) {
                        if (spatial instanceof Geometry) {
                            ((Geometry) spatial).getMaterial().getAdditionalRenderState().setWireframe(isWireframe);
                        }
                    }
                });
            }
        }
    }
}

