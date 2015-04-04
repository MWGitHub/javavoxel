package com.halboom.pgt.asseteditor;

import net.tomahawk.XFileDialog;
import org.apache.commons.io.FileUtils;

import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.WindowConstants;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/3/13
 * Time: 4:09 PM
 * Loads, removes, and copies assets to temporary directories.
 * Keeps a list of loaded assets that can be used.
 * TODO: Have a progress bar for copying files.
 * TODO: Have the table columns be sortable.
 * TODO: Have the table columns be scaled properly.
 */
public class AssetImporter {
    /**
     * Logger for the class.
     */
    private static Logger logger = Logger.getLogger(AssetImporter.class.getName());

    /**
     * Path to copy files to.
     */
    private String tempPath;

    /**
     * Root of the assets.
     */
    private String assetRoot;

    /**
     * Frame of the editor.
     */
    private JFrame frame;

    /**
     * Main panel of the editor.
     */
    private AssetsPanel panel;

    /**
     * Menu that pops up when right clicked.
     */
    private JPopupMenu popupMenu;

    /**
     * Dialog to edit assets with.
     */
    private EditAssetDialog editAssetDialog;

    /**
     * Assets in the editor.
     */
    private Map<String, Asset> assets = new HashMap<String, Asset>();

    /**
     * Current asset that is being edited.
     */
    private Asset editingAsset;

    /**
     * Import listeners.
     */
    private List<ImportListener> listeners = new ArrayList<ImportListener>();

