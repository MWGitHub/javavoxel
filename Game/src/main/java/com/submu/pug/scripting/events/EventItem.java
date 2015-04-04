package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.submu.pug.game.objects.systems.ItemSystem;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/30/13
 * Time: 1:23 PM
 * Handles the item callbacks.
 */
public class EventItem implements EventHook {
    /**
     * Names to run scripts of.
     */
    public static final String EVENT_ITEM_PICK_UP = "eventItemPickUp",
                               EVENT_ITEM_DROP = "eventItemDrop";

    /**
     * Initializes the hook.
     * @param itemSystem the item system to attach callbacks to.
     */
    public EventItem(ItemSystem itemSystem) {
        itemSystem.setCallbacks(new ItemSystem.Callbacks() {
            @Override
            public void onItemPickup(Entity entity, Entity item) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ITEM_PICK_UP, entity, item));
            }

            @Override
            public void onItemDrop(Entity entity, Entity item) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ITEM_DROP, entity, item));
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
