package com.submu.pug.resources;

import com.google.common.base.Charsets;
import com.jme3.asset.AssetInfo;
import com.jme3.asset.AssetLoader;

import java.io.IOException;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/6/13
 * Time: 1:40 PM
 * Loads data in text format.
 */
public class TextLoader implements AssetLoader {
    @Override
    public Object load(AssetInfo assetInfo) throws IOException {
        Scanner scan = new Scanner(assetInfo.openStream(), Charsets.UTF_8.name());
        StringBuilder output = new StringBuilder();

        // Convert the file to a string.
        try {
            while (scan.hasNextLine()) {
                output.append(scan.nextLine()).append("\n");
            }
        } finally {
            scan.close();
        }

        return output.toString();
    }
}
