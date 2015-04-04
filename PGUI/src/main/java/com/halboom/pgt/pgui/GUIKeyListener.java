package com.halboom.pgt.pgui;

import com.halboom.pgt.input.InputActions;
import com.jme3.input.InputManager;
import com.jme3.math.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/18/13
 * Time: 4:01 PM
 * Holds key inputs.
 * Make sure actions that read from the listener are done before the update.
 */
public class GUIKeyListener extends InputActions {
    /**
     * Mouse left click click status.
     */
    private boolean isMouseClicked = false;

    /**
     * Mouse left click release status.
     */
    private boolean isMouseReleased = false;

    /**
     * List of keys that were pressed last frame.
     */
    private List<String> pressedKeys = new ArrayList<String>();

    /**
     * List of keys that were released last frame.
     */
    private List<String> releasedKeys = new ArrayList<String>();

    /**
     * Initializes the listener.
     * @param inputManager the input manager to listen to.
     */
    public GUIKeyListener(InputManager inputManager) {
        super(inputManager);

        registerAction(GUIKeys.closeWindow);
        registerAction(GUIKeys.leftClick);
    }

    /**
     * @return true if the mouse is just clicked.
     */
    public boolean isMouseClicked() {
        return isMouseClicked;
    }

    /**
     * @return true if the mouse is just released.
     */
    public boolean isMouseReleased() {
        return isMouseReleased;
    }

    /**
     * @return the position of the cursor.
     */
    public Vector2f getCursorPosition() {
        return getInputManager().getCursorPosition();
    }

    /**
     * Retrieves if a key is pressed.
     * @param name the name of the key.
     * @return true if the key is pressed.
     */
    public boolean isKeyPressed(String name) {
        return pressedKeys.contains(name);
    }

    /**
     * Retrieves if a key is released.
     * @param name the name of the key.
     * @return true if the key is released.
     */
    public boolean isKeyReleased(String name) {
        return releasedKeys.contains(name);
    }

    @Override
    protected void onActionInput(String name, boolean isPressed, float tpf) {
        if (name.equals(GUIKeys.leftClick.name)) {
            if (isPressed) {
                isMouseClicked = true;
            } else {
                isMouseReleased = true;
            }
        }
        if (isPressed) {
            pressedKeys.add(name);
        }
    }

    @Override
    protected void onAnalogInput(String name, float value, float tpf) {
    }

    @Override
    protected void onUpdate(float tpf) {
        isMouseClicked = false;
        isMouseReleased = false;
        pressedKeys.clear();
        releasedKeys.clear();
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
}
