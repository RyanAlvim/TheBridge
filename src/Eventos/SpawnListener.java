package Eventos;

import Main.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAchievementAwardedEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.help.HelpTopic;

public class SpawnListener implements Listener {

    private final Main plugin;

    public SpawnListener(Main plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void aoDigitarComandoErrado(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String mensagem = event.getMessage();
        
        String[] args = mensagem.split(" ");
        String comando = args[0];

        HelpTopic topico = Bukkit.getServer().getHelpMap().getHelpTopic(comando);

        if (topico == null) {
            event.setCancelled(true);
            
            player.sendMessage("§cComando não encontrado.");
        }
    }
    
    @EventHandler
    public void onAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        if (!plugin.getConfig().contains("spawn.world")) {
            return;
        }

        World world = Bukkit.getWorld(
                plugin.getConfig().getString("spawn.world")
        );

        if (world == null) {
            return;
        }

        Location spawn = new Location(
                world,
                plugin.getConfig().getDouble("spawn.x"),
                plugin.getConfig().getDouble("spawn.y"),
                plugin.getConfig().getDouble("spawn.z"),
                (float) plugin.getConfig().getDouble("spawn.yaw"),
                (float) plugin.getConfig().getDouble("spawn.pitch")
        );

        e.getPlayer().teleport(spawn);
    }
}