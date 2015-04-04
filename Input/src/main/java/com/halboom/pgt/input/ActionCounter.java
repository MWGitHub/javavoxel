package com.halboom.pgt.input;

import com.jme3.input.InputManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/30/13
 * Time: 3:17 PM
 * Counts the number of times an action has been added to a specific input manager.
 */
public class ActionCounter {
    /**
     * Represents an action with an active counter.
     */
    private static class ActionMap {
        /**
         * Maps an action to the number of active actions.
         */
        private Map<String, Integer> actionCount = new HashMap<String, Integer>();

        /**
         * Adds an action.
         * @param action the action to increment.
         */
        public void addAction(String action) {
            Integer count = actionCount.get(action);
            if (count == null) {
                actionCount.put(action, 1);
            } else {
                actionCount.put(action, count + 1);
            }
        }

        /**
         * Removes an action.
         * @param action the action to decrement.
         */
        public void removeAction(String action) {
            Integer count = actionCount.get(action);
            if (count != null) {
                if (count > 1) {
                    actionCount.put(action, count - 1);
                } else {
                    actionCount.remove(action);
                }
            }
        }

        /**
         * Checks if an action is in use.
         * @param action the action to check.
         * @return true if the action is used, false otherwise.
         */
        public boolean isActionUsed(String action) {
            Integer count = actionCount.get(action);
            return count != null && count > 0;
        }
    }

    /**
     * Maps the input manager to their actions.
     */
    private static Map<InputManager, ActionMap> inputMap = new HashMap<InputManager, ActionMap>();

    /**
     * Increments an action to the action counter.
     * @param inputManager the input manager the action is attached to.
     * @param action the name of the action.
     */
    public static void addAction(InputManager inputManager, String action) {
        ActionMap actionMap = inputMap.get(inputManager);
        if (actionMap == null) {
            actionMap = new ActionMap();
            inputMap.put(inputManager, actionMap);
        }
        actionMap.addAction(action);
    }

    /**
     * Decrements an action from the action counter.
     * @param inputManager the input manager the action is attached to.
     * @param action the name of the action.
     */
    public static void removeAction(InputManager inputManager, String action) {
        ActionMap actionMap = inputMap.get(inputManager);
        if (actionMap == null) {
            return;
        }
        actionMap.removeAction(action);
    }

    /**
     * Checks if an action is being used.
     * @param inputManager the input manager the action is attached to.
     * @param action the name of the action.
     * @return true if being used, false otherwise.
     */
    public static boolean isActionUsed(InputManager inputManager, String action) {
        ActionMap actionMap = inputMap.get(inputManager);
        if (actionMap == null) {
            return false;
        }

        return actionMap.isActionUsed(action);
    }


    /**
     * Clears all action counters.
     */
    public static void clear() {
        inputMap.clear();
    }
}
