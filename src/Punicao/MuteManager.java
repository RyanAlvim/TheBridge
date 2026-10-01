package Punicao;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MuteManager implements Listener {


    public static void mutarPermanente(String alvoNome, String motivo, String autor) {
        FileConfiguration config = Main.Main.getInstance().getConfig();

        String path = "mutes.permanentes." + alvoNome.toLowerCase();
        config.set(path + ".nome", alvoNome);
        config.set(path + ".motivo", motivo);
        config.set(path + ".autor", autor);
        Main.Main.getInstance().saveConfig();

        Player alvo = Bukkit.getPlayerExact(alvoNome);
        if (alvo != null && alvo.isOnline()) {
            alvo.sendMessage("\n§c§lBAPPEP MC");
            alvo.sendMessage("§cVocê foi silenciado permanentemente no chat!");
            alvo.sendMessage("§fMotivo: §7" + motivo);
            alvo.sendMessage("§fAutor: §7" + autor + "\n");
        }
    }

   
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String path = "mutes.permanentes." + player.getName().toLowerCase();
        FileConfiguration config = Main.Main.getInstance().getConfig();

        if (config.contains(path)) {
            e.setCancelled(true);

            String motivo = config.getString(path + ".motivo", "Uso inadequado do chat");
            String autor = config.getString(path + ".autor", "CONSOLE");

            player.sendMessage("\n§c§lBAPPEP MC");
            player.sendMessage("§cVocê está silenciado permanentemente no chat!");
            player.sendMessage("§fMotivo: §7" + motivo);
            player.sendMessage("§fAutor: §7" + autor);
            player.sendMessage("§eCaso ache que isso foi um engano, entre em contato via Discord.\n");
        }
    }
}