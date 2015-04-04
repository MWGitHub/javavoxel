package com.submu.pug.game.gui;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.widgets.Bar;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.Vector3f;
import com.submu.pug.data.Data;
import com.submu.pug.data.GUIElementsData;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.game.gui.widgets.SquareWidget;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.components.AbilityComponent;
import com.submu.pug.game.objects.components.BuffedStatComponent;
import com.submu.pug.game.objects.components.ExperienceComponent;
import com.submu.pug.game.objects.components.InventoryComponent;
import com.submu.pug.game.objects.components.ItemComponent;
import com.submu.pug.game.objects.components.OwnerComponent;
import com.submu.pug.game.objects.components.StatComponent;
import com.submu.pug.game.objects.systems.AbilitySystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/6/13
 * Time: 1:53 PM
 * Displays the observed entity's status.
 * The observed entity can also be the entity the player is controlling.
 */
public class ObservedStatus extends Frame {
    /**
     * Asset manager to load resources from.
     */
    private AssetManager assetManager;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Ability system to use for checking cooldowns and loading icons.
     */
    private AbilitySystem abilitySystem;

    /**
     * Player assigner to retrieve player information from.
     */
    private PlayerAssigner playerAssigner;

    /**
     * The entity the player is observing or controlling.
     */
    private Entity observedEntity;

    /**
     * Health bar to display.
     */
    private Bar observedHealth;

    /**
     * Mana bar to display.
     */
    private Bar observedMana;

    /**
     * Experience bar to display.
     */
    private Bar observedExp;

    /**
     * Bars for cooldowns.
     */
    private List<Bar> cooldownBars = new ArrayList<Bar>();

    /**
     * Displays the amount of money the player has.
     */
    private BitmapText money;

    /**
     * Displays the amount of skill points an entity has.
     */
    private BitmapText skillPoints;

    /**
     * Displays the level of the entity.
     */
    private BitmapText level;

    /**
     * Inventory widgets.
     */
    private List<SquareWidget> inventory = new LinkedList<SquareWidget>();

    /**
     * Initializes the frame.
     * @param assetManager the asset manager the resources are loaded from.
     * @param entitySystem the entity system to use.
     * @param abilitySystem the ability system to track the ability cooldowns and icons with.
     * @param playerAssigner the player assigner to retrieve the player information from.
     */
    public ObservedStatus(AssetManager assetManager, EntitySystem entitySystem, AbilitySystem abilitySystem,
                          PlayerAssigner playerAssigner) {
        this.assetManager = assetManager;
        this.entitySystem = entitySystem;
        this.abilitySystem = abilitySystem;
        this.playerAssigner = playerAssigner;

        // Vitals for the observed entity.
        GUIElementsData.ElementLocation barProperties = Data.getInstance().getGuiElementsData().healthBar;
        observedHealth = WidgetFactory.createBar(assetManager, barProperties);
        attach(observedHealth);

        barProperties = Data.getInstance().getGuiElementsData().manaBar;
        observedMana = WidgetFactory.createBar(assetManager, barProperties);
        attach(observedMana);

        barProperties = Data.getInstance().getGuiElementsData().expBar;
        observedExp = WidgetFactory.createBar(assetManager, barProperties);
        attach(observedExp);

        // Money text display
        barProperties = Data.getInstance().getGuiElementsData().money;
        BitmapFont font = Fonts.mainFont;
        money = new BitmapText(font, false);
        money.setSize(font.getPreferredSize());
        money.setBox(new Rectangle(0, 0, barProperties.width, barProperties.height));
        money.setAlignment(BitmapFont.Align.Right);
        money.setText("0 Curls");
        addToNode(money);

        // Skill points text display
        barProperties = Data.getInstance().getGuiElementsData().skillPoints;
        skillPoints = new BitmapText(font, false);
        skillPoints.setSize(font.getPreferredSize());
        skillPoints.setBox(new Rectangle(0, 0, barProperties.width, barProperties.height));
        skillPoints.setAlignment(BitmapFont.Align.Right);
        skillPoints.setText("0 Points");
        addToNode(skillPoints);

        // Skill points text display
        barProperties = Data.getInstance().getGuiElementsData().level;
        level = new BitmapText(font, false);
        level.setSize(font.getPreferredSize());
        level.setBox(new Rectangle(0, 0, barProperties.width, barProperties.height));
        level.setAlignment(BitmapFont.Align.Right);
        level.setText("LV. 0");
        addToNode(level);
    }

