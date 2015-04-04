package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.jme3.scene.Spatial;
import com.submu.pug.game.world.AtmosphereCallbacks;
import com.submu.pug.game.world.AtmosphereSystem;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/7/13
 * Time: 11:56 AM
 * Events for the atmosphere.
 */
public class EventAtmosphere implements EventHook {
    /**
     * Name of light events.
     */
    public static final String EVENT_LIGHT_CREATED = "eventLightCreated",
    EVENT_LIGHT_CHANGED = "eventLightChanged",
    EVENT_LIGHT_REMOVED = "eventLightRemoved";

    /**
     * Skybox changed event.
     */
    public static final String EVENT_SKYBOX_CHANGED = "eventSkyboxChanged";

    /**
     * Variable for lights.
     */
    public static final String VAR_LAST_CREATED_LIGHT = "varLastCreatedLight";

    /**
     * Initializes the atmosphere.
     * @param atmosphereSystem the atmosphere to hook to.
     */
    public EventAtmosphere(AtmosphereSystem atmosphereSystem) {
        atmosphereSystem.setCallbacks(new AtmosphereCallbacks() {
            @Override
            public void onLightCreated(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_LIGHT_CREATED, entity));
                ScriptGlobals.getInstance().putData(VAR_LAST_CREATED_LIGHT, entity);
            }

            @Override
            public void onLightChanged(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_LIGHT_CHANGED, entity));
            }

            @Override
            public void onLightRemoved(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_LIGHT_REMOVED, entity));
            }

            @Override
            public void onSkyboxChanged(Spatial skybox) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_SKYBOX_CHANGED, skybox));
            }
        });
    }

    @Override
    public void updateEvent(float tpf) {
    }

    @Override
    public void destroyEvent() {
    }
}
