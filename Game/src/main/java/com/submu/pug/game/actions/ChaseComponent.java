package com.submu.pug.game.actions;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/19/13
 * Time: 5:25 PM
 * Component for chased objects actions.
 * The component makes an object controllable with the movement keys.
 */
public class ChaseComponent implements Component {
    @Override
    public Component copy() {
        return new ChaseComponent();
    }
}
