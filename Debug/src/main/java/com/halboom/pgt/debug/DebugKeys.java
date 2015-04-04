package com.halboom.pgt.debug;

import com.halboom.pgt.input.HotKey;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.KeyTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/30/13
 * Time: 4:32 PM
 * Hot keys for the debugging actions.
 */
public class DebugKeys {
    /**
     * Toggles the wireframe on or off; will reset current wireframe toggles on models.
     */
    public static HotKey toggleWireframe = new HotKey("Debug Toggle Wireframe", new KeyTrigger(KeyInput.KEY_T));
    /**
     * Toggles the wireframe grid on or off.
     */
    public static HotKey toggleGrid = new HotKey("Debug Toggle Grid", new KeyTrigger(KeyInput.KEY_Y));
    /**
     * Toggles the stats on or off.
     */
    public static HotKey toggleStats = new HotKey("Debug Toggle Stats", new KeyTrigger(KeyInput.KEY_G));
    /**
     * Toggles the FPS counter on or off.
     */
    public static HotKey toggleFPS = new HotKey("Debug Toggle FPS", new KeyTrigger(KeyInput.KEY_F));
    /**
     * Toggles the coordinate axis on or off.
     */
    public static HotKey toggleAxis = new HotKey("Debug Toggle Axis", new KeyTrigger(KeyInput.KEY_U));
}
