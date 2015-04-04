package com.submu.pug.game.objects;


import com.exploringlines.entitysystem.Entity;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/27/13
 * Time: 3:38 PM
 * Callback functions the world can accept.
 */
public interface ActorCallbacks {
    /**
     * Runs when an actor has been created.
     * @param entity the actor that has been created.
     */
    void onActorCreated(Entity entity);

    /**
     * Runs when an actor has been changed.
     * @param entity the actor that has been changed.
     */
    void onActorChanged(Entity entity);

    /**
     * Runs when an actor has been removed.
     * @param entity the actor that has been removed.
     */
    void onActorRemoved(Entity entity);
}
