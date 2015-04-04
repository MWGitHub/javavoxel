package com.submu.pug.application;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/14/13
 * Time: 4:52 PM
 * Main loop for the game.
 * Logic will be updated when ready and will allow for integration if over step size.
 * The logic can be set to cap and skip remaining steps if the computer cannot handle the updates fast enough.
 * Renderer will only render when ready and only once.
 * TODO: Be able to handle when time to update takes too long.
 */
public class MainLoop {
    /**
     * Number of nanoseconds per second.
     */
    private static final long NANOSECONDS_PER_SECOND = 1000000000;

    /**
     * Size of each logic update in seconds.
     */
    private float logicStepSize;

    /**
     * Number of times to update the logic.
     */
    private int timesToUpdate;

    /**
     * Size of each display update in seconds.
     */
    private float displayStepSize;

    /**
     * Number of times to render the display.
     */
    private int timesToDisplay;

    /**
     * Last logic update time.
     */
    private long lastLogicTime;

    /**
     * Last display update time.
     */
    private long lastDisplayTime;

    /**
     * True if the loop is enabled.
     */
    private boolean isEnabled;

    /**
     * Initializes the variables.
     * @param logicUpdatesPerSecond the number of logic updates per second.
     * @param displayUpdatesPerSecond the number of display updates per second.
     */
    public MainLoop(int logicUpdatesPerSecond, int displayUpdatesPerSecond) {
        logicStepSize = 1f / logicUpdatesPerSecond;
        displayStepSize = 1f / displayUpdatesPerSecond;

        lastLogicTime = 0;
        lastDisplayTime = 0;

        timesToUpdate = 0;
        timesToDisplay = 0;

        isEnabled = false;
    }

    /**
     * Sets the beginning values for the last times.
     */
    public final void enable() {
        lastLogicTime = System.nanoTime();
        lastDisplayTime = System.nanoTime();
        isEnabled = true;
    }

    /**
     * Disable the loop.
     */
    public final void disable() {
        isEnabled = false;
    }

    /**
     * Update the loop timers.
     */
    public final void update() {
        if (isEnabled) {
            long currentTime = System.nanoTime();
            while (lastLogicTime + (long) (logicStepSize * NANOSECONDS_PER_SECOND) <= currentTime) {
                timesToUpdate++;
                lastLogicTime += (long) (logicStepSize * NANOSECONDS_PER_SECOND);
            }
            if (lastDisplayTime + (long) (displayStepSize * NANOSECONDS_PER_SECOND) <= currentTime) {
                timesToDisplay++;
                lastDisplayTime = (long) (displayStepSize * NANOSECONDS_PER_SECOND);
            }
        }
    }

    /**
     * Checks if the logic should update and manages how many times.
     * @return true if logic should update.
     */
    public final boolean shouldUpdateLogic() {
        if (timesToUpdate > 0) {
            timesToUpdate--;
            return true;
        }

        return false;
    }

    /**
     * Checks if the display should update.
     * @return true if the display should update.
     */
    public final boolean shouldUpdateDisplay() {
        if (timesToDisplay > 0) {
            timesToDisplay = 0;
            return true;
        }

        return false;
    }

    /**
     * @return the step size per logic update.
     */
    public final float getLogicStepSize() {
        return logicStepSize;
    }

    /**
     * @return the step size per display update.
     */
    public final float getDisplayStepSize() {
        return displayStepSize;
    }
}
