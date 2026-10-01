package Comandos;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.UUID;
import TheBridge.BridgeGame;

public class LobbyCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }

        Player p = (Player) sender;
        UUID id = p.getUniqueId();

        World lobbyWorld = Bukkit.getWorld("world");
        Location lobbyLoc = (lobbyWorld != null) ? new Location(lobbyWorld, -5.5, 69.0, 2.5) : Bukkit.getWorlds().get(0).getSpawnLocation();

        BridgeGame game = BridgeGame.getGameByPlayer(id);
        if (game != null) {
            p.sendMessage("§cVocê saiu da partida! O jogo foi encerrado.");
            
            Player p1 = game.redPlayer;
            Player p2 = game.bluePlayer;
            
            game.resetGame(); 
            
            if (p1 != null && p1.isOnline()) { setupLobbyState(p1, lobbyLoc); }
            if (p2 != null && p2.isOnline()) { setupLobbyState(p2, lobbyLoc); }
            return true; 
        }

        if (EntrarBridgeCommand.getQueueSize() > 0) {
            EntrarBridgeCommand.removeFromQueue(id);
        }

        setupLobbyState(p, lobbyLoc);
        p.sendMessage("§aVocê foi teletransportado para o lobby!");
        
        return true;
    }


    private void setupLobbyState(Player p, Location lobbyLoc) {
        if (p.getGameMode() == GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SURVIVAL);
        }

        p.setHealth(p.getMaxHealth());
        p.setFoodLevel(20);
        p.setFireTicks(0);
        p.setFallDistance(0.0F);

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);

        p.getInventory().setItem(0, createItem(Material.NETHER_STAR, "§6§lRANKS"));
        p.getInventory().setItem(2, createItem(Material.STORAGE_MINECART, "§a§lMENU"));
        p.getInventory().setItem(4, createItem(Material.COMPASS, "§b§lJOGOS"));
        p.getInventory().setItem(6, createItem(Material.STRING, "§e§lCONFIGURAÇÕES"));
        p.getInventory().setItem(8, createItem(Material.JUKEBOX, "§d§lEFEITOS"));

        p.updateInventory();
        p.teleport(lobbyLoc);
    }


    private ItemStack createItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(displayName);
            item.setItemMeta(meta);
        }
        return item;
    }
}