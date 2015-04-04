package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.submu.pug.game.world.Regions;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/20/13
 * Time: 5:03 PM
 * Creates events when things happen in regions.
 */
public class EventRegions implements EventHook {
    /**
     * Region events for entering, within, and leaving.
     */
    public static final String EVENT_REGION_ENTER = "eventRegionEnter",
    EVENT_REGION_INSIDE = "eventRegionInside",
    EVENT_REGION_LEAVE = "eventRegionLeave";

    /**
     * Initializes the class.
     * @param regions the regions to set the callbacks to.
     */
    public EventRegions(Regions regions) {
        regions.setCallbacks(new Regions.Callbacks() {
            @Override
            public void onEntityEnters(String name, Bounds bounds, Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_REGION_ENTER, name, bounds, entity));
            }

            @Override
            public void onEntityInside(String name, Bounds bounds, Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_REGION_INSIDE, name, bounds, entity));
            }

            @Override
            public void onEntityLeaves(String name, Bounds bounds, Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_REGION_LEAVE, name, bounds, entity));
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
