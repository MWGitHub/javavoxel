package com.halboom.pgt.pgui.widgets;

import com.halboom.pgt.pgui.Color;
import com.halboom.pgt.pgui.GUIKeyListener;
import com.halboom.pgt.pgui.GUIObject;
import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/18/13
 * Time: 3:26 PM
 * The button class can be used in both 3D and 2D view.
 * Giving an input manager to the button will have the button manage its own state.
 * Without an input manager, the user will have to simulate the button presses with
 * the leftClick, release, and hover methods. Helper methods have been given to make the
 * simulated presses easier to check.
 */
public class Button extends GUIObject {
    /**
     * Default colors for the button.
     */
    private static final ColorRGBA DEFAULT_COLOR = ColorRGBA.Red,
                                   DEFAULT_HOVER = new ColorRGBA(510 * Color.MAX_COLOR,
                                           510 * Color.MAX_COLOR,
                                           510 * Color.MAX_COLOR,
                                           255 / Color.MAX_COLOR),
                                   DEFAULT_CLICK = ColorRGBA.Gray;

    /**
     * Asset manager to use for loading images.
     */
    private AssetManager assetManager;

    /**
     * Listens to GUI key events and allows retrieval of mouse positions.
     */
    private GUIKeyListener guiKeyListener;

    /**
     * Display of the button.
     */
    private Geometry display;

    /**
     * Dimensions of the button.
     */
    private float width = 0, height = 0;

    /**
     * Callbacks for the various button actions.
     */
    private List<Callback> clickCallbacks = new LinkedList<Callback>(),
                           releaseCallbacks = new LinkedList<Callback>(),
                           hoverCallbacks = new LinkedList<Callback>(),
                           unHoverCallbacks = new LinkedList<Callback>();

    /**
     * Colors of each button state.
     */
    private ColorRGBA colorIdle = DEFAULT_COLOR,
                      colorHover = DEFAULT_HOVER,
                      colorClick = DEFAULT_CLICK;


    /**
     * Flag to check if the button was hovering last frame.
     */
    private boolean wasHovering = false;

    /**
     * Flag to check if the button is being held down.
     */
    private boolean isClicked = false;

    /**
     * Name of the button; useful for finding out which button is pressed or
     * sharing buttons without having to reference a specific button.
     */
    private String name;

    /**
     * Custom data for the button.
     */
    private Map<String, Object> userData = new HashMap<String, Object>();


    /**
     * Initializes the bar with a parent.
     * @param assetManager asset manager to use.
     * @param width the width of the bar.
     * @param height the height of the bar.
     */
    public Button(AssetManager assetManager, float width, float height) {
        this.assetManager = assetManager;
        this.width = width;
        this.height = height;
        Quad quad = new Quad(width, height);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", colorIdle);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        display = new Geometry("Bar", quad);
        display.setMaterial(material);
        addToNode(display);

        // Keep the quad centered with the node's center.
        display.setLocalTranslation(-width / 2, -height / 2, 0.0f);
    }

    /**
     * Checks if the mouse is over the button.
     * @param cursorPosition the position of the cursor.
     * @return true if the mouse is over the button, false otherwise.
     */
    public boolean isMouseOver(Vector2f cursorPosition) {
        Vector3f center = getPosition();
        float minX = center.x - width / 2f;
        float maxX = center.x + width / 2f;
        float minY = center.y - height / 2f;
        float maxY = center.y + height / 2f;
        if (cursorPosition.x >= minX && cursorPosition.x <= maxX
                && cursorPosition.y >= minY && cursorPosition.y <= maxY) {
            return true;
        }
        return false;
    }

    /**
     * Runs the leftClick callback and actions.
     */
    public void click() {
        isClicked = true;
        setColor(colorClick);
        for (Callback callback : clickCallbacks) {
            callback.onAction(this);
        }
    }

    /**
     * Runs the release callback and actions.
     * @param isOnButton true to set that the mouse was released on the button.
     */
    public void release(boolean isOnButton) {
        setColor(colorIdle);
        if (isOnButton && isClicked) {
            for (Callback callback : releaseCallbacks) {
                callback.onAction(this);
            }
        }
        isClicked = false;
    }

