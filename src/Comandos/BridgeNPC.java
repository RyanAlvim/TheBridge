package Comandos;

import org.bukkit.ChatColor;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

public class BridgeNPC {

    public static void spawn(Player player) {

        Blaze blaze = (Blaze) player.getWorld().spawnEntity(
                player.getLocation(),
                EntityType.BLAZE
        );

        blaze.setCustomName("§b§lTHE BRIDGE");
        blaze.setCustomNameVisible(true);

        blaze.setRemoveWhenFarAway(false);
        blaze.setMaxHealth(2048.0);
        blaze.setHealth(2048.0);

        blaze.setCanPickupItems(false);
    }
}