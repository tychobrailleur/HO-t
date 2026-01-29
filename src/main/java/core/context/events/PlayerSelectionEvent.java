package core.context.events;

import core.model.player.Player;

/**
 * Event fired when a player is selected in the UI.
 */
public class PlayerSelectionEvent {
    private final Player player;
    private final Object source;

    public PlayerSelectionEvent(Player player, Object source) {
        this.player = player;
        this.source = source;
    }

    public Player getPlayer() {
        return player;
    }

    public Object getSource() {
        return source;
    }
}
