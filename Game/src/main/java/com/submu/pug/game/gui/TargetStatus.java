package com.submu.pug.game.gui;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.widgets.Bar;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.submu.pug.data.Data;
import com.submu.pug.data.GUIElementsData;
import com.submu.pug.game.objects.components.BuffedStatComponent;
import com.submu.pug.game.objects.components.StatComponent;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/6/13
 * Time: 12:26 PM
 * Displays the target's status.
 */
public class TargetStatus extends Frame {
    /**
     * Entity system to retrieve the target information from.
     */
    private EntitySystem entitySystem;

    /**
     * Targeted entity to display the status of.
     */
    private Entity target;

    /**
     * Health bar for the target.
     */
    private Bar targetHealth;

    /**
     * Initializes the target status.
     * @param assetManager the asset manager to load resources from.
     * @param entitySystem the entity system to use.
     */
    public TargetStatus(AssetManager assetManager, EntitySystem entitySystem) {
        this.entitySystem = entitySystem;

        GUIElementsData.ElementLocation barProperties = Data.getInstance().getGuiElementsData().enemyBar;
        targetHealth = WidgetFactory.createBar(assetManager, barProperties);
        attach(targetHealth);
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        if (target != null) {
            StatComponent statComponent = entitySystem.getComponent(target, StatComponent.class);
            BuffedStatComponent calculatedStat = entitySystem.getComponent(target, BuffedStatComponent.class);
            if (statComponent != null && calculatedStat.maxHealth != 0) {
                attach(targetHealth);
                targetHealth.setAmount(statComponent.health / calculatedStat.maxHealth);
            }
        } else {
            detach(targetHealth);
        }
    }

    @Override
    public void onAttached() {
        super.onAttached();

        float width = getParent().getExtentX() * 2;
        float height = getParent().getExtentY() * 2;

        GUIElementsData.ElementLocation barProperties = Data.getInstance().getGuiElementsData().enemyBar;
        targetHealth.setPosition(new Vector3f(width * barProperties.positionX, height * barProperties.positionY, 0));
    }

    /**
     * @param target the target to display the status of.
     */
    public void setTarget(Entity target) {
        this.target = target;
    }
}
