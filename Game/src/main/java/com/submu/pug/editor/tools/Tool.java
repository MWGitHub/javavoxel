package com.submu.pug.editor.tools;

import com.halboom.pgt.pgutil.math.Vector3Int;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/16/13
 * Time: 3:42 PM
 * Interface for tools
 */
public interface Tool {
    /**
     * Runs when the tool is selected.
     */
    void onSelected();

    /**
     * Runs when the tool is just deselected.
     */
    void onDeselected();

    /**
     * Tool action when a add block command is given.
     * @param selectedIndex the index of the selected tile.
     * @param selectedType the tile type to selection.
     * @param isBatch true to signify that the block is one of a batch of blocks.
     */
    void addBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch);

    /**
     * Tool action when a remove block command is given.
     * @param selectedIndex the index of the selected tile.
     * @param selectedType the tile type selection.
     * @param isBatch true to signify that the block is on of a batch of blocks.
     */
    void removeBlock(Vector3Int selectedIndex, byte selectedType, boolean isBatch);
}
