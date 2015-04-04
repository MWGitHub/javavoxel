package com.submu.pug.play;

import com.halboom.pgt.input.InputActions;
import com.jme3.input.InputManager;
import com.submu.pug.data.KeyMap;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/28/13
 * Time: 2:38 PM
 * Actions for the play state.
 */
public class PlayActions extends InputActions {
    /**
     * Callback function to run when quitting.
     */
    private PlayerActionCallback quitCallback;

    /**
     * Initializes the actions.
     * @param inputManager the input manager to use.
     */
    public PlayActions(InputManager inputManager) {
        super(inputManager);

        registerAction(KeyMap.gameQuit);
    }

    @Override
    protected void onActionInput(String name, boolean isPressed, float tpf) {
        if (isPressed) {
            if (name.equals(KeyMap.gameQuit.name) && quitCallback != null) {
                quitCallback.execute();
            }
        }
    }

    @Override
    protected void onAnalogInput(String name, float value, float tpf) {
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

    /**
     * @param quitCallback the callback to execute when the gameQuit action is pressed.
     */
    public void setQuitCallback(PlayerActionCallback quitCallback) {
        this.quitCallback = quitCallback;
    }

    /**
     * Callback interface for play state actions.
     */
    public interface PlayerActionCallback {
        /**
         * Executes the callback function.
         */
        void execute();
    }
}
