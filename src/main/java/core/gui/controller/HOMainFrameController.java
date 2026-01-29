package core.gui.controller;

import core.context.ApplicationContext;
import core.context.events.PlayerSelectionEvent;
import core.model.player.Player;

public class HOMainFrameController {

    private final ApplicationContext context;

    public HOMainFrameController(ApplicationContext context) {
        this.context = context;
    }

    public void selectPlayer(Player player, Object source) {
        if (context == null)
            return;

        Player current = context.getModelManager().getModel().getSelectedPlayer();
        if (current != player) {
            context.getModelManager().getModel().setSelectedPlayer(player);
            if (context.getEventBus() != null) {
                context.getEventBus().post(new PlayerSelectionEvent(player, source));
            }
        }
    }

    public Player getSelectedPlayer() {
        if (context == null)
            return null;
        return context.getModelManager().getModel().getSelectedPlayer();
    }
}
