package com.submu.pug.scripting;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/7/13
 * Time: 2:40 PM
 * Globals for use with the scripting system.
 * Never use this outside of the script package unless registering systems.
 */
public final class ScriptGlobals {
    /**
     * Instance of the globals.
     */
    private static ScriptGlobals ourInstance = new ScriptGlobals();

    /**
     * @return the singleton instance.
     */
    public static ScriptGlobals getInstance() {
        return ourInstance;
    }

    /**
     * Do not allow initialization.
     */
    private ScriptGlobals() {
    }

    /**
     * Event system to use for the scripts.
     */
    private EventSystem eventSystem;

    /**
     * Adds and event to the event system.
     * @param scriptEvent the event to add.
     */
    public void addEvent(ScriptEvent scriptEvent) {
        eventSystem.addQueuedEvent(scriptEvent);
    }

    /**
     * Puts data into the scripting engine.
     * @param name the name of the data.
     * @param data the data value.
     */
    public void putData(String name, Object data) {
        eventSystem.putData(name, data);
    }

    /**
     * @param eventSystem the event system to set.
     */
    public void setEventSystem(EventSystem eventSystem) {
        this.eventSystem = eventSystem;
    }

    /**
     * Destroys the globals.
     */
    public void destroy() {
        if (eventSystem != null) {
            eventSystem.reset();
        }
    }
}
