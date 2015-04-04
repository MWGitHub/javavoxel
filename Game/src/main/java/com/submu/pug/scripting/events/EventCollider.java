package com.submu.pug.scripting.events;

import com.halboom.pgt.physics.PhysicsSystem;
import com.halboom.pgt.physics.simple.CollisionCallbacks;
import com.halboom.pgt.physics.simple.CollisionInformation;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/15/13
 * Time: 1:40 PM
 * Grid collision events.
 */
public class EventCollider implements EventHook {
    /**
     * Event for tile collision, sensor, and static collision.
     */
    public static final String EVENT_TILE_COLLIDED = "eventTileCollided",
            EVENT_STATIC_COLLIDED = "eventStaticCollided",
            EVENT_SENSOR_COLLIDED = "eventSensorCollided";

    /**
     * Initializes the event.
     * @param physicsSystem the system to hook the events to.
     */
    public EventCollider(PhysicsSystem physicsSystem) {
        physicsSystem.addCollisionCallbacks(new CollisionCallbacks() {
            @Override
            public void onBlockerCollide(CollisionInformation collisionInformation) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_STATIC_COLLIDED, collisionInformation));
            }

            @Override
            public void onSensorCollide(CollisionInformation collisionInformation) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_SENSOR_COLLIDED, collisionInformation));
            }

            @Override
            public void onTileCollide(CollisionInformation collisionInformation) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_TILE_COLLIDED, collisionInformation));
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
