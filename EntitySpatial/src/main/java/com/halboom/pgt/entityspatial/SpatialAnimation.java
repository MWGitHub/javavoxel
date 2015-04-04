package com.halboom.pgt.entityspatial;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/6/13
 * Time: 4:16 PM
 * Manages animations for an object.
 */
public class SpatialAnimation implements AnimEventListener {
    /**
     * Animation component attached to the entity.
     */
    private AnimationComponent animationComponent;

    /**
     * Initializes the animation listener.
     * @param animationComponent the animation component attached to the entity.
     */
    public SpatialAnimation(AnimationComponent animationComponent) {
        this.animationComponent = animationComponent;
    }

    @Override
    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        for (AnimationComponent.AnimationElement element : animationComponent.animations) {
            if (Objects.equals(element.currentAnimation, animName) && channel.getLoopMode().equals(LoopMode.DontLoop)) {
                element.currentAnimation = null;
            }
        }
    }

    @Override
    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
