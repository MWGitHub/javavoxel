package com.halboom.pgt.debug;

import com.halboom.pgt.input.InputActions;
import com.jme3.app.SimpleApplication;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.SceneGraphVisitor;
import com.jme3.scene.Spatial;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/10/13
 * Time: 2:07 PM
 * Debugging hotkeys for both the editor and game.
 */
public class DebugTools extends InputActions {
    /**
     * The application to debug.
     */
    private SimpleApplication app;

    /**
     * Node containing all of the in game objects.
     */
    private Node rootNode;

    /**
     * True to toggle wireframe on.
     */
    private boolean isWireframe = false;

    /**
     * True to show the stats.
     */
    private boolean areStatsShown = true;

    /**
     * True to show the fps.
     */
    private boolean isFpsShown = true;

    /**
     * Grid geometry to show starting from the camera location.
     */
    private Grid grid;
    /**
     * Offset for the grid's Y axis.
     */
    private static final float GRID_HEIGHT_OFFSET = 5f;
    /**
     * Offset amount of the grid when attaching the object.
     */
    private float heightOffset = GRID_HEIGHT_OFFSET;

    /**
     * Compass showing the direction and location of the camera.
     */
    private Compass compass;
    /**
     * Default compass scale for the GUI.
     */
    private static final float COMPASS_SCALE = 50.0f;
    /**
     * Amount the camera goes into the screen from the top left corner.
     */
    private static final float COMPASS_INSET = 60.0f;

    /**
     * Initializes the movement.
     * @param app the app to use with the movement.
     */
    public DebugTools(SimpleApplication app) {
        super(app.getInputManager());

        this.app = app;
        this.rootNode = app.getRootNode();
        grid = new Grid(app.getAssetManager());
        compass = new Compass(app.getAssetManager());

        registerAction(DebugKeys.toggleWireframe);
        registerAction(DebugKeys.toggleGrid);
        registerAction(DebugKeys.toggleStats);
        registerAction(DebugKeys.toggleFPS);
        registerAction(DebugKeys.toggleAxis);
    }

    @Override
    protected final void onActionInput(String name, boolean isPressed, float tpf) {
        if (!isPressed) {
            // Enable wireframe
            if (name.equals(DebugKeys.toggleWireframe.name)) {
                isWireframe = !isWireframe;
                rootNode.depthFirstTraversal(new SceneGraphVisitor() {
                    @Override
                    public void visit(Spatial spatial) {
                        if (spatial instanceof Geometry) {
                            ((Geometry) spatial).getMaterial().getAdditionalRenderState().setWireframe(isWireframe);
                        }
                    }
                });
            } else if (name.equals(DebugKeys.toggleStats.name)) {
                areStatsShown = !areStatsShown;
                app.setDisplayStatView(areStatsShown);
            } else if (name.equals(DebugKeys.toggleFPS.name)) {
                isFpsShown = !isFpsShown;
                app.setDisplayFps(isFpsShown);
            } else if (name.equals(DebugKeys.toggleGrid.name)) {
                grid.toggle(rootNode);
            } else if (name.equals(DebugKeys.toggleAxis.name)) {
                compass.toggle(app.getGuiNode());
                Camera camera = app.getCamera();
                compass.setPosition(new Vector3f(COMPASS_INSET, camera.getHeight() - COMPASS_INSET, 0));
                compass.setScale(COMPASS_SCALE);
            }
        }
    }

    @Override
    protected void onAnalogInput(String name, float value, float tpf) {
    }

    @Override
    protected void onUpdate(float tpf) {
        // Shift the grid to match the location.
        if (grid.isAttached()) {
            grid.setPosition(app.getCamera().getLocation().subtract(0, heightOffset, 0));
        }
        // Update the compass' direction if attached to the GUI.
        if (compass.isAttached()) {
            compass.setRotation(app.getCamera().getRotation());
            Vector3f cameraPosition = app.getCamera().getLocation();
            compass.setCoordinateLabels(cameraPosition.x, cameraPosition.y, cameraPosition.z);
        }
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

    /**
     * Remaps all the hot keys.
     */
    public void remapHotKeys() {
        remapAction(DebugKeys.toggleWireframe);
        remapAction(DebugKeys.toggleGrid);
        remapAction(DebugKeys.toggleStats);
        remapAction(DebugKeys.toggleFPS);
        remapAction(DebugKeys.toggleAxis);
    }

    /**
     * @return the debug grid.
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * @return the debug compass.
     */
    public Compass getCompass() {
        return compass;
    }
}
