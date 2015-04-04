package com.halboom.pgt.pgui;

import com.jme3.math.ColorRGBA;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 8/5/13
 * Time: 1:38 PM
 * Standard colors used within the GUI.
 */
public class Color {
    /**
     * Max value for colors using hex values.
     */
    public static final float MAX_COLOR = 255.0f;

    /**
     * Converts color values to RGBA from MAX_COLOR.
     * @param r the red value.
     * @param g the green value.
     * @param b the blue value.
     * @param a the alpha value.
     * @return the color in RGBA.
     */
    public static ColorRGBA colorToRGBA(int r, int g, int b, int a) {
        return new ColorRGBA(r / Color.MAX_COLOR, g / Color.MAX_COLOR, b / Color.MAX_COLOR,
                a / Color.MAX_COLOR);
    }
}
