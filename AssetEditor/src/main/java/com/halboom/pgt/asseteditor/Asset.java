package com.halboom.pgt.asseteditor;

import org.apache.commons.io.FileUtils;

import java.io.File;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 4/5/13
 * Time: 4:50 PM
 * Represents an asset.
 */
public class Asset {
    /**
     * Path of the asset.
     */
    private String path;

    /**
     * Size of the asset.
     */
    private long size;

    /**
     * Type of asset.
     */
    private String type;

    /**
     * Creates an asset.
     * @param file the file to retrieve the data from.
     * @param path the path of the asset.
     */
    public Asset(File file, String path) {
        this.path = path;
        size = file.length() / FileUtils.ONE_KB;

        // Get the extension and parse the type.
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        type = extension;
    }

    /**
     * @return the path of the asset.
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path the path to set the asset as.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * @return the size of the asset.
     */
    public long getSize() {
        return size;
    }

    /**
     * @return the type of asset.
     */
    public String getType() {
        return type;
    }
}
