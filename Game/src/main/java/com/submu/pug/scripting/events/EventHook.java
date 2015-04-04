package com.submu.pug.scripting.events;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/26/13
 * Time: 7:48 PM
 */
public interface EventHook {
    /**
     * Updates the event.
     * @param tpf the time passed since the last frame.
     */
    void updateEvent(float tpf);

    /**
     * Destroys the event hook.
     */
    void destroyEvent();
}
