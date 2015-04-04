package com.halboom.pgt.input;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.Trigger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 1/10/13
 * Time: 2:11 PM
 * Handles both adding analog and action listeners and cleanup.
 */
public abstract class InputActions implements AnalogListener, ActionListener {
    /**
     * InputManager to attach to.
     */
    private InputManager inputManager;

    /**
     * True if the inputs should be followed.
     */
    private boolean isActivated = true;

    /**
     * A list of commands stored in the action listener.
     */
    private List<String> actions = new ArrayList<String>();

    /**
     * Initializes the inputs.
     * @param inputManager the InputManager to initialize inputs with.
     */
    public InputActions(InputManager inputManager) {
        this.inputManager = inputManager;
    }

    /**
     * Remaps an action.
     * @param key the mapping to remap.
     * @param triggers the triggers of the action.
     */
    public final void remapAction(String key, Trigger... triggers) {
        if (!actions.contains(key)) {
            return;
        }
        if (!inputManager.hasMapping(key)) {
            return;
        }
        inputManager.deleteMapping(key);
        inputManager.addMapping(key, triggers);
        inputManager.addListener(this, key);
    }

    /**
     * Remaps an action.
     * @param hotKey the hotkey to remap.
     */
    public final void remapAction(HotKey hotKey) {
        Trigger[] triggers = new Trigger[hotKey.triggers.size()];
        triggers = hotKey.triggers.toArray(triggers);
        remapAction(hotKey.name, triggers);
    }

    /**
     * Adds an action to the input.
     * @param key the mapping to add to.
     * @param triggers the triggers of the action.
     */
    public final void registerAction(String key, Trigger... triggers) {
        boolean hasExisted = true;
        if (!actions.contains(key)) {
            actions.add(key);
            hasExisted = false;
        }
        // Adds the action to the input manager only if the action has not been added to it yet.
        if (!ActionCounter.isActionUsed(inputManager, key)) {
            inputManager.addMapping(key, triggers);
            inputManager.addListener(this, key);
        }
        // Only add to the counter if the action has not been added yet.
        if (!hasExisted) {
            ActionCounter.addAction(inputManager, key);
        }
    }

    /**
     * Registers a hot key.
     * @param hotKey the hot key to register.
     */
    public final void registerAction(HotKey hotKey) {
        Trigger[] triggers = new Trigger[hotKey.triggers.size()];
        triggers = hotKey.triggers.toArray(triggers);
        registerAction(hotKey.name, triggers);
    }

    /**
     * Action to remove.
     * @param key the mapping to remove.
     */
    public final void removeAction(String key) {
        if (!actions.contains(key)) {
            return;
        }
        ActionCounter.removeAction(inputManager, key);
        if (!ActionCounter.isActionUsed(inputManager, key)) {
            inputManager.deleteMapping(key);
        }
        actions.remove(key);
    }

    @Override
    public final void onAction(String name, boolean isPressed, float tpf) {
        if (isActivated) {
            onActionInput(name, isPressed, tpf);
        }
    }

    /**
     * Actions to do for specific movement.
     * @param name the name of the key.
     * @param isPressed the state of the key.
     * @param tpf the time passed since the last frame.
     */
    protected abstract void onActionInput(String name, boolean isPressed, float tpf);

    @Override
    public final void onAnalog(String name, float value, float tpf) {
        if (isActivated) {
            onAnalogInput(name, value, tpf);
        }
    }

    /**
     * Analog movement to do for specific movement.
     * @param name the name of the key.
     * @param value the analog value.
     * @param tpf the time passed since the last frame.
     */
    protected abstract void onAnalogInput(String name, float value, float tpf);

    /**
     * Updates the movement if needed but only when active.
     * @param tpf the time passed since the last frame.
     */
    public final void update(float tpf) {
        if (isActivated) {
            onUpdate(tpf);
        }
    }

    /**
     * Updates the input movement if needed.
     * @param tpf the time passed since the last frame.
     */
    protected abstract void onUpdate(float tpf);

    /**
     * Activates the movement.
     */
    public final void activate() {
        isActivated = true;
        onActivated();
    }

    /**
     * Action specific activation procedures.
     */
    protected abstract void onActivated();

    /**
     * Deactivates the movement.
     */
    public final void deactivate() {
        isActivated = false;
        onDeactivated();
    }

    /**
     * Action specific deactivation procedures.
     */
    protected abstract void onDeactivated();

    /**
     * Removes the inputs.
     */
    public final void cleanup() {
        // Remove the actions and remove from the input manager only if they are no longer being used.
        for (String action : actions) {
            ActionCounter.removeAction(inputManager, action);
            if (!ActionCounter.isActionUsed(inputManager, action)) {
                inputManager.deleteMapping(action);
            }
        }
        inputManager.removeListener(this);
        actions.clear();

        cleanupAction();
    }

    /**
     * Cleans up action specific items.
     */
    protected abstract void cleanupAction();

    /**
     * @return the input manager.
     */
    public final InputManager getInputManager() {
        return inputManager;
    }

    /**
     * @return true if is activated, false otherwise.
     */
    public final boolean isActivated() {
        return isActivated;
    }
}
