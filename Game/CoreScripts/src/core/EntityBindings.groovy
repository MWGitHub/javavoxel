package core

import com.halboom.pgt.entityspatial.TransformComponent
import com.exploringlines.entitysystem.Entity
import com.exploringlines.entitysystem.EntitySet
import com.halboom.pgt.physics.filters.ColliderHistoryComponent
import com.halboom.pgt.physics.simple.components.AABBComponent
import com.halboom.pgt.physics.simple.components.CollisionComponent
import com.halboom.pgt.physics.simple.components.MovementComponent
import com.halboom.pgt.physics.simple.components.SpeedComponent
import com.halboom.pgt.physics.simple.shapes.Bounds
import com.jme3.math.Quaternion
import com.jme3.math.Vector3f
import com.jme3.scene.Spatial
import com.submu.pug.game.CollisionFilter
import com.submu.pug.game.objects.Player
import com.submu.pug.game.objects.components.*
import com.submu.pug.scripting.ScriptAPI

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/26/13
 * Time: 5:09 PM
 * Bindings for entities.
 */
class EntityBindings {
    /**
     * APi to use for the bindings.
     */
    private ScriptAPI api

    /**
     * Statistic types.
     */
    public enum StatType {
        HEALTH,
        MANA,
        ARMOR,
        DAMAGE,
        SPEED,
        COOLDOWN_REDUCTION,
        JUMP,
        RANGE,
        FORCE,
        WEIGHT
    }

    /**
     * Modification type for the stats.
     */
    public enum StatModType {
        ADD,
        MULTIPLY
    }

    /**
     * Initializes the bindings.
     * @param api the api to use for the bindings.
     */
    EntityBindings(ScriptAPI api) {
        this.api = api
    }

    /**
     * Retrieves the name of the entity.
     * @param entity the entity to retrieve from.
     * @return the name of the entity or null if none exists.
     */
    public String getName(Entity entity) {
        return entity.name
    }

    /**
     * Retrieves an entity by the name.
     * @param name the name of the entity.
     * @return the entity that matches the name or null if none found.
     */
    public Entity getByName(String name) {
        return api.entitySystem.getEntity(name)
    }

    /**
     * Retrieves the ID of the entity.
     * @param entity the entity to retrieve from.
     * @return the ID of the entity.
     */
    public long getId(Entity entity) {
        return entity.id
    }

    /**
     * Retrieves an entity by the ID.
     * @param id the id of the entity to retrieve.
     * @return the entity or null if none found.
     */
    public Entity getById(long id) {
        return api.entitySystem.getEntity(id)
    }

    /**
     * Retrieves the position of an actor.
     * @param entity the entity to retrieve the position from.
     * @return the position of the entity.
     */
    public Vector3f getPosition(Entity entity) {
        TransformComponent transformComponent = api.entitySystem.getComponent(entity, TransformComponent.class)
        if (transformComponent != null) {
            return new Vector3f(transformComponent.positionX, transformComponent.positionY, transformComponent.positionZ)
        }
        return null
    }

    /**
     * Sets the position of the entity.
     * @param entity the entity to set the position of.
     * @param x the x location.
     * @param y the y location.
     * @param z the z location.
     */
    public void setPosition(Entity entity, float x, float y, float z) {
        TransformComponent transformComponent = api.entitySystem.getComponent(entity, TransformComponent.class)
        if (transformComponent != null) {
            transformComponent.positionX = x
            transformComponent.positionY = y
            transformComponent.positionZ = z

            // Update bounds to prevent events at (0, 0, 0)
            AABBComponent aabbComponent = api.entitySystem.getComponent(entity, AABBComponent.class)
            if (aabbComponent != null) {
                aabbComponent.centerX = transformComponent.positionX
                aabbComponent.centerY = transformComponent.positionY
                aabbComponent.centerZ = transformComponent.positionZ
            }
        }
    }

    /**
     * @param entity the entity to get the speed of.
     * @return the speed or null if none exists.
     */
    public Vector3f getSpeed(Entity entity) {
        SpeedComponent speedComponent = api.entitySystem.getComponent(entity, SpeedComponent.class)
        if (speedComponent != null) {
            return new Vector3f(speedComponent.speedX, speedComponent.speedY, speedComponent.speedZ)
        }
        return null
    }

