/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/
package com.submu.pug.processors;

import com.jme3.asset.AssetManager;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import java.io.IOException;

/**
 * A filter to render the GLSL implementation of a fog effect.
 * @author t0neg0d
 */
public class CustomFogFilter extends Filter {
    /**
     * Modes the fog can be set as.
     */
    public static enum FOG_MODE {
        /**
         * Linear fog blending.
         */
        LINEAR,
        /**
         * Fog blending to a certain distance.
         */
        EXP2_CAM_TO_DISTANCE,
        /**
         * Fog blending to infinity.
         */
        EXP2_DISTANCE_TO_INFINITY
    }

    /**
     * Color of the fog.
     */
    private ColorRGBA fogColor = ColorRGBA.White.clone();
    /**
     * Density of the fog.
     */
    private float fogDensity = 1.0f;
    /**
     * Default fog starting distance.
     */
    private static final float DEFAULT_FOG_START_DISTANCE = 200f;
    /**
     * Fog starting distance.
     */
    private float fogStartDistance = DEFAULT_FOG_START_DISTANCE;
    /**
     * Default fog end distance.
     */
    private static final float DEFAULT_FOG_END_DISTANCE = 500f;
    /**
     * Fog end distance.
     */
    private float fogEndDistance = DEFAULT_FOG_END_DISTANCE;
    /**
     * True to exclude the sky from fog effects.
     */
    private boolean excludeSky = false;
    /**
     * Fog mode to use.
     */
    private FOG_MODE fogMode = FOG_MODE.EXP2_CAM_TO_DISTANCE;

    /**
     * Creates a CustomFogFilter.
     */
    public CustomFogFilter() {
        super("CustomFogFilter");
    }

    /**
     * Create a fog filter.
     * @param fogMode the mode to use for rendering fog (default is EXP2_CAM_TO_DISTANCE)
     * @param fogColor the color of the fog (default is white)
     * @param fogDensity the density of the fog (default is 1.0)
     * @param fogStartDistance Start distance is the absolute distance of the fog in mode EXP2_CAM_TO_DISTANCE
     *                         and the start distance in modes LINEAR and EXP2_DISTANCE_TO_INFINITY (default is 200).
     * @param fogEndDistance End distance (100% density) in mode LINEAR
     */
    public CustomFogFilter(FOG_MODE fogMode, ColorRGBA fogColor, float fogDensity, float fogStartDistance, float fogEndDistance) {
        this();
        this.fogMode = fogMode;
        this.fogColor = fogColor;
        this.fogDensity = fogDensity;
        this.fogStartDistance = fogStartDistance;
        this.fogEndDistance = fogEndDistance;
    }

    @Override
    protected boolean isRequiresDepthTexture() {
        return true;
    }

    @Override
    protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
        material = new Material(manager, "Core/MatDefs/CustomFog.j3md");
        material.setInt("FogMode", fogMode.ordinal());
        material.setColor("FogColor", fogColor);
        material.setFloat("FogDensity", fogDensity);
        material.setFloat("FogStartDistance", fogStartDistance);
        material.setFloat("FogEndDistance", fogEndDistance);
        material.setBoolean("ExcludeSky", excludeSky);
    }

    @Override
    protected Material getMaterial() {

        return material;
    }

    /**
     * Sets the fog mode (default is EXP2_CAM_TO_DISTANCE).
     * @param fogMode the mode to set the fog.
     */
    public void setFogMode(FOG_MODE fogMode) {
        if (material != null) {
            material.setInt("FogMode", fogMode.ordinal());
        }
        this.fogMode = fogMode;
    }

    /**
     * @return fogMode the fog mode (default is EXP2_CAM_TO_DISTANCE).
     */
    public FOG_MODE getFogMode() {
        return this.fogMode;
    }

    /**
     * @return fogColor the fog color.
     */
    public ColorRGBA getFogColor() {
        return fogColor;
    }

    /**
     * Sets the color of the fog.
     * @param fogColor the color of the fog.
     */
    public void setFogColor(ColorRGBA fogColor) {
        if (material != null) {
            material.setColor("FogColor", fogColor);
        }
        this.fogColor = fogColor;
    }

    /**
     * @return the fog density.
     */
    public float getFogDensity() {
        return fogDensity;
    }

    /**
     * Sets the density of the fog, a high value gives a thick fog.
     * @param fogDensity the density to set.
     */
    public void setFogDensity(float fogDensity) {
        if (material != null) {
            material.setFloat("FogDensity", fogDensity);
        }
        this.fogDensity = fogDensity;
    }

    /**
     * @return fogStartDistance the fog start distance.
     */
    public float getFogStartDistance() {
        return fogStartDistance;
    }

    /**
     * Start distance is the absolute distance of the fog in mode EXP2_CAM_TO_DISTANCE and
     * the start distance in modes LINEAR and EXP2_DISTANCE_TO_INFINITY (default is 200).
     * @param fogStartDistance the distance the fog starts at.
     */
    public void setFogStartDistance(float fogStartDistance) {
        if (material != null) {
            material.setFloat("FogStartDistance", fogStartDistance);
        }
        this.fogStartDistance = fogStartDistance;
    }

    /**
     * @return fogEndDistance the fog end distance.
     */
    public float getFogEndDistance() {
        return fogEndDistance;
    }

    /**
     * End distance (100% density) in mode LINEAR.
     * @param fogEndDistance the distance the fog ends at.
     */
    public void setFogEndDistance(float fogEndDistance) {
        if (material != null) {
            material.setFloat("FogEndDistance", fogEndDistance);
        }
        this.fogEndDistance = fogEndDistance;
    }

    /**
     * Sets the exclude sky flag.
     * @param excludeSky true to exclude the sky, false to apply fog to the sky.
     */
    public void setExcludeSky(boolean excludeSky) {
        if (material != null) {
            material.setBoolean("ExcludeSky", excludeSky);
        }
        this.excludeSky = excludeSky;
    }

    /**
     * @return the exclude sky flag.
     */
    public boolean getExcludeSky() {
        return excludeSky;
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        super.write(ex);
        final float defaultFogDensity = 0.7f;

        OutputCapsule oc = ex.getCapsule(this);
        oc.write(fogColor, "fogColor", ColorRGBA.White.clone());
        oc.write(fogDensity, "fogDensity", defaultFogDensity);
        oc.write(fogStartDistance, "fogStartDistance", DEFAULT_FOG_START_DISTANCE);
        oc.write(fogEndDistance, "fogEndDistance", DEFAULT_FOG_END_DISTANCE);
        oc.write(excludeSky, "excludeSky", false);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        super.read(im);
        final float defaultFogDensity = 0.7f;

        InputCapsule ic = im.getCapsule(this);
        fogColor = (ColorRGBA) ic.readSavable("fogColor", ColorRGBA.White.clone());
        fogDensity = ic.readFloat("fogDensity", defaultFogDensity);
        fogStartDistance = ic.readFloat("fogStartDistance", DEFAULT_FOG_START_DISTANCE);
        fogEndDistance = ic.readFloat("fogEndDistance", DEFAULT_FOG_END_DISTANCE);
        excludeSky = ic.readBoolean("excludeSky", false);
    }
}
