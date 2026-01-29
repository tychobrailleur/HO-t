package core.context.events;

import core.model.player.Player;

/**
 * Event fired when a player is selected in the UI.
 */
public class PlayerSelectionEvent {
    private final Player player;

    public PlayerSelectionEvent(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
