package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/16/13
 * Time: 2:29 PM
 * Component that holds an entity's target.
 * Useful for homing abilities.
 */
public class TargetComponent implements Component {
    /**
     * Target of the entity.
     */
    public Entity target;

    @Override
    public Component copy() {
        TargetComponent output = new TargetComponent();
        output.target = target;

        return output;
    }
}
