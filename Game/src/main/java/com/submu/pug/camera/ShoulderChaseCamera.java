package com.submu.pug.camera;

import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Spatial;
import com.submu.pug.data.Data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/8/13
 * Time: 4:03 PM
 * Custom chase camera control that changes to first person when zoomed in.
 */
public class ShoulderChaseCamera extends ChaseCamera {
    /**
     * Default minimum distance of the camera.
     * This is unchangeable to prevent objects from clipping when too close.
     */
    private static final float MIN_CAM_DISTANCE = 0.01f;

    /**
     * Default maximum distance of the camera.
     */
    private static final float DEFAULT_MAX_CAM_DISTANCE = 5f;

    /**
     * Maximum camera distance.
     */
    private float maxCamDistance = DEFAULT_MAX_CAM_DISTANCE;

    /**
     * Default camera distance tolerance before becoming first person.
     */
    private static final float DEFAULT_CAM_DISTANCE_TOLERANCE = 0.15f;

    /**
     * Tolerance to switch to first person view.
     */
    private float camDistanceTolerance = DEFAULT_CAM_DISTANCE_TOLERANCE;

    /**
     * True if in first person mode.
     */
    private boolean isFirstPerson = false;

    /**
     * Default sensitivity of the camera rotation and chasing.
     */
    private static final float DEFAULT_CHASE_SENSITIVITY = 10000000f;

    /**
     * Target the camera follows.
     */
    private Spatial currentTarget;

    /**
     * First person view offset.
     */
    private Vector3f firstPersonOffset = new Vector3f();

    /**
     * Initializes the camera.
     * @param cam the camera to attach to.
     * @param inputManager the input manager to use with movement.
     */
    public ShoulderChaseCamera(Camera cam, InputManager inputManager) {
        super(cam, inputManager);

        setDragToRotate(false);

        // Set the min and max cam distance, where min will be first person view.
        setMinDistance(MIN_CAM_DISTANCE);
        // Set the max cam distance where max will be based on a preset amount and scaled with object bounds.
        setMaxDistance(maxCamDistance);
        setDefaultDistance(MIN_CAM_DISTANCE);

        // Set vertical rotation limits.
        final float rotationLimit = 100f;
        setMinVerticalRotation(-FastMath.HALF_PI + FastMath.PI / rotationLimit);
        setMaxVerticalRotation(FastMath.HALF_PI - FastMath.PI / rotationLimit);

        // Allow for smooth zooming but make chasing and rotation instant.
        setSmoothMotion(true);
        setChasingSensitivity(DEFAULT_CHASE_SENSITIVITY);
        setRotationSensitivity(DEFAULT_CHASE_SENSITIVITY);
    }

    /**
     * Updates the camera settings to the current object.
     */
    private void updateCameraSettings() {
        // Only update when attached to an object.
        if (currentTarget != null) {
            setInvertVerticalAxis(!Data.getInstance().getConfigData().controls.invertY);
            setRotationSpeed(Data.getInstance().getConfigData().controls.mouseSensitivity);
            setZoomSpeed(Data.getInstance().getConfigData().controls.mouseZoomSpeed);
            setLookAtOffset(firstPersonOffset);
        }
    }

    /**
     * Sets the followed target.
     * @param target the target to followEntity.
     * @param cameraComponent the data to use for the camera.
     */
    public void setTarget(Spatial target, CameraComponent cameraComponent) {
        // Remove control from previous target if needed and add to new target.
        if (currentTarget != null) {
            currentTarget.removeControl(this);
        }
        target.addControl(this);
        currentTarget = target;

        if (cameraComponent != null) {
            firstPersonOffset.set(cameraComponent.cameraOffsetX, cameraComponent.cameraOffsetY, cameraComponent.cameraOffsetZ);
            maxCamDistance = cameraComponent.maxCameraDistance;
        }

        // Set the max cam distance where max will be based on a preset amount and scaled with object bounds.
        setMaxDistance(maxCamDistance);
        setDefaultDistance(MIN_CAM_DISTANCE);

        updateCameraSettings();
    }

    /**
     * Sets the offset of the view.
     * @param offset the offset to set.
     */
    public void setOffset(Vector3f offset) {
        firstPersonOffset.set(offset);
    }

    /**
     * Sets the camera to first person mode.
     */
    private void setToFirstPerson() {
        if (!isFirstPerson) {
            isFirstPerson = true;
            target.setCullHint(Spatial.CullHint.Always);
        }
    }

    /**
     * Sets the camera to third person mode.
     */
    private void setToThirdPerson() {
        if (isFirstPerson) {
            isFirstPerson = false;
            target.setCullHint(Spatial.CullHint.Inherit);
        }
    }

    /**
     * Checks and binds the camera to within a certain degree of vertical rotation.
     */
    private void checkCameraVerticalRotation() {
        if (targetVRotation > maxVerticalRotation) {
            targetVRotation = maxVerticalRotation;
        }
        if ((targetVRotation < minVerticalRotation)) {
            targetVRotation = minVerticalRotation;
        }
    }

    /**
     * Updates the shoulder camera to see if it should be first or third person.
     * Additionally it limits the amount the camera can look up and down.
     */
    private void updateShoulderCamera() {
        // Rotate the camera around the model.
        // Set to first person view if set to closest else followEntity third person view rules.
        if (getDistanceToTarget() <= MIN_CAM_DISTANCE + camDistanceTolerance) {
            distance = MIN_CAM_DISTANCE;
            setToFirstPerson();
            checkCameraVerticalRotation();
        } else {
            setToThirdPerson();
        }

        cam.lookAt(targetLocation, initialUpVec);
    }

    @Override
    public void update(float tpf) {
        updateShoulderCamera();
        super.update(tpf);
    }

    /**
     * @return true if first person, false if third person view.
     */
    public boolean getIsFirstPerson() {
        return isFirstPerson;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (enabled) {
            updateCameraSettings();
        } else {
            if (currentTarget != null) {
                currentTarget.setCullHint(Spatial.CullHint.Inherit);
                currentTarget.removeControl(this);
                currentTarget = null;
            }
        }
    }

    /**
     * Disable drag to rotate and show mouse cursor.
     * @param dragToRotate true to go into drag to rotate mode with drag to rotate distabled.
     */
    @Override
    public void setDragToRotate(boolean dragToRotate) {
        super.setDragToRotate(dragToRotate);

        if (dragToRotate) {
            super.dragToRotate = false;
        }
    }

    /**
     * @param speed the speed to set the zooming at.
     */
    public void setZoomSpeed(float speed) {
        setZoomSensitivity(speed);
    }

    /**
     * Destroys the chase camera.
     */
    public void destroy() {
        if (target != null) {
            target.removeControl(this);
        }
        setEnabled(false);
        inputManager.setCursorVisible(true);

        // De-register inputs.
        inputManager.deleteMapping(ChaseCamDown);
        inputManager.deleteMapping(ChaseCamUp);
        inputManager.deleteMapping(ChaseCamZoomIn);
        inputManager.deleteMapping(ChaseCamZoomOut);
        inputManager.deleteMapping(ChaseCamMoveLeft);
        inputManager.deleteMapping(ChaseCamMoveRight);
        inputManager.deleteMapping(ChaseCamToggleRotate);
        inputManager.removeListener(this);

        // Reset the camera.
        cam.lookAtDirection(new Vector3f(0, 0, 0), Vector3f.UNIT_Y);
    }
}
