package com.submu.pug.game.gui;

import com.halboom.pgt.pgui.widgets.Bar;
import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.font.Rectangle;
import com.jme3.math.Vector3f;
import com.submu.pug.data.GUIElementsData;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/6/13
 * Time: 1:04 PM
 * Creates specific elements given the data.
 */
public class WidgetFactory {
    /**
     * Creates a bar given the gui data.
     * @param assetManager the asset manager to use for loading resources.
     * @param barProperties the data of the bar.
     * @return the created bar.
     */
    public static Bar createBar(AssetManager assetManager, GUIElementsData.ElementLocation barProperties) {
        Bar bar = new Bar(assetManager, barProperties.width, barProperties.height);
        bar.setColor(barProperties.color.red, barProperties.color.green, barProperties.color.blue, barProperties.color.alpha);
        bar.setImage(barProperties.image);
        return bar;
    }

    /**
     * Creates a label.
     * @return the label to create.
     */
    public static BitmapText createLabel() {
        BitmapFont font = Fonts.mainFont;
        BitmapText text = new BitmapText(font, false);
        text.setBox(new Rectangle(0, 0, 1.0f, 1.0f));
        text.setSize(font.getCharSet().getRenderedSize());
        text.setAlignment(BitmapFont.Align.Left);

        return text;
    }
}
