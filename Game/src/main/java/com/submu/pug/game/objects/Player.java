package com.submu.pug.game.objects;

import com.exploringlines.entitysystem.Entity;
import com.jme3.math.Vector3f;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/15/13
 * Time: 4:30 PM
 * Represents a player.
 */
public class Player {
    /**
     * Unique ID number of the player.
     */
    private int id;

    /**
     * Object currently controlled by the player.
     */
    private Entity controlledEntity;

    /**
     * Camera location of the player.
     */
    private Vector3f cameraLocation = new Vector3f();

    /**
     * Camera direction of the player.
     */
    private Vector3f cameraDirection = new Vector3f();

    /**
     * Amount of money the player has.
     */
    private float money = 0;

    /**
     * Creates the player.
     * @param id the id to assign to the player.
     */
    public Player(int id) {
        this.id = id;
    }

    /**
     * @return the camera location to retrieve.
     */
    public Vector3f getCameraLocation() {
        return cameraLocation;
    }

    /**
     * @param cameraLocation the camera location to set.
     */
    public void setCameraLocation(Vector3f cameraLocation) {
        this.cameraLocation.set(cameraLocation);
    }

    /**
     * @return the camera direction.
     */
    public Vector3f getCameraDirection() {
        return cameraDirection;
    }

    /**
     * @param cameraDirection the camera direction to set the data of but not the actual camera.
     */
    public void setCameraDirection(Vector3f cameraDirection) {
        this.cameraDirection.set(cameraDirection);
    }

    /**
     * @param entity the object to set as the object controlled by the player.
     */
    public void setControlledEntity(Entity entity) {
        controlledEntity = entity;
    }

    /**
     * @return the object controlled by the player.
     */
    public Entity getControlledEntity() {
        return controlledEntity;
    }

    /**
     * @return the id of the player.
     */
    public int getId() {
        return id;
    }

    /**
     * @return the money the player has.
     */
    public float getMoney() {
        return money;
    }

    /**
     * @param money the money to set the player.
     */
    public void setMoney(float money) {
        this.money = money;
    }
}