    /**
     * Runs the hovering display and callback for hovering over the button.
     */
    public void hover() {
        // Update the color as long as the button isn't being clicked.
        if (!isClicked) {
            setColor(colorHover);
        }
        if (wasHovering || isClicked) {
            return;
        }
        wasHovering = true;
        for (Callback callback : hoverCallbacks) {
            callback.onAction(this);
        }
    }

    /**
     * Runs when the button is recently un-hovered.
     */
    public void unHover() {
        // Update the color as long as the button isn't being clicked.
        if (!isClicked) {
            setColor(colorIdle);
        }
        if (!wasHovering || isClicked) {
            return;
        }
        wasHovering = false;
        for (Callback callback : unHoverCallbacks) {
            callback.onAction(this);
        }
    }

    @Override
    public void update(float tpf) {
        super.update(tpf);

        // Only handle the state if there is an input manager.
        if (guiKeyListener != null) {
            Vector2f position = guiKeyListener.getCursorPosition();
            boolean isHovering = isMouseOver(position);
            if (guiKeyListener.isMouseReleased()) {
                release(isHovering);
            }
            if (isMouseOver(position)) {
                hover();
                if (guiKeyListener.isMouseClicked()) {
                    click();
                }
            } else {
                unHover();
            }
        }
    }

    /**
     * @return the current color of the button.
     */
    private ColorRGBA getColor() {
        return (ColorRGBA) display.getMaterial().getParam("Color").getValue();
    }

    /**
     * @param color the color of the button to set.
     */
    private void setColor(ColorRGBA color) {
        display.getMaterial().setColor("Color", color);
    }

    /**
     * Set the idle color of the button overlay.
     * @param color the idle color to set.
     */
    public void setIdleColor(ColorRGBA color) {
        colorIdle = color;
    }

    /**
     * Set the hover color of the button overlay.
     * @param color the hover color to set.
     */
    public void setHoverColor(ColorRGBA color) {
        colorHover = color;
    }

    /**
     * Set the click color of the button overlay.
     * @param color the click color to set.
     */
    public void setClickColor(ColorRGBA color) {
        this.colorClick = color;
    }

    /**
     * @param path the path to set the image from.
     */
    public void setImage(String path) {
        if (path != null) {
            display.getMaterial().setTexture("ColorMap", assetManager.loadTexture(path));
        }
    }

    /**
     * Sets the key listener to use for automatically updating the button.
     * The automatic updates assume 2D view.
     * @param guiKeyListener the input manager to use.
     */
    public void setGUIKeyListener(GUIKeyListener guiKeyListener) {
        this.guiKeyListener = guiKeyListener;
    }

    /**
     * Removes the input manager and prevents the button from being automatically updated.
     */
    public void removeGUIKeyListener() {
        guiKeyListener = null;
    }

    /**
     * @param callback the leftClick callback to add.
     */
    public void addClickCallback(Callback callback) {
        clickCallbacks.add(callback);
    }

    /**
     * @param callback the release callback to add.
     */
    public void addReleaseCallback(Callback callback) {
        releaseCallbacks.add(callback);
    }

    /**
     * @param callback the hover callback to add.
     */
    public void addHoverCallback(Callback callback) {
        hoverCallbacks.add(callback);
    }

    /**
     * @param callback the un-hover callback to add.
     */
    public void addUnHoverCallback(Callback callback) {
        unHoverCallbacks.add(callback);
    }

    /**
     * @return the name of the button.
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name of the button to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set user data for the button.
     * @param key the key to store with.
     * @param data the data to store.
     */
    public void setUserData(String key, Object data) {
        userData.put(key, data);
    }

    /**
     * Retrieves user data given the key.
     * @param key the key of the data to retrieve.
     * @return the data or null if none found.
     */
    public Object getUserData(String key) {
        return userData.get(key);
    }

    /**
     * Callback for the button.
     */
    public static interface Callback {
        /**
         * Runs when a button action has been triggered.
         * @param button the button that the action was on.
         */
        void onAction(Button button);
    }
}
