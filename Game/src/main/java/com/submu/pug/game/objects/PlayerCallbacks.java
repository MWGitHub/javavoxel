package com.submu.pug.game.objects;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/15/13
 * Time: 4:14 PM
 * Callbacks for players.
 */
public interface PlayerCallbacks {
    /**
     * Runs when a player is added.
     * @param player the player that was added.
     */
    void onPlayerAdded(Player player);
}
