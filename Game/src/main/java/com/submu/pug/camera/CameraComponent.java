package com.submu.pug.camera;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/24/13
 * Time: 5:49 PM
 * Component for camera properties on the entity.
 */
public class CameraComponent implements Component {
    /**
     * Camera offsets.
     */
    public float cameraOffsetX = 0, cameraOffsetY = 0, cameraOffsetZ = 0;

    /**
     * Minimum distance the camera can be moved in.
     */
    public float minCameraDistance = 0;

    /**
     * Maximum distance the camera can be moved back.
     */
    public float maxCameraDistance = 10f;

    @Override
    public Component copy() {
        CameraComponent output = new CameraComponent();
        output.cameraOffsetX = cameraOffsetX;
        output.cameraOffsetY = cameraOffsetY;
        output.cameraOffsetZ = cameraOffsetZ;
        output.minCameraDistance = minCameraDistance;
        output.maxCameraDistance = maxCameraDistance;

        return output;
    }
}
