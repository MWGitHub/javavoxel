package core

import com.exploringlines.entitysystem.Entity
import com.halboom.pgt.physics.simple.CollisionInformation
import com.halboom.pgt.physics.simple.shapes.Bounds
import com.jme3.math.Vector3f
import com.submu.pug.game.objects.Player

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/14/13
 * Time: 3:55 PM
 * Provides interfaces for event functions.
 */
abstract class EventFunctions {
    /**
     * Used for assigning function IDs.
     */
    private static int counter = 0;

    /**
     * ID to use when searching for functions.
     */
    private int id;

    /**
     * Name of the event function.
     */
    private String name;

    /**
     * Initializes the function.
     */
    EventFunctions(String name = null) {
        id = counter;
        counter++;
        this.name = name;
    }

    // Input callbacks.
    public void onAnalogKeyPressed(String pressedKey, float pressedAmount, Player player, boolean isGUIActive) {}
    public void onKeyPressed(String pressedKey, Player player, boolean isGUIActive) {}
    public void onKeyReleased(String releasedKey, Player player, boolean isGUIActive) {}
    // Ability callbacks
    public void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {}
    public void onAbilityUpgrade(Entity entity, int abilityIndex, String upgrade, int level) {}
    // Time callbacks
    public void onMapInitialization() {}
    public void onPeriodic(float timeElapsed) {}
    // Collision callbacks
    public void onTileCollision(CollisionInformation collisionInformation) {}
    public void onBoundsCollision(CollisionInformation collisionInformation) {}
    // Region callbacks
    public void onRegionEvent(String name, Bounds bounds, Entity entity) {}
    // AI callbacks
    public void onCombat(Entity entity, Entity target, String script) {}
    public void onLeaveCombat(Entity entity) {}
    // Item callbacks
    public void onItemPickUpOrDrop(Entity holder, Entity item) {}

    /**
     * Generic function to call the trigger without parameters.
     * Used for allowing a trigger to wait.
     */
    public void call() {}

    /**
     * @return the ID of the function.
     */
    int getId() {
        return id
    }

    /**
     * @return the name of the function.
     */
    String getName() {
        return name
    }
}
