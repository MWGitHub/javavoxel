package com.submu.pug.editor;

import java.awt.event.MouseEvent;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/18/13
 * Time: 2:24 PM
 */
public abstract class EditorCallbacks {
    /**
     * Callback function for menu items that do not require parameters.
     */
    public void onAction() {

    }

    /**
     * Callback function for items that require a path.
     * @param path the path of the file.
     */
    public void onFileSelected(String path) {

    }

    /**
     * Callback function for mouse events.
     * @param event the mouse event.
     */
    public void onMouseAction(MouseEvent event) {

    }
}
