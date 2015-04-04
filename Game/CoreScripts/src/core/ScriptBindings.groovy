package core

import com.halboom.pgt.entityspatial.TransformComponent
import com.exploringlines.entitysystem.Entity
import com.halboom.pgt.physics.simple.shapes.Bounds
import com.jme3.bounding.BoundingVolume
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.submu.pug.data.KeyMap
import com.submu.pug.data.ObjectsData
import com.submu.pug.game.objects.components.AbilityComponent
import com.submu.pug.scripting.ScriptAPI
import com.submu.pug.scripting.ScriptEvent
import com.submu.pug.scripting.events.*
import org.codehaus.groovy.runtime.StackTraceUtils
import scripts.MapScripts

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/14/13
 * Time: 5:17 PM
 * Bindings for scripts to use without having to reference the java code directly.
 * The functions here will be less likely to break in future versions compared to using
 * the types directly.
 */

class ScriptBindings {
    /**
     * Persistent variables.
     */
    public static final String PERSIST_BINDING = "Persist Binding"

    /**
     * Event names.
     */
    public static final String EVENT_NONE = "None",
    // Key Inputs
                               EVENT_KEY_ANALOG = EventKey.EVENT_ANALOG,
                               EVENT_KEY_PRESSED = EventKey.EVENT_KEY_PRESSED,
                               EVENT_KEY_RELEASED = EventKey.EVENT_KEY_RELEASED,
    // Commands
                               EVENT_ABILITY_CAST_BEGIN = EventAbilities.EVENT_ABILITY_CAST_BEGIN,
                               EVENT_ABILITY_CAST = EventAbilities.EVENT_ABILITY_CAST,
                               EVENT_ABILITY_CANCEL = EventAbilities.EVENT_ABILITY_CANCEL,
                               EVENT_ABILITY_UPGRADE = EventAbilities.EVENT_ABILITY_UPGRADE,
    // Camera movement
                               EVENT_CAMERA_MOVES = EventCameraMoves.EVENT_CAMERA_MOVEMENT,
    // Map timer
                               EVENT_MAP_INITIALIZATION = EventTime.EVENT_INITIALIZATION,
                               EVENT_PERIODIC = EventTime.EVENT_PERIODIC,
    // Actor callbacks
                               EVENT_ACTOR_CREATED = EventActor.EVENT_ACTOR_CREATED,
    // Atmosphere callbacks
                               EVENT_SKYBOX_CHANGED = EventAtmosphere.EVENT_SKYBOX_CHANGED,
    // Session callbacks
                               EVENT_PLAYER_JOINED = EventPlayers.EVENT_PLAYER_JOINED,
    // Collision callbacks
                               EVENT_TILE_COLLIDED = EventCollider.EVENT_TILE_COLLIDED,
                               EVENT_STATIC_COLLIDED = EventCollider.EVENT_STATIC_COLLIDED,
                               EVENT_SENSOR_COLLIDED = EventCollider.EVENT_SENSOR_COLLIDED,
    // Region callbacks
                               EVENT_REGION_ENTER = EventRegions.EVENT_REGION_ENTER,
                               EVENT_REGION_INSIDE = EventRegions.EVENT_REGION_INSIDE,
                               EVENT_REGION_LEAVE = EventRegions.EVENT_REGION_LEAVE,
    // AI callbacks
                               EVENT_AI_COMBAT = EventAI.EVENT_COMBAT,
                               EVENT_AI_LEAVE_COMBAT = EventAI.EVENT_LEAVE_COMBAT,
    // Item callbacks
                               EVENT_ITEM_PICK_UP = EventItem.EVENT_ITEM_PICK_UP,
                               EVENT_ITEM_DROP = EventItem.EVENT_ITEM_DROP

    /**
     * Action names for key inputs.
     */
    public static final String ACTION_JUMP = KeyMap.jump.name,
                               ACTION_FORWARD = KeyMap.moveForward.name,
                               ACTION_BACKWARD = KeyMap.moveBackward.name,
                               ACTION_LEFT =KeyMap.moveLeft.name,
                               ACTION_RIGHT = KeyMap.moveRight.name,
                               ACTION_UP = KeyMap.moveUp.name,
                               ACTION_DOWN = KeyMap.moveDown.name,
                               ACTION_ABILITY1 = KeyMap.gameAbility1.name,
                               ACTION_ABILITY2 = KeyMap.gameAbility2.name,
                               ACTION_ABILITY3 = KeyMap.gameAbility3.name,
                               ACTION_USE = KeyMap.use.name

