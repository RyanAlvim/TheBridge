package Comandos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import TheBridge.BridgeGame;

public class EntrarBridgeCommand implements CommandExecutor, Listener {

    public static final List<UUID> matchmakingQueue = new ArrayList<>();
    private static final Random random = new Random();
    
    public static String mapaAtualFila = null; 

    private Location getQueueLocation(World world, String mapaNome) {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        String path = "mapas." + mapaNome + ".queue-spawn";
        if (config.contains(path)) {
            String[] split = config.getString(path).split(",");
            double x = Double.parseDouble(split[0].trim());
            double y = Double.parseDouble(split[1].trim());
            double z = Double.parseDouble(split[2].trim());
            float yaw = Float.parseFloat(split[3].trim());
            float pitch = Float.parseFloat(split[4].trim());
            return new Location(world, x, y, z, yaw, pitch);
        }
        return world.getSpawnLocation();
    }

    private String getRandomMapName() {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        
        Main.Main.getInstance().reloadConfig(); 
        
        if (!config.contains("mapas") || config.getConfigurationSection("mapas") == null) {
            Bukkit.getLogger().warning("[TheBridge] SEÇÃO 'mapas:' NÃO FOI ENCONTRADA NO CONFIG.YML!");
            return "nethermap"; 
        }
        
        Set<String> mapas = config.getConfigurationSection("mapas").getKeys(false);
        List<String> mapaList = new ArrayList<>(mapas);
        
        if (mapaList.isEmpty()) {
            Bukkit.getLogger().warning("[TheBridge] A lista de mapas na config está vazia!");
            return "nethermap";
        }
        
        String sorteado = mapaList.get(random.nextInt(mapaList.size()));
        Bukkit.getLogger().info("[TheBridge] Mapa sorteado para a fila: " + sorteado);
        return sorteado;
    }

    private boolean mapExists(String mapaNome) {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        Main.Main.getInstance().reloadConfig();
        return config.contains("mapas." + mapaNome);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;

        Player p = (Player) sender;
        UUID id = p.getUniqueId();

        if (BridgeGame.getGameByPlayer(id) != null) {
            p.sendMessage("§cVocê já está em jogo!");
            return true;
        }

        if (matchmakingQueue.contains(id)) {
            p.sendMessage("§cVocê já está na fila.");
            return true;
        }

        if (matchmakingQueue.isEmpty()) {
            if (args.length > 0) {
                if (p.hasPermission("tag.vip")) {
                    String mapaEscolhido = args[0].toLowerCase();
                    
                    if (mapExists(mapaEscolhido)) {
                        mapaAtualFila = mapaEscolhido;
                    } else {
                        p.sendMessage("§cO mapa '" + args[0] + "' não existe. Selecionando um mapa aleatório...");
                        mapaAtualFila = getRandomMapName();
                    }
                } else {
                    p.sendMessage("§c§lERRO§7: Apenas jogadores §a§lVIP §7podem selecionar mapas!");
                    p.sendMessage("§7Procurando uma partida em mapa aleatório...");
                    mapaAtualFila = getRandomMapName();
                }
            } else {
                mapaAtualFila = getRandomMapName();
            }
        } else {
            if (args.length > 0 && !args[0].equalsIgnoreCase(mapaAtualFila)) {
                p.sendMessage("§eVocê entrou em uma fila já aberta. O mapa definido é: §f" + mapaAtualFila);
            }
        }

        p.getInventory().clear();
        p.getInventory().setArmorContents(null);
        p.updateInventory();

        matchmakingQueue.add(id);
        p.sendMessage("§7" + p.getName() + " §aentrou na fila do The Bridge. §7(" + matchmakingQueue.size() + "/2)");

        FileConfiguration config = Main.Main.getInstance().getConfig();
        String worldModeloNome = config.getString("mapas." + mapaAtualFila + ".world-modelo", mapaAtualFila);

        World queueWorld = Bukkit.getWorld(worldModeloNome);
        if (queueWorld == null) {
            queueWorld = Bukkit.createWorld(new WorldCreator(worldModeloNome));
        }
        
        if (queueWorld != null) {
            p.teleport(getQueueLocation(queueWorld, mapaAtualFila));
        } else {
            p.sendMessage("§cAviso: Não foi possível carregar o mapa modelo: " + worldModeloNome);
        }

        if (matchmakingQueue.size() >= 2) {
            Player p1 = Bukkit.getPlayer(matchmakingQueue.remove(0));
            Player p2 = Bukkit.getPlayer(matchmakingQueue.remove(0));

            if (p1 == null || !p1.isOnline()) { if(p2 != null) matchmakingQueue.add(p2.getUniqueId()); return true; }
            if (p2 == null || !p2.isOnline()) { if(p1 != null) matchmakingQueue.add(p1.getUniqueId()); return true; }

            p1.sendMessage("§aPartida encontrada! Criando mundo isolado...");
            p2.sendMessage("§aPartida encontrada! Criando mundo isolado...");

            new BridgeGame(p1, p2, mapaAtualFila);
        }
        return true;
    }

    @EventHandler
    public void onQueueMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();

        if (matchmakingQueue.contains(id) && mapaAtualFila != null) {
            FileConfiguration config = Main.Main.getInstance().getConfig();
            double minY = config.getDouble("mapas." + mapaAtualFila + ".queue-min-y", 57.0);
            String worldModeloNome = config.getString("mapas." + mapaAtualFila + ".world-modelo", mapaAtualFila);

            if (p.getLocation().getY() <= minY) {
                World queueWorld = Bukkit.getWorld(worldModeloNome);
                if (queueWorld != null) {
                    Location loc = getQueueLocation(queueWorld, mapaAtualFila);
                    loc.setYaw(p.getLocation().getYaw());
                    loc.setPitch(p.getLocation().getPitch());
                    p.teleport(loc);
                }
            }
        }
    }
    
    public static boolean isInQueue(Player player) {
        return matchmakingQueue.contains(player.getUniqueId());
    }

    @EventHandler
    public void onQueueQuit(PlayerQuitEvent e) {
        matchmakingQueue.remove(e.getPlayer().getUniqueId());
        if (matchmakingQueue.isEmpty()) mapaAtualFila = null;
    }

    
    public static void removeFromQueue(UUID uuid) {
        matchmakingQueue.remove(uuid);
        if (matchmakingQueue.isEmpty()) mapaAtualFila = null;
    }

    public static int getQueueSize() {
        return matchmakingQueue.size();
    }
}