package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.TransformComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.components.ActionComponent;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/5/13
 * Time: 3:55 PM
 * Makes the entity look at a point.
 */
public class LookSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Quaternion to use for calculations.
     */
    private Quaternion quaternion = new Quaternion();

    /**
     * Direction of the looking angle.
     */
    private Vector3f direction = new Vector3f();

    /**
     * Player to check for looking angle on directly controlled entities.
     */
    private Player localPlayer;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public LookSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(ActionComponent.class);
        for (Entity entity : set.getEntities()) {
            TransformComponent transformComponent = entitySystem.getComponent(entity, TransformComponent.class);
            if (transformComponent != null) {
                ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
                // Set the direction to the camera looking direction if nothing is happening.
                if (localPlayer != null && Objects.equals(entity, localPlayer.getControlledEntity())
                        && !actionComponent.isWalking && !actionComponent.isCasting) {
                    direction.set(localPlayer.getCameraDirection());
                } else {
                    direction.set(actionComponent.lookDirectionX, actionComponent.lookDirectionY, actionComponent.lookDirectionZ);
                    actionComponent.headDirectionX = direction.x;
                    actionComponent.headDirectionY = direction.y;
                    actionComponent.headDirectionZ = direction.z;
                }
                // Always have the controlled entity looking at the camera direction.
                if (localPlayer != null && Objects.equals(entity, localPlayer.getControlledEntity())) {
                    Vector3f cameraDirection = localPlayer.getCameraDirection();
                    actionComponent.headDirectionX = cameraDirection.x;
                    actionComponent.headDirectionY = cameraDirection.y;
                    actionComponent.headDirectionZ = cameraDirection.z;
                }
                // Y axis should not rotate.
                direction.y = 0;
                quaternion.set(Quaternion.IDENTITY);
                quaternion.lookAt(direction, Vector3f.UNIT_Y);
                transformComponent.rotationX = quaternion.getX();
                transformComponent.rotationY = quaternion.getY();
                transformComponent.rotationZ = quaternion.getZ();
                transformComponent.rotationW = quaternion.getW();
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * @param localPlayer the local player to set for handling idle looking angles.
     */
    public void setLocalPlayer(Player localPlayer) {
        this.localPlayer = localPlayer;
    }

    @Override
    public void destroy() {
    }
}
