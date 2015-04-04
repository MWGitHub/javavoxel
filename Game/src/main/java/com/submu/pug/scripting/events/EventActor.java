package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.submu.pug.game.objects.ActorCallbacks;
import com.submu.pug.game.objects.GameObjectFactory;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/27/13
 * Time: 3:36 PM
 * Hooks to events in the world.
 */
public class EventActor implements EventHook {
    /**
     * Name of when a new unit has been created.
     */
    public static final String EVENT_ACTOR_CREATED = "eventEntityCreated",
    EVENT_ACTOR_CHANGED = "eventActorChanged",
    EVENT_ACTOR_REMOVED = "eventActorRemoved";

    /**
     * Variable for the last created unit.
     */
    public static final String VAR_LAST_CREATED_ACTOR = "varLastCreatedActor";

    /**
     * Callbacks to hook into the world with.
     */
    private ActorCallbacks callbacks;

    /**
     * Initializes the event.
     * @param gameObjectFactory the gameObjectFactory to hook to.
     */
    public EventActor(GameObjectFactory gameObjectFactory) {
        callbacks = new ActorCallbacks() {
            @Override
            public void onActorCreated(Entity entity) {
                ScriptGlobals.getInstance().putData(VAR_LAST_CREATED_ACTOR, entity);
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ACTOR_CREATED, entity));
            }

            @Override
            public void onActorChanged(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ACTOR_CHANGED, entity));
            }

            @Override
            public void onActorRemoved(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ACTOR_REMOVED, entity));
            }
        };
        gameObjectFactory.setCallbacks(callbacks);
    }

    @Override
    public void updateEvent(float tpf) {
    }

    @Override
    public void destroyEvent() {
    }
}
