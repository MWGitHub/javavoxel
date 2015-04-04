package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.submu.pug.game.objects.systems.AISystem;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/27/13
 * Time: 3:42 PM
 */
public class EventAI implements EventHook {
    /**
     * Event for being in combat.
     */
    public static final String EVENT_COMBAT = "eventCombat";

    /**
     * Event for leaving combat.
     */
    public static final String EVENT_LEAVE_COMBAT = "eventLeaveCombat";

    /**
     * Initializes the event hook.
     * @param aiSystem the AI system to hook to.
     */
    public EventAI(AISystem aiSystem) {
        aiSystem.setCallbacks(new AISystem.Callbacks() {
            @Override
            public void onCombat(Entity entity, Entity target, String script) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_COMBAT, entity, target, script));
            }

            @Override
            public void onLeaveCombat(Entity entity) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_LEAVE_COMBAT, entity));
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
