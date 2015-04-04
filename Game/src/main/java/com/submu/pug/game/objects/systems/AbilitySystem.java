package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.jme3.math.Vector3f;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.components.AbilityCommandComponent;
import com.submu.pug.game.objects.components.AbilityComponent;
import com.submu.pug.game.objects.components.ActionComponent;
import com.submu.pug.game.objects.components.ActorAnimationComponent;
import com.submu.pug.game.objects.components.BuffedStatComponent;
import com.submu.pug.game.objects.components.ExperienceComponent;
import com.submu.pug.game.objects.components.StatComponent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/21/13
 * Time: 9:17 PM
 * Manages ability cooldowns.
 */
public class AbilitySystem implements Subsystem {
    /**
     * Hotkey names for abilities.
     */
    public static final String HOTKEY_JUMP = "Jump",
    HOTKEY_ABILITY1 = "Ability1", HOTKEY_ABILITY2 = "Ability2", HOTKEY_ABILITY3 = "Ability3";

    /**
     * Flag signifying no ability has been found.
     */
    private static final int NO_ABILITY = -1;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Ability data for the map.
     */
    private Map<String, ObjectsData.AbilityData> abilities = new HashMap<String, ObjectsData.AbilityData>();

    /**
     * Entities that are in the process of casting an ability.
     */
    private List<Caster> casters = new LinkedList<Caster>();

    /**
     * Callbacks for the ability.
     */
    private Callbacks callbacks;

    /**
     * Initializes the system.
     * @param entitySystem the entity system to use.
     * @param objectsData the data to get abilities from.
     */
    public AbilitySystem(EntitySystem entitySystem, ObjectsData objectsData) {
        this.entitySystem = entitySystem;
        this.abilities = objectsData.abilities;
    }

    /**
     * Reduces the cooldown of the ability.
     * @param cooldown the current cooldown.
     * @param amount the amount to reduce the cooldown by.
     * @return the reduced cooldown.
     */
    private float reduceCooldown(float cooldown, float amount) {
        float output = cooldown - amount;
        if (output < 0) {
            output = 0;
        }
        return output;
    }

    /**
     * Checks if the caster has the resources to cast.
     * @param statComponent the component to retrieve resources from.
     * @param abilityData the ability that is being cast.
     * @return true if the entity can cast, false otherwise.
     */
    private boolean hasResources(StatComponent statComponent, ObjectsData.AbilityData abilityData) {
        return statComponent.mana >= abilityData.energy;
    }

    /**
     * Reduce the resources of an entity by the amount needed for the ability.
     * @param statComponent the mana to reduce the resources from.
     * @param abilityData the ability that is casted.
     */
    private void reduceResources(StatComponent statComponent, ObjectsData.AbilityData abilityData) {
        statComponent.mana -= abilityData.energy;
    }

    /**
     * Retrieves the index of an ability.
     * @param entity the entity to retrieve the ability of.
     * @param ability the ability to retrieve.
     * @return the index of the ability of -1 if none found.
     */
    public int getAbilityIndex(Entity entity, AbilityComponent.Ability ability) {
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return NO_ABILITY;
        }
        for (int i = 0; i < abilityComponent.abilities.size(); i++) {
            if (ability.equals(abilityComponent.abilities.get(i))) {
                return i;
            }
        }

