package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/22/13
 * Time: 11:21 AM
 * Spatial data component.
 */
public class SpatialComponent implements Component {
    /**
     * Parent entity.
     */
    public Entity parent = null;

    /**
     * Model to use if the spatial is a geometry.
     */
    public String model = null;

    @Override
    public Component copy() {
        SpatialComponent output = new SpatialComponent();
        output.parent = parent;
        output.model = model;

        return output;
    }
}
