package TheBridge;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import InventariosBridge.KitManager;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

public class BridgeDeathListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        BridgeGame game = BridgeGame.getGameByPlayer(p.getUniqueId());

        if (game == null) return;

        e.getDrops().clear();
        e.setDroppedExp(0);
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        final Player p = e.getPlayer();
        final BridgeGame game = BridgeGame.getGameByPlayer(p.getUniqueId());

        if (game == null) return;

        if (game.redPlayer != null && p.getUniqueId().equals(game.redPlayer.getUniqueId())) {
            e.setRespawnLocation(game.getRedSpawn());
        } else if (game.bluePlayer != null && p.getUniqueId().equals(game.bluePlayer.getUniqueId())) {
            e.setRespawnLocation(game.getBlueSpawn());
        }

        Bukkit.getScheduler().runTaskLater(
            Bukkit.getPluginManager().getPlugins()[0], 
            new Runnable() {
                @Override
                public void run() {
                    if (!p.isOnline()) return;
                    
                    if (game.redPlayer != null && p.getUniqueId().equals(game.redPlayer.getUniqueId())) {
                        KitManager.giveRedKit(p);
                    } else if (game.bluePlayer != null && p.getUniqueId().equals(game.bluePlayer.getUniqueId())) {
                    	KitManager.giveRedKit(p);
                    }
                    p.updateInventory();
                }
            }, 
            2L
        );
    }
}