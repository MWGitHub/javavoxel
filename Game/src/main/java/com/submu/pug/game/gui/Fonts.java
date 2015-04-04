package com.submu.pug.game.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/12/13
 * Time: 5:57 PM
 * Fonts used for the GUI and other parts of the game.
 */
public class Fonts {
    /**
     * Main font to use.
     */
    public static BitmapFont mainFont;

    /**
     * Loads the fonts for the GUI.
     * @param assetManager the asset manager to use.
     */
    public static void loadFonts(AssetManager assetManager) {
        mainFont = assetManager.loadFont("Core/Interface/BebasNeue.fnt");
    }
}
