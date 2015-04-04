package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/17/13
 * Time: 3:19 PM
 * Component for handling the head model of an entity.
 */
public class HeadComponent implements Component {
    /**
     * Model to use for the head.
     */
    public String model;

    /**
     * Offset locations of the head.
     */
    public float offsetX = 0, offsetY = 0, offsetZ = 0;

    /**
     * Extra amount to scale the head by.
     */
    public float scaleX = 1, scaleY = 1, scaleZ = 1;

    /**
     * True to show the body in first person view.
     */
    public boolean isBodyShown = false;

    @Override
    public Component copy() {
        HeadComponent output = new HeadComponent();
        output.model = model;
        output.offsetX = offsetX;
        output.offsetY = offsetY;
        output.offsetZ = offsetZ;
        output.scaleX = scaleX;
        output.scaleY = scaleY;
        output.scaleZ = scaleZ;
        output.isBodyShown = isBodyShown;

        return output;
    }
}
