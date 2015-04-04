package com.halboom.pgt.physics.simple.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/25/13
 * Time: 12:49 PM
 * Component for AABB bounds that do not have orientation.
 */
public class AABBComponent implements Component {
    /**
     * Center of the AABB.
     */
    public float centerX = 0, centerY = 0, centerZ = 0;

    /**
     * Offset of the bounds.
     */
    public float localOffsetX = 0, localOffsetY = 0, localOffsetZ = 0;

    /**
     * Offsets of the bounds after scaling.
     */
    public float worldOffsetX = 0, worldOffsetY = 0, worldOffsetZ = 0;

    /**
     * Local bound extents which is the distance from the center to an edge.
     */
    public float localExtentX = 0, localExtentY = 0, localExtentZ = 0;

    /**
     * Calculated bound extents after scaling.
     */
    public float worldExtentX = 0, worldExtentY = 0, worldExtentZ = 0;

    @Override
    public Component copy() {
        AABBComponent output = new AABBComponent();
        output.centerX = centerX;
        output.centerY = centerY;
        output.centerZ = centerZ;
        output.localOffsetX = localOffsetX;
        output.localOffsetY = localOffsetY;
        output.localOffsetZ = localOffsetZ;
        output.worldOffsetX = worldOffsetX;
        output.worldExtentY = worldOffsetY;
        output.worldExtentZ = worldOffsetZ;
        output.localExtentX = localExtentX;
        output.localExtentY = localExtentY;
        output.localExtentZ = localExtentZ;
        output.worldExtentX = worldExtentX;
        output.worldExtentY = worldExtentY;
        output.worldExtentZ = worldExtentZ;

        return output;
    }
}
