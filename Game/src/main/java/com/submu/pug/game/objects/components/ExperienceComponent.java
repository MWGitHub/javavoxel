package com.submu.pug.game.objects.components;

import com.exploringlines.entitysystem.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/15/13
 * Time: 3:20 PM
 * Component to hold experience related data.
 */
public class ExperienceComponent implements Component {
    /**
     * Current amount of skill points.
     */
    public int skillPoints = 0;

    /**
     * Current experience of the entity.
     */
    public int experience = 0;

    /**
     * Experience needed per level.
     */
    public int experiencePerLevel = 300;

    /**
     * Level of the entity.
     */
    public int level = 1;

    /**
     * Amount of experience given per level.
     */
    public int experienceGivenPerLevel = 100;

    @Override
    public Component copy() {
        ExperienceComponent output = new ExperienceComponent();
        output.skillPoints = skillPoints;
        output.experience = experience;
        output.experiencePerLevel = experiencePerLevel;
        output.level = level;
        output.experienceGivenPerLevel = experienceGivenPerLevel;

        return output;
    }
}