    /**
     * Sets the speed of an entity.
     * @param entity the entity to set the speed of.
     * @param x the x speed.
     * @param y the y speed.
     * @param z the z speed.
     */
    public void setSpeed(Entity entity, float x, float y, float z) {
        SpeedComponent speedComponent = api.entitySystem.getComponent(entity, SpeedComponent.class)
        if (speedComponent != null) {
            speedComponent.speedX = x
            speedComponent.speedY = y
            speedComponent.speedZ = z
        }
    }

    /**
     * Retrieves the acceleration for an entity.
     * @param entity the entity to get the acceleration of.
     * @return the acceleration of the entity or null if none if found.
     */
    public Vector3f getAcceleration(Entity entity) {
        SpeedComponent speedComponent = api.entitySystem.getComponent(entity, SpeedComponent.class)
        if (speedComponent != null) {
            return new Vector3f(speedComponent.accelX, speedComponent.accelY, speedComponent.accelZ)
        }
        return null
    }

    /**
     * Sets the acceleration of an entity.
     * @param entity the entity to set the acceleration of.
     * @param x the x acceleration.
     * @param y the y acceleration.
     * @param z the z acceleration.
     */
    public setAcceleration(Entity entity, float x, float y, float z) {
        SpeedComponent speedComponent = api.entitySystem.getComponent(entity, SpeedComponent.class)
        if (speedComponent != null) {
            speedComponent.accelX = x
            speedComponent.accelY = y
            speedComponent.accelZ = z
        }
    }

    /**
     * Moves an actor by the given amount.
     * @param entity the entity to move.
     * @param x the x amount to move.
     * @param y the y amount to move.
     * @param z the z amount to move.
     */
    public void move(Entity entity, float x, float y, float z) {
        MovementComponent movementComponent = api.entitySystem.getComponent(entity, MovementComponent.class)
        if (movementComponent != null) {
            movementComponent.moveX += x
            movementComponent.moveY += y
            movementComponent.moveZ += z
        }
    }

    /**
     * Retrieves the rotation of an entity.
     * @param entity the entity to retrieve the rotation of.
     * @return the rotation of the entity.
     */
    public Quaternion getRotation(Entity entity) {
        TransformComponent transformComponent = api.entitySystem.getComponent(entity, TransformComponent.class)
        if (transformComponent != null) {
            return new Quaternion(transformComponent.rotationX, transformComponent.rotationY, transformComponent.rotationZ,
                    transformComponent.rotationW)
        }
        return null
    }

    /**
     * Sets the rotation of an entity.
     * @param entity the entity to set the rotation of.
     * @param quaternion the quaternion rotation.
     */
    public void setRotation(Entity entity, Quaternion quaternion) {
        TransformComponent transformComponent = api.entitySystem.getComponent(entity, TransformComponent.class)
        if (transformComponent != null) {
            transformComponent.rotationX = quaternion.x
            transformComponent.rotationY = quaternion.y
            transformComponent.rotationZ = quaternion.z
            transformComponent.rotationW = quaternion.w
        }
    }

    /**
     * Sets the creator of an entity.
     * @param entity the entity to set the creator of.
     * @param creator the creator of the entity.
     */
    public void setCreator(Entity entity, Entity creator) {
        CreatorComponent creatorComponent = api.entitySystem.getComponent(entity, CreatorComponent.class)
        if (creatorComponent == null) {
            creatorComponent = new CreatorComponent()
        }
        creatorComponent.creator = creator;
        api.entitySystem.setComponent(entity, creatorComponent)
    }

    /**
     * Retrieves the creator of an entity.
     * @param entity the entity to retrieve the creator of.
     * @return the creator of the given entity or null if none exists.
     */
    public Entity getCreator(Entity entity) {
        CreatorComponent creatorComponent = api.entitySystem.getComponent(entity, CreatorComponent.class)
        if (creatorComponent != null) {
            return creatorComponent.creator
        }
        return null
    }

    /**
     * Retrieves the owner of an entity.
     * @param entity the entity to retrieve the owner from.
     * @return the owner of the entity or null if none found.
     */
    public Player getOwner(Entity entity) {
        if (entity == null)
            api.logError("Unable to get owner, entity does not exist.")
        OwnerComponent ownerComponent = api.entitySystem.getComponent(entity, OwnerComponent.class)
        if (ownerComponent == null) {
            api.logError("Unable to get owner, no owner component.")
            return null
        }
        Player player = api.playerAssigner.getPlayer(ownerComponent.playerID)
        if (player == null) {
            api.logError("Player with the ID " + ownerComponent.playerID + " does not exist.")
        }
        return player
    }

