package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/13/13
 * Time: 12:50 PM
 * Holds calculated statistics.
 * If the statistic is here then it should be used over the statistics component as
 * this will be where buffs are added to.
 * Any buff needs to be applied every frame in order to be part of the calculated component.
 * Not every statistic needs to be calculated.
 */
public class BuffedStatComponent implements Component {
    /**
     * Maximum health the entity can have and the modifiers.
     */
    public float maxHealth = 0,
                 addMaxHealth = 0,
                 multMaxHealth = 1.0f;

    /**
     * Maximum mana the entity can have and the modifiers.
     */
    public float maxMana = 0,
                 addMaxMana = 0,
                 multMaxMana = 1.0f;

    /**
     * Bonus damage the entity does.
     */
    public float damage = 0,
                 addDamage = 0,
                 multDamage = 1.0f;

    /**
     * Armor of the entity.
     */
    public float armor = 0,
                 addArmor = 0,
                 multArmor = 1.0f;

    /**
     * Bonus range for the entity.
     */
    public float range = 0,
                 addRange = 0,
                 multRange = 1.0f;

    /**
     * Cooldown reduction the entity has.
     */
    public float cooldownReduction = 0,
                 addCooldownReduction = 0;

    /**
     * Speed bonus of the entity.
     */
    public float speed = 0,
                 addSpeed = 0,
                 multSpeed = 1.0f;

    /**
     * Jump bonus for the entity.
     */
    public float jump = 0,
                 addJump = 0,
                 multJump = 1.0f;

    /**
     * Amount of knockback that occurs when hitting an entity.
     */
    public float force = 0,
                 addForce = 0,
                 multForce = 1.0f;

    /**
     * How much the entity weighs for force calculations.
     * A weight of zero means the object is not affected by force.
     */
    public float weight = 1f,
                 addWeight = 0,
                 multWeight = 1.0f;

    @Override
    public Component copy() {
        BuffedStatComponent output = new BuffedStatComponent();
        output.maxHealth = maxHealth;
        output.addMaxHealth = addMaxHealth;
        output.multMaxHealth = multMaxHealth;
        output.maxMana = maxMana;
        output.addMaxMana = addMaxMana;
        output.multMaxMana = multMaxMana;
        output.damage = damage;
        output.addDamage = addDamage;
        output.multDamage = multDamage;
        output.armor = armor;
        output.addArmor = addArmor;
        output.multArmor = multArmor;
        output.range = range;
        output.addRange = addRange;
        output.multRange = multRange;
        output.cooldownReduction = cooldownReduction;
        output.addCooldownReduction = addCooldownReduction;
        output.speed = speed;
        output.addSpeed = addSpeed;
        output.multSpeed = multSpeed;
        output.jump = jump;
        output.addJump = addJump;
        output.multJump = multJump;
        output.force = force;
        output.addForce = addForce;
        output.multForce = multForce;
        output.weight = weight;
        output.addWeight = addWeight;
        output.multWeight = multWeight;

        return output;
    }
}
