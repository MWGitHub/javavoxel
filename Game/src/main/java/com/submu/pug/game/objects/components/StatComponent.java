package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/10/13
 * Time: 4:59 PM
 * Statistics for an entity.
 */
public class StatComponent implements Component {
    /**
     * Maximum health the entity can have.
     */
    public float maxHealth = 0;

    /**
     * Health the entity has.
     */
    public float health = 0;

    /**
     * Maximum mana the entity can have.
     */
    public float maxMana = 0;

    /**
     * Mana the entity has.
     */
    public float mana = 0;

    /**
     * Bonus damage the entity does.
     */
    public float damage = 0;

    /**
     * Armor of the entity.
     */
    public float armor = 0;

    /**
     * Bonus range for the entity.
     */
    public float range = 0;

    /**
     * Cooldown reduction the entity has.
     */
    public float cooldownReduction = 0;

    /**
     * Maximum speed of the entity.
     */
    public float speed = 0;

    /**
     * Jump bonus for the entity.
     */
    public float jump = 0;

    /**
     * Amount of knockback that occurs when hitting an entity.
     */
    public float force = 0;

    /**
     * How much the entity weighs for force calculations.
     * A weight of zero means the object is not affected by force.
     */
    public float weight = 1f;

    @Override
    public Component copy() {
        StatComponent output = new StatComponent();
        output.maxHealth = maxHealth;
        output.health = health;
        output.maxMana = maxMana;
        output.mana = mana;
        output.damage = damage;
        output.armor = armor;
        output.range = range;
        output.cooldownReduction = cooldownReduction;
        output.speed = speed;
        output.jump = jump;
        output.force = force;
        output.weight = weight;

        return output;
    }
}