package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;
import com.jme3.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/28/13
 * Time: 3:38 PM
 * Component to handle ability commands.
 * Due to the limit of a single component class per entity the ability commands will all be in one component.
 */
public class AbilityCommandComponent implements Component {
    /**
     * Ability numbers that are cast.
     */
    public List<Integer> castedAbilities = new ArrayList<Integer>();

    /**
     * Targets or directions to look depending on the ability for each ability.
     */
    public List<Vector3f> targets = new ArrayList<Vector3f>();

    /**
     * Abilities whose casting buttons have been released.
     * Useful for channeling spells or charging up.
     */
    public List<Integer> releasedAbilities = new ArrayList<Integer>();

    @Override
    public Component copy() {
        AbilityCommandComponent output = new AbilityCommandComponent();
        for (Integer i : castedAbilities) {
            output.castedAbilities.add(i);
        }
        for (Vector3f target : targets) {
            output.targets.add(new Vector3f(target));
        }
        for (Integer i : releasedAbilities) {
            output.releasedAbilities.add(i);
        }

        return output;
    }
}
