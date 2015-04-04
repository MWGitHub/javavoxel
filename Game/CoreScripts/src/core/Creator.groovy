package core

import com.exploringlines.entitysystem.Entity
import com.submu.pug.scripting.ScriptAPI

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 7/9/13
 * Time: 6:14 PM
 * Creates entities with preset components.
 */
class Creator {
    /**
     * API to use.
     */
    private ScriptAPI api

    /**
     * Entity bindings to use.
     */
    private EntityBindings entityBindings

    /**
     * Initializes the Creator bindings.
     * @param api the API to use.
     * @param entityBindings the entity bindings to use.
     */
    Creator(ScriptAPI api, EntityBindings entityBindings) {
        this.api = api
        this.entityBindings = entityBindings
    }

    /**
     * Creates an entity without any components.
     * @param name the name of the entity.
     * @return the created entity.
     */
    public Entity createEntity(String name = null) {
        if (name != null) {
            return api.gameObjectFactory.createEntity(name)
        } else {
            return api.gameObjectFactory.createEntity()
        }
    }

    /**
     * Removes an entity.
     * @param entity the entity to remove.
     */
    public void removeEntity(Entity entity) {
        api.entitySystem.removeEntity(entity)
    }

    /**
     * Creates an actor given the name.
     * @param dataName the name of the actor data.
     * @param player the id of the owning player.
     * @param entityName the name of the entity.
     * @return the actor.
     */
    public Entity createActor(String dataName, int player, String entityName = null) {
        return api.gameObjectFactory.createActor(dataName, player, entityName)
    }

    /**
     * Creates a projectile.
     * Projectiles are specialized actors.
     * @param type the name of the actor data.
     * @param model the model to use for the projectile (null to use default).
     * @param player the id of the owning player.
     * @param creator the creator of the projectile.
     * @param category the category of the entity.
     * @return the projectile.
     */
    public Entity createProjectile(String type, String model, int player, Entity creator, String category = null) {
        Entity projectile = api.gameObjectFactory.createActor(type, player)
        entityBindings.setCreator(projectile, creator)
        entityBindings.copyStats(creator, projectile)
        entityBindings.setActorCategory(projectile, category)
        entityBindings.setMaxHealth(projectile, 0)

        if (model != null) {
            api.gameObjectFactory.changeActorModel(projectile, model)
        }

        return projectile
    }
}
