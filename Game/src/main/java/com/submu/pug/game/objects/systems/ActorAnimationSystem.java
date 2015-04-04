package com.submu.pug.game.objects.systems;

import com.halboom.pgt.entityspatial.AnimationComponent;
import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.animation.LoopMode;
import com.submu.pug.game.objects.components.ActionComponent;
import com.submu.pug.game.objects.components.ActorAnimationComponent;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/22/13
 * Time: 5:10 PM
 * Handles what animations an actor is playing and should be playing.
 */
public class ActorAnimationSystem implements Subsystem {
    /**
     * Channels for animations.
     */
    public static final int CHANNEL_BOTTOM = 0,
            CHANNEL_TOP = 1;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     */
    public ActorAnimationSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    @Override
    public void update(float tpf) {
        EntitySet set = entitySystem.getEntities(ActorAnimationComponent.class);
        // Create animation elements.
        for (Entity entity : set.getAddedEntities()) {
            AnimationComponent animationComponent = entitySystem.getComponent(entity, AnimationComponent.class);
            if (animationComponent == null) {
                animationComponent = new AnimationComponent();
                entitySystem.setComponent(entity, animationComponent);
            }
            while (animationComponent.animations.size() <= CHANNEL_TOP) {
                animationComponent.animations.add(new AnimationComponent.AnimationElement());
            }
        }

        // Check for animation updates.
        for (Entity entity : set.getEntities()) {
            AnimationComponent animation = entitySystem.getComponent(entity, AnimationComponent.class);
            if (animation == null) {
                continue;
            }
            boolean isAnimationChanged = false;
            ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
            if (actionComponent != null) {
                ActorAnimationComponent actorAnimations = entitySystem.getComponent(entity, ActorAnimationComponent.class);
                boolean isAnimationPlaying = false;
                boolean isTopPlaying = false;
                boolean isBottomPlaying = false;
                AnimationComponent.AnimationElement element;
                // Play the casting animation.
                if (actionComponent.isCasting) {
                    if (actorAnimations.abilityTopAnimation != null) {
                        element = animation.animations.get(CHANNEL_TOP);
                        boolean canOverride = actorAnimations.allowAbilityOverride
                                || element.currentAnimation == null
                                || !element.currentAnimation.equals(actorAnimations.abilityTopAnimation);
                        if (canOverride) {
                            element.playAnimation = actorAnimations.abilityTopAnimation;
                            isAnimationChanged = true;
                        }
                        isTopPlaying = true;
                    }
                    if (actorAnimations.abilityBottomAnimation != null) {
                        element = animation.animations.get(CHANNEL_BOTTOM);
                        boolean canOverride = actorAnimations.allowAbilityOverride
                                || element.currentAnimation == null
                                || !element.currentAnimation.equals(actorAnimations.abilityTopAnimation);
                        if (canOverride) {
                            element.playAnimation = actorAnimations.abilityBottomAnimation;
                            isAnimationChanged = true;
                        }
                        isBottomPlaying = true;
                    }
                }
                // Set flags for when an animation should not be overridden.
                boolean isTopOverridden = isTopPlaying || (actorAnimations.abilityTopAnimation != null
                        && Objects.equals(animation.animations.get(CHANNEL_TOP).currentAnimation, actorAnimations.abilityTopAnimation));
                boolean isBottomOverridden = isBottomPlaying || (actorAnimations.abilityBottomAnimation != null
                        && Objects.equals(animation.animations.get(CHANNEL_BOTTOM).currentAnimation, actorAnimations.abilityBottomAnimation));

                // Play the walk animation.
                if (actionComponent.isWalking) {
                    if (!isBottomOverridden) {
                        element = animation.animations.get(CHANNEL_BOTTOM);
                        if (!Objects.equals(element.currentAnimation, actorAnimations.walkBottomAnimation)) {
                            element.playAnimation = actorAnimations.walkBottomAnimation;
                            element.loopMode = LoopMode.DontLoop.ordinal();
                            isAnimationChanged = true;
                        }
                    }
                    // Play the top animation also if nothing is using it.
                    if (!isTopOverridden) {
                        element = animation.animations.get(CHANNEL_TOP);
                        if (!Objects.equals(element.currentAnimation, actorAnimations.walkTopAnimation)) {
                            element.playAnimation = actorAnimations.walkTopAnimation;
                            element.loopMode = LoopMode.DontLoop.ordinal();
                            isAnimationChanged = true;
                        }
                    }
                    isAnimationPlaying = true;
                }

                // Default to idle if nothing is playing.
                if (!isAnimationPlaying) {
                    if (!isBottomOverridden) {
                        element = animation.animations.get(CHANNEL_BOTTOM);
                        if (!Objects.equals(element.currentAnimation, actorAnimations.idleBottomAnimation)) {
                            element.playAnimation = actorAnimations.idleBottomAnimation;
                            element.loopMode = LoopMode.DontLoop.ordinal();
                            isAnimationChanged = true;
                        }
                    }
                    if (!isTopOverridden) {
                        element = animation.animations.get(CHANNEL_TOP);
                        // Play the top animation if nothing is using it.
                        if (!Objects.equals(element.currentAnimation, actorAnimations.idleTopAnimation)) {
                            element.playAnimation = actorAnimations.idleTopAnimation;
                            element.loopMode = LoopMode.DontLoop.ordinal();
                            isAnimationChanged = true;
                        }
                    }
                }
            }
            if (isAnimationChanged) {
                entitySystem.setComponent(entity, animation);
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
