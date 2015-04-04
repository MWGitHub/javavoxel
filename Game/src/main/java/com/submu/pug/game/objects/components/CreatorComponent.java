package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/22/13
 * Time: 1:42 PM
 * Holds the creator entity information.
 */
public class CreatorComponent implements Component {
    /**
     * Creator of the entity.
     */
    public Entity creator;

    @Override
    public Component copy() {
        CreatorComponent output = new CreatorComponent();
        output.creator = creator;

        return output;
    }
}
