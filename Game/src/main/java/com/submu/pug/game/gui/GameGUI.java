package com.submu.pug.game.gui;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.GUIDebug;
import com.halboom.pgt.pgui.GUIKeyListener;
import com.halboom.pgt.physics.filters.Filter;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.submu.pug.camera.CameraState;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.components.BuffedStatComponent;
import com.submu.pug.game.objects.components.TargetableComponent;
import com.submu.pug.game.objects.systems.AbilitySystem;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/3/13
 * Time: 12:26 PM
 * Handles events for the game and sends them to individual GUI elements.
 * TODO: When the UI is more solid, refactor to use data files instead.
 */
public class GameGUI {
    /**
     * Asset manager to load from.
     */
    private AssetManager assetManager;

    /**
     * Input manager to use.
     */
    private InputManager inputManager;

    /**
     * Root to attach gui objects to.
     */
    private Node root;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Ability system to retrieve data from.
     */
    private AbilitySystem abilitySystem;

    /**
     * Used for retrieving players.
     */
    private PlayerAssigner playerAssigner;

    /**
     * Used for toggling mouse states.
     */
    private CameraState cameraState;

    /**
     * Dimensions of the GUI.
     */
    private float width, height;

    /**
     * Listens to key events for use with GUI components.
     */
    private GUIKeyListener guiKeyListener;

    /**
     * Current entity under direct control.
     */
    private Entity controlledEntity;

    /**
     * Current target entity.
     */
    private Entity targetedEntity;

    /**
     * Status display for the observed entity.
     */
    private ObservedStatus observedStatus;

    /**
     * Collision filter for finding the target.
     */
    private Filter targetFilter;

    /**
     * UI to use for upgrades.
     */
    private UpgradeFrame upgradeFrame;

    /**
     * Frame to attach the GUI to.
     */
    private Frame frame;

    /**
     * Target status display.
     */
    private TargetStatus targetStatus;

    /**
     * Initializes the GUI.
     * @param assetManager the asset manager to use.
     * @param inputManager the input manager to use.
     * @param root the root to attach gui items to.
     * @param entitySystem the entity system to use.
     * @param abilitySystem the ability system to retrieve data from.
     * @param playerAssigner the assigner to retrieve players from.
     * @param cameraState the camera state to toggle the mouse.
     * @param dimensions the dimensions of the GUI.
     */
    public GameGUI(AssetManager assetManager, InputManager inputManager, Node root,
                   EntitySystem entitySystem, AbilitySystem abilitySystem, PlayerAssigner playerAssigner,
                   CameraState cameraState,
                   Vector2f dimensions) {
        this.assetManager = assetManager;
        this.inputManager = inputManager;
        this.root = root;
        this.entitySystem = entitySystem;
        this.abilitySystem = abilitySystem;
        this.playerAssigner = playerAssigner;
        this.cameraState = cameraState;
        this.width = dimensions.x;
        this.height = dimensions.y;
        guiKeyListener = new GUIKeyListener(inputManager);

        Fonts.loadFonts(assetManager);

        frame = new Frame();
        frame.setWidth(width);
        frame.setHeight(height);
        frame.attachRoot(root);

        observedStatus = new ObservedStatus(assetManager, entitySystem, abilitySystem, playerAssigner);
        frame.attach(observedStatus);

        targetStatus = new TargetStatus(assetManager, entitySystem);
        frame.attach(targetStatus);

        // Create a filter for targeting only actor entities.
        final EntitySystem system = entitySystem;
        targetFilter = new Filter() {
            @Override
            public boolean filterEntity(Entity entity, Bounds bounds) {
                if (entity.equals(controlledEntity)) {
                    return false;
                }
                TargetableComponent targetableComponent = system.getComponent(entity, TargetableComponent.class);
                if (targetableComponent == null) {
                    return false;
                }
                BuffedStatComponent calculatedStat = system.getComponent(entity, BuffedStatComponent.class);
                if (calculatedStat == null) {
                    return false;
                } else {
                    if (calculatedStat.maxHealth == 0) {
                        return  false;
                    }
                }
                return true;
            }
        };

        GUIDebug.setAssetManager(assetManager);
        //GUIDebug.showObjectBounds(frame);
    }

    /**
     * Updates the game GUI.
     * @param tpf the time passed since the last frame.
     */
    public void update(float tpf) {
        //GUIDebug.showObjectBounds(frame);
        observedStatus.setObservedEntity(controlledEntity);

        // Update target status.
        targetStatus.setTarget(targetedEntity);

        frame.update(tpf);

        // Update the keys.
        guiKeyListener.update(tpf);
    }

    /**
     * @param controlledEntity the entity to update the stats on.
     */
    public void setControlledEntity(Entity controlledEntity) {
        this.controlledEntity = controlledEntity;
    }

    /**
     * @param targetedEntity the entity that the player is looking at.
     */
    public void setTargetedEntity(Entity targetedEntity) {
        this.targetedEntity = targetedEntity;
    }

    /**
     * @return the targeting filter.
     */
    public Filter getTargetFilter() {
        return targetFilter;
    }

    /**
     * Opens the upgrade menu.
     */
    public void openUpgradeMenu() {
        if (controlledEntity == null) {
            return;
        }
        upgradeFrame = new UpgradeFrame(assetManager, entitySystem, abilitySystem, guiKeyListener, controlledEntity);
        upgradeFrame.setPosition(new Vector3f(width * 0.2f, height * 0.8f, 0));
        frame.attach(upgradeFrame);
        cameraState.setDragToRotate(true);
    }

    /**
     * Closes the upgrade menu.
     */
    public void closeUpgradeMenu() {
        cameraState.setDragToRotate(false);
        frame.detach(upgradeFrame);
        upgradeFrame = null;
    }

    /**
     * Opens or closes the upgrade menu depending on the last state.
     */
    public void toggleUpgradeMenu() {
        if (upgradeFrame == null) {
            openUpgradeMenu();
        } else {
            closeUpgradeMenu();
        }
    }
}
