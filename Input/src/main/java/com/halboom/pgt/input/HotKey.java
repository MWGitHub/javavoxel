package com.halboom.pgt.input;

import com.jme3.input.controls.Trigger;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/19/13
 * Time: 12:49 PM
 * Holds data for a hot key.
 */
public class HotKey {
    /**
     * Name of the hot key.
     */
    public String name;

    /**
     * Triggers used to activate the hot key.
     */
    public List<Trigger> triggers = new LinkedList<Trigger>();

    /**
     * Creates the hot key.
     * @param name the name of the hot key.
     * @param triggers the triggers of the hot key.
     */
    public HotKey(String name, Trigger... triggers) {
        this.name = name;
        for (Trigger trigger : triggers) {
            if (!this.triggers.contains(trigger)) {
                this.triggers.add(trigger);
            }
        }
    }
}
