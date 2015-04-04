package com.submu.pug.camera;

import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.submu.pug.data.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/18/13
 * Time: 6:20 PM
 * Manages the camera for the game and allows attachments of special cameras.
 */
public class CameraState extends AbstractAppState implements ActionListener {
    /**
     * Action name for toggling mouse lock.
     */
    private static final String ACTION_TOGGLE_MOUSE_LOCK = "Toggle Mouse Lock";

    /**
     * Application the state is attached to.
     */
    private SimpleApplication application;

    /**
     * Chase camera for the game.
     */
    private ShoulderChaseCamera chaseCamera;

    /**
     * Callback that runs when mouse lock is toggled.
     */
    private List<MouseToggleCallback> mouseToggleCallbacks = new ArrayList<MouseToggleCallback>();

    /**
     * True to show the mouse and use dragging to rotate the screen.
     */
    private boolean isDragToRotate = false;

    /**
     * Current focus of the camera.
     */
    private Spatial currentFocus;

    /**
     * Creates the state.
     */
    public CameraState() {
    }

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        application = (SimpleApplication) app;

        Camera camera = app.getCamera();
        // Set the camera up and limit the view distance
        final float fovY = 45f;
        final float near = 0.01f;
        camera.setFrustumPerspective(fovY, (float) camera.getWidth() / (float) camera.getHeight(),
                near, Data.getInstance().getConfigData().graphics.viewDistance);
        camera.update();

        application.getFlyByCamera().setDragToRotate(true);

        InputManager inputManager = app.getInputManager();
        chaseCamera = new ShoulderChaseCamera(camera, inputManager);

        // Add mouse display toggling.
        inputManager.addMapping(ACTION_TOGGLE_MOUSE_LOCK, new KeyTrigger(KeyInput.KEY_TAB), new KeyTrigger(KeyInput.KEY_V));
        inputManager.addListener(this, ACTION_TOGGLE_MOUSE_LOCK);

        application.getFlyByCamera().setEnabled(false);
        chaseCamera.setEnabled(false);
    }

    /**
     * Sets the scale at which the camera's maximum draw will be changed by.
     * @param scale the scale to modify the view distance by.
     */
    public void setTileScale(float scale) {
        Camera camera = application.getCamera();
        // Set the camera up and limit the view distance
        final float fovY = 45f;
        final float near = 0.01f;
        camera.setFrustumPerspective(fovY, (float) camera.getWidth() / (float) camera.getHeight(),
                near, Data.getInstance().getConfigData().graphics.viewDistance * scale);
        camera.update();
    }

    /**
     * Sets the active camera to focus on a specific object.
     * @param object the object to focus on.
     * @param cameraComponent the data to use for the camera settings.
     */
    public void setFocus(Spatial object, CameraComponent cameraComponent) {
        if (object == null) {
            removeFocus();
        } else {
            if (currentFocus != object) {
                application.getFlyByCamera().setEnabled(false);
                chaseCamera.setTarget(object, cameraComponent);
                chaseCamera.setEnabled(true);
                currentFocus = object;
            }
        }
    }

    /**
     * Removes the focus from an object.
     */
    public void removeFocus() {
        application.getFlyByCamera().setEnabled(true);
        chaseCamera.setEnabled(false);
        currentFocus = null;
    }

    /**
     * @param speed the speed of the fly by camera to set.
     */
    public void setFlySpeed(float speed) {
        application.getFlyByCamera().setMoveSpeed(speed);
    }

    @Override
    public void onAction(String name, boolean keyPressed, float tpf) {
        if (keyPressed && name.equals(ACTION_TOGGLE_MOUSE_LOCK)) {
            isDragToRotate = !isDragToRotate;
            setDragToRotate(isDragToRotate);
        }
    }

    /**
     * Set to true to lock the mouse and hide the cursor.
     * @param isDragToRotate true if setting to locked.
     */
    public void setDragToRotate(boolean isDragToRotate) {
        this.isDragToRotate = isDragToRotate;
        chaseCamera.setDragToRotate(isDragToRotate);
        application.getFlyByCamera().setDragToRotate(isDragToRotate);
        for (MouseToggleCallback callback : mouseToggleCallbacks) {
            callback.execute(isDragToRotate);
        }
    }

    /**
     * Adds a callback function to the mouse toggling.
     * @param callback the callback function to add.
     */
    public void addMouseToggleCallback(MouseToggleCallback callback) {
        mouseToggleCallbacks.add(callback);
    }

    /**
     * Removes a callback from the mouse toggling.
     * @param callback the callback to remove.
     */
    public void removeMouseToggleCallback(MouseToggleCallback callback) {
        mouseToggleCallbacks.remove(callback);
    }

    /**
     * Removes callbacks.
     */
    public void removeCallbacks() {
        mouseToggleCallbacks.clear();
    }

    @Override
    public void cleanup() {
        super.cleanup();

        chaseCamera.destroy();

        InputManager inputManager = application.getInputManager();
        inputManager.deleteMapping(ACTION_TOGGLE_MOUSE_LOCK);
        inputManager.removeListener(this);
    }

    /**
     * Callback function for mouse toggling.
     */
    public interface MouseToggleCallback {
        /**
         * Executes the callback function.
         * @param isDragToRotate true if the mouse is enabled.
         */
        void execute(boolean isDragToRotate);
    }
}
