package Comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import Eventos.BuildManager;

public class BuildCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {

        if (!(sender instanceof Player))
            return true;

        Player p = (Player) sender;

        if (!p.hasPermission("build.admin")) {
            p.sendMessage("§c§lERRO§7: Você não possui permissão.");
            return true;
        }

        if (args.length != 1) {
            p.sendMessage("§eUse: /build <on/off>");
            return true;
        }

        if (args[0].equalsIgnoreCase("on")) {

            BuildManager.enableBuild(
                    p.getUniqueId());

            p.sendMessage(
                    "§a§lSUCESSO§7: Modo construção ativado."
            );

            return true;
        }

        if (args[0].equalsIgnoreCase("off")) {

            Eventos.BuildManager.disableBuild(
                    p.getUniqueId());

            p.sendMessage(
                    "§c§lSUCESSO§7: Modo construção desativado."
            );

            return true;
        }

        p.sendMessage("§eUse: /build <on/off>");
        return true;
    }
}