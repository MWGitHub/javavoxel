package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/22/13
 * Time: 11:14 AM
 * Holds transform data.
 */
public class TransformComponent implements Component {
    /**
     * Local position to set on a spatial.
     */
    public float positionX = 0, positionY = 0, positionZ = 0;

    /**
     * Local scale to set on a spatial.
     */
    public float scaleX = 1f, scaleY = 1f, scaleZ = 1f;

    /**
     * Quaternion parameters to use for rotation.
     */
    public float rotationX = 0, rotationY = 0, rotationZ = 0, rotationW = 1f;

    @Override
    public Component copy() {
        TransformComponent output = new TransformComponent();
        output.positionX = positionX;
        output.positionY = positionY;
        output.positionZ = positionZ;
        output.scaleX = scaleX;
        output.scaleY = scaleY;
        output.scaleZ = scaleZ;
        output.rotationX = rotationX;
        output.rotationY = rotationY;
        output.rotationZ = rotationZ;
        output.rotationW = rotationW;

        return output;
    }
}
