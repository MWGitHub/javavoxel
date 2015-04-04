package com.halboom.pgt.terrainsystem;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.texture.Texture;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/4/13
 * Time: 11:43 AM
 * An atlas for tile textures.
 * TODO: Use own atlas, jMonkey leaks memory.
 */
public class TileAtlas {
    /**
     * Texture position in the atlas.
     */
    public static class TexturePosition {
        /**
         * Texture position coordinates.
         */
        public float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

        /**
         * Initializes the positions.
         */
        public TexturePosition() {
            x1 = 0;
            x2 = 0;
            y1 = 0;
            y2 = 0;
        }

        @Override
        public String toString() {
            return "TexturePosition {"
                    + "x1=" + x1
                    + ", x2=" + x2
                    + ", y1=" + y1
                    + ", y2=" + y2
                    + '}';
        }
    }

    /**
     * Default dimensions for the texture width and height.
     */
    private static final int DEFAULT_DIMENIONS = 64;

    /**
     * Default shininess.
     */
    private static final float DEFAULT_SHININESS = 0.1f;

    /**
     * Default texture name.
     */
    private static final String DEFAULT_TILE_SHEET = "com.halboom.pgt.terrainsystem.assets/Tiles.png";

    /**
     * Asset manager to load assets from.
     */
    private AssetManager assetManager;

    /**
     * Dimensions of the atlas image.
     */
    private int atlasDimensions;

    /**
     * Width of each tile in the atlas.
     */
    private int textureWidth = DEFAULT_DIMENIONS;

    /**
     * Height of each tile in the atlas.
     */
    private int textureHeight = DEFAULT_DIMENIONS;

    /**
     * Material to use for the quads.
     */
    private Material material;

    /**
     * Tile sheet to use for retrieving textures.
     */
    private String tileSheet;

    /**
     * Data of the tiles to retrieve texture faces from.
     */
    private TileBank tileBank;

    /**
     * True if shading is enabled.
     */
    private boolean isShadingEnabled = true;

    /**
     * Shininess of the textures.
     */
    private float shininess = DEFAULT_SHININESS;

    /**
     * Texture used for the tiles.
     */
    private Texture texture;

    /**
     * Initializes the tile atlas.
     * @param assetManager the asset manager to load resources from.
     * @param tileBank the types of tiles to retrieve the texture faces from.
     * @param tileSheet the texture tile sheet used for the tiles.
     */
    public TileAtlas(AssetManager assetManager, TileBank tileBank, String tileSheet) {
        this.assetManager = assetManager;
        this.tileBank = tileBank;
        this.tileSheet = tileSheet;

        // Create the atlas.
        TextureKey textureKey;
        if (tileSheet == null) {
            textureKey = new TextureKey(DEFAULT_TILE_SHEET, true);
            texture = assetManager.loadTexture(DEFAULT_TILE_SHEET);
        } else {
            textureKey = new TextureKey(tileSheet, true);
            texture = assetManager.loadTexture(tileSheet);
        }
        textureKey.setAnisotropy(16);
        textureKey.setGenerateMips(true);
        texture = assetManager.loadTexture(textureKey);
        atlasDimensions = texture.getImage().getWidth();

        // Create lighted materials when enabled.
        // TODO: Allow material to be more customizable globally.
        setShadingEnabled(isShadingEnabled);
    }

    /**
     * @param isShadingEnabled true to enable shading of the tiles.
     */
    public void setShadingEnabled(boolean isShadingEnabled) {
        if (isShadingEnabled) {
            material = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
            material.setTexture("DiffuseMap", texture);
            material.setBoolean("UseMaterialColors", true);
            material.setColor("Ambient", ColorRGBA.White);
            material.setColor("Diffuse", ColorRGBA.White);
            material.setColor("Specular", ColorRGBA.Gray);
            material.setReceivesShadows(true);
            setShininess(shininess);
        } else {
            material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            material.setTexture("ColorMap", texture);
        }
        material.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        this.isShadingEnabled = isShadingEnabled;
    }

    /**
     * @return true if shading is enabled.
     */
    public boolean isShadingEnabled() {
        return isShadingEnabled;
    }

    /**
     * @param shininess the shininess to set.
     */
    public void setShininess(float shininess) {
        if (isShadingEnabled) {
            material.setFloat("Shininess", shininess);
        }
    }

    /**
     * @return the shininess of the material.
     */
    public float getShininess() {
        return shininess;
    }

    /**
     * @return the tile sheet used.
     */
    public String getTileSheet() {
        return tileSheet;
    }

    /**
     * @param tileSheet the tile sheet to set.
     */
    public void setTileSheet(String tileSheet) {
        this.tileSheet = tileSheet;
        texture = assetManager.loadTexture(tileSheet);
        setShadingEnabled(isShadingEnabled);
    }

    /**
     * @param textureWidth the texture width of each image to set.
     */
    public void setTextureWidth(int textureWidth) {
        this.textureWidth = textureWidth;
    }

    /**
     * @param textureHeight the texture height of each image to set.
     */
    public void setTextureHeight(int textureHeight) {
        this.textureHeight = textureHeight;
    }

    /**
     * @param minFilter the minification filter to set.
     */
    public void setMinFilter(Texture.MinFilter minFilter) {
        texture.setMinFilter(minFilter);
    }

    /**
     * @param magFilter the magnification filter to set.
     */
    public void setMagFilter(Texture.MagFilter magFilter) {
        texture.setMagFilter(magFilter);
    }

    /**
     * @param level the anisotropic filter level to set.
     */
    public void setAnisotropicFilter(int level) {
        texture.setAnisotropicFilter(level);
    }

    /**
     * @return the material used for the atlas.
     */
    public Material getMaterial() {
        return material;
    }

    /**
     * Retrieves the texture coordinates from the given index.
     * TODO: Make sure this works with height greater than the first row.
     * @param index the index of the texture.
     * @param direction the direction of the cube.
     * @param store the vector to store the position in.
     * @return the vector with the position.
     */
    public TexturePosition getTextureFromIndex(int index, int direction, TexturePosition store) {
        int[] tile = tileBank.getTile(index).type;
        int textureIndex = tile[direction];
        float texturesPerWidth = (float) atlasDimensions / (float) textureWidth;
        float texturesPerHeight = (float) atlasDimensions / (float) textureHeight;
        float unitX = 1.0f / texturesPerWidth;
        float unitY = 1.0f / texturesPerHeight;
        store.x1 = textureIndex * unitX;
        store.x2 = (textureIndex + 1) * unitX;
        store.y1 = 0;
        store.y2 = unitY;

        return store;
    }
}
