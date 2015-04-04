package com.submu.pug.scripting.events;

import com.halboom.pgt.debug.DebugGlobals;
import com.halboom.pgt.input.InputActions;
import com.jme3.input.InputManager;
import com.submu.pug.data.KeyMap;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.systems.AbilitySystem;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/15/13
 * Time: 12:52 PM
 * Creates a key pressed event when a key is pressed.
 * Also handles key event passing to systems other than movement.
 */
public class EventKey extends InputActions implements EventHook {
    /**
     * Name of the event on key down.
     */
    public static final String EVENT_ANALOG = "eventKeyAnalog";

    /**
     * Name of the event on key pressed.
     */
    public static final String EVENT_KEY_PRESSED = "eventKeyPressed";

    /**
     * Name of the event on key up.
     */
    public static final String EVENT_KEY_RELEASED = "eventKeyReleased";

    /**
     * Player that the event is attached to.
     */
    private Player player;

    /**
     * Ability system to send key events to.
     */
    private AbilitySystem abilitySystem;

    /**
     * True to set the GUI as activated.
     */
    private boolean isGUIActivated = false;

    /**
     * Initializes the event.
     * @param inputManager the inputManager to attach to.
     * @param player the player that the event is attached to.
     * @param abilitySystem the ability system to send key events to.
     */
    public EventKey(InputManager inputManager, Player player, AbilitySystem abilitySystem) {
        super(inputManager);
        this.player = player;
        this.abilitySystem = abilitySystem;

        // Register all the events.
        registerAction(KeyMap.jump);
        registerAction(KeyMap.moveForward);
        registerAction(KeyMap.moveBackward);
        registerAction(KeyMap.moveLeft);
        registerAction(KeyMap.moveRight);
        registerAction(KeyMap.moveUp);
        registerAction(KeyMap.moveDown);
        registerAction(KeyMap.gameAbility1);
        registerAction(KeyMap.gameAbility2);
        registerAction(KeyMap.gameAbility3);
        registerAction(KeyMap.look);
        registerAction(KeyMap.use);
    }

    @Override
    protected void onActionInput(String name, boolean isPressed, float tpf) {
        ScriptEvent event;
        // Only allow action triggers when the GUI is not active.
        if (!isGUIActivated) {
            if (name.equals(KeyMap.gameAbility1.name)) {
                abilitySystem.onHotkeyPressed(player, AbilitySystem.HOTKEY_ABILITY1, isPressed);
            } else if (name.equals(KeyMap.gameAbility2.name)) {
                abilitySystem.onHotkeyPressed(player, AbilitySystem.HOTKEY_ABILITY2, isPressed);
            } else if (name.equals(KeyMap.gameAbility3.name)) {
                abilitySystem.onHotkeyPressed(player, AbilitySystem.HOTKEY_ABILITY3, isPressed);
            } else if (name.equals(KeyMap.jump.name)) {
                abilitySystem.onHotkeyPressed(player, AbilitySystem.HOTKEY_JUMP, isPressed);
            }
        }
        // Send the key input to the scripting system.
        if (isPressed) {
            event = new ScriptEvent(EVENT_KEY_PRESSED, name, player, isGUIActivated);
        } else {
            event = new ScriptEvent(EVENT_KEY_RELEASED, name, player, isGUIActivated);
        }
        ScriptGlobals.getInstance().addEvent(event);
    }

    @Override
    protected void onAnalogInput(String name, float value, float tpf) {
        // Only allow action triggers when the GUI is not active.
        if (!isGUIActivated) {
            if (name.equals(KeyMap.gameAbility1.name)) {
                abilitySystem.onHotkeyAnalog(player, AbilitySystem.HOTKEY_ABILITY1);
            } else if (name.equals(KeyMap.gameAbility2.name)) {
                abilitySystem.onHotkeyAnalog(player, AbilitySystem.HOTKEY_ABILITY2);
            } else if (name.equals(KeyMap.gameAbility3.name)) {
                abilitySystem.onHotkeyAnalog(player, AbilitySystem.HOTKEY_ABILITY3);
            } else if (name.equals(KeyMap.jump.name)) {
                abilitySystem.onHotkeyAnalog(player, AbilitySystem.HOTKEY_JUMP);
            }
        }
        ScriptEvent event = new ScriptEvent(EVENT_ANALOG, name, value, player, isGUIActivated);
        ScriptGlobals.getInstance().addEvent(event);
    }

    /**
     * Sets if the events are done when the GUI is active.
     * @param active true to flag the GUI as active.
     */
    public void isGUIActivated(boolean active) {
        isGUIActivated = active;
    }

    @Override
    public void destroyEvent() {
        cleanup();
    }

    @Override
    protected void onUpdate(float tpf) {
    }

    @Override
    protected void onActivated() {
    }

    @Override
    protected void onDeactivated() {
    }

    @Override
    protected void cleanupAction() {
    }

    @Override
    public void updateEvent(float tpf) {
    }
}
