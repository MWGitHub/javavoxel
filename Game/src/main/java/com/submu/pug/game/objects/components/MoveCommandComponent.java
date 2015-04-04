package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;
import com.exploringlines.entitysystem.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/23/13
 * Time: 8:29 PM
 * Component that has data on where the entity should be moving to.
 * Only one of the destinations can be used at a time.
 */
public class MoveCommandComponent implements Component {
    /**
     * Destination of the final position.
     */
    public float destinationX = 0, destinationY = 0, destinationZ = 0;

    /**
     * Direction to move.
     */
    public float directionX = 0, directionY = 0, directionZ = 0;

    /**
     * Entity as the destination.
     */
    public Entity entity;

    @Override
    public Component copy() {
        MoveCommandComponent output = new MoveCommandComponent();
        output.destinationX = destinationX;
        output.destinationY = destinationY;
        output.destinationZ = destinationZ;
        output.directionX = directionX;
        output.directionY = directionY;
        output.directionZ = directionZ;
        output.entity = entity;

        return output;
    }
}
