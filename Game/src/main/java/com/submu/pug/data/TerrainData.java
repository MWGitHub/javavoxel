package com.submu.pug.data;

import com.halboom.pgt.terrainsystem.TileBank;
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
 * Time: 4:18 PM
 */
public class TerrainData implements Savable {
    /**
     * Width of the tile textures.
     */
    public int textureWidth = 64;

    /**
     * Height of the tile textures.
     */
    public int textureHeight = 64;

    /**
     * Shading parameters to use on the terrain.
     */
    public Shading shading = new Shading();

    /**
     * Data for the tiles.
     */
    public TileTypes tileTypes = new TileTypes();

    /**
     * Shading class to determine if the tiles should have shadows.
     */
    public static class Shading {
        /**
         * True to enable shadows, false ignores all parameters in here.
         */
        public Boolean isEnabled;

        /**
         * Shininess of the tiles.
         */
        public float shininess;
    }

    /**
     * Types of tiles and the resources used for them.
     */
    public static class TileTypes {
        /**
         * Tile sheet to use for the textures.
         */
        public String tileSheet;

        /**
         * Tile properties.
         */
        public TileBank.Tile[] properties;
    }

    @Override
    public void write(JmeExporter jmeExporter) throws IOException {
        OutputCapsule capsule = jmeExporter.getCapsule(this);
        capsule.write(textureWidth, "textureWidth", 64);
        capsule.write(textureHeight, "textureHeight", 64);
        capsule.write(shading.isEnabled, "shadingIsEnabled", true);
        capsule.write(shading.shininess, "shadingShininess", 0.1f);
        capsule.write(tileTypes.tileSheet, "tileTypeTileSheet", "");
    }

    @Override
    public void read(JmeImporter jmeImporter) throws IOException {
        InputCapsule capsule = jmeImporter.getCapsule(this);
        textureWidth = capsule.readInt("textureWidth", 64);
        textureHeight = capsule.readInt("textureHeight", 64);
        shading = new Shading();
        shading.isEnabled = capsule.readBoolean("shadingIsEnabled", true);
        shading.shininess = capsule.readFloat("shadingShininess", 0.1f);
        tileTypes = new TileTypes();
        tileTypes.tileSheet = capsule.readString("tileTypeTileSheet", null);
    }
}
