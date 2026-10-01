package Eventos;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import TheBridge.BridgeGame;

public class Eventos implements Listener {

    @EventHandler
    public void onFood(FoodLevelChangeEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onFallDamage(EntityDamageEvent e) {

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            e.setCancelled(true);
        }
    }
    
}