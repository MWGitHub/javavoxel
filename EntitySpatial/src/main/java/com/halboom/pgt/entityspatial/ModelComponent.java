package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/18/13
 * Time: 11:36 AM
 * Holds the original model data when loaded.
 */
public class ModelComponent implements Component {
    /**
     * Original scales of the model.
     */
    public float scaleX = 1.0f, scaleY = 1.0f, scaleZ = 1.0f;

    @Override
    public Component copy() {
        ModelComponent output = new ModelComponent();
        output.scaleX = scaleX;
        output.scaleY = scaleY;
        output.scaleZ = scaleZ;

        return output;
    }
}
