package Permissao;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.connorlinfoot.titleapi.TitleAPI;

public class GrupoComando implements CommandExecutor {

    private final PermissionManager permissionManager;

    public GrupoComando(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        
        if (!sender.hasPermission("bappep.admin")) {
            sender.sendMessage(ChatColor.RED + "Você não tem permissão para isso.");
            return true;
        }

        if (args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Uso correto: /grupo <jogador> <grupo>");
            return true;
        }

        String targetName = args[0];
        String grupo = args[1];

        permissionManager.setPlayerGroup(targetName, grupo);

        sender.sendMessage(ChatColor.GREEN + "Grupo de " + targetName + " definido para " + grupo + "!");

        Player target = Bukkit.getPlayer(targetName);
        if (target != null) {
        	
        	if(grupo.equals("membro")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §7§lMEMBRO");
                target.chat("/tag membro");
        	}else if(grupo.equals("vip")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §a§lVIP");
                target.chat("/tag vip");
        	}else if(grupo.equals("frost")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §b§lFROST");
                target.chat("/tag frost");
        	}else if(grupo.equals("bloom")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §5§lBLOOM");
                target.chat("/tag bloom");
        	}else if(grupo.equals("summer")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §6§lSUMMER");
                target.chat("/tag summer");
        	}
        	else if(grupo.equals("natal")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §c§lNATAL");
                target.chat("/tag natal");
        	}
        	else if(grupo.equals("2026")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §b§l2026");
                target.chat("/tag 2026");
        	}else if(grupo.equals("beta")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §1§lBETA");
                target.chat("/tag beta");
        	}else if(grupo.equals("partner")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §b§lPARTNER");
                target.chat("/tag partner");
        	}else if(grupo.equals("helper")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §9§lHELPER");
                target.chat("/tag helper");
        	}else if(grupo.equals("mod")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §5§lMOD");
                target.chat("/tag mod");
        	}
        	else if(grupo.equals("admin")) {
        		TitleAPI.sendTitle(target, 5, 15, 5, "§a§lGRUPO ALTERADO!", "§7Seu grupo foi alterado para §4§lADMIN");
                target.chat("/tag admin");
        	}
        	
        

            target.sendMessage(ChatColor.YELLOW + "Seu grupo foi alterado para " + grupo + ".");
        }

        return true;
    }
}