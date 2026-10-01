package Comandos;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TagCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            p.sendMessage(" ");
            p.sendMessage("§aSuas tags são:");
            
            List<String> tagsDisponiveis = getTagsDisponiveis(p);
            
            if (tagsDisponiveis.isEmpty()) {
                p.sendMessage(" §7• Você não possui nenhuma tag disponível.");
            } else {
                p.sendMessage(" " + String.join("§7, ", tagsDisponiveis));
            }
            p.sendMessage(" ");
            p.sendMessage("§7Para aplicar uma tag use: §f/tag <nome>");
            return true;
        }

        switch (args[0].toLowerCase()) {

            case "admin":
                if (!p.hasPermission("tag.admin")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §4§lADMIN§7.");
                    return true;
                }
                Tag.setTag(p, "02ADMIN", "§4§lADMIN §4");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §4§lADMIN");
                break;

            case "mod":
                if (!p.hasPermission("tag.mod")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §5§lMOD§7.");
                    return true;
                }
                Tag.setTag(p, "03MOD", "§5§lMOD §5");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §5§lMOD");
                break;

            case "helper":
                if (!p.hasPermission("tag.helper")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §9§lHELPER§7.");
                    return true;
                }
                Tag.setTag(p, "04HELPER", "§9§lHELPER §9");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §9§lHELPER");
                break;

            case "partner":
                if (!p.hasPermission("tag.partner")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §b§lPARTNER§7.");
                    return true;
                }
                Tag.setTag(p, "05PARTNER", "§b§lPARTNER §b");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §b§lPARTNER");
                break;

            case "beta":
                if (!p.hasPermission("tag.beta")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §1§lBETA§7.");
                    return true;
                }
                Tag.setTag(p, "06BETA", "§1§lBETA §1");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §1§lBETA");
                break;

            case "2026":
                if (!p.hasPermission("tag.2026")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §b§l2026§7.");
                    return true;
                }
                Tag.setTag(p, "07ANO2026", "§b§l2026 §b");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §b§l2026");
                break;

            case "natal":
                if (!p.hasPermission("tag.natal")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §c§lNATAL§7.");
                    return true;
                }
                Tag.setTag(p, "08NATAL", "§c§lNATAL §c");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §c§lNATAL");
                break;

            case "summer":
                if (!p.hasPermission("tag.summer")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §6§lSUMMER§7.");
                    return true;
                }
                Tag.setTag(p, "09SUMMER", "§6§lSUMMER §6");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §6§lSUMMER");
                break;

            case "bloom":
                if (!p.hasPermission("tag.bloom")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §5§lBLOOM§7.");
                    return true;
                }
                Tag.setTag(p, "10BLOOM", "§5§lBLOOM §5");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §5§lBLOOM");
                break;

            case "frost":
                if (!p.hasPermission("tag.frost")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §b§lFROST§7.");
                    return true;
                }
                Tag.setTag(p, "11FROST", "§b§lFROST §b");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §b§lFROST");
                break;

            case "vip":
                if (!p.hasPermission("tag.vip")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §a§lVIP§7.");
                    return true;
                }
                Tag.setTag(p, "12VIP", "§a§lVIP §a");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §a§lVIP");
                break;

            case "membro":
                if (!p.hasPermission("tag.membro")) {
                    p.sendMessage("§c§lERRO§7: Você não possui acesso à tag §7§lMEMBRO§7.");
                    return true;
                }
                Tag.setTag(p, "99MEMBRO", "§7");
                p.sendMessage("§a§lSUCESSO§7: Sua tag foi alterada para §7§lMEMBRO");
                break;

            default:
                p.sendMessage("§c§lERRO§7: Esta tag não existe.");
                break;
        }
        return true;
    }

    private List<String> getTagsDisponiveis(Player p) {
        List<String> tags = new ArrayList<>();

        if (p.hasPermission("tag.admin")) tags.add("§4ADMIN");
        if (p.hasPermission("tag.mod")) tags.add("§5MOD");
        if (p.hasPermission("tag.helper")) tags.add("§9HELPER");
        if (p.hasPermission("tag.partner")) tags.add("§bPARTNER");
        if (p.hasPermission("tag.beta")) tags.add("§1BETA");
        
        if (p.hasPermission("tag.2026")) tags.add("§b2026");
        if (p.hasPermission("tag.natal")) tags.add("§cNATAL");
        if (p.hasPermission("tag.summer")) tags.add("§6SUMMER");
        if (p.hasPermission("tag.bloom")) tags.add("§5BLOOM");
        if (p.hasPermission("tag.frost")) tags.add("§bFROST");
        if (p.hasPermission("tag.vip")) tags.add("§aVIP");
        if (p.hasPermission("tag.membro")) tags.add("§7MEMBRO");

        return tags;
    }
}