package com.submu.pug.game.objects;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 5/15/13
 * Time: 4:08 PM
 * Handles the creation and removal of players.
 */
public class PlayerAssigner {
    /**
     * Status for alliances.
     */
    public enum AllianceStatus {
        /**
         * Flags for valid alliance types.
         */
        ALLIED, HOSTILE
    }

    /**
     * Preset player numbers.
     */
    public static final int PLAYER_NEUTRAL_PASSIVE = 0,
    PLAYER_NEUTRAL_HOSTILE = -1;

    /**
     * List of players.
     */
    private List<Player> players = new LinkedList<Player>();

    /**
     * Player alliances with other players given the player id.
     */
    private Map<Player, Map<Integer, AllianceStatus>> alliances = new HashMap<Player, Map<Integer, AllianceStatus>>();

    /**
     * Callbacks to run on events.
     */
    private PlayerCallbacks callbacks;

    /**
     * Local player index.
     */
    private Player localPlayer;

    /**
     * Neutral passive player.
     */
    private Player neutralPassivePlayer;

    /**
     * Neutral hostile player.
     */
    private Player neutralHostilePlayer;

    /**
     * Initializes the class.
     */
    public PlayerAssigner() {
        neutralPassivePlayer = createPlayer(PLAYER_NEUTRAL_PASSIVE);
        neutralHostilePlayer = createPlayer(PLAYER_NEUTRAL_HOSTILE);
    }

    /**
     * Creates a player and adds it to the player list.
     * @param id the player number.
     * @return the created player.
     */
    public Player createPlayer(int id) {
        Player player = new Player(id);
        if (callbacks != null) {
            callbacks.onPlayerAdded(player);
        }
        players.add(player);

        // Set the default alliances for the player.
        alliances.put(player, new HashMap<Integer, AllianceStatus>());
        if (neutralHostilePlayer != null) {
            setAlliance(player, neutralHostilePlayer, AllianceStatus.HOSTILE);
            setAlliance(neutralHostilePlayer, player, AllianceStatus.HOSTILE);
        }

        return player;
    }

    /**
     * Retrieves the player by the id.
     * @param id the id of the player.
     * @return the retrieved player or null if none matches the id.
     */
    public Player getPlayer(int id) {
        for (Player player : players) {
            if (player.getId() == id) {
                return player;
            }
        }
        return null;
    }

    /**
     * Removes a player.
     * @param player the player to remove.
     */
    public void removePlayer(Player player) {
        players.remove(player);
    }

    /**
     * Sets the alliance for one player in regards to another.
     * @param player1 a player to set the alliance of.
     * @param player2 the player to check the alliance of.
     * @param status the status of the alliance.
     */
    public void setAlliance(Player player1, Player player2, AllianceStatus status) {
        alliances.get(player1).put(player2.getId(), status);
    }

    /**
     * Sets the alliance for both player in regards to each other.
     * @param player1 a player to set the alliance of.
     * @param player2 another player to set the alliance of.
     * @param status the status of the alliance.
     */
    public void setMutualAlliance(Player player1, Player player2, AllianceStatus status) {
        setAlliance(player1, player2, status);
        setAlliance(player2, player1, status);
    }

    /**
     * Checks if the given players are allied.
     * @param player1 a player to check with.
     * @param player2 another player to check with.
     * @return true if the players are allied.
     */
    public boolean arePlayersAllied(Player player1, Player player2) {
        // If the players are the same then treat it as allied.
        if (player1.equals(player2)) {
            return true;
        }
        AllianceStatus status = alliances.get(player1).get(player2.getId());
        // Players without alliances assigned will not be an ally.
        if (status == null) {
            return false;
        }
        if (status.equals(AllianceStatus.ALLIED)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if the given players are enemies.
     * @param player1 a player to check with.
     * @param player2 another player to check with.
     * @return true if the players are enemies.
     */
    public boolean arePlayersEnemies(Player player1, Player player2) {
        // If the players are the same then treat it as allied.
        if (player1.equals(player2)) {
            return false;
        }
        AllianceStatus status = alliances.get(player1).get(player2.getId());
        // Players without alliances assigned will not be hostile.
        if (status == null) {
            return false;
        }
        if (status.equals(AllianceStatus.HOSTILE)) {
            return true;
        }
        return false;
    }

    /**
     * @param player the player to use as the local player.
     */
    public void setLocalPlayer(Player player) {
        localPlayer = player;
    }

    /**
     * @return the local player.
     */
    public Player getLocalPlayer() {
        return localPlayer;
    }

    /**
     * @return the neutral passive player.
     */
    public Player getNeutralPassivePlayer() {
        return neutralPassivePlayer;
    }

    /**
     * @return the neutral hostile player.
     */
    public Player getNeutralHostilePlayer() {
        return neutralHostilePlayer;
    }

    /**
     * @param callbacks the callbacks to set.
     */
    public void setCallbacks(PlayerCallbacks callbacks) {
        this.callbacks = callbacks;
    }
}
