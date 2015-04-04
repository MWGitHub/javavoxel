/**
 * Main groovy class to run the script from.
 * For backwards and future compatibility use the functions from bindings instead of
 * directly from the java classes.
 * The main class runs all events attached to the bindings and handles the parameter retrieval
 * from the java events.
 */

import com.exploringlines.entitysystem.Entity
import com.halboom.pgt.physics.simple.CollisionInformation
import com.halboom.pgt.physics.simple.shapes.Bounds
import com.jme3.math.Vector3f
import com.submu.pug.game.objects.Player
import com.submu.pug.scripting.ScriptAPI
import com.submu.pug.scripting.ScriptEvent
import core.EventFunctions
import core.ScriptBindings

/**
 * Create bindings to use if needed.
 * Try not use groovyScriptBindings anywhere else.
 */
ScriptBindings bindings = groovyScriptBindings.getVariable(ScriptBindings.PERSIST_BINDING)
if (bindings == null) {
    bindings = new ScriptBindings(groovyScriptBindings)
    groovyScriptBindings.setVariable(ScriptBindings.PERSIST_BINDING, bindings)
}

// Check if the script is on the first pass and do nothing if it is.
boolean isFirstPass = bindings.getVariable(ScriptAPI.IS_FIRST_PASS)
if (!isFirstPass) {
    updateGenericEvents(bindings)
    updateEvents(bindings)
}

/**
 * Updates the generic events.
 * @param bindings the bindings to use.
 */
void updateGenericEvents(ScriptBindings bindings) {
    // Run all the generic events before the main events to prevent double updating.
    List<EventFunctions> genericEvents = new LinkedList<>()
    for (EventFunctions eventFunctions : bindings.genericEvents) {
        genericEvents.add(eventFunctions)
    }
    bindings.flushGenericEvents()
    for (EventFunctions eventFunctions : genericEvents) {
        eventFunctions.call()
    }
}

/**
 * Updates the script events.
 * @param bindings the bindings to use.
 */
