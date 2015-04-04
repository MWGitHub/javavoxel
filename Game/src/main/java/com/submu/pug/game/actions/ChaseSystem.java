package com.submu.pug.game.actions;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.halboom.pgt.input.InputActions;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.submu.pug.data.KeyMap;
import com.submu.pug.game.objects.components.MoveCommandComponent;
import com.submu.pug.game.objects.components.WalkComponent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/1/13
 * Time: 11:35 AM
 * Handles movement based on a chase camera type view but does not modify
 * the camera itself.
 * TODO: Handle the conflicts between the updates for subsystem.
 */
public class ChaseSystem extends InputActions implements Subsystem {
    /**
     * Movement direction flags.
     */
    private static final int MOVED_NONE = 0,
    MOVED_FORWARD = 1,
    MOVED_BACKWARD = 2,
    MOVED_LEFT = 4,
    MOVED_RIGHT = 8,
    MOVED_UP = 16,
    MOVED_DOWN = 32;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Camera to modify the direction of.
     */
    private Camera camera;

    /**
     * Direction the object should be moving.
     */
    private Vector3f direction = new Vector3f();

    /**
     * Direction the object has moved this frame.
     */
    private int movementDirection = 0;

    /**
     * Initializes the movement.
     * @param entitySystem the entity system to use.
     * @param inputManager the input manager to use.
     * @param camera the camera to use to determine movement direction.
     */
    public ChaseSystem(EntitySystem entitySystem, InputManager inputManager, Camera camera) {
        super(inputManager);
        this.entitySystem = entitySystem;
        this.camera = camera;

        registerAction(KeyMap.moveForward);
        registerAction(KeyMap.moveBackward);
        registerAction(KeyMap.moveLeft);
        registerAction(KeyMap.moveRight);
        registerAction(KeyMap.moveUp);
        registerAction(KeyMap.moveDown);
    }

    @Override
    protected void onActionInput(String name, boolean isPressed, float tpf) {
    }
    /**
     * Checks if vertical movement is allowed.
     * @param entity the entity to check movement of.
     * @return true if allowed.
     */
    private boolean isVerticalAllowed(Entity entity) {
        WalkComponent walkComponent = entitySystem.getComponent(entity, WalkComponent.class);
        if (walkComponent != null) {
            return walkComponent.isYMovementAllowed;
        }
        return false;
    }

    /**
     * Sanitizes the movement direction bits for opposite directions.
     * @param entity the entity to sanitize the movement direction of.
     * @param movementDirection the movement direction bits to sanitize.
     * @return the sanitized movement direction.
     */
    private int sanitizeMovementDirection(Entity entity, int movementDirection) {
        int cleanDirection = movementDirection;
        // Disallow movements in opposite directions.
        if ((cleanDirection & MOVED_FORWARD) != 0 && (cleanDirection & MOVED_BACKWARD) != 0) {
            cleanDirection = (cleanDirection & ~MOVED_FORWARD) & ~MOVED_BACKWARD;
        }
        if ((cleanDirection & MOVED_LEFT) != 0 && (cleanDirection & MOVED_RIGHT) != 0) {
            cleanDirection = (cleanDirection & ~MOVED_LEFT) & ~MOVED_RIGHT;
        }
        if ((cleanDirection & MOVED_UP) != 0 && (cleanDirection & MOVED_DOWN) != 0) {
            cleanDirection = (cleanDirection & ~MOVED_UP) & ~MOVED_DOWN;
        }

        // Check if the vertical direction should be applied.
        if (!isVerticalAllowed(entity)) {
            cleanDirection = (cleanDirection & ~MOVED_UP) & ~MOVED_DOWN;
        }

        return cleanDirection;
    }


    /**
     * Moves if directional inputs were made.
     * @param entity the entity to update the movement of.
     * @param camera the camera to use for the forward direction.
     * @param movementDirection the movement direction bit.
     */
    private void updateMovement(Entity entity, Camera camera, int movementDirection) {
        if (movementDirection != MOVED_NONE) {
            // Only have a forward direction if forward or backwards is pressed.
            camera.getDirection(direction);
            if ((movementDirection & MOVED_FORWARD) == 0 && (movementDirection & MOVED_BACKWARD) == 0) {
                direction.set(Vector3f.ZERO);
            }

            // Check if should move up or down.
            if (!isVerticalAllowed(entity)) {
                direction.y = 0;
                direction.normalizeLocal();
            }
            // Move in the specified directions.
            if ((movementDirection & MOVED_BACKWARD) != 0) {
                direction.multLocal(-1);
            }
            if ((movementDirection & MOVED_LEFT) != 0) {
                direction.addLocal(camera.getLeft());
            } else if ((movementDirection & MOVED_RIGHT) != 0) {
                direction.addLocal(camera.getLeft().negate());
            }
            // Normalize the horizontal movement.
            direction.normalizeLocal();
            // Set the vertical movement for just up and down.
            if ((movementDirection & MOVED_UP) != 0) {
                direction.y = 1;
            } else if ((movementDirection & MOVED_DOWN) != 0) {
                direction.y = -1;
            }

            // Set the direction of the walking angle.
            MoveCommandComponent moveCommandComponent = entitySystem.getComponent(entity, MoveCommandComponent.class);
            if (moveCommandComponent == null) {
                moveCommandComponent = new MoveCommandComponent();
            }
            moveCommandComponent.directionX = direction.x;
            moveCommandComponent.directionY = direction.y;
            moveCommandComponent.directionZ = direction.z;
            entitySystem.setComponent(entity, moveCommandComponent);
        }
    }

    /**
     * Handles the movement and object looking direction.
     * @param name the name of the key.
     * @param value the analog value.
     * @param tpf the time passed since the last frame.
     */
    @Override
    protected final void onAnalogInput(String name, float value, float tpf) {
        if (name.equals(KeyMap.moveForward.name)) {
            movementDirection = movementDirection | MOVED_FORWARD;
        } else if (name.equals(KeyMap.moveBackward.name)) {
            movementDirection = movementDirection | MOVED_BACKWARD;
        }
        if (name.equals(KeyMap.moveLeft.name)) {
            movementDirection = movementDirection | MOVED_LEFT;
        } else if (name.equals(KeyMap.moveRight.name)) {
            movementDirection = movementDirection | MOVED_RIGHT;
        }
        if (name.equals(KeyMap.moveUp.name)) {
            movementDirection = movementDirection | MOVED_UP;
        } else if (name.equals(KeyMap.moveDown.name)) {
            movementDirection = movementDirection | MOVED_DOWN;
        }
    }

    /**
     * Updates the movement direction and rotation.
     * @param tpf the time passed since the last frame.
     */
    @Override
    protected final void onUpdate(float tpf) {
        EntitySet entitySet = entitySystem.getEntities(ChaseComponent.class);
        for (Entity entity : entitySet.getEntities()) {
            int cleanDirection = sanitizeMovementDirection(entity, movementDirection);
            // Move the object and cancel conflicting moves.
            updateMovement(entity, camera, cleanDirection);
        }

        // Reset the movement direction for the next frame.
        movementDirection = MOVED_NONE;
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

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
        cleanup();
    }
}
