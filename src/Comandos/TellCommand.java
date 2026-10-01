package Comandos;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;

public class TellCommand implements CommandExecutor {

    private final JavaPlugin plugin; 
    private final String CONFIG_PATH = "tell-desativado";

    public TellCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player p = (Player) sender;
        String playerUUID = p.getUniqueId().toString();

        List<String> listaDesativados = plugin.getConfig().getStringList(CONFIG_PATH);

        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("toggle"))) {
            if (listaDesativados.contains(playerUUID)) {
                listaDesativados.remove(playerUUID);
                p.sendMessage("§a[Mensagem] Suas mensagens privadas foram ATIVADAS!");
            } else {
                listaDesativados.add(playerUUID);
                p.sendMessage("§c[Mensagem] Suas mensagens privadas foram DESATIVADAS!");
            }
            
            plugin.getConfig().set(CONFIG_PATH, listaDesativados);
            plugin.saveConfig();
            return true;
        }

        if (args.length < 2) {
            p.sendMessage("§cUso correto: /tell <jogador> <mensagem> ou /tell toggle");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            p.sendMessage("§cJogador não encontrado ou offline.");
            return true;
        }

        if (listaDesativados.contains(playerUUID)) {
            p.sendMessage("§cVocê não pode enviar mensagens privadas enquanto seu Tell estiver desativado.");
            return true;
        }

        String targetUUID = target.getUniqueId().toString();
        if (listaDesativados.contains(targetUUID)) {
            p.sendMessage("§cEste jogador desativou as mensagens privadas.");
            return true;
        }

        if (p.equals(target)) {
            p.sendMessage("§cVocê não pode enviar mensagens para si mesmo.");
            return true;
        }

        StringBuilder msgBuilder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            msgBuilder.append(args[i]).append(" ");
        }
        String mensagem = msgBuilder.toString().trim();

        p.sendMessage("§8[§7Eu §b➔ §7" + target.getName() + "§8] §f" + mensagem);
        target.sendMessage("§8[§7" + p.getName() + " §b➔ §7Eu§8] §f" + mensagem);

        return true;
    }

    public boolean temTellAtivo(UUID uuid) {
        return !plugin.getConfig().getStringList(CONFIG_PATH).contains(uuid.toString());
    }
}