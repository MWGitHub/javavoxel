package core

import com.exploringlines.entitysystem.Entity
import com.submu.pug.camera.ChaseCameraComponent
import com.submu.pug.game.actions.ChaseComponent
import com.submu.pug.game.objects.Player
import com.submu.pug.scripting.ScriptAPI

/**
 * Created with IntelliJ IDEA.
 * User: Michael Wang
 * Date: 6/27/13
 * Time: 1:10 PM
 * Bindings related to players.
 */
class PlayerBindings {
    /**
     * API to use for the bindings.
     */
    ScriptAPI api

    /**
     * Initializes the bindings.
     * @param api the api to use.
     */
    PlayerBindings(ScriptAPI api) {
        this.api = api
    }

    /**
     * Retrieves the player matching the id.
     * @param id the id of the player.
     * @return the player.
     */
    public Player getPlayer(int id) {
        return api.playerAssigner.getPlayer(id)
    }

    /**
     * @return the local player.
     */
    public Player getLocalPlayer() {
        return api.playerAssigner.getLocalPlayer()
    }

    /**
     * @return the neutral passive player.
     */
    public Player getNeutralPassivePlayer() {
        return api.playerAssigner.getNeutralPassivePlayer()
    }

    /**
     * @return the neutral hostile player.
     */
    public Player getNeutralHostilePlayer() {
        return api.playerAssigner.getNeutralHostilePlayer()
    }

    /**
     * Retrieves the actor a player is controlling.
     * @param player the player to retrieve from.
     * @return the actor the player is controlling.
     */
    public Entity getControlledEntity(Player player) {
        return player.controlledEntity
    }

    /**
     * Set the entity that is to be directly controlled by the player.
     * It is assumed that the camera will followEntity the directly controlled entity.
     * @param entity the entity that is directly controlled by the player.
     * @param player the player to control the entity.
     */
    public void setControlledActor(Entity entity, Player player) {
        Entity lastEntity = getControlledEntity(player)
        if (lastEntity != null) {
            api.entitySystem.removeComponent(lastEntity, ChaseCameraComponent.class)
            api.entitySystem.removeComponent(lastEntity, ChaseComponent.class)
        }
        api.entitySystem.setComponent(entity, new ChaseCameraComponent())
        api.entitySystem.setComponent(entity, new ChaseComponent())
        player.setControlledEntity(entity)
    }

    /**
     * Retrieves the amount of money a player has.
     * @param player the player to retrieve the money from.
     * @return the money the player has or 0 if none found.
     */
    public float getMoney(Player player) {
        return player.money
    }

    /**
     * Set the money for the player.
     * @param player the player to set the money for.
     * @param money the money to set.
     */
    public void setMoney(Player player, float money) {
        player.money = money
    }
}
