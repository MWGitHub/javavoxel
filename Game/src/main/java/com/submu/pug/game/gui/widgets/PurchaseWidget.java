package com.submu.pug.game.gui.widgets;

import com.halboom.pgt.pgui.Color;
import com.halboom.pgt.pgui.Frame;
import com.halboom.pgt.pgui.GUIKeyListener;
import com.halboom.pgt.pgui.widgets.Button;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Quad;
import com.submu.pug.game.gui.WidgetFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/9/13
 * Time: 4:41 PM
 * Tooltip for purchasing an ability or upgrade.
 */
public class PurchaseWidget extends Frame {
    /**
     * Colors for the purchase button.
     */
    private static final ColorRGBA IDLE_COLOR = Color.colorToRGBA(50, 196, 110, 255),
                                   HOVER_COLOR = Color.colorToRGBA(102, 245, 161, 255),
                                   DISABLED_COLOR = ColorRGBA.Gray,
                                   PURCHASED_COLOR = Color.colorToRGBA(150, 150, 150, 255);

    /**
     * Dimensions of the background.
     */
    private static final float BACKGROUND_WIDTH = 185.0f, BACKGROUND_HEIGHT = 165.0f;

    /**
     * Background of the widget.
     */
    private Geometry background;

    /**
     * Name of the item.
     */
    private BitmapText title;

    /**
     * Description of the item.
     */
    private BitmapText description;

    /**
     * Level of the item.
     */
    private BitmapText level;

    /**
     * Cost of the item.
     */
    private BitmapText cost;

    /**
     * Purchase button.
     */
    private Button purchaseButton;

    /**
     * Text for the purchase button.
     */
    private BitmapText purchaseLabel;

    /**
     * Text to use when the upgrade can be purchased.
     */
    private String purchasableText;

    /**
     * Text to use when the upgrade is already purchased.
     */
    private String alreadyPurchasedText;

