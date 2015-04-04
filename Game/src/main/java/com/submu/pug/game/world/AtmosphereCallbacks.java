package com.submu.pug.game.world;

import com.exploringlines.entitysystem.Entity;
import com.jme3.scene.Spatial;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/7/13
 * Time: 11:44 AM
 * Callbacks for atmosphere events.
 */
public interface AtmosphereCallbacks {
    /**
     * Runs when a light has been created.
     * @param entity the entity with the light.
     */
    void onLightCreated(Entity entity);

    /**
     * Runs when a light has been changed.
     * @param entity the changed light.
     */
    void onLightChanged(Entity entity);

    /**
     * Runs when a light has been removed.
     * @param entity the removed light.
     */
    void onLightRemoved(Entity entity);

    /**
     * Runs when the skybox has changed.
     * @param skybox the skybox that changed.
     */
    void onSkyboxChanged(Spatial skybox);
}
