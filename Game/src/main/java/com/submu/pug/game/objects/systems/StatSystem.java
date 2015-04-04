package com.submu.pug.game.objects.systems;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySet;
import com.exploringlines.entitysystem.EntitySystem;
import com.exploringlines.entitysystem.Subsystem;
import com.submu.pug.game.objects.components.BuffedStatComponent;
import com.submu.pug.game.objects.components.StatComponent;
import com.submu.pug.game.objects.components.WalkComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/10/13
 * Time: 5:02 PM
 * System to handle updating of statistics.
 */
public class StatSystem implements Subsystem {
    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Initializes the stats system.
     * @param entitySystem the entity system to use.
     */
    public StatSystem(EntitySystem entitySystem) {
        this.entitySystem = entitySystem;
    }

    /**
     * Copies statistics to the calculated statistics.
     * @param statComponent the statistics component to mirror.
     * @param buffedStatComponent the calculated statistics component to set.
     */
    private void mirrorStats(StatComponent statComponent, BuffedStatComponent buffedStatComponent) {
        buffedStatComponent.armor = statComponent.armor;
        buffedStatComponent.cooldownReduction = statComponent.cooldownReduction;
        buffedStatComponent.damage = statComponent.damage;
        buffedStatComponent.force = statComponent.force;
        buffedStatComponent.jump = statComponent.jump;
        buffedStatComponent.maxHealth = statComponent.maxHealth;
        buffedStatComponent.maxMana = statComponent.maxMana;
        buffedStatComponent.range = statComponent.range;
        buffedStatComponent.speed = statComponent.speed;
        buffedStatComponent.weight = statComponent.weight;
    }

    /**
     * Calculates the statistics based on the base statistics.
     * @param statComponent the base statistics component.
     * @param buffedStatComponent the buffed statistics component.
     */
    private void calculateStats(StatComponent statComponent, BuffedStatComponent buffedStatComponent) {
        buffedStatComponent.maxHealth = (statComponent.maxHealth + buffedStatComponent.addMaxHealth) * buffedStatComponent.multMaxHealth;
        buffedStatComponent.maxMana = (statComponent.maxMana + buffedStatComponent.addMaxMana) * buffedStatComponent.multMaxMana;
        buffedStatComponent.armor = (statComponent.armor + buffedStatComponent.addArmor) * buffedStatComponent.multArmor;
        buffedStatComponent.cooldownReduction = statComponent.cooldownReduction + buffedStatComponent.addCooldownReduction;
        buffedStatComponent.damage = (statComponent.damage + buffedStatComponent.addDamage) * buffedStatComponent.multDamage;
        buffedStatComponent.force = (statComponent.force + buffedStatComponent.addForce) * buffedStatComponent.multForce;
        buffedStatComponent.jump = (statComponent.jump + buffedStatComponent.addJump) * buffedStatComponent.multJump;
        buffedStatComponent.range = (statComponent.range + buffedStatComponent.addRange) * buffedStatComponent.multRange;
        buffedStatComponent.speed = (statComponent.speed + buffedStatComponent.addSpeed) * buffedStatComponent.multSpeed;
        buffedStatComponent.weight = (statComponent.weight + buffedStatComponent.addWeight) * buffedStatComponent.multWeight;
    }

    @Override
    public void update(float tpf) {
        // Set the initial health and mana.
        EntitySet set = entitySystem.getEntities(StatComponent.class);
        for (Entity entity : set.getAddedEntities()) {
            StatComponent statComponent = entitySystem.getComponent(entity, StatComponent.class);
            statComponent.health = statComponent.maxHealth;
            statComponent.mana = statComponent.maxMana;
            BuffedStatComponent buffedStatComponent = new BuffedStatComponent();
            mirrorStats(statComponent, buffedStatComponent);
            entitySystem.setComponent(entity, buffedStatComponent);
            // Set the maximum speed of the entity.
            WalkComponent walkComponent = entitySystem.getComponent(entity, WalkComponent.class);
            if (walkComponent != null) {
                walkComponent.horizontalAcceleration = buffedStatComponent.speed;
            }
        }

        EntitySet calculatedSet = entitySystem.getEntities(BuffedStatComponent.class);
        // Update the statistics of each entity.
        for (Entity entity : calculatedSet.getChangedEntities()) {
            StatComponent statComponent = entitySystem.getComponent(entity, StatComponent.class);
            BuffedStatComponent buffedStat = entitySystem.getComponent(entity, BuffedStatComponent.class);
            calculateStats(statComponent, buffedStat);

            // Set the maximum speed of the entity.
            WalkComponent walkComponent = entitySystem.getComponent(entity, WalkComponent.class);
            if (walkComponent != null) {
                walkComponent.horizontalAcceleration = buffedStat.speed;
            }
        }

        // Check and limit the health and mana of each entity.
        for (Entity entity : set.getChangedEntities()) {
            StatComponent statComponent = entitySystem.getComponent(entity, StatComponent.class);
            BuffedStatComponent buffedStat = entitySystem.getComponent(entity, BuffedStatComponent.class);
            // Remove entities with no health.
            if (statComponent.health <= 0 && buffedStat.maxHealth > 0) {
                entitySystem.removeEntity(entity);
            }
            // Prevent health and mana from going over the maximum amount.
            if (statComponent.health > buffedStat.maxHealth) {
                statComponent.health = buffedStat.maxHealth;
            }
            if (statComponent.mana > buffedStat.maxMana) {
                statComponent.mana = buffedStat.maxMana;
            }
        }

        // Remove the calculated portion of the statistics.
        for (Entity entity : set.getRemovedEntities()) {
            entitySystem.removeComponent(entity, BuffedStatComponent.class);
        }
    }

    @Override
    public void cleanupSubsystem() {
    }

    @Override
    public void destroy() {
    }
}
