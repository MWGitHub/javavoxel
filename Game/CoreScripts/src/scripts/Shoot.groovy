package scripts

import com.exploringlines.entitysystem.Entity
import com.halboom.pgt.physics.simple.CollisionInformation
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import core.EntityBindings
import core.EventFunctions
import core.ScriptBindings

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/2/13
 * Time: 10:23 PM
 * Scripts that handle shooting.
 */
class Shoot {
    /**
     * Bindings to use for the script.
     */
    private ScriptBindings bindings;

    /**
     * Initialize the script.
     * @param scriptBindings the bindings to use.
     */
    Shoot(ScriptBindings bindings) {
        this.bindings = bindings

        // Create bullet
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_CAST, new EventFunctions() {
            @Override
            void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {
                if (internalName == "Shoot" || internalName == "TowerShoot") {
                    Entity bullet = bindings.creator.createProjectile(
                            "Bullet",
                            bindings.getAbilityUserData("Shoot", "model"),
                            bindings.entityBindings.getOwner(entity).id, entity, "Bullet")
                    Vector3f position = bindings.getAbilitySpawn(entity, "Shoot")
                    bindings.entityBindings.setPosition(bullet, position.x, position.y, position.z)
                    Vector3f direction = target.normalize()
                    // Target the entity if there is one.
                    Entity targetEntity = bindings.entityBindings.getTarget(entity)
                    if (targetEntity != null) {
                        direction = bindings.getDirectionToPoint(
                                bindings.getAbilitySpawn(entity, bindings.getAbilityName(entity, 0)),
                                bindings.entityBindings.getCenter(targetEntity)).normalizeLocal()
                    }
                    Quaternion quaternion = new Quaternion()
                    quaternion.lookAt(direction, Vector3f.UNIT_Y)
                    bindings.entityBindings.setRotation(bullet, quaternion)
                    bindings.entityBindings.setSpeed(bullet,
                            (float) 20f * direction.x, (float) 20f * direction.y, (float) 20f * direction.z)
                    // If the entity has the double shot upgrade then shoot two bullets
                    if (bindings.entityBindings.getAbilityUpgradeLevel(entity, index, "doubleShot") > 0) {
                        bullet = bindings.creator.createProjectile(
                                "Bullet",
                                bindings.getAbilityUserData("Shoot", "model"),
                                bindings.entityBindings.getOwner(entity).id, entity, "Bullet")
                        position = bindings.getAbilitySpawn(entity, "Shoot")
                        bindings.entityBindings.setPosition(bullet, position.x, position.y, position.z)
                        direction = target.add(0.1f, 0, 0).normalize()
                        quaternion = new Quaternion()
                        quaternion.lookAt(direction, Vector3f.UNIT_Y)
                        bindings.entityBindings.setRotation(bullet, quaternion)
                        bindings.entityBindings.setSpeed(bullet,
                                (float) 20f * direction.x, (float) 20f * direction.y, (float) 20f * direction.z)
                    }
                }
            }
        })

        // Bullet collision
        bindings.addEventBinding(ScriptBindings.EVENT_TILE_COLLIDED, new EventFunctions() {
            @Override
            void onTileCollision(CollisionInformation collisionInformation) {
                Entity entity = collisionInformation.collider
                if (bindings.entityBindings.getActorCategory(entity) == "Bullet") {
                    bindings.creator.removeEntity(entity)
                }
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_SENSOR_COLLIDED, new EventFunctions() {
            @Override
            void onBoundsCollision(CollisionInformation collisionInformation) {
                Entity bullet = collisionInformation.collider
                Entity target = collisionInformation.collidee
                if (bindings.entityBindings.getActorCategory(bullet) == "Bullet") {
                    bindings.entityBindings.damage(bullet, target)
                    bindings.creator.removeEntity(bullet)
                }
            }
        })

        // Start casting rocket
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_CAST_BEGIN, new EventFunctions() {
            @Override
            void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {
                if (internalName == "Rocket") {
                    bindings.entityBindings.buffStatistic(entity, EntityBindings.StatType.SPEED, EntityBindings.StatModType.MULTIPLY, -0.5f)
                }
            }
        })
        // Create rocket
        bindings.addEventBinding(ScriptBindings.EVENT_ABILITY_CAST, new EventFunctions() {
            @Override
            void onAbilityCast(Entity entity, int index, String internalName, Vector3f target) {
                if (internalName == "Rocket") {
                    bindings.entityBindings.buffStatistic(entity, EntityBindings.StatType.SPEED, EntityBindings.StatModType.MULTIPLY, 0.5f)
                    Entity bullet = bindings.creator.createProjectile("Bullet",
                            null,
                            bindings.entityBindings.getOwner(entity).id, entity, "Rocket")
                    Vector3f position = bindings.getAbilitySpawn(entity, "Rocket")
                    bindings.entityBindings.setPosition(bullet, position.x, position.y, position.z)
                    Vector3f direction = target.normalize()
                    Quaternion quaternion = new Quaternion()
                    quaternion.lookAt(direction, Vector3f.UNIT_Y)
                    bindings.entityBindings.setRotation(bullet, quaternion)
                    bindings.entityBindings.setSpeed(bullet,
                            (float) 20f * direction.x, (float) 20f * direction.y, (float) 20f * direction.z)
                }
            }
        })

        // Rocket collision
        bindings.addEventBinding(ScriptBindings.EVENT_TILE_COLLIDED, new EventFunctions() {
            @Override
            void onTileCollision(CollisionInformation collisionInformation) {
                Entity entity = collisionInformation.collider
                if (bindings.entityBindings.getActorCategory(entity) == "Rocket") {
                    Entity explosion = bindings.creator.createProjectile("Explosion", null,
                            bindings.entityBindings.getOwner(entity).id,
                        bindings.entityBindings.getCreator(entity), "Explosion")
                    Vector3f position = collisionInformation.collisionPoint
                    if (position.x != 0 || position.y != 0 || position.z != 0) {
                        position = collisionInformation.collisionPoint
                    } else {
                        position = bindings.entityBindings.getPosition(entity)
                    }
                    bindings.entityBindings.setPosition(explosion, position.x, position.y, position.z)
                    bindings.creator.removeEntity(entity)
                }
            }
        })
        bindings.addEventBinding(ScriptBindings.EVENT_SENSOR_COLLIDED, new EventFunctions() {
            @Override
            void onBoundsCollision(CollisionInformation collisionInformation) {
                Entity rocket = collisionInformation.collider
                if (bindings.entityBindings.getActorCategory(rocket) == "Rocket") {
                    Entity explosion = bindings.creator.createProjectile("Explosion", null,
                            bindings.entityBindings.getOwner(rocket).id,
                            bindings.entityBindings.getCreator(rocket), "Explosion")
                    Vector3f position = bindings.entityBindings.getPosition(rocket)
                    bindings.entityBindings.setPosition(explosion, position.x, position.y, position.z)
                    bindings.creator.removeEntity(rocket)
                }
            }
        })
        // Create the explosion
        bindings.addEventBinding(ScriptBindings.EVENT_SENSOR_COLLIDED, new EventFunctions() {
            @Override
            void onBoundsCollision(CollisionInformation collisionInformation) {
                Entity explosion = collisionInformation.collider
                Entity target = collisionInformation.collidee
                if (bindings.entityBindings.getActorCategory(explosion) != "Explosion") {
                    return
                }
                if (!bindings.entityBindings.hasCollidedWithBefore(explosion, target)) {
                    bindings.entityBindings.damage(explosion, target)
                }
            }
        })
    }
}
