package Punicao;

import org.bukkit.BanEntry;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class BanManager implements Listener {


    public static String getMensagemBanFormatada(String motivo, String autor) {
        return "§c§lBAPPEP MC\n" +
               "§7Você foi banido permanentemente do servidor!\n\n" +
               "§fPunição: §c§lBAN PERMANENTE\n" +
               "§fMotivo: §7" + motivo + "\n" +
               "§fAutor: §7" + autor + "\n\n" +
               "§eCaso ache que isso foi um engano ou queira revisão, entre em contato via Discord:";
    }

   
    public static void banirPermanente(String alvoNome, String motivo, String autor) {
        Bukkit.getBanList(BanList.Type.NAME).addBan(alvoNome, motivo, null, autor);

        String mensagemBan = getMensagemBanFormatada(motivo, autor);

        Player alvo = Bukkit.getPlayerExact(alvoNome);
        if (alvo != null && alvo.isOnline()) {
            alvo.kickPlayer(mensagemBan);
        }
    }

    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerLogin(PlayerLoginEvent e) {
        String nomeJogador = e.getPlayer().getName();
        
        if (Bukkit.getBanList(BanList.Type.NAME).isBanned(nomeJogador)) {
            BanEntry banEntry = Bukkit.getBanList(BanList.Type.NAME).getBanEntry(nomeJogador);
            
            String motivo = (banEntry != null && banEntry.getReason() != null) 
                    ? banEntry.getReason() 
                    : "Violação das regras";
                    
            String autor = (banEntry != null && banEntry.getSource() != null) 
                    ? banEntry.getSource() 
                    : "CONSOLE";

            e.disallow(PlayerLoginEvent.Result.KICK_BANNED, getMensagemBanFormatada(motivo, autor));
        }
    }
}