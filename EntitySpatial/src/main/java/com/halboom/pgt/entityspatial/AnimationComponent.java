package com.halboom.pgt.entityspatial;

import com.exploringlines.entitysystem.Component;
import com.jme3.animation.LoopMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/1/13
 * Time: 2:18 PM
 * Component to hold animation data.
 */
public class AnimationComponent implements Component {
    /**
     * Animations where the index is the channel.
     */
    public List<AnimationElement> animations = new ArrayList<AnimationElement>();

    /**
     * Animation data for an animation.
     */
    public static class AnimationElement {
        /**
         * Animation to play, will reset to null after playing.
         */
        public String playAnimation = null;

        /**
         * Current animation being played (goes back to null once the animation is done).
         */
        public String currentAnimation = null;

        /**
         * Loop mode for the animation.
         */
        public int loopMode = LoopMode.DontLoop.ordinal();

        /**
         * Speed of the animation.
         */
        public float speed = 1f;

        /**
         * Blending time of the animation.
         */
        public float blendTime = 0.0f;
    }

    @Override
    public Component copy() {
        AnimationComponent output = new AnimationComponent();
        for (AnimationElement element : animations) {
            AnimationElement copiedElement = new AnimationElement();
            copiedElement.playAnimation = element.playAnimation;
            copiedElement.currentAnimation = element.currentAnimation;
            copiedElement.loopMode = element.loopMode;
            copiedElement.speed = element.speed;
            copiedElement.blendTime = element.blendTime;
            output.animations.add(copiedElement);
        }

        return output;
    }
}
