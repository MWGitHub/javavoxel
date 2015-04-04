package com.submu.pug.game.gui.widgets;

import com.google.common.base.Strings;
import com.halboom.pgt.pgui.Color;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.GUIKeyListener;
import com.halboom.pgt.pgui.GUIObject;
import com.halboom.pgt.pgui.widgets.Button;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.jme3.texture.Texture;
import com.submu.pug.game.gui.WidgetFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/9/13
 * Time: 11:37 AM
 * General button used in the game for the upgrade menu.
 */
public class GameButtonWidget extends GUIObject {
    /**
     * Width and height of the button.
     */
    private static final float WIDTH = 135.0f, HEIGHT = 38.0f;

    /**
     * Icon width and height.
     */
    private static final float ICON_WIDTH = 29.0f, ICON_HEIGHT = 29.0f;

    /**
     * Alpha to set.
     */
    private static final float FADED_ALPHA = 0.5f;

    /**
     * Colors of the button.
     */
    public static final ColorRGBA IDLE_COLOR = ColorRGBA.White,
                                  HOVER_COLOR = Color.colorToRGBA(134, 186, 255, 255),
                                  CLICK_COLOR = Color.colorToRGBA(249, 178, 95, 255),
                                  SELECTED_COLOR = Color.colorToRGBA(70, 119, 185, 255),
                                  FADED_COLOR = Color.colorToRGBA(161, 160, 160, 255),

                                  ICON_COLOR = ColorRGBA.Black,
                                  SELECTED_ICON_COLOR = ColorRGBA.White,
                                  FADED_ICON_COLOR = Color.colorToRGBA(0, 0, 0, (int) (255 * FADED_ALPHA)),
                                  LEVEL_COLOR = ColorRGBA.White,
                                  SELECTED_LEVEL_COLOR = ColorRGBA.Black;



    /**
     * Asset manager to load assets from.
     */
    private AssetManager assetManager;

    /**
     * Actual button of the widget.
     */
    private Button button;

    /**
     * Icon of the widget.
     */
    private Geometry icon;

    /**
     * Label for the upgrade level.
     */
    private BitmapText levelLabel;

    /**
     * Label of the button.
     */
    private BitmapText label;

    /**
     * When active the button will use the active icon.
     */
    private boolean isActive = false;

    /**
     * Initializes the widget.
     * @param assetManager the asset manager to load assets from.
     * @param guiKeyListener the gui key listener to activate the buttons.
     */
    public GameButtonWidget(AssetManager assetManager, GUIKeyListener guiKeyListener) {
        this.assetManager = assetManager;

        button = new Button(assetManager, WIDTH, HEIGHT);
        button.setGUIKeyListener(guiKeyListener);
        button.setImage("Core/Textures/Game GUI/Upgrades/Upgrade Bar.png");
        button.setIdleColor(ColorRGBA.White);
        button.setClickColor(CLICK_COLOR);
        button.setHoverColor(HOVER_COLOR);
        attach(button);

        Quad iconQuad = new Quad(ICON_WIDTH, ICON_HEIGHT);
        icon = new Geometry("Icon", iconQuad);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Core/Textures/Game GUI/Upgrades/Upgrade Icon.png"));
        material.setColor("Color", ICON_COLOR);
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        icon.setMaterial(material);
        icon.setLocalTranslation(-WIDTH / 2.0f + 5.0f, -ICON_HEIGHT / 2.0f - 1.0f, 0.0f);
        addToNode(icon);

        levelLabel = WidgetFactory.createLabel();
        levelLabel.setBox(new Rectangle(0, 0, 29.0f, 29.0f));
        levelLabel.setColor(LEVEL_COLOR);
        levelLabel.setAlignment(BitmapFont.Align.Center);
        levelLabel.setVerticalAlignment(BitmapFont.VAlign.Center);
        levelLabel.setLocalTranslation(-WIDTH / 2.0f + 5.0f, levelLabel.getHeight() / 2.0f, 1.0f);
        levelLabel.setSize(18.0f);
        addToNode(levelLabel);

        label = WidgetFactory.createLabel();
        label.setBox(new Rectangle(0, 0, 95.0f, 38.0f));
        label.setColor(ColorRGBA.Black);
        label.setAlignment(BitmapFont.Align.Left);
        label.setVerticalAlignment(BitmapFont.VAlign.Center);
        label.setLocalTranslation(-WIDTH / 2.0f + 40.0f, label.getHeight() / 2.0f, 1.0f);
        label.setSize(22.0f);
        addToNode(label);
    }

