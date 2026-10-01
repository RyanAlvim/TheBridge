package Comandos;

import Main.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetSpawnCommand implements CommandExecutor {

    private final Main plugin;

    public SetSpawnCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }

        Player p = (Player) sender;

        if (!p.hasPermission("spawn.admin")) {
            p.sendMessage("§c§lERRO§7: Você não possui permissão.");
            return true;
        }

        Location loc = p.getLocation();

        plugin.getConfig().set("spawn.world", loc.getWorld().getName());
        plugin.getConfig().set("spawn.x", loc.getX());
        plugin.getConfig().set("spawn.y", loc.getY());
        plugin.getConfig().set("spawn.z", loc.getZ());
        plugin.getConfig().set("spawn.yaw", loc.getYaw());
        plugin.getConfig().set("spawn.pitch", loc.getPitch());

        plugin.saveConfig();

        p.sendMessage("§a§lSUCESSO§7: Spawn definido.");
        return true;
    }
}