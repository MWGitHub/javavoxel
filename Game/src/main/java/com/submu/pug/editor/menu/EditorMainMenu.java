package com.submu.pug.editor.menu;

import com.google.common.base.Strings;
import com.submu.pug.data.Data;
import com.submu.pug.editor.EditorCallbacks;
import net.tomahawk.ExtensionsFilter;
import net.tomahawk.XFileDialog;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/13/13
 * Time: 12:45 PM
 * Main menu for the editor.
 */
public class EditorMainMenu {
    /**
     * Frame to create menus in.
     */
    private JFrame frame;

    /**
     * Menu bar to create menus in.
     */
    private JMenuBar menuBar;

    /**
     * Callback for creating a new map.
     */
    private EditorCallbacks newMapCallback;

    /**
     * File browser callback to run when the two save items are clicked.
     */
    private EditorCallbacks saveFileCallback;

    /**
     * File browser callback to run when load is clicked.
     */
    private EditorCallbacks loadFileCallback;

    /**
     * Callback to run when testing map button is clicked.
     */
    private EditorCallbacks testMapCallback;

    /**
     * Callback to run when the asset editor is clicked.
     */
    private EditorCallbacks assetEditorCallback;

    /**
     * Current path of the file.
     */
    private String currentFilePath;

    /**
     * Initializes the menu.
     * @param frame the frame to create the menu on.
     */
    public EditorMainMenu(JFrame frame) {
        this.frame = frame;

        // Add elements to the frame.
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        createFileMenu();
        createEditMenu();
        createPlacersMenu();
        createEditorsMenu();
        createHelpMenu();

        // Remove XFileDialog debug
        XFileDialog.setTraceLevel(0);
    }

