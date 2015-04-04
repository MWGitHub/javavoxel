package com.halboom.pgt.asseteditor;

import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/5/13
 * Time: 1:57 PM
 */
public abstract class AssetEditorCallbacks {
    /**
     * Callback function for menu items that do not require parameters.
     */
    public void onAction() {

    }

    /**
     * Callback function for mouse events.
     * @param event the mouse event.
     */
    public void onMouseAction(MouseEvent event) {

    }
}
