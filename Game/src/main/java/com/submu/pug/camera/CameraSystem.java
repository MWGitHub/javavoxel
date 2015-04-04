package com.submu.pug.camera;

import com.halboom.pgt.entityspatial.SpatialSystem;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.scene.Spatial;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/17/13
 * Time: 12:38 PM
 * System to handle camera focus.
 */
public class CameraSystem implements Subsystem {
    /**
     * Camera state to use.
     */
    private CameraState cameraState;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Spatial system to use.
     */
    private SpatialSystem spatialSystem;

    /**
     * Initializes the camera system.
     * @param cameraState the camera state to use.
     * @param entitySystem the entity system to use.
     * @param spatialSystem the spatial system to retrieve the entity spatial from.
     */
    public CameraSystem(CameraState cameraState, EntitySystem entitySystem, SpatialSystem spatialSystem) {
        this.cameraState = cameraState;
        this.entitySystem = entitySystem;
        this.spatialSystem = spatialSystem;
    }

    @Override
    public void update(float tpf) {
        boolean isCameraSet = false;
        // Attach the chase camera to the first entity found with the chase component.
        EntitySet entitySet = entitySystem.getEntities(ChaseCameraComponent.class);
        for (Entity entity : entitySet.getEntities()) {
            Spatial spatial = spatialSystem.getSpatial(entity);
            if (spatial != null) {
                CameraComponent cameraComponent = entitySystem.getComponent(entity, CameraComponent.class);
                cameraState.setFocus(spatial, cameraComponent);
                isCameraSet = true;
                break;
            }
        }
        if (!isCameraSet) {
            cameraState.removeFocus();
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
        cameraState.removeFocus();
    }
}
