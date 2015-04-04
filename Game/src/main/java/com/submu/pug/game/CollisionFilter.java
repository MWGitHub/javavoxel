package com.submu.pug.game;

import com.exploringlines.entitysystem.Entity;
import com.exploringlines.entitysystem.EntitySystem;
import com.halboom.pgt.physics.filters.Filter;
import com.halboom.pgt.physics.simple.shapes.Bounds;
import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.components.ActorComponent;
import com.submu.pug.game.objects.components.CreatorComponent;
import com.submu.pug.game.objects.components.FilterFlagComponent;
import com.submu.pug.game.objects.components.OwnerComponent;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/12/13
 * Time: 3:41 PM
 * Collision flags for preset collision filters.
 */
public class CollisionFilter extends Filter {
    /**
     * Groups that are commonly used throughout the game.
     * Keep this as a pre calculated list to keep things simple.
     * A unit will be able to collide with other units.
     *  (generic FPS character)
     * A projectile will be able to collide with units.
     *  (generic FPS bullet)
     * A unit projectile will be able to collide with both.
     *  (rocket that pushes units and projectiles around)
     *  (rocket that can get shot down by bullets)
     */
    public static final long COLLISION_UNIT = 1,
                             COLLISION_PROJECTILE = 2;

    /**
     * Entity system to use.
     */
    private EntitySystem entitySystem;

    /**
     * Player assigner to use.
     */
    private PlayerAssigner playerAssigner;

    /**
     * Initializes the collision filter.
     * @param entitySystem the entity system to use.
     * @param playerAssigner the player assigner to use.
     */
    public CollisionFilter(EntitySystem entitySystem, PlayerAssigner playerAssigner) {
        this.entitySystem = entitySystem;
        this.playerAssigner = playerAssigner;
    }

    @Override
    public boolean filterBounds(Bounds bounds1, Bounds bounds2) {
        // Only filter entities with the filter component.
        FilterFlagComponent filter = entitySystem.getComponent(bounds1.getEntity(), FilterFlagComponent.class);
        if (filter == null) {
            return true;
        }

        Entity entity1 = bounds1.getEntity();
        Entity entity2 = bounds2.getEntity();
        // Bounds to bounds checks should always have entities.
        if (entity1 == null || entity2 == null) {
            LoggerFactory.getLogger(CollisionFilter.class).warn("Trying to filter bounds without valid entities.");
            return true;
        }

        // Disallow if the entity cannot collide with the self then disallow.
        if (!filter.collidesSelf) {
            if (bounds1.getEntity().equals(bounds2.getEntity())) {
                return false;
            }
        }
        // Disallow if the entity cannot collide with another entity by the same owner.
        if (!filter.collidesOwner) {
            OwnerComponent owner1 = entitySystem.getComponent(bounds1.getEntity(), OwnerComponent.class);
            if (owner1 != null) {
                OwnerComponent owner2 = entitySystem.getComponent(bounds2.getEntity(), OwnerComponent.class);
                if (owner2 != null) {
                    if (owner1.equals(owner2)) {
                        return false;
                    }
                }
            }
        }
        // Disallow if the entity cannot collide with the creator.
        if (!filter.collidesCreator) {
            CreatorComponent creator = entitySystem.getComponent(bounds1.getEntity(), CreatorComponent.class);
            if (creator != null) {
                if (creator.creator.equals(bounds2.getEntity())) {
                    return false;
                }
            }
        }
        // Disallow if the entity cannot collide with an entity that shares the same creator.
        if (!filter.collidesSharedCreator) {
            CreatorComponent creator1 = entitySystem.getComponent(bounds1.getEntity(), CreatorComponent.class);
            if (creator1 != null) {
                CreatorComponent creator2 = entitySystem.getComponent(bounds2.getEntity(), CreatorComponent.class);
                if (creator2 != null) {
                    if (creator1.creator.equals(creator2.creator)) {
                        return false;
                    }
                }
            }
        }
        // Disallow if the entity cannot collide with allies.
        if (!filter.collidesAlly) {
            OwnerComponent owner1 = entitySystem.getComponent(bounds1.getEntity(), OwnerComponent.class);
            if (owner1 != null) {
                OwnerComponent owner2 = entitySystem.getComponent(bounds2.getEntity(), OwnerComponent.class);
                if (owner2 != null) {
                    if (playerAssigner.arePlayersAllied(playerAssigner.getPlayer(owner1.playerID), playerAssigner.getPlayer(owner2.playerID))) {
                        return false;
                    }
                }
            }
        }
        // Disallow if the entity cannot collide with enemies.
        if (!filter.collidesEnemy) {
            OwnerComponent owner1 = entitySystem.getComponent(bounds1.getEntity(), OwnerComponent.class);
            if (owner1 != null) {
                OwnerComponent owner2 = entitySystem.getComponent(bounds2.getEntity(), OwnerComponent.class);
                if (owner2 != null) {
                    if (playerAssigner.arePlayersEnemies(playerAssigner.getPlayer(owner1.playerID), playerAssigner.getPlayer(owner2.playerID))) {
                        return false;
                    }
                }
            }
        }
        // Disallow if the entity cannot collide with neutrals.
        if (!filter.collidesNeutral) {
            OwnerComponent owner = entitySystem.getComponent(bounds2.getEntity(), OwnerComponent.class);
            if (owner != null) {
                Player player = playerAssigner.getPlayer(owner.playerID);
                // Only check the neutral passive player.
                if (player.equals(playerAssigner.getNeutralPassivePlayer())) {
                    return false;
                }
            }
        }
        // Disallow if entity is the same type.
        if (!filter.collidesSame) {
            ActorComponent actor1 = entitySystem.getComponent(bounds1.getEntity(), ActorComponent.class);
            if (actor1 != null) {
                ActorComponent actor2 = entitySystem.getComponent(bounds2.getEntity(), ActorComponent.class);
                if (actor2 != null && actor1.type.equals(actor2.type)) {
                    return false;
                }
            }
        }
        return true;
    }
}
