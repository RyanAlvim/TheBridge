package Eventos;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BuildListener implements Listener {

    @EventHandler
    public void onBreak(BlockBreakEvent e) {

        if (!BuildManager.canBuild(
                e.getPlayer().getUniqueId())) {

            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {

        if (!BuildManager.canBuild(
                e.getPlayer().getUniqueId())) {

            e.setCancelled(true);
        }
    }
}