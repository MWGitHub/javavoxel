package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/26/13
 * Time: 4:54 PM
 * Holds data for when AI should trigger for entities.
 */
public class AIComponent implements Component {
    /**
     * Range when the entity will begin to chase after another entity.
     */
    public float aggroRange = 1.0f;

    /**
     * Range when an entity will run combat AI scripts.
     */
    public float combatRange = 1.0f;

    /**
     * Name of the combat script to execute each frame.
     */
    public String combatScript;

    /**
     * True when the entity is in combat.
     */
    public boolean isInCombat = false;

    @Override
    public Component copy() {
        AIComponent output = new AIComponent();
        output.aggroRange = aggroRange;
        output.combatRange = combatRange;
        output.combatScript = combatScript;
        output.isInCombat = isInCombat;

        return output;
    }
}