    /**
     * Initializes the asset editor.
     * @param tempPath the path to copy files to.
     * @param assetRoot the root of the assets in the temp path.
     */
    public AssetImporter(String tempPath, String assetRoot) {
        // Set the default directory if none provided.
        if (tempPath == null) {
            this.tempPath = System.getenv("APPDATA") + "/AssetEditor/";
        } else {
            this.tempPath = tempPath;
        }
        // Add an ending slash if none is provided.
        if (this.tempPath.charAt(this.tempPath.length() - 1) != '/') {
            this.tempPath += "/";
        }
        this.assetRoot = assetRoot;
        if (this.assetRoot.charAt(this.assetRoot.length() - 1) != '/') {
            this.assetRoot += "/";
        }

        // Create the frame.
        frame = new JFrame("Assets Editor");
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        frame.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                hidePopupMenu();
            }
        });

        // Create the panels.
        panel = new AssetsPanel();
        // Create the callbacks for the menu,
        panel.setMousePressCallback(new AssetEditorCallbacks() {
            @Override
            public void onMouseAction(MouseEvent event) {
                frame.toFront();
                frame.setState(Frame.NORMAL);
                hidePopupMenu();
                onClick();
            }
        });
        panel.setRightReleaseCallback(new AssetEditorCallbacks() {
            @Override
            public void onMouseAction(MouseEvent event) {
                frame.toFront();
                frame.setState(Frame.NORMAL);
                hidePopupMenu();
                onRightClick(event);
            }
        });
        panel.setDoubleClickItemCallback(new AssetEditorCallbacks() {
            @Override
            public void onMouseAction(MouseEvent event) {
                hidePopupMenu();
                onDoubleClickItem();
            }
        });
        frame.add(panel);

        createPopupMenu();

        // Create the editor assets.
        editAssetDialog = new EditAssetDialog();
        editAssetDialog.setModal(true);
        editAssetDialog.setCommitCallback(new AssetEditorCallbacks() {
            @Override
            public void onAction() {
                onEditCommit();
            }
        });


        frame.pack();
    }

    /**
     * Creates the popup menu.
     */
    private void createPopupMenu() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        popupMenu = new JPopupMenu();
        JMenuItem item = new JMenuItem("Import Files...");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hidePopupMenu();
                onImportFilesClicked();
            }
        });
        popupMenu.add(item);

        item = new JMenuItem("Import Directories...");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hidePopupMenu();
                onImportDirectoriesClicked();
            }
        });
        popupMenu.add(item);
        popupMenu.addSeparator();

        item = new JMenuItem("Edit Asset");
        popupMenu.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hidePopupMenu();
                onEditAssetClicked();
            }
        });
        popupMenu.addSeparator();

        item = new JMenuItem("Remove Asset");
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hidePopupMenu();
                onRemoveAssetClicked();
            }
        });
        popupMenu.add(item);
    }

    /**
     * Shows the editor.
     */
    public void show() {
        frame.setVisible(true);
    }

    /**
     * Hides the editor.
     */
    public void hide() {
        frame.setVisible(false);
    }

    /**
     * Runs on right click within the editor.
     * @param event the event for the mouse.
     */
    private void onRightClick(MouseEvent event) {
        popupMenu.setLocation(event.getXOnScreen(), event.getYOnScreen());
        popupMenu.setVisible(true);
    }

    /**
     * Runs on mouse click within the editor.
     */
    private void onClick() {
        popupMenu.setVisible(false);
    }

    /**
     * Copies a file to the destination.
     * @param filePath the file to copy.
     * @param destination the destination to copy to.
     * @return true if copying was successful.
     */
    private boolean copyFile(String filePath, String destination) {
        File fromFile = new File(filePath);
        File toFile = new File(destination);

        try {
            FileUtils.copyFile(fromFile, toFile);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error importing " + filePath + " to " + destination + ".", e);
            return false;
        }

        return true;
    }

    /**
     * Adds a file without copying to the temporary directory.
     * @param path the path to the file relative to the temporary path.
     */
    public void addFile(String path) {
        if (!assets.containsKey(path)) {
            File file = new File(tempPath + path);
            Asset asset = new Asset(file, path);
            assets.put(asset.getPath(), asset);
            panel.addFile(asset.getPath(), asset.getType(), asset.getSize());
        }
    }

    /**
     * Adds a single file to the importer.
     * @param fromPath the file to import and copy.
     * @param key the key to store the file as.
     * @return true if successful.
     */
    private boolean addFile(String fromPath, String key) {
        // Create an asset pointing to the temporary file.
        if (!assets.containsKey(key)) {
            // Copy the file to the temporary directory.
            boolean fileCopied = copyFile(fromPath, tempPath + key);
            if (fileCopied) {
                File copiedFile = new File(tempPath + key);
                Asset asset = new Asset(copiedFile, key);
                assets.put(asset.getPath(), asset);
                panel.addFile(asset.getPath(), asset.getType(), asset.getSize());
                for (ImportListener listener : listeners) {
                    listener.onAssetImported(asset);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "The file " + key + " could not be imported.");
                return false;
            }
        } else {
            JOptionPane.showMessageDialog(frame, "The file " + key + " already exists.");
            return false;
        }

        return true;
    }

    /**
     * Adds the files to the imported list.
     * @param files the files to add.
     * @param root the directory the files reside in.
     */
    private void addFiles(String[] files, String root) {
        for (String fileName : files) {
            // Create an asset pointing to the temporary file.
            addFile(root + fileName, assetRoot + fileName);
        }
    }

    /**
     * Add the directories to the imported list.
     * @param directories the directories to add.
     */
    private void addDirectories(String[] directories) {
        for (String directoryPath : directories) {
            // Get the end directory.
            String agnosticPath = directoryPath.replace("\\", "/");
            String root = "" + agnosticPath.substring(agnosticPath.lastIndexOf('/') + 1) + "/";
            File directory = new File(directoryPath);
            for (File file : FileUtils.listFiles(directory, null, true)) {
                // Create the key to store in the temporary path.
                String path = file.getAbsolutePath().replace("\\", "/");
                // Get the directory of the file without the root.
                String filePathDirectory = path.substring(path.lastIndexOf(root));
                // Create the key to store the file as.
                String key = assetRoot + filePathDirectory;
                // Create an asset pointing to the temporary file.
                addFile(file.getAbsolutePath(), key);
            }
        }
    }

    /**
     * Runs when import asset is clicked.
     */
    private void onImportFilesClicked() {
        // Create the dialog and show the user the default format for the map.
        XFileDialog dialog = new XFileDialog(frame);
        dialog.setTitle("Import Files");

        // Open the file and get the path.
        String[] fileNames = dialog.getFiles();
        String directory = dialog.getDirectory();

        dialog.dispose();

        if (fileNames != null) {
            addFiles(fileNames, directory);
        }
    }

    /**
     * Runs when the import directories button is clicked.
     */
    private void onImportDirectoriesClicked() {
        // Create the dialog and show the user the default format for the map.
        XFileDialog dialog = new XFileDialog(frame);
        dialog.setTitle("Import Directories");

        // Open the file and get the path.
        String[] directories = dialog.getFolders();

        dialog.dispose();

        if (directories != null) {
            addDirectories(directories);
        }
    }

    /**
     * Runs when the edit asset button is clicked.
     */
    private void onEditAssetClicked() {
        String[] selectedPaths = panel.getSelectedAssets();
        if (selectedPaths != null) {
            // Only use the first selected path.
            String path = selectedPaths[0];
            Asset asset = assets.get(path);
            editingAsset = asset;

            editAssetDialog.setPath(asset.getPath());
            editAssetDialog.setSize(asset.getSize());
            editAssetDialog.setType(asset.getType());
            editAssetDialog.setVisible(true);
        }
    }

    /**
     * Deletes an imported file.
     * @param path the path of the imported file.
     * @return true if the delete is successful.
     */
    private boolean deleteImport(String path) {
        File file = new File(path);
        try {
            FileUtils.forceDelete(file);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "The file " + path + " could not be deleted.", e);
        }

        return true;
    }

    /**
     * Runs when an asset is removed.
     */
    private void onRemoveAssetClicked() {
        String[] selectedPaths = panel.getSelectedAssets();
        if (selectedPaths != null) {
            for (String path : selectedPaths) {
                boolean isDeleted = deleteImport(tempPath + path);
                if (isDeleted) {
                    panel.removeAsset(path);
                    for (ImportListener listener : listeners) {
                        listener.onAssetDeleted(assets.get(path));
                    }
                    assets.remove(path);
                } else {
                    JOptionPane.showMessageDialog(frame, "The file " + path
                            + " could not be deleted.");
                }
            }
        }
    }

    /**
     * Runs when an asset has been edited and committed.
     */
    private void onEditCommit() {
        // Only edit assets that are moved.
        if (!editingAsset.getPath().equals(editAssetDialog.getPath())) {
            String oldPath = editingAsset.getPath();
            String newPath = editAssetDialog.getPath();
            // Copy then delete the old file to move.
            boolean success = copyFile(tempPath + oldPath, tempPath + newPath);
            if (success) {
                success = deleteImport(tempPath + oldPath);
            }
            if (success) {
                panel.updateAsset(oldPath, newPath);
                editingAsset.setPath(newPath);
                assets.remove(oldPath);
                assets.put(newPath, editingAsset);
                for (ImportListener listener : listeners) {
                    listener.onAssetEdited(editingAsset);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "The file " + oldPath + " could not be moved to "
                        + newPath + ".");
                logger.warning("The file " + oldPath + " could not be moved to " + newPath + ".");
            }
        }

        editAssetDialog.setVisible(false);
    }

    /**
     * Runs when an asset is double clicked.
     */
    private void onDoubleClickItem() {
        onEditAssetClicked();
    }

    /**
     * Hides the popup menu.
     */
    private void hidePopupMenu() {
        popupMenu.setVisible(false);
    }

    /**
     * Sets the close operation of the frame.
     * @param operation the operation to set.
     */
    public void setCloseOperation(int operation) {
        frame.setDefaultCloseOperation(operation);
        // Delete the temporary directory when the asset editor is closed.
        if (operation == JFrame.EXIT_ON_CLOSE) {
            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    deleteAllImports();
                }
            });
        }
    }

    /**
     * Deletes all the imports.
     * @return true if successful, false otherwise.
     */
    public boolean deleteAllImports() {
        try {
            FileUtils.deleteDirectory(new File(tempPath));
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Unable to delete the import directory.", e);
            return false;
        }

        return true;
    }

    /**
     * Adds an import listener.
     * @param importListener the import listener to add.
     */
    public void addImportListener(ImportListener importListener) {
        listeners.add(importListener);
    }

    /**
     * Removes an import listener.
     * @param importListener the import listener to remove.
     */
    public void removeImportListener(ImportListener importListener) {
        listeners.remove(importListener);
    }

    /**
     * Retrieves assets matching the given extension.
     * @param extensions the extensions that are valid.
     * @return the assets matching the extensions.
     */
    public List<Asset> getAssetsWithExtension(String... extensions) {
        List<Asset> matchingAssets = new ArrayList<Asset>();
        for (Map.Entry<String, Asset> entry : assets.entrySet()) {
            for (String extension : extensions) {
                if (entry.getKey().contains(extension)) {
                    matchingAssets.add(entry.getValue());
                }
            }
        }
        return matchingAssets;
    }

    /**
     * @return all the imported assets.
     */
    public List<Asset> getAllAssets() {
        List<Asset> assetList = new ArrayList<Asset>();
        for (Asset asset : assets.values()) {
            assetList.add(asset);
        }

        return assetList;
    }

    /**
     * Destroys the asset importer but leaves the temporary import folder.
     */
    public void destroy() {
        listeners.clear();
    }
}
