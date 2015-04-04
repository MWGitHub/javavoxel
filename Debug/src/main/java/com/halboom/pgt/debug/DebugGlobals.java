package com.halboom.pgt.debug;

import com.jme3.asset.AssetManager;
import com.jme3.scene.Node;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 2/11/13
 * Time: 8:21 PM
 * Global objects used for debugging to keep production code clean.
 */
public final class DebugGlobals {
    /**
     * Instance of the singleton.
     */
    private static DebugGlobals ourInstance = new DebugGlobals();

    /**
     * @return the instance of the singleton.
     */
    public static DebugGlobals getInstance() {
        return ourInstance;
    }

    /**
     * If enabled debug movement will be allowed.
     */
    private static final boolean IS_DEBUG_ENABLED = true;

    /**
     * Error message to display when debugging with it turned off.
     */
    private static final String DEBUG_ERROR_MESSAGE = "Debugging is turned off!";

    /**
     * AssetManager used throughout the program.
     */
    private AssetManager assetManager;

    /**
     * RootNode of the current state.
     */
    private Node rootNode;

    /**
     * Initializes the globals.
     */
    private DebugGlobals() {
    }

    /**
     * @return the AssetManager used for loading assets.
     */
    public AssetManager getAssetManager() {
        if (IS_DEBUG_ENABLED) {
            LoggerFactory.getLogger(DebugGlobals.class).warn(
                    "Using debug AssetManager - " + sun.reflect.Reflection.getCallerClass(2).getName());
        } else {
            throw new IllegalAccessError(DEBUG_ERROR_MESSAGE);
        }
        return assetManager;
    }

    /**
     * Sets the asset manager to load resources from.
     * @param assetManager the asset manager to load resources from.
     */
    public void setAssetManager(AssetManager assetManager) {
        this.assetManager = assetManager;
    }

    /**
     * @return the node debug objects are attached to.
     */
    public Node getRootNode() {
        if (IS_DEBUG_ENABLED) {
            LoggerFactory.getLogger(DebugGlobals.class).warn(
                    "Using debug rootNode - " + sun.reflect.Reflection.getCallerClass(2).getName());
        } else {
            throw new IllegalAccessError(DEBUG_ERROR_MESSAGE);
        }
        return rootNode;
    }

    /**
     * Set the root node debug objects will be attached to.
     * @param rootNode the node to attach debug objects to.
     */
    public void setRootNode(Node rootNode) {
        this.rootNode = rootNode;
    }

    /**
     * Prints the objects without any other information.
     * @param isDebug set to true to set as a debug message.
     * @param x the objects to print.
     */
    public static void println(boolean isDebug, Object... x) {
        StringBuilder output = new StringBuilder();
        for (Object i : x) {
            if (i != null) {
                output.append(i.toString());
            } else {
                output.append("null");
            }
            if (i != x[x.length - 1]) {
                output.append(", ");
            }
        }

        if (isDebug) {
            LoggerFactory.getLogger(DebugGlobals.class).debug("{}", output);
        } else {
            LoggerFactory.getLogger(DebugGlobals.class).info("{}", output);
        }
    }

    /**
     * Retrieves the caller class and line number of where the method is called and returns
     * a format to use to prepend to outputs. Multiple inputs will be separated by a comma.
     * @param x the inputs to print.
     */
    public static void println(Object... x) {
        StringBuilder output = new StringBuilder();
        for (Object i : x) {
            if (i != null) {
                output.append(i.toString());
            } else {
                output.append("null");
            }
            if (i != x[x.length - 1]) {
                output.append(", ");
            }
        }

        StackTraceElement trace = Thread.currentThread().getStackTrace()[2];
        LoggerFactory.getLogger(trace.getClass()).debug("{}", trace.getClassName()
                + ":" + trace.getMethodName()
                + ":" + trace.getLineNumber() + ": " + output);
    }

    /**
     * Retrieves the caller class and line number of where the method is called and returns
     * a format to use to prepend to outputs. Multiple inputs will be separated by a comma.
     * @param trace the stack to print.
     * @param x the inputs to print.
     */
    public static void println(StackTraceElement trace, Object... x) {
        StringBuilder output = new StringBuilder();
        for (Object i : x) {
            if (i != null) {
                output.append(i.toString());
            } else {
                output.append("null");
            }
            if (i != x[x.length - 1]) {
                output.append(", ");
            }
        }

        LoggerFactory.getLogger(DebugGlobals.class).debug("{}", trace.getClassName()
                + ":" + trace.getMethodName()
                + ":" + trace.getLineNumber() + ": " + output);
    }
}
