package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/22/13
 * Time: 3:43 PM
 * Holds the data for animation names for actors.
 */
public class ActorAnimationComponent implements Component {
    /**
     * Animation to run when the actor is in control and not moving.
     */
    public String idleBottomAnimation, idleTopAnimation;

    /**
     * Animation to run when the actor is in control and moving.
     */
    public String walkBottomAnimation, walkTopAnimation;

    /**
     * Animation to run when the actor is using an ability.
     */
    public String abilityBottomAnimation, abilityTopAnimation;

    /**
     * Set to true to allow abilities with the same animation to play over another
     * ability even before the animation is complete.
     */
    public boolean allowAbilityOverride = true;

    @Override
    public Component copy() {
        ActorAnimationComponent output = new ActorAnimationComponent();
        output.idleBottomAnimation = idleBottomAnimation;
        output.idleTopAnimation = idleTopAnimation;
        output.walkBottomAnimation = walkBottomAnimation;
        output.walkTopAnimation = walkTopAnimation;
        output.abilityBottomAnimation = abilityBottomAnimation;
        output.abilityTopAnimation = abilityTopAnimation;
        output.allowAbilityOverride = allowAbilityOverride;

        return output;
    }
}
