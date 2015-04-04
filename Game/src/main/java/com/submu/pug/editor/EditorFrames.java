package com.submu.pug.editor;

import com.halboom.pgt.asseteditor.AssetImporter;
import com.submu.pug.data.Data;
import com.submu.pug.data.PathNames;
import com.submu.pug.editor.menu.EditorMainMenu;
import com.submu.pug.resources.map.ExtractedMapFile;

import javax.swing.JFrame;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/3/13
 * Time: 3:57 PM
 * Manages the creation, showing, hiding, and closing for editor frames.
 */
public class EditorFrames {
    /**
     * Main frame of the editor.
     */
    private JFrame frame;

    /**
     * Main menu for the editor.
     */
    private EditorMainMenu mainMenu;

    /**
     * Imports and deletes assets.
     */
    private AssetImporter assetImporter;

    /**
     * Initializes the frames.
     * @param frame the main frame of the editor.
     */
    public EditorFrames(JFrame frame) {
        this.frame = frame;

        // Create the menu.
        mainMenu = new EditorMainMenu(frame);
        mainMenu.setAssetEditorCallback(new EditorCallbacks() {
            @Override
            public void onAction() {
                assetImporter.show();
            }
        });

        // Create the assets editor.
        assetImporter = new AssetImporter(Data.getInstance().getResourcePaths().getPath(PathNames.MAP_DIRECTORY),
                ExtractedMapFile.DIRECTORY_ASSETS);
    }

    /**
     * Shows the editor frames.
     */
    public void show() {
        mainMenu.show();
    }

    /**
     * Hides the editor frames.
     */
    public void hide() {
        mainMenu.hide();
    }

    /**
     * @return the main menu of the editor.
     */
    public EditorMainMenu getMainMenu() {
        return mainMenu;
    }

    /**
     * @return the asset importer.
     */
    public AssetImporter getAssetImporter() {
        return assetImporter;
    }
}