    /**
     * Resets the widget to default colors and textures.
     */
    private void resetColors() {
        button.setIdleColor(IDLE_COLOR);
        button.setClickColor(CLICK_COLOR);
        button.setHoverColor(HOVER_COLOR);

        icon.getMaterial().setColor("Color", ICON_COLOR);
        label.setAlpha(1.0f);
        label.setColor(ICON_COLOR);

        levelLabel.setAlpha(1.0f);
        levelLabel.setColor(LEVEL_COLOR);
    }

    /**
     * Set to true to make the button display active mode colors.
     * @param isActive true to set the button to active mode.
     */
    public void setActive(boolean isActive) {
        if (this.isActive != isActive) {
            resetColors();
        }
        this.isActive = isActive;
        if (isActive) {
            button.setIdleColor(SELECTED_COLOR);
            icon.getMaterial().setColor("Color", SELECTED_ICON_COLOR);
            label.setColor(ColorRGBA.White);
            levelLabel.setColor(SELECTED_LEVEL_COLOR);
        }
    }

    /**
     * Set to true to make the button faded.
     * @param isFaded true to set the button to faded mode.
     */
    public void setFaded(boolean isFaded) {
        // Faded buttons cannot be active.
        if (isFaded && !isActive) {
            resetColors();
            // Set the button to be faded.
            ColorRGBA color = new ColorRGBA(FADED_COLOR);
            color.a *= FADED_ALPHA;
            button.setIdleColor(color);
            color = new ColorRGBA(HOVER_COLOR);
            color.a *= FADED_ALPHA;
            button.setHoverColor(color);

            // Set the icon and text to be faded.
            icon.getMaterial().setColor("Color", FADED_ICON_COLOR);
            levelLabel.setAlpha(FADED_ALPHA);
            label.setAlpha(FADED_ALPHA);
        }
    }

    @Override
    public float getExtentX() {
        return WIDTH / 2;
    }

    @Override
    public float getExtentY() {
        return HEIGHT / 2;
    }

    /**
     * @param label the label to set for the button.
     */
    public void setLabel(String label) {
        this.label.setText(label);
    }

    /**
     * @param name the name to set for the button.
     */
    public void setButtonName(String name) {
        button.setName(name);
    }

    /**
     * @return the name of the button.
     */
    public String getButtonName() {
        return button.getName();
    }

    /**
     * @param icon the icon image to set.
     */
    public void setIcon(String icon) {
        if (!Strings.isNullOrEmpty(icon)) {
            this.icon.getMaterial().setTexture("ColorMap", assetManager.loadTexture(icon));
        }
    }

    /**
     * @param level the level to set.
     */
    public void setLevel(String level) {
        if (level != null) {
            levelLabel.setText(level);
        }
    }

    /**
     * Set the user data for the button.
     * @param key the key of the data.
     * @param data the data to set.
     */
    public void setUserData(String key, Object data) {
        button.setUserData(key, data);
    }

    /**
     * Get the button user data.
     * @param key the key of the data.
     * @return the data or null if none found.
     */
    public Object getUserData(String key) {
        return button.getUserData(key);
    }

    /**
     * Simulates a release on the button.
     */
    public void trigger() {
        button.release(true);
    }

    /**
     * @param callback the mouse release callback on the button to set.
     */
    public void addReleaseCallback(Button.Callback callback) {
        button.addReleaseCallback(callback);
    }
}
