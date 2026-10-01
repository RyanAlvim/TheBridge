package Punicao;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KickManager {

  
    public static void expulsarJogador(String alvoNome, String motivo, String autor) {
        Player alvo = Bukkit.getPlayerExact(alvoNome);
        
        String mensagemKick = 
            "§c§lBAPPEP MC\n" +
            "§7Você foi desconectado do servidor!\n\n" +
            "§fPunição: §a§lKICK\n" +
           
            "§fAutor: §7" + autor + "\n\n" +
            "§eCaso ache que isso foi um engano, entre em contato via Discord:";

        if (alvo != null && alvo.isOnline()) {
            alvo.kickPlayer(mensagemKick);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "kick " + alvoNome + " " + motivo);
        }
    }
}