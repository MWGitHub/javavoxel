package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/28/13
 * Time: 2:57 PM
 * Signifies that the entity is targettable.
 */
public class TargetableComponent implements Component {
    @Override
    public Component copy() {
        return new TargetableComponent();
    }
}
