package Comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SummonBridgeCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("bridge.admin")) {
            p.sendMessage("§cSem permissão.");
            return true;
        }

        BridgeNPC.spawn(p);

        p.sendMessage("§aNPC THE BRIDGE criado.");

        return true;
    }
}