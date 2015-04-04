package com.submu.pug.scripting;

import groovy.lang.Binding;
import groovy.util.GroovyScriptEngine;
import groovy.util.ResourceException;
import groovy.util.ScriptException;

import java.io.IOError;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/14/13
 * Time: 12:55 PM
 * Loads scala scripts.
 */
public class GroovyRunner {
    /**
     * Scripting engine to run groovy scripts with.
     */
    private GroovyScriptEngine gse;

    /**
     * Main binding for the script.
     */
    private Binding mainBinding;

    /**
     * Variables to put into the binding dynamically.
     */
    private Map<String, Object> variables;

    /**
     * API that scripts can use.
     */
    private ScriptAPI scriptAPI;

    /**
     * Run queued events.
     */
    private EventSystem eventSystem;

    /**
     * Initializes the script loader.
     * @param scriptAPI the script API to use.
     * @param path the path of the groovy scripts.
     */
    public GroovyRunner(ScriptAPI scriptAPI, String... path) {
        try {
            gse = new GroovyScriptEngine(path, getClass().getClassLoader());
        } catch (IOException ex) {
            throw new IOError(ex);
        }

        this.scriptAPI = scriptAPI;
        variables = scriptAPI.getVariables();
        // System to get called events from.
        eventSystem = new EventSystem(variables);
        ScriptGlobals.getInstance().setEventSystem(eventSystem);

        // Run the main class once to compile.
        mainBinding = new Binding();
        mainBinding.setVariable(ScriptAPI.WRAPPER_NAME, scriptAPI);
        mainBinding.setVariable(ScriptAPI.VARIABLE_NAME, variables);
        mainBinding.setVariable(ScriptAPI.TRIGGERED_EVENTS, eventSystem.getQueuedEvents());
        mainBinding.setVariable(ScriptAPI.IS_FIRST_PASS, true);

        try {
            gse.run("Main.groovy", mainBinding);
            mainBinding.setVariable(ScriptAPI.IS_FIRST_PASS, false);
        } catch (ScriptException ex) {
            throw new InternalError("Script failed to run.");
        } catch (ResourceException ex) {
            throw new UnknownError("A resource exception has occurred.");
        }
    }

    /**
     * Updates the groovy scripts.
     * @param tpf the time passed per frame.
     */
    public void update(float tpf) {
        long startTime = System.currentTimeMillis();
        try {
            // Copy the events and pass all the queued events to the groovy script to parse and run.
            List<ScriptEvent> events = new LinkedList<ScriptEvent>();
            events.addAll(eventSystem.getQueuedEvents());
            // Clear the current events now in case the script creates new queued events.
            eventSystem.clearQueuedEvents();
            mainBinding.setVariable(ScriptAPI.TRIGGERED_EVENTS, events);
            mainBinding.setVariable("tpf", tpf);
            gse.run("Main.groovy", mainBinding);
        } catch (ScriptException ex) {
            throw new InternalError("Script failed to run.");
        } catch (ResourceException ex) {
            throw new UnknownError("A resource exception has occurred.");
        }
        long duration = System.currentTimeMillis() - startTime;
        //DebugGlobals.println(duration);
    }

    /**
     * Flushes events.
     */
    public void flushEvents() {
        eventSystem.clearQueuedEvents();
    }

    /**
     * Destroys the runner.
     */
    public void destroy() {
        gse.getGroovyClassLoader().clearCache();
    }
}
