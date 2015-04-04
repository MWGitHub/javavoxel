package com.submu.pug.scripting.events;

import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/26/13
 * Time: 7:32 PM
 * Fires off events when the camera moves.
 */
public class EventCameraMoves implements EventHook {
    /**
     * Name of the camera movement event.
     */
    public static final String EVENT_CAMERA_MOVEMENT = "eventCameraMovement";

    /**
     * Camera to track movements of.
     */
    private Camera camera;

    /**
     * Last location of the camera.
     */
    private Vector3f lastLocation = new Vector3f();

    /**
     * Initializes the camera event hooks.
     * @param camera the camera to hook events to.
     */
    public EventCameraMoves(Camera camera) {
        this.camera = camera;
        lastLocation = camera.getLocation().clone();
    }

    @Override
    public void updateEvent(float tpf) {
        if (!lastLocation.equals(camera.getLocation())) {
            ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_CAMERA_MOVEMENT, camera.getLocation(), lastLocation));
        }
        lastLocation.set(camera.getLocation());
    }

    @Override
    public void destroyEvent() {
    }
}