    /**
     * Retrieves the type of an actor.
     * @param entity the entity to retrieve from.
     * @return the type of the actor or null if none exists.
     */
    public String getActorType(Entity entity) {
        ActorComponent actorComponent = api.entitySystem.getComponent(entity, ActorComponent.class)
        if (actorComponent != null) {
            return actorComponent.type
        }
        return null
    }

    /**
     * Sets the actor type.
     * This is not recommended for entities created using data.
     * @param entity the entity to set the type of.
     * @param type the type to set.
     */
    public void setActorType(Entity entity, String type) {
        ActorComponent actorComponent = api.entitySystem.getComponent(entity, ActorComponent.class)
        if (actorComponent != null) {
            actorComponent.type = type
        }
    }

    /**
     * Retrieves a list of actors by the type.
     * @param type the type of actor.
     * @return the actors that match the type.
     */
    public List<Entity> getActorsByType(String type) {
        EntitySet set = api.entitySystem.getEntities(ActorComponent.class)
        List<Entity> actors = new LinkedList<>()
        for (Entity entity in set.getEntities()) {
            ActorComponent actorComponent = api.entitySystem.getComponent(entity, ActorComponent.class)
            if (actorComponent.type == type) {
                actors.add(entity)
            }
        }
        return actors
    }

    /**
     * Retrieves the category of the actor.
     * @param entity the entity to retrieve the category from.
     * @return the category of the actor or null if none found.
     */
    public String getActorCategory(Entity entity) {
        ActorComponent actorComponent = api.entitySystem.getComponent(entity, ActorComponent.class)
        if (actorComponent != null) {
            return actorComponent.category
        }
        return null
    }

    /**
     * Sets the category for the actor.
     * @param entity the entity to set the category of.
     * @param category the category to set.
     */
    public void setActorCategory(Entity entity, String category) {
        ActorComponent actorComponent = api.entitySystem.getComponent(entity, ActorComponent.class)
        if (actorComponent != null) {
            actorComponent.category = category
        }
    }

    /**
     * Copies statistics from one entity to another.
     * @param original the original entity to copy the stats from.
     * @param copy the entity to copy the stats to.
     */
    public void copyStats(Entity original, Entity copy) {
        StatComponent originalStats = api.entitySystem.getComponent(original, StatComponent.class)
        if (originalStats == null)
            return
        api.entitySystem.setComponent(copy, originalStats.copy())
    }

