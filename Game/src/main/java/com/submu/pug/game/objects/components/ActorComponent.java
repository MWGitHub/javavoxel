package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/7/13
 * Time: 12:29 PM
 * Represents a game actor.
 * The game actor itself should just have data that other systems read from.
 * Data for the actor should also rarely change.
 */
public class ActorComponent implements Component {
    /**
     * Name used to load the actor.
     */
    public String type;

    /**
     * The category of an actor is for user use.
     * Categories allow behaviors between actors to be shared.
     * Use categories most of the time instead of type even when the category is unique.
     */
    public String category;

    @Override
    public Component copy() {
        ActorComponent output = new ActorComponent();
        output.type = type;
        output.category = category;

        return output;
    }
}