    /**
     * Removes all inventory icons.
     */
    private void removeAllInventoryIcons() {
        for (SquareWidget widget : inventory) {
            detach(widget);
        }
        inventory.clear();
    }

    /**
     * Updates the inventory.
     * TODO: Put the inventory parameters in a data file.
     */
    private void updateInventory() {
        if (observedEntity == null) {
            return;
        }
        InventoryComponent inventoryComponent = entitySystem.getComponent(observedEntity, InventoryComponent.class);
        if (inventoryComponent == null) {
            removeAllInventoryIcons();
            return;
        }
        // Recreate the inventory if it has changed in size.
        if (inventory.size() > inventoryComponent.itemIDs.size()) {
            removeAllInventoryIcons();
        }
        // Create additional inventory icons if needed.
        for (int i = 0; inventory.size() < inventoryComponent.itemIDs.size(); i++) {
            SquareWidget squareWidget = new SquareWidget(assetManager, 32f, 32f);
            Entity item = entitySystem.getEntity(inventoryComponent.itemIDs.get(i));
            ItemComponent itemComponent = entitySystem.getComponent(item, ItemComponent.class);
            if (itemComponent.icon != null) {
                squareWidget.setIcon(itemComponent.icon);
            }
            attach(squareWidget);
            inventory.add(squareWidget);
        }
        // Get the starting position of the cooldown bars.
        final float width = getParent().getExtentX() * 2;
        final float height = getParent().getExtentY() * 2;
        final float iconWidth = 32f;
        final float iconHeight = 32f;
        final float buffer = iconWidth * 0.1f;
        // The last bar's buffer is not counting nor is its size due to the center of the bar.
        float totalWidth = (inventory.size() - 1) * (iconWidth + buffer);
        float startingLocation = width / 2 - totalWidth / 2;
        for (int i = 0; i < inventory.size(); i++) {
            // Set the bar positions.
            SquareWidget widget = inventory.get(i);
            widget.setPosition(new Vector3f(startingLocation + i * (iconWidth + buffer),
                    height* 0.3f, 0));
            Entity item = entitySystem.getEntity(inventoryComponent.itemIDs.get(i));
            ItemComponent itemComponent = entitySystem.getComponent(item, ItemComponent.class);
            if (itemComponent.icon != null) {
                widget.setIcon(itemComponent.icon);
            }
        }
    }

    /**
     * Removes all the cooldown bars.
     */
    private void removeAllCooldownBars() {
        for (Bar bar : cooldownBars) {
            detach(bar);
        }
        cooldownBars.clear();
    }

