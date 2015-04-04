package com.submu.pug.game.gui;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.GUIKeyListener;
import com.halboom.pgt.pgui.widgets.Button;
import com.jme3.asset.AssetManager;
import com.jme3.math.Vector3f;
import com.submu.pug.data.ObjectsData;
import com.submu.pug.game.gui.widgets.GameButtonWidget;
import com.submu.pug.game.gui.widgets.PurchaseWidget;
import com.submu.pug.game.objects.components.AbilityComponent;
import com.submu.pug.game.objects.systems.AbilitySystem;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/19/13
 * Time: 11:48 AM
 * GUI for upgrading abilities.
 */
public class UpgradeFrame extends Frame {
    /**
     * Key of the data to store for the ability button's parent.
     */
    private static final String BUTTON_PARENT = "ability button parent";

    /**
     * Asset manager to use.
     */
    private AssetManager assetManager;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Ability system to use for ability data.
     */
    private AbilitySystem abilitySystem;

    /**
     * Used for buttons.
     */
    private GUIKeyListener guiKeyListener;

    /**
     * Entity to upgrade.
     */
    private Entity entity;

    /**
     * Ability buttons.
     */
    private List<GameButtonWidget> abilityButtons = new LinkedList<GameButtonWidget>();

    /**
     * Upgrades for the ability.
     */
    private List<GameButtonWidget> upgrades = new LinkedList<GameButtonWidget>();

    /**
     * Tooltip used for purchasing abilities.
     */
    private PurchaseWidget abilityPurchaseWidget;

    /**
     * Tooltip used for purchasing upgrades.
     */
    private PurchaseWidget upgradePurchaseWidget;

    /**
     * Selected ability index.
     */
    private int selectedIndex;

    /**
     * Initializes the GUI.
     * @param assetManager the asset manager to use.
     * @param entitySystem the entity system to use.
     * @param abilitySystem the ability system to use for ability data.
     * @param guiKeyListener the input system to use for buttons.
     * @param entity the entity to upgrade.
     */
    public UpgradeFrame(AssetManager assetManager,
                        EntitySystem entitySystem, AbilitySystem abilitySystem, GUIKeyListener guiKeyListener,
                        Entity entity) {
        this.assetManager = assetManager;
        this.entitySystem = entitySystem;
        this.abilitySystem = abilitySystem;
        this.guiKeyListener = guiKeyListener;
        this.entity = entity;
    }

    /**
     * Set the purchase status of an item.
     * @param index the index of the ability.
     */
    private void updateAbilityPurchaseStatus(int index) {
        if (abilityPurchaseWidget != null) {
            // Update if the widget can be purchased.
            AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
            AbilityComponent.Ability ability = abilityComponent.abilities.get(index);
            if (!ability.isUnlocked) {
                if (abilitySystem.canBuyAbility(entity, index)) {
                    abilityPurchaseWidget.setPurchasable(true);
                } else {
                    abilityPurchaseWidget.setPurchasable(false);
                }
            }
        }
    }

