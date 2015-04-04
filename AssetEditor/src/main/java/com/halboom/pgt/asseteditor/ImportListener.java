package com.halboom.pgt.asseteditor;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/9/13
 * Time: 2:23 PM
 * Listener class for listening to asset changes.
 */
public interface ImportListener {
    /**
     * Runs when an asset has been edited.
     * @param asset the asset that has been edited.
     */
    void onAssetEdited(Asset asset);

    /**
     * Runs when an asset has been deleted.
     * @param asset the asset that has been deleted.
     */
    void onAssetDeleted(Asset asset);

    /**
     * Runs when an asset has been imported.
     * @param asset the asset that has been imported.
     */
    void onAssetImported(Asset asset);
}
