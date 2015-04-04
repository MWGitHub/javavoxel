package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/3/13
 * Time: 12:59 PM
 * Data for gui element positions.
 * Positions are in screen percentages.
 */
public class GUIElementsData {
    /**
     * Scale of the whole UI.
     */
    public float scale = 1f;

    /**
     * Standard health bar.
     */
    public ElementLocation healthBar;

    /**
     * Bar used for abilities.
     */
    public ElementLocation manaBar;

    /**
     * Bar used for experience.
     */
    public ElementLocation expBar;

    /**
     * Center location of the cooldowns and width.
     */
    public ElementLocation cooldownLocation;

    /**
     * Bar for the enemy health.
     */
    public ElementLocation enemyBar;

    /**
     * Text for the money display.
     */
    public ElementLocation money;

    /**
     * Name of the money.
     */
    public String moneyName = "gold";

    /**
     * Text for the skill points display.
     */
    public ElementLocation skillPoints;

    /**
     * Name of the skill points.
     */
    public String skillPointsName = "points";

    /**
     * Text for the level display.
     */
    public ElementLocation level;

    /**
     * Bar properties.
     */
    public static class ElementLocation {
        /**
         * Position of the bar.
         */
        public float positionX, positionY;

        /**
         * Width and height of the bar.
         */
        public float width, height;

        /**
         * Color overlay for the bar.
         */
        public Color color = new Color();

        /**
         * Image of the bar if used.
         */
        public String image;
    }

    /**
     * Color of an element.
     */
    public static class Color {
        /**
         * Color of the element.
         */
        public int red = 0, green = 0, blue = 0, alpha = 255;
    }
}
