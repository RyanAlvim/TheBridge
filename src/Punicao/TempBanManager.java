package Punicao;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class TempBanManager implements Listener {

    public static void banirTemporario(String alvoNome, long duracaoMs, String motivo, String autor) {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        long expiraEm = System.currentTimeMillis() + duracaoMs;

        String path = "tempbans." + alvoNome.toLowerCase();
        config.set(path + ".nome", alvoNome);
        config.set(path + ".expira", expiraEm);
        config.set(path + ".motivo", motivo);
        config.set(path + ".autor", autor);
        Main.Main.getInstance().saveConfig();

        String mensagemBan = getMensagemTempBanFormatada(motivo, autor, formatarTempoRestante(duracaoMs));

        Player alvo = Bukkit.getPlayerExact(alvoNome);
        if (alvo != null && alvo.isOnline()) {
            alvo.kickPlayer(mensagemBan);
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e) {
        String nomeJogador = e.getPlayer().getName();
        String path = "tempbans." + nomeJogador.toLowerCase();
        FileConfiguration config = Main.Main.getInstance().getConfig();

        if (config.contains(path)) {
            long expiraEm = config.getLong(path + ".expira");
            long tempoRestante = expiraEm - System.currentTimeMillis();

            if (tempoRestante <= 0) {
                config.set(path, null);
                Main.Main.getInstance().saveConfig();
            } else {
                String motivo = config.getString(path + ".motivo", "Uso de trapaças");
                String autor = config.getString(path + ".autor", "CONSOLE");

                String mensagem = getMensagemTempBanFormatada(motivo, autor, formatarTempoRestante(tempoRestante));
                e.disallow(PlayerLoginEvent.Result.KICK_BANNED, mensagem);
            }
        }
    }

 
    public static String getMensagemTempBanFormatada(String motivo, String autor, String tempoRestanteStr) {
        return "§c§lBAPPEP MC\n" +
               "§7Você está temporariamente banido do servidor!\n\n" +
               "§fPunição: §e§lBAN TEMPORÁRIO\n" +
               "§fDuração Restante: §c" + tempoRestanteStr + "\n" +
               "§fMotivo: §7" + motivo + "\n" +
               "§fAutor: §7" + autor + "\n\n" +
               "§eCaso ache que isso foi um engano ou queira revisão, entre em contato via Discord:";
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