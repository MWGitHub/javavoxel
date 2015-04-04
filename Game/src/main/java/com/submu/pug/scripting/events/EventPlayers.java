package com.submu.pug.scripting.events;

import com.submu.pug.game.objects.Player;
import com.submu.pug.game.objects.PlayerAssigner;
import com.submu.pug.game.objects.PlayerCallbacks;
import com.submu.pug.scripting.ScriptEvent;
import com.submu.pug.scripting.ScriptGlobals;

/**
 * Created with IntelliJ IDEA.
 * User: MW
 * Date: 3/27/13
 * Time: 4:43 PM
 */
public class EventPlayers implements EventHook {
    /**
     * Player joined event name.
     */
    public static final String EVENT_PLAYER_JOINED = "eventPlayerJoined";

    /**
     * Name for the last joined player.
     */
    public static final String VAR_LAST_JOINED_PLAYER = "varLastJoinedPlayer";

    /**
     * Callbacks for the game session.
     */
    private PlayerCallbacks callbacks;

    /**
     * Initializes the session.
     * @param playerAssigner the player assigner to attach callbacks to.
     */
    public EventPlayers(PlayerAssigner playerAssigner) {
        callbacks = new PlayerCallbacks() {
            @Override
            public void onPlayerAdded(Player player) {
                ScriptGlobals.getInstance().putData(VAR_LAST_JOINED_PLAYER, player);
                ScriptGlobals.getInstance().addEvent(new ScriptEvent(EVENT_PLAYER_JOINED, player));
            }
        };
        playerAssigner.setCallbacks(callbacks);
    }

    @Override
    public void updateEvent(float tpf) {
    }

    @Override
    public void destroyEvent() {
    }
}
