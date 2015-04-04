package com.submu.pug.scripting.events;

import com.submu.pug.game.objects.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/19/13
 * Time: 12:25 PM
 * Handles GUI events.
 * The events aren't created by the GUI but by the parts that make up the GUI.
 */
public class EventGUI implements EventHook {
    /**
     * Actions for the GUI.
     */
    public static final String ACTION_USE = "UseGUI";

    /**
     * Player the events are tied to.
     */
    private Player player;

    /**
     * Initializes the GUI events.
     * @param player the player the events are tied to.
     */
    public EventGUI(Player player) {
        this.player = player;
    }

    @Override
    public void updateEvent(float tpf) {
    }

    @Override
    public void destroyEvent() {
    }
}
