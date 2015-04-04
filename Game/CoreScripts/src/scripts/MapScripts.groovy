package scripts

import com.exploringlines.entitysystem.Entity
import com.halboom.pgt.physics.simple.shapes.Bounds
import com.jme3.math.Vector3f
import com.submu.pug.game.objects.Player
import core.EventFunctions
import core.ScriptBindings

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/14/13
 * Time: 3:14 PM
 * Map specific scripts.
 * Class that can be used with a low chance of breaking compatibility are:
 *  Vector3f (jme)
 *  Quaternion (jme)
 *  Entity
 */
class MapScripts {
    private ScriptBindings bindings;

    /**
     * IDs of the non local players.
     */
    public static final int ALLY_ID = 2,
                            ENEMY_ID = 3

    /**
     * Headquarters for both sides.
     */
    private Entity alliedHQ, enemyHQ

    /**
     * Flag to check if the mobs should spawn.
     */
    private boolean isSpawning = true

    /**
     * Last spawning time.
     */
    private float lastSpawnTime = 0

    /**
     * Period to spawn each wave.
     */
    private float spawnPeriod = 5

    MapScripts(ScriptBindings bindings) {
        this.bindings = bindings

        Shoot shoot = new Shoot(bindings)
        Jump jump = new Jump(bindings)

        /**
         * Time based scripts.
         */
        bindings.addEventBinding(ScriptBindings.EVENT_MAP_INITIALIZATION, new EventFunctions() {
            @Override
            void onMapInitialization() {
                Entity entity = bindings.entityBindings.getByName("Player1")
                bindings.playerBindings.setControlledActor(entity, bindings.playerBindings.getLocalPlayer())
                alliedHQ = bindings.entityBindings.getByName("Allied HQ")
                enemyHQ = bindings.entityBindings.getByName("Enemy HQ")
                Entity item = bindings.entityBindings.getByName("Sword1")
                bindings.entityBindings.pickUpItem(entity, item)
                bindings.entityBindings.dropItem(entity, item)
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_PERIODIC, new EventFunctions() {
            @Override
            void onPeriodic(float timeElapsed) {
                if (!isSpawning) {
                    return
                }
                if (timeElapsed - lastSpawnTime > spawnPeriod) {
                    lastSpawnTime = timeElapsed
                    Vector3f target = bindings.entityBindings.getPosition(enemyHQ)
                    Vector3f location = bindings.getRegionCenter("AlliedSpawn1")
                    // Spawn normal allied units
                    Entity spawn = bindings.creator.createActor("Enemy", ALLY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    location = bindings.getRegionCenter("AlliedSpawn2")
                    spawn = bindings.creator.createActor("Enemy", ALLY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    location = bindings.getRegionCenter("AlliedSpawn3")
                    spawn = bindings.creator.createActor("Enemy", ALLY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    // Spawn normal enemy units
                    target = bindings.entityBindings.getPosition(alliedHQ)
                    location = bindings.getRegionCenter("EnemySpawn1")
                    spawn = bindings.creator.createActor("Enemy", ENEMY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    location = bindings.getRegionCenter("EnemySpawn2")
                    spawn = bindings.creator.createActor("Enemy", ENEMY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    location = bindings.getRegionCenter("EnemySpawn3")
                    spawn = bindings.creator.createActor("Enemy", ENEMY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)

                    // Spawn special enemy units
                    location = bindings.getRegionCenter("EnemySpawn2")
                    spawn = bindings.creator.createActor("HeavyEnemy", ENEMY_ID)
                    bindings.entityBindings.setPosition(spawn, location.x, location.y, location.z)
                    bindings.entityBindings.issueMoveTo(spawn, target.x, target.y, target.z)
                }
            }
        })
        /**
         * Upgrade scripts.
         */
        // Increase the cooldown when cooldown ability is learned.
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_UPGRADE, new EventFunctions() {
            @Override
            void onAbilityUpgrade(Entity entity, int abilityIndex, String upgrade, int level) {
                if (upgrade == "longCooldown") {
                    float cooldown = bindings.entityBindings.getAbilityCooldown(entity, abilityIndex)
                    bindings.entityBindings.setAbilityCooldown(entity, abilityIndex, (float) cooldown / 2)
                }
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_UPGRADE, new EventFunctions() {
            @Override
            void onAbilityUpgrade(Entity entity, int abilityIndex, String upgrade, int level) {
                if (upgrade == "healthUp") {
                    bindings.entityBindings.setMaxHealth(entity, 10)
                    bindings.entityBindings.setHealth(entity, 99999999)
                }
            }
        })
        /**
         * Map scripts.
         */

        /**
         * AI scripts.
         */
        // AI
        bindings.addEventBinding(ScriptBindings.EVENT_AI_COMBAT, new EventFunctions() {
            @Override
            void onCombat(Entity entity, Entity target, String script) {
                if (script == "Shoot") {
                    Vector3f direction = bindings.getDirectionToPoint(
                        bindings.getAbilitySpawn(entity, bindings.getAbilityName(entity, 0)),
                        bindings.entityBindings.getCenter(target))
                    bindings.entityBindings.cast(entity, 0, direction)
                    bindings.entityBindings.followEntity(entity, null)
                    bindings.entityBindings.setTarget(entity, target)
                }
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_AI_LEAVE_COMBAT, new EventFunctions() {
            @Override
            void onLeaveCombat(Entity entity) {
                if (bindings.entityBindings.getOwner(entity) == bindings.playerBindings.getPlayer(ALLY_ID)) {
                    Vector3f target = bindings.entityBindings.getPosition(enemyHQ)
                    bindings.entityBindings.issueMoveTo(entity, target.x, target.y, target.z)
                } else if (bindings.entityBindings.getOwner(entity) == bindings.playerBindings.getPlayer(ENEMY_ID)) {
                    Vector3f target = bindings.entityBindings.getPosition(alliedHQ)
                    bindings.entityBindings.issueMoveTo(entity, target.x, target.y, target.z)
                }
            }
        })
        // Boss Spawning Check
        bindings.addEventBinding(ScriptBindings.EVENT_REGION_LEAVE, new EventFunctions() {
            @Override
            void onRegionEvent(String name, Bounds bounds, Entity entity) {
                if (name == "ArenaArea" && (entity == enemyHQ || entity == alliedHQ)) {
                    isSpawning = false
                }
            }
        })

        /**
         * Upgrade menu opening.
         */
        // Upgrade Menu
        bindings.addEventBinding(ScriptBindings.EVENT_KEY_PRESSED, new EventFunctions() {
            @Override
            void onKeyPressed(String pressedKey, Player player, boolean isGUIActive) {
                if (pressedKey == ScriptBindings.ACTION_USE) {
                    bindings.toggleUpgradeMenu()
                }
            }
        })

        // Item stuff
        bindings.addEventBinding(ScriptBindings.EVENT_ITEM_PICK_UP, new EventFunctions() {
            @Override
            void onItemPickUpOrDrop(Entity holder, Entity item) {
                println "GOT"
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_ITEM_DROP, new EventFunctions() {
            @Override
            void onItemPickUpOrDrop(Entity holder, Entity item) {
                println "DROP"
            }
        })
    }
}
