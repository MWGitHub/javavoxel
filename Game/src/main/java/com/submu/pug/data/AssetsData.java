package com.submu.pug.data;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/11/13
 * Time: 9:38 PM
 * Holds data for assets.
 */
public class AssetsData implements Savable {
    /**
     * Assets used for the map.
     */
    public String[] assets = {};

    @Override
    public void write(JmeExporter jmeExporter) throws IOException {
        OutputCapsule capsule = jmeExporter.getCapsule(this);
        capsule.write(assets, "assets", new String[]{});
    }

    @Override
    public void read(JmeImporter jmeImporter) throws IOException {
        InputCapsule capsule = jmeImporter.getCapsule(this);
        assets = capsule.readStringArray("assets", new String[]{});
    }
}