    /**
     * Retrieves the health of an entity.
     * @param entity the entity to retrieve the health of.
     * @return the health of the entity or 0 if none available.
     */
    public float getHealth(Entity entity) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            return statComponent.health
        }
        return 0.0f
    }

    /**
     * Set the health of the entity.
     * @param entity the entity to set the health of.
     * @param amount the amount to set the health.
     */
    public void setHealth(Entity entity, float amount) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            statComponent.health = amount
            api.entitySystem.setComponent(entity, statComponent)
        }
    }

    /**
     * Set the maximum health of the entity.
     * @param entity the entity to set the maximum health of.
     * @param amount the amount to set the maximum health.
     */
    public void setMaxHealth(Entity entity, float amount) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            statComponent.maxHealth = amount
            api.entitySystem.setComponent(entity, statComponent)
        }
    }

    /**
     * Adds health to an entity.
     * @param entity the entity to add health to.
     * @param amount the amount of health to add, negative for subtract.
     */
    public void addHealth(Entity entity, float amount) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            statComponent.health += amount
            api.entitySystem.setComponent(entity, statComponent)
        }
    }

    /**
     * Retrieves the mana of an entity.
     * @param entity the entity to get the mana of.
     * @return the mana of the entity or zero if none found.
     */
    public float getMana(Entity entity) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            return statComponent.mana
        }
        return 0.0f
    }

    /**
     * Sets the amount of mana for the entity.
     * @param Entity the entity to set the mana of.
     * @param amount the amount of mana to set.
     */
    public void setMana(Entity entity, float amount) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            statComponent.mana = amount
            api.entitySystem.setComponent(entity, statComponent)
        }
    }

    /**
     * Adds mana to an entity.
     * @param entity the entity to add mana to.
     * @param amount the amount of mana to add, negative for subtract.
     */
    public void addMana(Entity entity, float amount) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent != null) {
            statComponent.mana += amount
            api.entitySystem.setComponent(entity, statComponent)
        }
    }

    /**
     * Retrieves the damage of an entity.
     * @param entity the entity to retrieve the damage of.
     * @return the damage of the entity or zero if none found.
     */
    public float getDamage(Entity entity) {
        BuffedStatComponent buffedComponent = api.entitySystem.getComponent(entity, BuffedStatComponent.class)
        if (buffedComponent != null) {
            return buffedComponent.damage
        }
        return 0.0f
    }

    /**
     * Set the damage of an entity.
     * @param entity the entity to set the damage of.
     * @param damage the damage to set.
     */
    public void setDamage(Entity entity, float damage) {
        StatComponent statComponent = api.entitySystem.getComponent(entity, StatComponent.class)
        if (statComponent == null) {
            statComponent = new StatComponent()
            api.entitySystem.setComponent(entity, statComponent)
        }
        statComponent.damage = damage
    }

    /**
     * Adds experience to an entity.
     * @param entity the entity to give experience to.
     * @param experience the amount of experience to give.
     */
    public void addExperience(Entity entity, float experience) {
        ExperienceComponent experienceComponent = api.entitySystem.getComponent(entity, ExperienceComponent.class)
        if (experienceComponent == null)
            return
        experienceComponent.experience += experience
        api.entitySystem.setComponent(entity, experienceComponent)
    }

    /**
     * Damages an entity.
     * @param attacker the attacker that deals the damage.
     * @param target the target to deal the damage to.
     */
    public void damage(Entity attacker, Entity target) {
        StatComponent attackerStats = api.entitySystem.getComponent(attacker, StatComponent.class)

        if (attackerStats == null)
            return
        StatComponent targetStats = api.entitySystem.getComponent(target, StatComponent.class)
        if (targetStats == null)
            return
        BuffedStatComponent attackerBuffs = api.entitySystem.getComponent(attacker, BuffedStatComponent.class);
        if (attackerBuffs == null)
            return
        BuffedStatComponent targetBuffs = api.entitySystem.getComponent(target, BuffedStatComponent.class);
        if (targetBuffs == null)
            return
        float finalDamage = attackerBuffs.damage - targetBuffs.armor
        if (finalDamage <= 0) {
            finalDamage = 1
        }
        targetStats.health -= finalDamage
        api.entitySystem.setComponent(target, targetStats)
        // Entity kills another entity.
        // TODO: Add callback for killing.
        if (targetStats.health <= 0) {
            Entity creator = getCreator(attacker)
            if (creator != null) {
                // Add experience to the killer.
                ExperienceComponent attackerExp = api.entitySystem.getComponent(creator, ExperienceComponent.class)
                if (attackerExp != null) {
                    ExperienceComponent targetExp = api.entitySystem.getComponent(target, ExperienceComponent.class)
                    if (targetExp != null) {
                        addExperience(creator, targetExp.experienceGivenPerLevel * targetExp.level)
                    }
                }
            }
        }

        // Weightless objects are not movable.
        if (targetStats.weight <= 0) {
            return
        }

        // Apply pushing force if applicable.
        Vector3f acceleration = getAcceleration(target)
        if (acceleration == null)
            return
        // Handle pushing for non explosions.
        Vector3f speed = getSpeed(attacker)
        float force = attackerBuffs.force * attackerBuffs.weight / targetBuffs.weight
        if (speed != null) {
            speed.normalizeLocal()
            if (isEntityOnFloor(target)) {
                speed.y = 0.1f
            }
            acceleration.addLocal((float) (speed.x * force), (float) speed.y * force, (float) speed.z * force)
        } else {
            // Explosions do not have speed so accelerate based on direction difference.
            Vector3f attackerCenter = getCenter(attacker)
            if (attackerCenter == null)
                return
            Vector3f targetCenter = getCenter(target)
            if (targetCenter == null)
                return
            Vector3f direction = targetCenter.subtractLocal(attackerCenter).normalizeLocal()
            acceleration.addLocal(
                    (float) (direction.x * force), (float) direction.y * force, (float) direction.z * force)
        }

        setAcceleration(target, acceleration.x, acceleration.y, acceleration.z)
    }

    /**
     * Checks if an entity has collided with the target before.
     * @param entity the entity to check for collision history.
     * @param target the target to check with.
     * @return true if the entity has collided with the target before.
     */
    public boolean hasCollidedWithBefore(Entity entity, Entity target) {
        ColliderHistoryComponent history = api.entitySystem.getComponent(entity, ColliderHistoryComponent.class)
        if (history != null) {
            return history.colliders.contains(target)
        }

        return false
    }

    /**
     * Retrieves the jumping power of an entity.
     * @param entity the entity to retrieve the jumping power of.
     * @return the jumping power of the entity or zero if none found.
     */
    public float getJumpPower(Entity entity) {
        BuffedStatComponent buffedStat = api.entitySystem.getComponent(entity, BuffedStatComponent.class)
        if (buffedStat != null) {
            return buffedStat.jump
        }

        return 0.0f
    }

    /**
     * Move to a specific point.
     * @param entity the entity to move.
     * @param x the x point.
     * @param y the y point.
     * @param z the z point.
     */
    public void issueMoveTo(Entity entity, float x, float y, float z) {
        MoveCommandComponent moveCommandComponent = new MoveCommandComponent()
        moveCommandComponent.destinationX = x
        moveCommandComponent.destinationY = y
        moveCommandComponent.destinationZ = z
        api.entitySystem.setComponent(entity, moveCommandComponent)
    }

    /**
     * Makes an entity follow an entity.
     * @param entity the entity to move.
     * @param target the entity target.
     */
    public void followEntity(Entity entity, Entity target) {
        MoveCommandComponent moveCommandComponent = new MoveCommandComponent()
        moveCommandComponent.entity = target
        api.entitySystem.setComponent(entity, moveCommandComponent)
    }

    /**
     * Retrieves an attachment point based on the name of the point.
     * @param entity the entity to retrieve the attachment point from.
     * @param name the name of the attachment point.
     * @return the attachment point or the entity's current location if none is found.
     */
    public Vector3f getAttachmentPoint(Entity entity, String name) {
        Spatial spatial = api.spatialSystem.getSpatial(entity)
        if (spatial != null && spatial instanceof com.jme3.scene.Node) {
            com.jme3.scene.Node node = (com.jme3.scene.Node) spatial
            Spatial root = node.getChild(api.modelData.dataNodes.attachRootNode)
            if (root != null && root instanceof com.jme3.scene.Node) {
                Spatial point = root.getChild(name)
                if (point != null) {
                    Vector3f translation = point.getWorldTranslation()
                    return new Vector3f(translation.x, translation.y, translation.z)
                }
            }
        }
        return getPosition(entity)
    }

    /**
     * Retrieves the center point of an entity.
     * @param entity the entity to retrieve the center of.
     * @return the center point of the entity or the position if none available.
     */
    public Vector3f getCenter(Entity entity) {
        Bounds bounds = api.boundsSystem.getBounds(entity)
        if (bounds == null) {
            return getPosition(entity)
        }
        return bounds.bounds.center.clone()
    }

    /**
     * Sets the life time of the entity.
     * @param entity the entity to set the life time of.
     * @param duration the time in seconds before the entity is removed.
     */
    public void setTimedLife(Entity entity, duration) {
        TimedLifeComponent timedLifeComponent = api.entitySystem.getComponent(entity, TimedLifeComponent.class)
        if (timedLifeComponent == null) {
            timedLifeComponent = new TimedLifeComponent()
        }
        timedLifeComponent.lifeTime = duration
        api.entitySystem.setComponent(entity, timedLifeComponent)
    }

    /**
     * Checks if an entity is touching the floor or any other object's top.
     * @param entity the entity to check.
     * @return true if touching the floor or if floor checks do not exist.
     */
    public boolean isEntityOnFloor(Entity entity) {
        CollisionComponent boundsCollisionComponent = api.entitySystem.getComponent(entity, CollisionComponent.class)
        return boundsCollisionComponent == null || boundsCollisionComponent.isOnFloor
    }

    /**
     * Checks if an entity is within the actor group.
     * @param entity the entity to check.
     * @return true if within the actor group, false otherwise.
     */
    public boolean isActor(Entity entity) {
        CollisionComponent boundsCollisionComponent = api.entitySystem.getComponent(entity, CollisionComponent.class)
        if ((boundsCollisionComponent.groups & CollisionFilter.COLLISION_UNIT) != 0) {
            return true
        }
        return false
    }

    /**
     * Checks if an entity is a projectile.
     * @param entity the entity to check.
     * @return true if within the projectile group, false otherwise.
     */
    public boolean isProjectile(Entity entity) {
        CollisionComponent boundsCollisionComponent = api.entitySystem.getComponent(entity, CollisionComponent.class)
        if ((boundsCollisionComponent.groups & CollisionFilter.COLLISION_PROJECTILE) != 0) {
            return true
        }
        return false
    }

    /**
     * Buffs a certain statistic.
     * @param entity the entity to buff.
     * @param statType the stat to buff.
     * @param statModType the type of buff.
     * @param amount the amount to buff.
     */
    public void buffStatistic(Entity entity, StatType statType, StatModType statModType, float amount) {
        if (entity == null)
            return
        BuffedStatComponent buffedStat = api.entitySystem.getComponent(entity, BuffedStatComponent)
        if (buffedStat == null)
            return
        boolean hasBuffed = true
        switch (statType) {
            case StatType.HEALTH:
                if (statModType == StatModType.ADD)
                    buffedStat.addMaxHealth += amount
                else
                    buffedStat.multMaxHealth += amount
                break
            case StatType.MANA:
                if (statModType == StatModType.ADD)
                    buffedStat.addMaxMana += amount
                else
                    buffedStat.multMaxMana += amount
                break
            case StatType.ARMOR:
                if (statModType == StatModType.ADD)
                    buffedStat.addArmor += amount
                else
                    buffedStat.multArmor += amount
                break
            case StatType.COOLDOWN_REDUCTION:
                buffedStat.cooldownReduction += amount
                break
            case StatType.DAMAGE:
                if (statModType == StatModType.ADD)
                    buffedStat.addDamage += amount
                else
                    buffedStat.multDamage += amount
                break
            case StatType.FORCE:
                if (statModType == StatModType.ADD)
                    buffedStat.addForce += amount
                else
                    buffedStat.multForce += amount
                break
            case StatType.JUMP:
                if (statModType == StatModType.ADD)
                    buffedStat.addJump += amount
                else
                    buffedStat.multJump += amount
                break
            case StatType.RANGE:
                if (statModType == StatModType.ADD)
                    buffedStat.addRange += amount
                else
                    buffedStat.multRange += amount
                break
            case StatType.SPEED:
                if (statModType == StatModType.ADD)
                    buffedStat.addSpeed += amount
                else
                    buffedStat.multSpeed += amount
                break
            case StatType.WEIGHT:
                if (statModType == StatModType.ADD)
                    buffedStat.addWeight += amount
                else
                    buffedStat.multWeight += amount
                break
            default:
                hasBuffed = false
        }
        if (hasBuffed)
            api.entitySystem.setComponent(entity, buffedStat)
    }

    /**********************************************
     * Entity Abilities
     **********************************************/

    /**
     * Retrieves the ability index matching the first occurance of the ability name.
     * @param entity the entity to retrieve the index of.
     * @param ability the internal name of the ability.
     * @return the ability index or -1 if none is found.
     */
    public int getAbilityIndex(Entity entity, String ability) {
        AbilityComponent abilityComponent = api.entitySystem.getComponent(entity, AbilityComponent.class)
        if (abilityComponent == null) return -1

        for (int i = 0; i < abilityComponent.abilities.size(); i++) {
            AbilityComponent.Ability element = abilityComponent.abilities.get(i)
            if (element.internalName == ability) {
                return i
            }
        }

        return -1
    }

    /**
     * Retrieves the name of an ability given the index.
     * @param entity the entity to get the ability of.
     * @param abilityIndex the index of the ability within the component.
     * @return the ability name or null if none found.
     */
    public String getAbilityName(Entity entity, int abilityIndex) {
        AbilityComponent abilityComponent = api.entitySystem.getComponent(entity, AbilityComponent.class)
        if (abilityComponent != null) {
            if (abilityIndex < abilityComponent.abilities.size()) {
                return abilityComponent.abilities.get(abilityIndex)
            }
        }
        return null
    }

    /**
     * Makes an entity cast an ability.
     * @param entity the entity to cast the ability.
     * @param abilityIndex the index of the ability to cast.
     * @param target the target of the ability.
     */
    public void cast(Entity entity, int abilityIndex, Vector3f target) {
        if (entity == null || target == null) return

        AbilityCommandComponent abilityCommandComponent = api.entitySystem.getComponent(entity, AbilityCommandComponent.class)
        if (abilityCommandComponent == null) {
            abilityCommandComponent = new AbilityCommandComponent()
            api.entitySystem.setComponent(entity, abilityCommandComponent)
        }
        abilityCommandComponent.castedAbilities.add(abilityIndex)
        abilityCommandComponent.targets.add(target)
    }

    /**
     * Upgrades the ability to the given level.
     * @param entity the entity to upgrade the ability of.
     * @param abilityIndex the index of the upgrading ability.
     * @param upgrade the name of the upgrade.
     * @param level the level to upgrade the ability to.
     */
    public void upgradeAbility(Entity entity, int abilityIndex, String upgrade, int level) {
        api.abilitySystem.upgradeAbility(entity, abilityIndex, upgrade, level)
    }

    /**
     * Retrieves the upgrade level of an ability.
     * @param entity the entity to get the upgrade level of.
     * @param abilityIndex the ability to get the upgrade level of.
     * @param upgrade the internal upgrade name.
     * @return the level of the upgrade or 0 if none found or learned.
     */
    public int getAbilityUpgradeLevel(Entity entity, int abilityIndex, String upgrade) {
        return api.abilitySystem.getUpgradeLevel(entity, abilityIndex, upgrade)
    }

    /**
     * Retrieves the cooldown of an entity's ability.
     * @param entity the entity to retrieve the cooldown of.
     * @param abilityIndex the index of the ability.
     * @return the cooldown of the ability.
     */
    public float getAbilityCooldown(Entity entity, int abilityIndex) {
        AbilityComponent abilityComponent = api.entitySystem.getComponent(entity, AbilityComponent.class)
        if (abilityComponent == null) {
            return 0
        }
        if (abilityIndex >= abilityComponent.abilities.size()) {
            return 0
        }
        return abilityComponent.abilities.get(abilityIndex).cooldown
    }

    /**
     * Sets the cooldown of an ability.
     * @param entity the entity to set the cooldown of.
     * @param abilityIndex the index of the ability.
     * @param cooldown the cooldown to set.
     */
    public void setAbilityCooldown(Entity entity, int abilityIndex, float cooldown) {
        AbilityComponent abilityComponent = api.entitySystem.getComponent(entity, AbilityComponent.class)
        if (abilityComponent == null) {
            return
        }
        if (abilityIndex >= abilityComponent.abilities.size()) {
            return
        }
        AbilityComponent.Ability element = abilityComponent.abilities.get(abilityIndex)
        element.cooldown = cooldown
    }

    /**
     * Sets a target for the entity.
     * @param entity the entity to set the target for.
     * @param target the targeted entity.
     */
    public void setTarget(Entity entity, Entity target) {
        TargetComponent targetComponent = api.entitySystem.getComponent(entity, TargetComponent.class)
        if (targetComponent == null)
            targetComponent = new TargetComponent()
        targetComponent.target = target
        api.entitySystem.setComponent(entity, targetComponent)
    }

    /**
     * Retrieves the target for an entity.
     * @param entity the entity to get the target of.
     * @return the target of the entity.
     */
    public Entity getTarget(Entity entity) {
        TargetComponent targetComponent = api.entitySystem.getComponent(entity, TargetComponent.class)
        if (targetComponent == null)
            return null
        return targetComponent.target
    }

    /**********************************************
     * Entity Experience
     **********************************************/

    /**
     * Sets the amount of skill points an entity has.
     * @param entity the entity to give the skill points to.
     * @param amount the amount of skill points to give.
     */
    public void setSkillPoints(Entity entity, int amount) {
        ExperienceComponent experienceComponent = api.entitySystem.getComponent(entity, ExperienceComponent.class)
        if (experienceComponent == null) return
        experienceComponent.skillPoints = amount
        api.entitySystem.setComponent(entity, experienceComponent)
    }

    /**********************************************
     * Entity Items
     **********************************************/

    /**
     * Equips an item.
     * @param entity the entity to give the item to.
     * @param item the item to equip.
     */
    public void pickUpItem(Entity entity, Entity item) {
        api.itemSystem.pickUpItem(entity, item)
    }

    /**
     * Drops an item.
     * @param entity the entity to drop an item from.
     * @param item the item to drop.
     */
    public void dropItem(Entity entity, Entity item) {
        api.itemSystem.dropItem(entity, item)
    }

}