    /**
     * Updates the cooldown bars.
     */
    private void updateCooldownBars() {
        if (observedEntity == null) {
            return;
        }
        GUIElementsData.ElementLocation barProperties = Data.getInstance().getGuiElementsData().cooldownLocation;
        AbilityComponent abilityComponent = entitySystem.getComponent(observedEntity, AbilityComponent.class);
        if (abilityComponent != null) {
            // Get only abilities that are shown.
            List<AbilityComponent.Ability> visibleAbilities = new LinkedList<AbilityComponent.Ability>();
            for (AbilityComponent.Ability element : abilityComponent.abilities) {
                ObjectsData.AbilityData abilityData = abilitySystem.getAbilityData(element.internalName);
                if (abilityData != null && abilityData.isShown) {
                    visibleAbilities.add(element);
                }
            }
            // Remove cooldown bars if there too many or create cooldown bars if there are not enough.
            if (cooldownBars.size() > visibleAbilities.size()) {
                removeAllCooldownBars();
            }
            while (cooldownBars.size() < visibleAbilities.size()) {
                Bar bar = new Bar(assetManager, barProperties.width, barProperties.height);
                bar.setColor(barProperties.color.red, barProperties.color.green, barProperties.color.blue, barProperties.color.alpha);
                bar.setType(Bar.Type.DOWN_UP);
                attach(bar);
                cooldownBars.add(bar);
            }
            // Get the starting position of the cooldown bars.
            final float buffer = barProperties.width * 0.1f;
            float width = getParent().getExtentX() * 2;
            float height = getParent().getExtentY() * 2;
            // The last bar's buffer is not counting nor is its size due to the center of the bar.
            float totalWidth = (cooldownBars.size() - 1) * (barProperties.width + buffer);
            float startingLocation = width / 2 - totalWidth / 2;
            for (int i = 0; i < cooldownBars.size(); i++) {
                // Set the bar positions.
                Bar cooldownBar = cooldownBars.get(i);
                cooldownBar.setPosition(new Vector3f(startingLocation + i * (barProperties.width + buffer),
                        height * barProperties.positionY, 0));
                AbilityComponent.Ability ability = visibleAbilities.get(i);
                if (ability.isUnlocked) {
                    cooldownBar.setAmount(1 - ability.cooldownTimer / ability.cooldown);
                }
            }
        } else {
            removeAllCooldownBars();
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        // Update controlled entity status.
        if (observedEntity != null) {
            StatComponent statComponent = entitySystem.getComponent(observedEntity, StatComponent.class);
            BuffedStatComponent calculatedStat = entitySystem.getComponent(observedEntity, BuffedStatComponent.class);
            // Update health.
            if (statComponent != null && calculatedStat.maxHealth != 0) {
                observedHealth.setAmount(statComponent.health / calculatedStat.maxHealth);
                attach(observedHealth);
            } else {
                detach(observedHealth);
            }
            // Update mana.
            if (statComponent != null && calculatedStat.maxMana != 0) {
                attach(observedMana);
                observedMana.setAmount(statComponent.mana / calculatedStat.maxMana);
            } else {
                detach(observedMana);
            }
        }

        // Update the gold.
        if (observedEntity != null) {
            OwnerComponent ownerComponent = entitySystem.getComponent(observedEntity, OwnerComponent.class);
            if (ownerComponent != null) {
                Player player = playerAssigner.getPlayer(ownerComponent.playerID);
                if (player != null) {
                    money.setText((int) player.getMoney() + " " + Data.getInstance().getGuiElementsData().moneyName);
                }
            }
        }

        // Update the experience.
        if (observedEntity != null) {
            ExperienceComponent experienceComponent = entitySystem.getComponent(observedEntity, ExperienceComponent.class);
            if (experienceComponent != null) {
                // Update skill points.
                addToNode(skillPoints);
                skillPoints.setText(experienceComponent.skillPoints + " "
                        + Data.getInstance().getGuiElementsData().skillPointsName);
                // Update experience.
                attach(observedExp);
                float remainingExp = experienceComponent.experiencePerLevel * experienceComponent.level
                        - experienceComponent.experience;
                observedExp.setAmount(1 - (remainingExp / (experienceComponent.experiencePerLevel)));
                // Update the level.
                addToNode(level);
                level.setText("LV. " + experienceComponent.level);
            } else {
                skillPoints.removeFromParent();
                detach(observedExp);
                level.removeFromParent();
            }
        } else {
            skillPoints.removeFromParent();
            detach(observedExp);
            level.removeFromParent();
        }

        updateCooldownBars();
        updateInventory();
    }

    @Override
    public void onAttached() {
        super.onAttached();

        float width = getParent().getExtentX() * 2;
        float height = getParent().getExtentY() * 2;

        GUIElementsData.ElementLocation barProperties = Data.getInstance().getGuiElementsData().healthBar;
        observedHealth.setPosition(new Vector3f(width * barProperties.positionX, height * barProperties.positionY, 0));

        barProperties = Data.getInstance().getGuiElementsData().manaBar;
        observedMana.setPosition(new Vector3f(width * barProperties.positionX, height * barProperties.positionY, 0));

        barProperties = Data.getInstance().getGuiElementsData().expBar;
        observedExp.setPosition(new Vector3f(width * barProperties.positionX, height * barProperties.positionY, 0));

        barProperties = Data.getInstance().getGuiElementsData().money;
        money.setLocalTranslation(width * barProperties.positionX, height * barProperties.positionY, 0);

        barProperties = Data.getInstance().getGuiElementsData().skillPoints;
        skillPoints.setLocalTranslation(width * barProperties.positionX, height * barProperties.positionY, 0);

        barProperties = Data.getInstance().getGuiElementsData().level;
        level.setLocalTranslation(width * barProperties.positionX, height * barProperties.positionY, 0);
    }

    /**
     * @param observedEntity the target to display the status of.
     */
    public void setObservedEntity(Entity observedEntity) {
        // Remove previous abilities if the entity is no longer the same.
        if (this.observedEntity != observedEntity) {
            removeAllCooldownBars();
        }
        this.observedEntity = observedEntity;
    }
}