    /**
     * Variables for the session.
     */
    public static final String VAR_LAST_CREATED_ACTOR = EventActor.VAR_LAST_CREATED_ACTOR

    /**
     * Names of model attachment points.
     */
    public static final String MODEL_ATTACH_HEAD = "Head"

    /**
     * API to use for the bindings.
     */
    private ScriptAPI api

    /**
     * Functions relating to entities.
     */
    private EntityBindings entityBindings

    /**
     * Functions relating to players.
     */
    private PlayerBindings playerBindings

    /**
     * Functions relating to the creation of entities.
     */
    private Creator creator

    /**
     * Map specific scripts.
     */
    private MapScripts mapScripts

    /**
     * Binds functions to events to be called in the main script.
     */
    private Map<String, List<EventFunctions>> eventBindings

    /**
     * Generic events to be run.
     */
    private List<EventFunctions> genericEvents = new LinkedList<>()

    /**
     * Initializes the bindings.
     * @param api the api to use.
     */
    ScriptBindings(Object api) {
        this.api = (ScriptAPI) api
        eventBindings = new HashMap<String, List<EventFunctions>>()
        entityBindings = new EntityBindings(this.api)
        playerBindings = new PlayerBindings(this.api)
        creator = new Creator(this.api, entityBindings)
        mapScripts = new MapScripts(this)
    }

    /**
     * Set a global variable.
     * @param name the name of the variable.
     * @param data the data to set.
     */
    public void setVariable(String name, Object data) {
        api.setVariable(name, data)
    }

    /**
     * Retrieve a variable from the global variables.
     * @param name the name of the variable to retrieve.
     * @return the retrieved variable or null if none is found.
     */
    public Object getVariable(String name) {
        return api.getVariable(name)
    }

    /**
     * Binds a function to an event.
     * @param event the event to bind to.
     * @param function the closure to bind.
     */
    public void addEventBinding(String event, EventFunctions function) {
        if (eventBindings.get(event) == null) {
            eventBindings.put(event, new LinkedList())
        }
        eventBindings.get(event).add(function)
    }

    /**
     * Retrieves all events for the bindings.
     * @param event the event to retrieve.
     * @return the bound functions of the event.
     */
    public List<EventFunctions> getEventBindings(String event) {
        return eventBindings.get(event)
    }

    /**
     * @return the generic events to be called.
     */
    public List<EventFunctions> getGenericEvents() {
        return genericEvents
    }

    /**
     * Flushes all the generic events.
     */
    public void flushGenericEvents() {
        genericEvents.clear()
    }

    /*******************************************************************************
     * Utility
     * Functions that provide utility.
     ****************************************************************************** */

    /**
     * Adds a script event to the next update.
     * @param event the event to add.
     */
    public void addEvent(ScriptEvent event) {
        api.addEvent(event)
    }

    /**
     * Adds a generic event to the event system to be run on the next update.
     * @param scriptEvent the functions to pass in to the generic event.
     */
    public void addGenericEvent(EventFunctions eventFunctions) {
        genericEvents.add(eventFunctions)
    }

    /**
     * @return the time elapsed since the beginning of the map.
     */
    public float getElapsedTime() {
        return (float) api.getVariable(EventTime.VAR_TIME_ELAPSED)
    }

    /**
     * Retrieves the direction to another entity.
     * @param entity the entity.
     * @param target the target to get the direction to.
     * @return the direction to the target.
     */
    public Vector3f getDirectionToEntity(Entity entity, Entity target) {
        TransformComponent transformComponent = api.entitySystem.getComponent(target, TransformComponent.class)
        Vector3f location1;
        Vector3f location2;
        if (transformComponent == null) {
            return null
        } else {
            location1 = new Vector3f(transformComponent.positionX, transformComponent.positionY, transformComponent.positionZ)
        }
        transformComponent = api.entitySystem.getComponent(entity, TransformComponent.class)
        if (transformComponent == null) {
            return null
        }
        location2 = new Vector3f(transformComponent.positionX, transformComponent.positionY, transformComponent.positionZ)
        return location1.subtractLocal(location2).normalizeLocal()
    }