        return NO_ABILITY;
    }

    /**
     * Retrieves an ability given the name.
     * @param name the name of the ability.
     * @return the ability data or null if none found.
     */
    public ObjectsData.AbilityData getAbilityData(String name) {
        return abilities.get(name);
    }

    /**
     * Retrieves the ability given the index.
     * @param entity the entity to retrieve the ability of.
     * @param abilityIndex the index of the ability in the abilities component.
     * @return the ability or null if none found.
     */
    public AbilityComponent.Ability getAbility(Entity entity, int abilityIndex) {
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return null;
        }
        if (abilityIndex >= abilityComponent.abilities.size()) {
            return null;
        }
        return abilityComponent.abilities.get(abilityIndex);
    }

    /**
     * Updates the casters.
     * @param tpf the time that has passed since the last frame.
     */
    private void updateCasters(float tpf) {
        // Use iterator to allow removal.
        for (Iterator<Caster> iter = casters.iterator(); iter.hasNext(); ) {
            Caster caster = iter.next();
            // Dead entities can not cast; cancel the ability.
            if (!entitySystem.hasEntity(caster.entity)) {
                if (callbacks != null) {
                    int index = getAbilityIndex(caster.entity, caster.ability);
                    callbacks.onAbilityCancel(caster.entity, index, caster.ability.internalName, caster.target);
                }
                iter.remove();
                continue;
            }
            // Replay the casting animation if the ability is channeled.
            if (caster.isChanneled) {
                // Prevent animation from replaying if it is already started.
                ActorAnimationComponent actorAnimationComponent = entitySystem.getComponent(caster.entity, ActorAnimationComponent.class);
                if (actorAnimationComponent != null) {
                    actorAnimationComponent.allowAbilityOverride = false;
                }
                // Make the action know about the casted ability even though it does not handle it.
                ActionComponent actionComponent = entitySystem.getComponent(caster.entity, ActionComponent.class);
                if (actionComponent != null) {
                    actionComponent.isCasting = true;
                    actionComponent.lookDirectionX = caster.target.x;
                    actionComponent.lookDirectionY = caster.target.y;
                    actionComponent.lookDirectionZ = caster.target.z;
                }
            }
            caster.castingTime -= tpf;
            if (caster.castingTime <= 0) {
                // Reset the animation overrides.
                ActorAnimationComponent actorAnimationComponent = entitySystem.getComponent(caster.entity, ActorAnimationComponent.class);
                if (actorAnimationComponent != null) {
                    actorAnimationComponent.allowAbilityOverride = false;
                }
                // Cast the ability.
                if (callbacks != null) {
                    int index = getAbilityIndex(caster.entity, caster.ability);
                    callbacks.onAbilityCast(caster.entity, index, caster.ability.internalName, caster.target);
                }
                iter.remove();
            }
        }
    }

    /**
     * Retrieves the caster that is casting a specific ability.
     * @param entity the casting entity to retrieve.
     * @param ability the ability that the entity is casting,
     * @return the caster or null if none found.
     */
    public Caster getCaster(Entity entity, AbilityComponent.Ability ability) {
        Caster output = null;
        for (Caster caster : casters) {
            if (caster.entity.equals(entity) && caster.ability.equals(ability)) {
                output = caster;
                break;
            }
        }

        return output;
    }

    /**
     * Checks if an entity is casting an ability.
     * @param entity the entity to check.
     * @return true if the entity is casting an ability.
     */
    public boolean isEntityCasting(Entity entity) {
        for (Caster caster : casters) {
            if (caster.entity.equals(entity)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(float tpf) {
        // Set just added cooldowns to the ability component.
        EntitySet set = entitySystem.getEntities(AbilityComponent.class);
        for (Entity entity : set.getAddedEntities()) {
            AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
            for (AbilityComponent.Ability element : abilityComponent.abilities) {
                ObjectsData.AbilityData abilityData = getAbilityData(element.internalName);
                if (abilityData != null) {
                    element.cooldown = abilityData.cooldown;
                }
            }
        }

        // Cast the ability relating to the commands if possible.
        set = entitySystem.getEntities(AbilityCommandComponent.class);
        for (Entity entity : set.getEntities()) {
            AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
            if (abilityComponent != null) {
                AbilityCommandComponent commands = entitySystem.getComponent(entity, AbilityCommandComponent.class);
                // Do not allow casting entities to cast another ability.
                if (isEntityCasting(entity)) {
                    commands.castedAbilities.clear();
                    commands.targets.clear();
                }
                // Run callbacks on casted commands that are valid.
                StatComponent statComponent = entitySystem.getComponent(entity, StatComponent.class);
                BuffedStatComponent buffedStat = entitySystem.getComponent(entity, BuffedStatComponent.class);
                for (int i = 0; i < commands.castedAbilities.size(); i++) {
                    int abilityIndex = commands.castedAbilities.get(i);
                    if (abilityIndex <= abilityComponent.abilities.size()) {
                        AbilityComponent.Ability ability = abilityComponent.abilities.get(abilityIndex);
                        // Check if the ability can be casted.
                        if (!ability.isUnlocked) {
                            continue;
                        }
                        if (ability.cooldownTimer > 0) {
                            continue;
                        }
                        ObjectsData.AbilityData abilityData = abilities.get(ability.internalName);
                        if (abilityData == null) {
                            continue;
                        }
                        if (!hasResources(statComponent, abilityData)) {
                            continue;
                        }
                        // Place the casted ability onto the casting list.
                        reduceResources(statComponent, abilityData);
                        ability.cooldownTimer = ability.cooldown * (1 - buffedStat.cooldownReduction);
                        Vector3f target = commands.targets.get(i);
                        Caster caster = new Caster(entity, ability, target, abilityData.castTime);
                        caster.isChanneled = abilityData.isChanneled;
                        casters.add(caster);
                        // Run the initial casting callback.
                        if (callbacks != null) {
                            callbacks.onAbilityCastBegin(entity, abilityIndex, ability.internalName, target);
                        }
                        // Make the action know about the casted ability even though it does not handle it.
                        ActionComponent actionComponent = entitySystem.getComponent(entity, ActionComponent.class);
                        if (actionComponent != null) {
                            actionComponent.isCasting = true;
                            actionComponent.lookDirectionX = target.x;
                            actionComponent.lookDirectionY = target.y;
                            actionComponent.lookDirectionZ = target.z;
                        }
                        // Only allow one valid ability to be cast per frame.
                        break;
                    }
                }
                // Make released commands that are channeled or charged up be placed on the cast stack.
                for (int i = 0; i < commands.releasedAbilities.size(); i++) {
                    int abilityIndex = commands.releasedAbilities.get(i);
                    AbilityComponent.Ability ability = abilityComponent.abilities.get(abilityIndex);
                    Caster caster = getCaster(entity, ability);
                    // Instantly cast the spell when released.
                    if (caster != null && caster.isChanneled) {
                        caster.castingTime = 0.0f;
                    }
                }
                commands.castedAbilities.clear();
                commands.targets.clear();
                commands.releasedAbilities.clear();
            }
        }

        // Cast all the ready abilities.
        updateCasters(tpf);

        set = entitySystem.getEntities(AbilityComponent.class);
        // Reduce the cooldown for the abilities.
        for (Entity entity : set.getEntities()) {
            AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
            for (AbilityComponent.Ability element : abilityComponent.abilities) {
                element.cooldownTimer = reduceCooldown(element.cooldownTimer, tpf);
            }
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    /**
     * Runs when an ability is in analog mode.
     * @param player the player that pressed the ability button.
     * @param hotkey the hotkey that is pressed.
     */
    public void onHotkeyAnalog(Player player, String hotkey) {
        Entity entity = player.getControlledEntity();
        if (entity == null) {
            return;
        }
        if (isEntityCasting(entity)) {
            return;
        }
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return;
        }
        // Create command for the ability.
        AbilityCommandComponent commands = entitySystem.getComponent(entity, AbilityCommandComponent.class);
        if (commands == null) {
            commands = new AbilityCommandComponent();
            entitySystem.setComponent(entity, commands);
        }
        Vector3f direction = player.getCameraDirection();
        for (int i = 0; i < abilityComponent.abilities.size(); i++) {
            ObjectsData.AbilityData abilityData = abilities.get(abilityComponent.abilities.get(i).internalName);
            if (!abilityData.isAnalog) {
                continue;
            }
            AbilityComponent.Ability element = abilityComponent.abilities.get(i);
            if (element.hotkey != null && element.hotkey.equals(hotkey)) {
                commands.castedAbilities.add(i);
                commands.targets.add(direction);
            }
        }
    }

    /**
     * Runs when a hotkey is pressed but does not signify a casting of the ability.
     * The pressed ability just adds a command.
     * @param player the player that pressed the ability button.
     * @param hotkey the hotkey that is pressed.
     * @param isPressed true to be pressed and false to be released.
     */
    public void onHotkeyPressed(Player player, String hotkey, boolean isPressed) {
        Entity entity = player.getControlledEntity();
        if (entity == null) {
            return;
        }
        // Do not allow casting while in the process of casting another ability unless to release channels.
        if (isPressed && isEntityCasting(entity)) {
            return;
        }
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return;
        }
        // Create command for the ability.
        AbilityCommandComponent commands = entitySystem.getComponent(entity, AbilityCommandComponent.class);
        if (commands == null) {
            commands = new AbilityCommandComponent();
            entitySystem.setComponent(entity, commands);
        }
        Vector3f direction = player.getCameraDirection();
        for (int i = 0; i < abilityComponent.abilities.size(); i++) {
            AbilityComponent.Ability element = abilityComponent.abilities.get(i);
            if (element.hotkey != null && element.hotkey.equals(hotkey)) {
                if (isPressed) {
                    commands.castedAbilities.add(i);
                    commands.targets.add(direction);
                } else {
                    commands.releasedAbilities.add(i);
                }
            }
        }
    }

    /**
     * Checks if an entity can buy an ability.
     * @param entity the entity buying the ability.
     * @param abilityIndex the index of the ability.
     * @return true if the entity can buy the ability.
     */
    public boolean canBuyAbility(Entity entity, int abilityIndex) {
        AbilityComponent.Ability ability = getAbility(entity, abilityIndex);
        if (ability == null || ability.isUnlocked) {
            return false;
        }

        ObjectsData.AbilityData data = getAbilityData(ability.internalName);
        ExperienceComponent experienceComponent = entitySystem.getComponent(entity, ExperienceComponent.class);
        if (experienceComponent == null) {
            return false;
        }
        if (experienceComponent.skillPoints < data.cost) {
            return false;
        }

        return true;
    }

    /**
     * Buy an ability.
     * @param entity the entity to buy the ability with.
     * @param abilityIndex the index of the ability.
     */
    public void buyAbility(Entity entity, int abilityIndex) {
        AbilityComponent.Ability ability = getAbility(entity, abilityIndex);
        if (ability == null || ability.isUnlocked) {
            return;
        }
        ability.isUnlocked = true;
    }

    /**
     * Buy an ability and checks the resources.
     * @param entity the entity to buy the ability with.
     * @param abilityIndex the index of the ability.
     * @return true if the ability has been bought.
     */
    public boolean buyAbilityChecked(Entity entity, int abilityIndex) {
        if (!canBuyAbility(entity, abilityIndex)) {
            return false;
        }
        AbilityComponent.Ability ability = getAbility(entity, abilityIndex);
        ObjectsData.AbilityData data = getAbilityData(ability.internalName);
        ExperienceComponent experienceComponent = entitySystem.getComponent(entity, ExperienceComponent.class);
        experienceComponent.skillPoints -= data.cost;
        buyAbility(entity, abilityIndex);

        return true;
    }

    /**
     * Retrieves the upgrade level of an ability.
     * @param ability the ability to retrieve the upgrade from.
     * @param upgrade the internal name of the upgrade.
     * @return the level of the upgrade or 0 if none found or learned.
     */
    public int getUpgradeLevel(AbilityComponent.Ability ability, String upgrade) {
        if (ability == null) {
            return 0;
        }
        AbilityComponent.Ability.AbilityUpgrade abilityUpgrade = ability.upgrades.get(upgrade);
        if (abilityUpgrade == null) {
            return  0;
        }
        return abilityUpgrade.level;
    }

    /**
     * Retrieves the upgrade level of an ability.
     * @param entity the entity to get the ability from.
     * @param abilityIndex the index of the ability.
     * @param upgrade the internal name of the upgrade.
     * @return the level of the upgrade or 0 if none found or learned.
     */
    public int getUpgradeLevel(Entity entity, int abilityIndex, String upgrade) {
        AbilityComponent.Ability ability = getAbility(entity, abilityIndex);
        return getUpgradeLevel(ability, upgrade);
    }

    /**
     * Retrieves the cost of an upgrade.
     * @param entity the entity to retrieve the cost of.
     * @param abilityIndex the index of the ability in the ability component.
     * @param upgrade the internal name of the upgrade.
     * @param level the level to check the cost of.
     * @return the cost of the ability or 0 if none found.
     */
    public float getUpgradeCost(Entity entity, int abilityIndex, String upgrade, int level) {
        AbilityComponent.Ability ability = getAbility(entity, abilityIndex);
        if (ability == null) {
            return 0;
        }
        ObjectsData.AbilityData abilityData = getAbilityData(ability.internalName);
        ObjectsData.AbilityData.UpgradeData upgradeData = abilityData.upgrades.get(upgrade);
        if (upgradeData == null || level >= upgradeData.costs.length) {
            return 0;
        }

        return upgradeData.costs[level];
    }

    /**
     * Checks if there are enough resources and if the ability upgrade is valid.
     * @param entity the entity to check.
     * @param abilityIndex the ability index.
     * @param upgrade the internal upgrade name.
     * @param level the level of the ability.
     * @param checkResources true to check if the entity has valid resources to upgrade with.
     * @return true if the upgrade is valid.
     */
    public boolean isAbleToUpgrade(Entity entity, int abilityIndex, String upgrade, int level, boolean checkResources) {
        // Check if the upgrade exists.
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return false;
        }
        if (abilityIndex >= abilityComponent.abilities.size()) {
            return false;
        }
        ObjectsData.AbilityData abilityData = getAbilityData(abilityComponent.abilities.get(abilityIndex).internalName);
        if (abilityData == null) {
            return false;
        }
        ObjectsData.AbilityData.UpgradeData upgradeDataData = abilityData.upgrades.get(upgrade);
        if (upgradeDataData == null) {
            return false;
        }
        AbilityComponent.Ability element = abilityComponent.abilities.get(abilityIndex);
        AbilityComponent.Ability.AbilityUpgrade abilityUpgrade = element.upgrades.get(upgrade);
        // Create a new upgrade if none is found at level 0.
        if (abilityUpgrade == null) {
            abilityUpgrade = new AbilityComponent.Ability.AbilityUpgrade();
            element.upgrades.put(upgrade, abilityUpgrade);
        }

        if (checkResources) {
            // Check if the entity has skill points to spend.
            ExperienceComponent experienceComponent = entitySystem.getComponent(entity, ExperienceComponent.class);
            if (experienceComponent == null) {
                return false;
            }
            // Do not allow upgrading over the max level.
            if (level > upgradeDataData.costs.length) {
                return false;
            }
            // Do not allow upgrading more than one level at a time.
            if (level - abilityUpgrade.level != 1) {
                return false;
            }
            if (experienceComponent.skillPoints < upgradeDataData.costs[level - 1]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Upgrades an ability for the given entity.
     * Only checks if the upgrade can reach that level.
     * @param entity the entity to upgrade the ability of.
     * @param abilityIndex the index of the ability.
     * @param upgrade the upgrade name.
     * @param level the level of the upgrade.
     */
    public void upgradeAbility(Entity entity, int abilityIndex, String upgrade, int level) {
        if (!isAbleToUpgrade(entity, abilityIndex, upgrade, level, false)) {
            return;
        }

        // Retrieve the ability data.
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        ObjectsData.AbilityData abilityData = getAbilityData(abilityComponent.abilities.get(abilityIndex).internalName);
        ObjectsData.AbilityData.UpgradeData upgradeDataData = abilityData.upgrades.get(upgrade);
        AbilityComponent.Ability element = abilityComponent.abilities.get(abilityIndex);
        AbilityComponent.Ability.AbilityUpgrade abilityUpgrade = element.upgrades.get(upgrade);
        // Set the level of the upgrade.
        int finalLevel = level;
        if (finalLevel > upgradeDataData.costs.length) {
            finalLevel = upgradeDataData.costs.length;
        }
        abilityUpgrade.level = finalLevel;
        if (callbacks != null) {
            callbacks.onAbilityUpgrade(entity, abilityIndex, upgrade, abilityUpgrade.level);
        }
    }

    /**
     * Upgrades an ability but checks if the upgrade is valid.
     * @param entity the entity to upgrade the ability of.
     * @param abilityIndex the index of the ability.
     * @param upgrade the upgrade name.
     * @param level the level of the upgrade.
     * @return true if the upgrade has been bought.
     */
    public boolean upgradeAbilityChecked(Entity entity, int abilityIndex, String upgrade, int level) {
        if (!isAbleToUpgrade(entity, abilityIndex, upgrade, level, true)) {
            return false;
        }

        // Retrieve the ability data.
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        ObjectsData.AbilityData abilityData = getAbilityData(abilityComponent.abilities.get(abilityIndex).internalName);
        ObjectsData.AbilityData.UpgradeData upgradeDataData = abilityData.upgrades.get(upgrade);
        AbilityComponent.Ability element = abilityComponent.abilities.get(abilityIndex);
        AbilityComponent.Ability.AbilityUpgrade abilityUpgrade = element.upgrades.get(upgrade);
        // Subtract the skill points.
        ExperienceComponent experienceComponent = entitySystem.getComponent(entity, ExperienceComponent.class);
        experienceComponent.skillPoints -= upgradeDataData.costs[level - 1];
        abilityUpgrade.level = level;
        if (callbacks != null) {
            callbacks.onAbilityUpgrade(entity, abilityIndex, upgrade, abilityUpgrade.level);
        }

        return true;
    }

    /**
     * @param callbacks the callbacks to set.
     */
    public void setCallbacks(Callbacks callbacks) {
        this.callbacks = callbacks;
    }

    @Override
    public void destroy() {
    }

    /**
     * Callbacks for the ability system.
     */
    public interface Callbacks {
        /**
         * Runs when an ability has been initially cast.
         * @param entity the entity that is casting the ability.
         * @param index the index of the ability.
         * @param internalName the name of the ability.
         * @param target the target for the ability.
         */
        void onAbilityCastBegin(Entity entity, int index, String internalName, Vector3f target);

        /**
         * Runs when an ability has been cast.
         * @param entity the entity that casted the ability.
         * @param index the index of the ability.
         * @param internalName the name of the ability.
         * @param target the target of the ability.
         */
        void onAbilityCast(Entity entity, int index, String internalName, Vector3f target);

        /**
         * Runs when an ability has been canceled.
         * @param entity the entity that casted the ability.
         * @param index the index of the ability.
         * @param internalName the name of the ability.
         * @param target the target of the ability.
         */
        void onAbilityCancel(Entity entity, int index, String internalName, Vector3f target);

        /**
         * Runs when an ability has been upgraded.
         * @param entity the entity that casted the ability.
         * @param index the index of the ability.
         * @param upgrade the name of the upgrade.
         * @param level the level of the upgrade.
         */
        void onAbilityUpgrade(Entity entity, int index, String upgrade, int level);
    }

    /**
     * A caster is an entity that has begun casting an ability.
     */
    private class Caster {
        /**
         * Entity that is casting the ability.
         */
        private Entity entity;

        /**
         * Ability to be cast.
         */
        private AbilityComponent.Ability ability;

        /**
         * Target of the ability.
         */
        private Vector3f target;

        /**
         * Time before actually casting the ability.
         */
        private float castingTime;

        /**
         * True to make the caster continually channel the ability.
         */
        private boolean isChanneled = false;

        /**
         * Creates the caster.
         * @param entity the casting entity.
         * @param ability the ability being cast.
         * @param castingTime the time it takes to cast the ability.
         */
        private Caster(Entity entity, AbilityComponent.Ability ability, Vector3f target, float castingTime) {
            this.entity = entity;
            this.ability = ability;
            this.target = target;
            this.castingTime = castingTime;
        }
    }
}
