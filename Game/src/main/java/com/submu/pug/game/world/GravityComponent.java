package com.submu.pug.game.world;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/2/13
 * Time: 2:02 PM
 * Gravity component data.
 */
public class GravityComponent implements Component {
    /**
     * Gravity force in each direction.
     */
    public float x = 0, y = 0, z = 0;

    @Override
    public Component copy() {
        GravityComponent output = new GravityComponent();
        output.x = x;
        output.y = y;
        output.z = z;

        return output;
    }
}
