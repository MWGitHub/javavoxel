package com.submu.pug.data;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/21/13
 * Time: 4:27 PM
 * Points to where scripts are and stores editor trigger information.
 */
public class ScriptsData {
    /**
     * Root of where scripts reside.
     */
    public String root = "CoreScripts/src/";

    /**
     * Main groovy script file to trigger events from.
     */
    public String main = "Main.groovy";

    /**
     * Data the editor uses but not the game.
     */
    public Editor editor = new Editor();

    /**
     * Data for use with the editor.
     */
    public static class Editor {
        /**
         * Triggers that the user sees in the editor.
         */
        public String[] triggers;
    }
}
