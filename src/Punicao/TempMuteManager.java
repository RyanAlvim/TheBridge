package Punicao;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TempMuteManager implements Listener {

   
    public static void mutarTemporario(String alvoNome, long duracaoMs, String motivo, String autor) {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        long expiraEm = System.currentTimeMillis() + duracaoMs;

        String path = "tempmutes." + alvoNome.toLowerCase();
        config.set(path + ".nome", alvoNome);
        config.set(path + ".expira", expiraEm);
        config.set(path + ".motivo", motivo);
        config.set(path + ".autor", autor);
        Main.Main.getInstance().saveConfig();

        Player alvo = Bukkit.getPlayerExact(alvoNome);
        if (alvo != null && alvo.isOnline()) {
            alvo.sendMessage("\n§c§lBAPPEP MC");
            alvo.sendMessage("§cVocê foi silenciado temporariamente no chat!");
            alvo.sendMessage("§fDuração: §c" + formatarTempoRestante(duracaoMs));
            alvo.sendMessage("§fMotivo: §7" + motivo);
            alvo.sendMessage("§fAutor: §7" + autor + "\n");
        }
    }

   
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        String path = "tempmutes." + player.getName().toLowerCase();
        FileConfiguration config = Main.Main.getInstance().getConfig();

        if (config.contains(path)) {
            long expiraEm = config.getLong(path + ".expira");
            long tempoRestante = expiraEm - System.currentTimeMillis();

            if (tempoRestante <= 0) {
                config.set(path, null);
                Main.Main.getInstance().saveConfig();
            } else {
                e.setCancelled(true);

                String motivo = config.getString(path + ".motivo", "Uso inadequado do chat");
                String autor = config.getString(path + ".autor", "CONSOLE");

                player.sendMessage("\n§c§lBAPPEP MC");
                player.sendMessage("§cVocê está silenciado temporariamente no chat!");
                player.sendMessage("§fTempo Restante: §c" + formatarTempoRestante(tempoRestante));
                player.sendMessage("§fMotivo: §7" + motivo);
                player.sendMessage("§fAutor: §7" + autor);
                player.sendMessage("§eCaso ache que isso foi um engano, entre em contato via Discord.\n");
            }
        }
    }

    
    private static String formatarTempoRestante(long millis) {
        long segundos = millis / 1000;
        long dias = segundos / 86400;
        segundos %= 86400;
        long horas = segundos / 3600;
        segundos %= 3600;
        long minutos = segundos / 60;
        segundos %= 60;

        StringBuilder sb = new StringBuilder();
        if (dias > 0) sb.append(dias).append("d ");
        if (horas > 0) sb.append(horas).append("h ");
        if (minutos > 0) sb.append(minutos).append("m ");
        if (segundos > 0 || sb.length() == 0) sb.append(segundos).append("s");

        return sb.toString().trim();
    }
}