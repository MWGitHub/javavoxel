package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/17/13
 * Time: 11:32 AM
 * Component that holds the names of abilities which can be queried at ability use time.
 */
public class AbilityComponent implements Component {
    /**
     * Named abilities the entity has.
     */
    public List<Ability> abilities = new ArrayList<Ability>();

    /**
     * An ability.
     */
    public static class Ability {
        /**
         * Name of the ability.
         */
        public String internalName;

        /**
         * Hotkey for the ability.
         */
        public String hotkey;

        /**
         * Cooldown of the skill (at first it'll be the same as the ability cooldown).
         */
        public float cooldown = 0.0f;

        /**
         * Cooldown timers for the abilities.
         * When the timer reaches zero the ability becomes ready to cast.
         */
        public float cooldownTimer = 0.0f;

        /**
         * True to set the flag as unlocked.
         */
        public boolean isUnlocked = false;

        /**
         * Current upgrade amounts of an ability.
         */
        public Map<String, AbilityUpgrade> upgrades = new HashMap<String, AbilityUpgrade>();

        /**
         * Holds data for the upgrades.
         */
        public static class AbilityUpgrade {
            /**
             * Level of the upgrade.
             * A level of zero signifies that the upgrade has not been purchased.
             */
            public int level = 0;
        }
    }

    @Override
    public Component copy() {
        AbilityComponent output = new AbilityComponent();
        for (Ability element : abilities) {
            Ability copy = new Ability();
            copy.internalName = element.internalName;
            copy.hotkey = element.hotkey;
            copy.cooldown = element.cooldown;
            copy.cooldownTimer = element.cooldownTimer;
            copy.isUnlocked = element.isUnlocked;
            for (Map.Entry<String, Ability.AbilityUpgrade> upgrade : element.upgrades.entrySet()) {
                Ability.AbilityUpgrade upgradeCopy = new Ability.AbilityUpgrade();
                upgradeCopy.level = upgrade.getValue().level;
                copy.upgrades.put(upgrade.getKey(), upgradeCopy);
            }
            output.abilities.add(copy);
        }

        return output;
    }
}