    /**
     * Retrieves the direction from one point to another.
     * @param origin the starting point.
     * @param point the end point.
     * @return the direction to the point.
     */
    public Vector3f getDirectionToPoint(Vector3f origin, Vector3f point) {
        return point.subtract(origin).normalizeLocal()
    }

    /*******************************************************************************
     * Ability Retrieval
     * Functions that retrieve ability data.
     ****************************************************************************** */

    /**
     * Retrieves the location to spawn an ability.
     * @param entity the entity casting the ability.
     * @param abilityName the name of the ability.
     * @return the location to spawn the ability or null if none are valid.
     */
    public Vector3f getAbilitySpawn(Entity entity, String abilityName) {
        ObjectsData.AbilityData ability = api.abilitySystem.getAbilityData(abilityName)
        if (ability == null || entity == null) {
            return null
        }
        // Calculate the offsets based on the facing direction of the entity.
        Vector3f attachPoint = entityBindings.getAttachmentPoint(entity, ability.attachPoint)
        Vector3f spawn = new Vector3f(attachPoint)
        Quaternion rotation = entityBindings.getRotation(entity)
        if (rotation == null) {
            spawn.addLocal(ability.offsetX, ability.offsetY, ability.offsetZ)
        } else {
            // Rotate on the Y axis using a rotation matrix.
            spawn.y += ability.offsetY
            float[] angles = rotation.toAngles(null)
            float yaw = angles[1]
            float offsetX = -(Math.cos(yaw) * ability.offsetX + Math.sin(yaw) * ability.offsetZ)
            float offsetZ = -(-Math.sin(yaw) * ability.offsetX + Math.cos(yaw) * ability.offsetZ)
            spawn.x += offsetX
            spawn.z += offsetZ
        }
        return spawn
    }

    /**
     * Retrieves user data for an ability.
     * @param abilityName the name of the ability.
     * @param key the key of the ability.
     * @return the user data or null if none found.
     */
    public String getAbilityUserData(String abilityName, String key) {
        ObjectsData.AbilityData ability = api.abilitySystem.getAbilityData(abilityName)
        if (ability == null || ability.userData == null) {
            return null
        }
        return ability.userData.get(key)
    }

    /**
     * Retrieves the ability name from the given index.
     * @param entity the entity to get the ability from.
     * @param index the index of the ability.
     * @return the name of the ability or null if none is found.
     */
    public String getAbilityName(Entity entity, int index) {
        AbilityComponent abilityComponent = api.entitySystem.getComponent(entity, AbilityComponent.class)
        if (index < abilityComponent.abilities.size()) {
            return abilityComponent.abilities.get(index).internalName
        }
        return null
    }

    /*******************************************************************************
     * Region Modifiers
     * Functions that modify, retrieve, destroy, and create regions.
     ****************************************************************************** */
    /**
     * Retrieves a region by name.
     * @param name the name of the region.
     * @return the retrieved region or null if none exists.
     */
    public Vector3f getRegionCenter(String name) {
        Bounds region = api.regions.getRegion(name)
        if (region == null) {
            return null;
        }
        Vector3f center = new Vector3f()
        if (region instanceof BoundingVolume) {
            center.set(((BoundingVolume) region).center)
        }
        return center
    }

    /**
     * Retrieves the entities within a region.
     * @param name the name of the region.
     * @return the entities in the region or null if none.
     */
    public List<Entity> getEntitiesInRegion(String name) {
        Bounds region = api.regions.getRegion(name)
        if (region == null) {
            return null
        }
        return api.boundsSystem.getEntitiesInBounds(region, null)
    }

    /*******************************************************************************
     * GUI Functions
     ****************************************************************************** */
    /**
     * Shows the upgrade menu.
     */
    public void toggleUpgradeMenu() {
        api.gameGUI.toggleUpgradeMenu()
    }


    /*******************************************************************************
     * Binding Categories
     ****************************************************************************** */
    EntityBindings getEntityBindings() {
        return entityBindings
    }

    PlayerBindings getPlayerBindings() {
        return playerBindings
    }

    Creator getCreator() {
        return creator
    }
}