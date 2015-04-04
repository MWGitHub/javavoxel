package com.submu.pug.data;

import com.halboom.pgt.input.HotKey;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/19/13
 * Time: 12:36 PM
 * Holds the hot keys for all the inputs in the game.
 */
public class KeyMap {
    /**
     * Quits the game when in play state.
     */
    public static HotKey gameQuit = new HotKey("Game Quit", new KeyTrigger(KeyInput.KEY_ESCAPE));

    /**
     * Direction movement keys.
     */
    public static HotKey moveForward = new HotKey("Move Forward", new KeyTrigger(KeyInput.KEY_W), new KeyTrigger(KeyInput.KEY_UP)),
            moveBackward = new HotKey("Move Backward", new KeyTrigger(KeyInput.KEY_S), new KeyTrigger(KeyInput.KEY_DOWN)),
            moveLeft = new HotKey("Move Left", new KeyTrigger(KeyInput.KEY_A), new KeyTrigger(KeyInput.KEY_LEFT)),
            moveRight = new HotKey("Move Right", new KeyTrigger(KeyInput.KEY_D), new KeyTrigger(KeyInput.KEY_RIGHT)),
            moveUp = new HotKey("Move Up", new KeyTrigger(KeyInput.KEY_Q)),
            moveDown = new HotKey("Move Down", new KeyTrigger(KeyInput.KEY_Z));

    /**
     * Look action key.
     */
    public static HotKey look = new HotKey("Look",
            new MouseAxisTrigger(MouseInput.AXIS_X, true), new MouseAxisTrigger(MouseInput.AXIS_X, false),
            new MouseAxisTrigger(MouseInput.AXIS_Y, true), new MouseAxisTrigger(MouseInput.AXIS_Y, false));

    /**
     * Hot key for the jump button.
     */
    public static HotKey jump = new HotKey("Jump", new KeyTrigger(KeyInput.KEY_SPACE));

    /**
     * Use key.
     */
    public static HotKey use = new HotKey("Use", new KeyTrigger(KeyInput.KEY_E));

    /**
     * Ability keys.
     */
    public static HotKey gameAbility1 = new HotKey("Game Ability 1", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)),
                         gameAbility2 = new HotKey("Game Ability 2", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT)),
                         gameAbility3 = new HotKey("Game Ability 3", new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE));

    /********************************************************
     * Keys for editor controls.
     ********************************************************/

    /**
     * Cancels any current editor actions that are cancelable.
     */
    public static HotKey editorCancel = new HotKey("Editor Cancel", new KeyTrigger(KeyInput.KEY_ESCAPE));

    /**
     * Remove block action string.
     */
    public static HotKey editorRemoveBlock = new HotKey("Editor Remove Block", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
    /**
     * Add block action string.
     */
    public static HotKey editorAddBlock = new HotKey("Editor Add Block", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    /**
     * Increments the block ID.
     */
    public static HotKey editorIncrementBlock = new HotKey("Editor Increment Block", new KeyTrigger(KeyInput.KEY_E));
    /**
     * Decrements the block ID.
     */
    public static HotKey editorDecrementBlock = new HotKey("Editor Decrement Block", new KeyTrigger(KeyInput.KEY_C));

    /**
     * Tool selection.
     */
    public static HotKey editorToolNormal = new HotKey("Editor Normal Tool", new KeyTrigger(KeyInput.KEY_1)),
                         editorToolDrill = new HotKey("Editor Drill Tool", new KeyTrigger(KeyInput.KEY_2)),
                         editorToolSelect = new HotKey("Editor Select Tool", new KeyTrigger(KeyInput.KEY_3));
}