    /**
     * Creates the file menu.
     */
    private void createFileMenu() {
        // Create the file dropdown.
        JMenu file = new JMenu("File");

        // Create map laoding items.
        JMenuItem newMap = new JMenuItem("New Map...");
        newMap.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentFilePath = null;
                newMapCallback.onAction();
            }
        });
        file.add(newMap);

        JMenuItem open = new JMenuItem("Open Map...");
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLoadClicked();
            }
        });
        file.add(open);

        JMenuItem close = new JMenuItem("Close Map");
        file.add(close);
        file.addSeparator();

        // Create map saving items.
        JMenuItem save = new JMenuItem("Save");
        save.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveClicked();
            }
        });
        file.add(save);

        JMenuItem saveAs = new JMenuItem("Save As...");
        saveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onSaveAsClicked();
            }
        });
        file.add(saveAs);
        file.addSeparator();

        JMenuItem test = new JMenuItem("Test Map");
        test.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testMapCallback.onAction();
            }
        });
        file.add(test);
        file.addSeparator();

        // Exit item.
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        file.add(exit);

        menuBar.add(file);
    }

    /**
     * Creates the edit menu.
     */
    private void createEditMenu() {
        JMenu editMenu = new JMenu("Edit");
        // Create the undo/redo items.
        JMenuItem undo = new JMenuItem("Undo");
        editMenu.add(undo);
        JMenuItem redo = new JMenuItem("Redo");
        editMenu.add(redo);
        editMenu.addSeparator();
        // Create the copy/paste items
        JMenuItem cut = new JMenuItem("Cut");
        editMenu.add(cut);
        JMenuItem copy = new JMenuItem("Copy");
        editMenu.add(copy);
        JMenuItem paste = new JMenuItem("Paste");
        editMenu.add(paste);

        menuBar.add(editMenu);
    }

    /**
     * Creates the placers menu.
     */
    private void createPlacersMenu() {
        JMenu placersMenu = new JMenu("Placers");
        // Create the kits.
        JMenuItem tileKit = new JMenuItem("Tiles");
        placersMenu.add(tileKit);
        JMenuItem terrainKit = new JMenuItem("Terrain");
        placersMenu.add(terrainKit);
        JMenuItem actorKit = new JMenuItem("Actors");
        placersMenu.add(actorKit);
        JMenuItem decorationKit = new JMenuItem("Decorations");
        placersMenu.add(decorationKit);
        JMenuItem regionKit = new JMenuItem("Regions");
        placersMenu.add(regionKit);

        menuBar.add(placersMenu);
    }

    /**
     * Creates the editors menu.
     */
    private void createEditorsMenu() {
        JMenu editorsMenu = new JMenu("Editors");
        // Create the editors
        JMenuItem map = new JMenuItem("Map Settings");
        editorsMenu.add(map);
        editorsMenu.addSeparator();

        JMenuItem tiles = new JMenuItem("Tiles");
        editorsMenu.add(tiles);
        JMenuItem objects = new JMenuItem("Objects");
        editorsMenu.add(objects);
        JMenuItem assets = new JMenuItem("Assets");
        assets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                assetEditorCallback.onAction();
            }
        });
        editorsMenu.add(assets);


        menuBar.add(editorsMenu);
    }

    /**
     * Creates the help menu.
     */
    private void createHelpMenu() {
        JMenu helpMenu = new JMenu("Help");
        // Create the items.
        JMenuItem help = new JMenuItem("Editor Help");
        helpMenu.add(help);
        JMenuItem about = new JMenuItem("About");
        helpMenu.add(about);

        menuBar.add(helpMenu);
    }

    /**
     * Runs when the save as button is clicked or when no file is saved.
     */
    private void onSaveAsClicked() {
        if (saveFileCallback != null) {
            // Create the dialog and show the user a default format for the map.
            XFileDialog dialog = new XFileDialog(frame);
            dialog.setTitle("Save Map As");
            ArrayList<String> ext = new ArrayList<String>();
            String mapExtension = Data.getInstance().getConfigData().assets.mapExtension;
            ext.add(mapExtension);
            ExtensionsFilter filter = new ExtensionsFilter("PG Map File", ext);
            dialog.addFilters(filter);

            // Open the file and get the path.
            String fileName = dialog.getSaveFile();
            String path = dialog.getDirectory() + fileName;
            if (Strings.isNullOrEmpty(fileName)) {
                path = null;
            }

            dialog.dispose();

            // Check if a file was chosen and append the map extension if not already there.
            if (path != null && !path.endsWith(mapExtension)) {
                path = path + "." + mapExtension;
            }
            if (path != null) {
                currentFilePath = path;
            }
            saveFileCallback.onFileSelected(path);
        } else {
            throw new NoSuchMethodError("No saveFileCallback set.");
        }
    }

    /**
     * Runs when the save button is clicked and creates a save file dialog.
     */
    private void onSaveClicked() {
        if (currentFilePath != null) {
            if (saveFileCallback != null) {
                saveFileCallback.onFileSelected(currentFilePath);
            } else {
                throw new NoSuchMethodError("No saveFileCallback set.");
            }
        } else {
            onSaveAsClicked();
        }
    }

    /**
     * Runs when the load button is clicked.
     */
    private void onLoadClicked() {
        // Create the dialog and show the user the default format for the map.
        XFileDialog dialog = new XFileDialog(frame);
        dialog.setTitle("Open Map");
        ArrayList<String> ext = new ArrayList<String>();
        ext.add(Data.getInstance().getConfigData().assets.mapExtension);
        ExtensionsFilter filter = new ExtensionsFilter("PG Map File", ext);
        dialog.addFilters(filter);

        // Open the file and get the path.
        String fileName = dialog.getFile();
        String path = dialog.getDirectory() + fileName;
        if (Strings.isNullOrEmpty(fileName)) {
            path = null;
        }

        dialog.dispose();

        if (path != null) {
            currentFilePath = path;
        }
        loadFileCallback.onFileSelected(path);
    }

    /**
     * Hides the menu.
     */
    public void hide() {
        menuBar.setVisible(false);
    }

    /**
     * Shows the menu.
     */
    public void show() {
        menuBar.setVisible(true);
    }

    /**
     * @param callback the callback to set for creating a new map.
     */
    public void setNewMapCallback(EditorCallbacks callback) {
        newMapCallback = callback;
    }

    /**
     * @param callback the callback to set for saving a file.
     */
    public void setSaveFileCallback(EditorCallbacks callback) {
        saveFileCallback = callback;
    }

    /**
     * @param callback the callback to set for loading a file.
     */
    public void setLoadFileCallback(EditorCallbacks callback) {
        loadFileCallback = callback;
    }

    /**
     * @param testMapCallback the callback to set for testing a map.
     */
    public void setTestMapCallback(EditorCallbacks testMapCallback) {
        this.testMapCallback = testMapCallback;
    }

    /**
     * @param assetEditorCallback the callback to set for opening the assets editor.
     */
    public void setAssetEditorCallback(EditorCallbacks assetEditorCallback) {
        this.assetEditorCallback = assetEditorCallback;
    }

    /**
     * Sets the path so that the current path will be saved there without a dialog.
     * Normally it is automatically set but if an error occurs in saving or loading the user
     * can set it manually.
     * @param path the path to set.
     */
    public void setCurrentFilePath(String path) {
        currentFilePath = path;
    }
}
