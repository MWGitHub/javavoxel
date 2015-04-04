package com.submu.pug.scripting;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/15/13
 * Time: 12:15 PM
 * An event that scripts can attach to.
 */
public class ScriptEvent {
    /**
     * Name of the script event.
     */
    private String name;

    /**
     * Arguments to pass.
     */
    private Object[] args;

    /**
     * Initializes the event.
     * @param name the name of the script.
     * @param args the arguments to pass.
     */
    public ScriptEvent(String name, Object... args) {
        this.name = name;
        this.args = args;
    }

    /**
     * @return the arguments of the event.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * @return the name of the event.
     */
    public String getName() {
        return name;
    }
}
