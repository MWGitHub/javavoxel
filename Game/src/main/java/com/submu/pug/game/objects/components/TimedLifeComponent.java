package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/10/13
 * Time: 2:19 PM
 * Component that represents timed life.
 */
public class TimedLifeComponent implements Component {
    /**
     * Life time of the entity before it is removed.
     */
    public float lifeTime = 0.0f;

    /**
     * Current life time of the entity.
     */
    public float currentLifeTime = 0.0f;

    @Override
    public Component copy() {
        TimedLifeComponent output = new TimedLifeComponent();
        output.lifeTime = lifeTime;
        output.currentLifeTime = currentLifeTime;

        return  output;
    }
}
