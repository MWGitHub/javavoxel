package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/23/13
 * Time: 2:02 PM
 * Handles available actions for the entity.
 */
public class ActionComponent implements Component {
    /**
     * True if the entity is walking.
     */
    public boolean isWalking = false;

    /**
     * Direction the entity is going to Walk.
     */
    public float walkDirectionX = 0, walkDirectionY = 0, walkDirectionZ = 0;

    /**
     * Direction the entity is going to look with the body.
     */
    public float lookDirectionX = 0, lookDirectionY = 0, lookDirectionZ = 0;

    /**
     * Direction the entity is going to look with the head if applicable.
     */
    public float headDirectionX = 0, headDirectionY = 0, headDirectionZ = 0;

    /**
     * True if the entity is casting an ability.
     */
    public boolean isCasting = false;

    /**
     * Direction the entity is casting.
     */
    public float castDirectionX = 0, castDirectionY = 0, castDirectionZ = 0;

    /**
     * True if the entity is jumping.
     */
    public boolean isJumping = false;

    @Override
    public Component copy() {
        ActionComponent output = new ActionComponent();
        output.isWalking = isWalking;
        output.walkDirectionX = walkDirectionX;
        output.walkDirectionY = walkDirectionY;
        output.walkDirectionZ = walkDirectionZ;
        output.lookDirectionX = lookDirectionX;
        output.lookDirectionY = lookDirectionY;
        output.lookDirectionZ = lookDirectionZ;
        output.headDirectionX = headDirectionX;
        output.headDirectionY = headDirectionY;
        output.headDirectionZ = headDirectionZ;
        output.isCasting = isCasting;
        output.castDirectionX = castDirectionX;
        output.castDirectionY = castDirectionY;
        output.castDirectionZ = castDirectionZ;
        output.isJumping = isJumping;

        return output;
    }
}
