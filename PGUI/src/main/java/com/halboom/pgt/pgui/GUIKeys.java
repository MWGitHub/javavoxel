package com.halboom.pgt.pgui;

import com.halboom.pgt.input.HotKey;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/5/13
 * Time: 4:01 PM
 * Keys used for GUI controls.
 */
public class GUIKeys {
    /**
     * Hot key for closing a window.
     */
    public static HotKey closeWindow = new HotKey("GUI Close Window", new KeyTrigger(KeyInput.KEY_ESCAPE));

    /**
     * Hot key for clicking on objects.
     */
    public static HotKey leftClick = new HotKey("GUI Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
}
