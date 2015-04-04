package com.submu.pug.scripting;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/15/13
 * Time: 2:40 PM
 * Registers attached events and runs all attached.
 */
public final class EventSystem {
    /**
     * Queued events to be executed on the next runner update.
     */
    private List<ScriptEvent> queuedEvents;

    /**
     * Variables to store data into.
     */
    private Map<String, Object> variables;

    /**
     * Initializes the event system.
     * @param variables the variables to store data into.
     */
    public EventSystem(Map<String, Object> variables) {
        queuedEvents = new ArrayList<ScriptEvent>();
        this.variables = variables;
    }

    /**
     * @return the events that are ready to be executed.
     */
    public List<ScriptEvent> getQueuedEvents() {
        return queuedEvents;
    }

    /**
     * Adds an event with parameters to the queue.
     * @param event the event to add to the queue.
     */
    public void addQueuedEvent(ScriptEvent event) {
        if (queuedEvents != null) {
            queuedEvents.add(event);
        }
    }

    /**
     * Clears the queued events.
     */
    public void clearQueuedEvents() {
        queuedEvents.clear();
    }

    /**
     * Puts data into the queued data.
     * @param key the key of the data.
     * @param data the value of the data.
     */
    public void putData(String key, Object data) {
        variables.put(key, data);
    }

    /**
     * Resets the system.
     */
    public void reset() {
        if (queuedEvents != null) {
            queuedEvents = new LinkedList<ScriptEvent>();
        }
        variables = null;
    }
}
