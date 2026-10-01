package TheBridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import Comandos.EntrarBridgeCommand;

public class BridgeListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        java.util.UUID id = e.getPlayer().getUniqueId();
        
        EntrarBridgeCommand.removeFromQueue(id);
        
        BridgeGame game = BridgeGame.getGameByPlayer(id);
        if (game != null) {
            game.resetGame();
        }
    }
}