    /**
     * Initializes the purchase tooltip.
     * @param assetManager the asset manager to load resources from.
     * @param guiKeyListener the key listener for the buy button.
     */
    public PurchaseWidget(AssetManager assetManager, GUIKeyListener guiKeyListener) {
        setWidth(BACKGROUND_WIDTH);
        setHeight(BACKGROUND_HEIGHT);

        // Create the background.
        Quad backgroundQuad = new Quad(BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        background = new Geometry("Icon", backgroundQuad);
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Core/Textures/Game GUI/Upgrades/Upgrade Tooltip.png"));
        background.setMaterial(material);
        background.setLocalTranslation(0, -BACKGROUND_HEIGHT, 0);
        addToNode(background);

        // Create the labels.
        title = WidgetFactory.createLabel();
        setTextProperties(title);
        title.setBox(new Rectangle(0, 0, 185.0f, 26.0f));
        title.setLocalTranslation(0, 0, 0);
        title.setSize(24.0f);
        title.setText("Title");
        addToNode(title);

        description = WidgetFactory.createLabel();
        setTextProperties(description);
        description.setBox(new Rectangle(0, 0, 185.0f, 102.0f));
        description.setLocalTranslation(0, -29.0f, 0);
        description.setText("Description");
        addToNode(description);

        level = WidgetFactory.createLabel();
        setTextProperties(level);
        level.setBox(new Rectangle(0, 0, 85.0f, 17.0f));
        level.setAlignment(BitmapFont.Align.Left);
        level.setLocalTranslation(5.0f, -131.0f, 0);
        addToNode(level);

        cost = WidgetFactory.createLabel();
        setTextProperties(cost);
        cost.setBox(new Rectangle(0, 0, 85.0f, 17.0f));
        cost.setAlignment(BitmapFont.Align.Right);
        cost.setLocalTranslation(96.0f, -131.0f, 0);
        cost.setText("9999999 SP");
        addToNode(cost);

        purchaseButton = new Button(assetManager, 185.0f, 17.0f);
        purchaseButton.setPosition(new Vector3f(92.5f, -156.5f, 0));
        purchaseButton.setIdleColor(IDLE_COLOR);
        purchaseButton.setHoverColor(HOVER_COLOR);
        purchaseButton.setClickColor(GameButtonWidget.CLICK_COLOR);
        purchaseButton.setGUIKeyListener(guiKeyListener);
        attach(purchaseButton);

        // Create the button label.
        purchaseLabel = WidgetFactory.createLabel();
        setTextProperties(purchaseLabel);
        purchaseLabel.setBox(new Rectangle(0, 0, 185.0f, 17.0f));
        purchaseLabel.setAlignment(BitmapFont.Align.Center);
        purchaseLabel.setLocalTranslation(0, -148.0f, 0);
        purchaseLabel.setText(purchasableText);
        addToNode(purchaseLabel);
    }

    /**
     * Sets the standard properties for the text.
     * @param bitmapText the text to set the properties of.
     */
    private void setTextProperties(BitmapText bitmapText) {
        bitmapText.setSize(20.0f);
        bitmapText.setColor(ColorRGBA.Black);
        bitmapText.setAlignment(BitmapFont.Align.Center);
        bitmapText.setVerticalAlignment(BitmapFont.VAlign.Center);
    }

    /**
     * Sets if the item is purchased.
     * @param isPurchased true to set as purchased.
     */
    public void setPurchased(boolean isPurchased) {
        if (isPurchased) {
            purchaseLabel.setText(alreadyPurchasedText);
            purchaseButton.setIdleColor(PURCHASED_COLOR);
            purchaseButton.setHoverColor(PURCHASED_COLOR);
            purchaseButton.setClickColor(PURCHASED_COLOR);
        } else {
            purchaseLabel.setText(purchasableText);
            purchaseButton.setIdleColor(IDLE_COLOR);
            purchaseButton.setHoverColor(HOVER_COLOR);
            purchaseButton.setClickColor(GameButtonWidget.CLICK_COLOR);
        }
    }

    /**
     * @param title the title of the purchase widget to set.
     */
    public void setTitle(String title) {
        this.title.setText(title);
    }

    /**
     * @param description the description of the item.
     */
    public void setDescription(String description) {
        this.description.setText(description);
    }

    /**
     * @param level the level to set.
     */
    public void setLevel(String level) {
        this.level.setText(level);
    }

    /**
     * @param cost the cost to set.
     */
    public void setCost(String cost) {
        this.cost.setText(cost);
    }

    /**
     * @param purchasableText the purchasable label on the button.
     */
    public void setPurchasableText(String purchasableText) {
        this.purchasableText = purchasableText;
        purchaseLabel.setText(purchasableText);
    }

    /**
     * @param alreadyPurchasedText the purchased label on the button.
     */
    public void setAlreadyPurchasedText(String alreadyPurchasedText) {
        this.alreadyPurchasedText = alreadyPurchasedText;
        purchaseLabel.setText(alreadyPurchasedText);
    }

    /**
     * Set the callback when the button is pressed.
     * @param callback the callback to set.
     */
    public void setPurchaseCallback(Button.Callback callback) {
        purchaseButton.addReleaseCallback(callback);
    }

    /**
     * Sets the button to be purchasable.
     * @param isPurchasable set to true if the item can be purchased.
     */
    public void setPurchasable(boolean isPurchasable) {
        if (isPurchasable) {
            purchaseButton.setIdleColor(IDLE_COLOR);
            purchaseButton.setHoverColor(HOVER_COLOR);
            purchaseButton.setClickColor(GameButtonWidget.CLICK_COLOR);
        } else {
            purchaseButton.setIdleColor(DISABLED_COLOR);
            purchaseButton.setHoverColor(DISABLED_COLOR);
            purchaseButton.setClickColor(DISABLED_COLOR);
        }
    }
}