    /**
     * Set the purchase status of an upgrade.
     * @param index the index of the ability.
     * @param upgradeKey the upgrade key.
     */
    private void updateUpgradePurchaseStatus(int index, String upgradeKey) {
        if (upgradePurchaseWidget != null) {
            int level = abilitySystem.getUpgradeLevel(entity, index, upgradeKey);
            if (abilitySystem.isAbleToUpgrade(entity, index, upgradeKey, level + 1, true)) {
                upgradePurchaseWidget.setPurchasable(true);
            } else {
                upgradePurchaseWidget.setPurchasable(false);
            }
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        updateAbilityPurchaseStatus(selectedIndex);
    }

    /**
     * Create the UI for the upgrades.
     */
    private void createUI() {
        // Create the upgrade buttons.
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        if (abilityComponent == null) {
            return;
        }

        createAbilityButtons(abilityComponent.abilities);
    }

    /**
     * Creates the ability buttons.
     * @param abilities the abilities to create buttons for.
     */
    private void createAbilityButtons(List<AbilityComponent.Ability> abilities) {
        float startingX = 0.0f;
        float startingY = 0.0f;
        final float ySpacing = 4.0f;
        abilityButtons.clear();
        for (int i = 0; i < abilities.size(); i++) {
            final AbilityComponent.Ability element = abilities.get(i);
            final ObjectsData.AbilityData abilityData = abilitySystem.getAbilityData(element.internalName);
            if (abilityData == null) {
                continue;
            }
            GameButtonWidget gameButtonWidget = new GameButtonWidget(assetManager, guiKeyListener);
            float buttonHeight = gameButtonWidget.getExtentY() * 2;
            gameButtonWidget.setButtonName(String.valueOf(i));
            gameButtonWidget.setLabel(abilityData.name);
            gameButtonWidget.setIcon(abilityData.icon);
            gameButtonWidget.setPosition(new Vector3f(startingX, startingY - i * (ySpacing + buttonHeight), 0.0f));
            gameButtonWidget.setUserData(BUTTON_PARENT, gameButtonWidget);
            gameButtonWidget.addReleaseCallback(new Button.Callback() {
                @Override
                public void onAction(Button button) {
                    destroyUpgradesDisplay();
                    destroyUpgradesPurchaseWidget();
                    destroyAbilityPurchaseWidget();
                    int index = Integer.valueOf(button.getName());
                    selectedIndex = index;
                    GameButtonWidget widget = (GameButtonWidget) button.getUserData(BUTTON_PARENT);
                    // Spacing between the buttons and purchase frame.
                    final float spacing = 3.0f;
                    float x = button.getExtentX() + spacing;
                    float y = widget.getLocalPosition().y + widget.getExtentY();
                    createAbilityBuyTooltip(x, y, index);
                    // Set the button state to be selected, faded, or in focus.
                    for (GameButtonWidget gameButton : abilityButtons) {
                        // Reset the states first.
                        if (gameButton.getButtonName().equals(button.getName())) {
                            gameButton.setActive(true);
                            gameButton.setFaded(false);
                        } else {
                            gameButton.setActive(false);
                            gameButton.setFaded(true);
                        }
                    }
                    // Create the upgrade display if the ability is bought.
                    if (element.isUnlocked && abilityData.upgrades.size() > 0) {
                        createUpgradeDisplay(index);
                    }
                }
            });
            abilityButtons.add(gameButtonWidget);
            attach(gameButtonWidget);
        }
    }

    /**
     * Creates the ability purchasing display.
     * @param x the x location to create the tooltip.
     * @param y the y location to create the tooltip.
     * @param index the index of the ability skill.
     */
    private void createAbilityBuyTooltip(final float x, final float y, final int index) {
        destroyAbilityPurchaseWidget();

        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        AbilityComponent.Ability ability = abilityComponent.abilities.get(index);
        ObjectsData.AbilityData abilityData = abilitySystem.getAbilityData(ability.internalName);

        abilityPurchaseWidget = new PurchaseWidget(assetManager, guiKeyListener);
        abilityPurchaseWidget.setPurchasableText("Learn");
        abilityPurchaseWidget.setAlreadyPurchasedText("Learnt");
        abilityPurchaseWidget.setPosition(new Vector3f(x, y, 0));
        // Set the labels.
        abilityPurchaseWidget.setTitle(abilityData.name);
        abilityPurchaseWidget.setDescription(abilityData.description);
        if (ability.isUnlocked) {
            abilityPurchaseWidget.setPurchased(true);
        } else {
            abilityPurchaseWidget.setPurchased(false);
        }
        abilityPurchaseWidget.setCost(String.valueOf((int) abilityData.cost) + " SP");
        updateAbilityPurchaseStatus(index);

        // Add the purchase callback.
        abilityPurchaseWidget.setPurchaseCallback(new Button.Callback() {
            @Override
            public void onAction(Button button) {
                boolean hasBought = abilitySystem.buyAbilityChecked(entity, index);
                if (hasBought) {
                    abilityPurchaseWidget.setPurchased(true);
                    createUpgradeDisplay(index);
                }
            }
        });
        attach(abilityPurchaseWidget);
    }

    /**
     * Creates the ability purchasing display.
     * @param widget the widget that created the triggered tooltip.
     * @param x the x location to create the tooltip.
     * @param y the y location to create the tooltip.
     * @param index the index of the ability.
     * @param upgradeKey the key of the upgrade.
     */
    private void createUpgradeTooltip(final GameButtonWidget widget, final float x, final float y, final int index, final String upgradeKey) {
        destroyUpgradesPurchaseWidget();

        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        AbilityComponent.Ability ability = abilityComponent.abilities.get(index);
        ObjectsData.AbilityData abilityData = abilitySystem.getAbilityData(ability.internalName);

        ObjectsData.AbilityData.UpgradeData upgradeData = abilityData.upgrades.get(upgradeKey);
        AbilityComponent.Ability.AbilityUpgrade abilityUpgrade = ability.upgrades.get(upgradeKey);

        upgradePurchaseWidget = new PurchaseWidget(assetManager, guiKeyListener);
        upgradePurchaseWidget.setPurchasableText("Learn");
        upgradePurchaseWidget.setAlreadyPurchasedText("Learnt");
        upgradePurchaseWidget.setPosition(new Vector3f(x, y, 0));
        // Set the labels.
        upgradePurchaseWidget.setTitle(upgradeData.name);
        upgradePurchaseWidget.setDescription(upgradeData.description);

        final int level = abilitySystem.getUpgradeLevel(entity, index, upgradeKey);
        final int maxLevel = upgradeData.costs.length;
        upgradePurchaseWidget.setLevel("LV " + String.valueOf(level) + "/" + String.valueOf(maxLevel));
        if (level >= maxLevel) {
            upgradePurchaseWidget.setPurchased(true);
            upgradePurchaseWidget.setCost("");
        } else {
            upgradePurchaseWidget.setPurchased(false);
            float cost = abilitySystem.getUpgradeCost(entity, index, upgradeKey, level);
            upgradePurchaseWidget.setCost(String.valueOf((int) cost) + " SP");
        }
        updateUpgradePurchaseStatus(index, upgradeKey);

        // Add the purchase callback.
        upgradePurchaseWidget.setPurchaseCallback(new Button.Callback() {
            @Override
            public void onAction(Button button) {
                boolean hasBought = abilitySystem.upgradeAbilityChecked(entity, index, upgradeKey, level + 1);
                if (hasBought) {
                    upgradePurchaseWidget.setPurchased(true);
                    upgradePurchaseWidget.setLevel("LV " + String.valueOf(level + 1) + "/" + String.valueOf(maxLevel));
                    float cost = abilitySystem.getUpgradeCost(entity, index, upgradeKey, level + 1);
                    if (cost == 0) {
                        upgradePurchaseWidget.setCost("");
                    } else {
                        upgradePurchaseWidget.setCost(String.valueOf((int) cost) + " SP");
                    }
                    widget.setLevel("LV. " + String.valueOf(level + 1));
                }
            }
        });
        attach(upgradePurchaseWidget);
    }

    /**
     * Creates the display for the upgrades.
     * @param index the index of the ability skill.
     */
    private void createUpgradeDisplay(final int index) {
        destroyUpgradesDisplay();

        // Retrieve the ability data.
        AbilityComponent abilityComponent = entitySystem.getComponent(entity, AbilityComponent.class);
        AbilityComponent.Ability element = abilityComponent.abilities.get(index);
        ObjectsData.AbilityData abilityData = abilitySystem.getAbilityData(element.internalName);

        boolean hasUpgrades = abilityData.upgrades.entrySet().size() > 0;
        if (hasUpgrades && element.isUnlocked) {
            final float spacing = 3.0f;
            final float ySpacing = 4.0f;
            upgrades.clear();

            int i = 0;
            for (Map.Entry<String, ObjectsData.AbilityData.UpgradeData> entry : abilityData.upgrades.entrySet()) {
                final AbilityComponent.Ability.AbilityUpgrade upgrade = element.upgrades.get(entry.getKey());
                final int upgradeLevel = abilitySystem.getUpgradeLevel(entity, index, entry.getKey());
                final GameButtonWidget gameButtonWidget = new GameButtonWidget(assetManager, guiKeyListener);
                float buttonX = gameButtonWidget.getExtentX() * 2
                        + abilityPurchaseWidget.getExtentX() * 2
                        + spacing * 2;
                float buttonY = abilityPurchaseWidget.getLocalPosition().y
                        - gameButtonWidget.getExtentY()
                        - i * (ySpacing + gameButtonWidget.getExtentY() * 2);
                gameButtonWidget.setButtonName(entry.getKey());
                gameButtonWidget.setLabel(entry.getValue().name);
                gameButtonWidget.setLevel("LV. " + String.valueOf(upgradeLevel));
                gameButtonWidget.setPosition(new Vector3f(buttonX, buttonY, 0.0f));
                gameButtonWidget.setUserData(BUTTON_PARENT, gameButtonWidget);
                final String upgradeKey = entry.getKey();
                gameButtonWidget.addReleaseCallback(new Button.Callback() {
                    @Override
                    public void onAction(Button button) {
                        destroyUpgradesPurchaseWidget();

                        GameButtonWidget widget = (GameButtonWidget) button.getUserData(BUTTON_PARENT);
                        // Spacing between the buttons and purchase frame.
                        final float spacing = 3.0f;
                        float x = gameButtonWidget.getLocalPosition().x + button.getExtentX() + spacing;
                        float y = widget.getLocalPosition().y + widget.getExtentY();
                        createUpgradeTooltip(gameButtonWidget, x, y, index, upgradeKey);
                        // Set the button state to be selected, faded, or in focus.
                        for (GameButtonWidget gameButton : upgrades) {
                            // Reset the states first.
                            if (gameButton.getButtonName().equals(button.getName())) {
                                gameButton.setActive(true);
                                gameButton.setFaded(false);
                            } else {
                                gameButton.setActive(false);
                                gameButton.setFaded(true);
                            }
                        }
                    }
                });
                upgrades.add(gameButtonWidget);
                attach(gameButtonWidget);

                i++;
            }
        }
    }

    /**
     * Destroys the purchasing display.
     */
    private void destroyAbilityPurchaseWidget() {
        if (abilityPurchaseWidget != null) {
            detach(abilityPurchaseWidget);
            abilityPurchaseWidget = null;
        }
    }

    /**
     * Destroys the upgrade purchasing display.
     */
    private void destroyUpgradesPurchaseWidget() {
        if (upgradePurchaseWidget != null) {
            detach(upgradePurchaseWidget);
            upgradePurchaseWidget = null;
        }
    }

    /**
     * Destroys the upgrade display.
     */
    private void destroyUpgradesDisplay() {
        for (GameButtonWidget upgrade : upgrades) {
            detach(upgrade);
        }
        upgrades.clear();
    }

    @Override
    public void onAttached() {
        super.onAttached();

        createUI();
    }
}
