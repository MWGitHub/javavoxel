package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/2/13
 * Time: 10:23 AM
 * Stores special collision flags for the filter.
 */
public class FilterFlagComponent implements Component {

    /**
     * True to collide with itself.
     */
    public boolean collidesSelf = false;

    /**
     * True to collide with an entity of the same owner.
     */
    public boolean collidesOwner = false;

    /**
     * True to collide with the creating entity.
     */
    public boolean collidesCreator = false;

    /**
     * True to collide with an entity that shares the same creator.
     */
    public boolean collidesSharedCreator = false;

    /**
     * True to collide with allied entities.
     */
    public boolean collidesAlly = false;

    /**
     * True to collide with enemy entities.
     */
    public boolean collidesEnemy = false;

    /**
     * True to collide with neutral entities.
     */
    public boolean collidesNeutral = false;

    /**
     * True to collide with entities of the same data type.
     */
    public boolean collidesSame = false;

    @Override
    public Component copy() {
        FilterFlagComponent output = new FilterFlagComponent();
        output.collidesSelf = collidesSelf;
        output.collidesOwner = collidesOwner;
        output.collidesCreator = collidesCreator;
        output.collidesSharedCreator = collidesSharedCreator;
        output.collidesAlly = collidesAlly;
        output.collidesEnemy = collidesEnemy;
        output.collidesNeutral = collidesNeutral;
        output.collidesSame = collidesSame;

        return output;
    }
}