void updateEvents(ScriptBindings bindings) {
    // No need to copy the events as the incoming ones are already copied in a new list.
    List<ScriptEvent> events = triggeredEvents
    // Run the queued events and set the event variables for calling into other functions.
    for (ScriptEvent event : events) {
        switch (event.name) {
        /**
         * Key event triggers.
         */
            case ScriptBindings.EVENT_KEY_ANALOG:
                String pressedKey = event.getArgs()[0]
                float pressedAmount = event.getArgs()[1]
                Player player = event.getArgs()[2]
                boolean isGUIActive = event.getArgs()[3]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_KEY_ANALOG)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onAnalogKeyPressed(pressedKey, pressedAmount, player, isGUIActive)
                    }
                }
                break
            case ScriptBindings.EVENT_KEY_PRESSED:
                String pressedKey = event.getArgs()[0]
                Player player = event.getArgs()[1]
                boolean isGUIActive = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_KEY_PRESSED)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onKeyPressed(pressedKey, player, isGUIActive)
                    }
                }
                break
            case ScriptBindings.EVENT_KEY_RELEASED:
                String pressedKey = event.getArgs()[0]
                Player player = event.getArgs()[1]
                boolean isGUIActive = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_KEY_RELEASED)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onKeyPressed(pressedKey, player, isGUIActive)
                    }
                }
                break
            case ScriptBindings.EVENT_CAMERA_MOVES:
                Vector3f cameraLocation = event.getArgs()[0]
                Vector3f lastCameraLocation = event.getArgs()[1]
                break
        /**
         * Commands and actions.
         */
            case ScriptBindings.EVENT_ABILITY_CAST_BEGIN:
                Entity entity = event.getArgs()[0]
                int index = event.getArgs()[1]
                String internalName = event.getArgs()[2]
                Vector3f target = event.getArgs()[3]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ABILITY_CAST_BEGIN)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onAbilityCast(entity, index, internalName, target)
                    }
                }
                break
            case ScriptBindings.EVENT_ABILITY_CAST:
                Entity entity = event.getArgs()[0]
                int index = event.getArgs()[1]
                String internalName = event.getArgs()[2]
                Vector3f target = event.getArgs()[3]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ABILITY_CAST)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onAbilityCast(entity, index, internalName, target)
                    }
                }
                break
            case ScriptBindings.EVENT_ABILITY_CANCEL:
                Entity entity = event.getArgs()[0]
                int index = event.getArgs()[1]
                String internalName = event.getArgs()[2]
                Vector3f target = event.getArgs()[3]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ABILITY_CANCEL)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onAbilityCast(entity, index, internalName, target)
                    }
                }
                break
            case ScriptBindings.EVENT_ABILITY_UPGRADE:
                Entity entity = event.getArgs()[0]
                int abilityIndex = event.getArgs()[1]
                String upgrade = event.getArgs()[2]
                int level = event.getArgs()[3]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ABILITY_UPGRADE)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onAbilityUpgrade(entity, abilityIndex, upgrade, level)
                    }
                }
                break
        /**
         * Time triggers.
         */
            case ScriptBindings.EVENT_MAP_INITIALIZATION:
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_MAP_INITIALIZATION)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onMapInitialization()
                    }
                }
                break
            case ScriptBindings.EVENT_PERIODIC:
                float elapsedTime = event.getArgs()[0]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_PERIODIC)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onPeriodic(elapsedTime)
                    }
                }
                break
            case ScriptBindings.EVENT_ACTOR_CREATED:
                Entity entity = event.getArgs()[0]
                break
            case ScriptBindings.EVENT_PLAYER_JOINED:
                Player player = event.getArgs()[0]
                break
        /**
         * Collision triggers.
         */
            case ScriptBindings.EVENT_TILE_COLLIDED:
                CollisionInformation collisionInformation = event.getArgs()[0]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_TILE_COLLIDED)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onTileCollision(collisionInformation)
                    }
                }
                break
            case ScriptBindings.EVENT_STATIC_COLLIDED:
                CollisionInformation collisionInformation = event.getArgs()[0]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_STATIC_COLLIDED)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onBoundsCollision(collisionInformation)
                    }
                }
                break
            case ScriptBindings.EVENT_SENSOR_COLLIDED:
                CollisionInformation collisionInformation = event.getArgs()[0]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_SENSOR_COLLIDED)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onBoundsCollision(collisionInformation)
                    }
                }
                break
        /**
         * Region event triggers.
         */
            case ScriptBindings.EVENT_REGION_ENTER:
                String name = event.getArgs()[0]
                Bounds bounds = event.getArgs()[1]
                Entity entity = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_REGION_ENTER)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onRegionEvent(name, bounds, entity)
                    }
                }
                break
            case ScriptBindings.EVENT_REGION_INSIDE:
                String name = event.getArgs()[0]
                Bounds bounds = event.getArgs()[1]
                Entity entity = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_REGION_INSIDE)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onRegionEvent(name, bounds, entity)
                    }
                }
                break
            case ScriptBindings.EVENT_REGION_LEAVE:
                String name = event.getArgs()[0]
                Bounds bounds = event.getArgs()[1]
                Entity entity = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_REGION_LEAVE)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onRegionEvent(name, bounds, entity)
                    }
                }
                break
        /**
         * AI event triggers.
         */
            case ScriptBindings.EVENT_AI_COMBAT:
                Entity entity = event.getArgs()[0]
                Entity target = event.getArgs()[1]
                String script = event.getArgs()[2]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_AI_COMBAT)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onCombat(entity, target, script)
                    }
                }
                break
            case ScriptBindings.EVENT_AI_LEAVE_COMBAT:
                Entity entity = event.getArgs()[0]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_AI_LEAVE_COMBAT)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onLeaveCombat(entity)
                    }
                }
                break
        /**
         * Item event triggers.
         */
            case ScriptBindings.EVENT_ITEM_PICK_UP:
                Entity holder = event.getArgs()[0]
                Entity item = event.getArgs()[1]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ITEM_PICK_UP)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onItemPickUpOrDrop(holder, item)
                    }
                }
                break
            case ScriptBindings.EVENT_ITEM_DROP:
                Entity holder = event.getArgs()[0]
                Entity item = event.getArgs()[1]
                List<EventFunctions> functions = bindings.getEventBindings(ScriptBindings.EVENT_ITEM_DROP)
                if (functions != null) {
                    for (EventFunctions func : functions) {
                        func.onItemPickUpOrDrop(holder, item)
                    }
                }
                break
        }
    }
}
