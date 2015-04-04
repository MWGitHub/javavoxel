package com.submu.pug.scripting.events;

import com.exploringlines.entitysystem.Entity;
import com.jme3.math.Vector3f;
import com.submu.pug.game.objects.systems.AbilitySystem;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/28/13
 * Time: 1:54 PM
 * Creates callbacks for when an ability has been cast.
 * Unlike the actions, the cast ability has been checked for if it can actually be cast.
 */
public class EventAbilities implements EventHook {
    /**
     * Event name for an ability initially being cast.
     */
    public static final String EVENT_ABILITY_CAST_BEGIN = "eventAbilityCastBegin";

    /**
     * Event name for the casted ability.
     */
    public static final String EVENT_ABILITY_CAST = "eventAbilityCast";

    /**
     * Event name for a canceled ability.
     */
    public static final String EVENT_ABILITY_CANCEL = "eventAbilityCancel";

    /**
     * Event name for upgrading an ability.
     */
    public static final String EVENT_ABILITY_UPGRADE = "eventAbilityUpgrade";

    /**
     * Initializes the hook.
     * @param abilitySystem the ability system to get ability information from.
     */
    public EventAbilities(AbilitySystem abilitySystem) {
        abilitySystem.setCallbacks(new AbilitySystem.Callbacks() {
            @Override
            public void onAbilityCastBegin(Entity entity, int index, String internalName, Vector3f target) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ABILITY_CAST_BEGIN, entity, index, internalName, target));
            }
            @Override
            public void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ABILITY_CAST, entity, index, internalName, target));
            }
            @Override
            public void onAbilityCancel(Entity entity, int index, String internalName, Vector3f target) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ABILITY_CANCEL, entity, index, internalName, target));
            }
            @Override
            public void onAbilityUpgrade(Entity entity, int index, String upgrade, int level) {
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_ABILITY_UPGRADE, entity, index, upgrade, level));
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
