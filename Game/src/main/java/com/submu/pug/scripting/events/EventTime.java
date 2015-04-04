package com.submu.pug.scripting.events;

import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;
import com.submu.pug.util.math.Units;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/27/13
 * Time: 12:07 PM
 * Hooks to time based events.
 */
public class EventTime implements  EventHook {
    /**
     * Name for initialization of a map.
     */
    public static final String EVENT_INITIALIZATION = "eventInitialization";
    /**
     * Name for the periodic timer.
     */
    public static final String EVENT_PERIODIC = "eventPeriodic";
    /**
     * Name for events that trigger through waiting functions.
     * Allows arbitrary parameters and should be created within the scripts.
     */
    public static final String EVENT_WAIT = "eventWait";

    /**
     * Time elapsed name.
     */
    public static final String VAR_TIME_ELAPSED = "varTimeElapsed";

    /**
     * Total time elapsed for a map.
     */
    private float timeElapsed = 0f;

    /**
     * Initializes the time based event.
     */
    public EventTime() {
    }

    @Override
    public void updateEvent(float tpf) {
        if (timeElapsed <= Units.TOLERANCE) {
            ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_INITIALIZATION));
        }
        ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_PERIODIC, timeElapsed));
        ScriptGlobals.getInstance().putData(VAR_TIME_ELAPSED, timeElapsed);
        timeElapsed += tpf;
    }

    @Override
    public void destroyEvent() {
    }
}
