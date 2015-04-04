package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/12/13
 * Time: 12:19 PM
 * Owner of the entity.
 */
public class OwnerComponent implements Component {
    /**
     * ID of the player.
     */
    public int playerID = 0;

    @Override
    public Component copy() {
        OwnerComponent output = new OwnerComponent();
        output.playerID = playerID;
        return output;
    }
}
