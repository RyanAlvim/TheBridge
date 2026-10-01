package TheBridge;

import java.io.*;
import java.util.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.*;
import com.connorlinfoot.titleapi.TitleAPI;

import Eventos.JoinItems;
import InventariosBridge.KitManager;

public class BridgeGame implements Listener {

    private static final Map<UUID, BridgeGame> gamesByPlayer = new HashMap<>();

    private static final Map<UUID, BridgeGame> spectatingGames = new HashMap<>();

    private final UUID gameId;
    private final String worldInstanceName;
    public final String mapaNome; 

    public World gameWorld;

    public Player redPlayer;
    public Player bluePlayer;
    public boolean gameStarted = false;
    private boolean cagesActive = false;

    public int kills_red = 0;
    public int kills_blue = 0;
    public int redPoints = 0;
    public int bluePoints = 0;
    
    public String playermarcou = null;
    public String partidaTempoFormatado = "15:00";
    private int partidaSegundos = 900;
    private BukkitTask timerTask = null;
    private long lastScoreTime = 0L;

    private final Map<UUID, UUID> lastDamageBy = new HashMap<>();
    private final Map<UUID, Location> preGameLocations = new HashMap<>();
    
    private final Map<UUID, Scoreboard> originalScoreboards = new HashMap<>();
    private Scoreboard gameScoreboard;
    private Objective healthObjective;

    private final Map<UUID, UUID> spectatorTargets = new HashMap<>();
    private final Map<UUID, BukkitTask> spectatorTasks = new HashMap<>();

    @SuppressWarnings("deprecation")
    public BridgeGame(Player p1, Player p2, String mapaNome) {
        this.gameId = UUID.randomUUID();
        this.worldInstanceName = "bridge_" + gameId.toString().substring(0, 8);
        this.mapaNome = mapaNome;
        this.redPlayer = p1;
        this.bluePlayer = p2;

        preGameLocations.put(p1.getUniqueId(), p1.getLocation());
        preGameLocations.put(p2.getUniqueId(), p2.getLocation());

        gamesByPlayer.put(p1.getUniqueId(), this);
        gamesByPlayer.put(p2.getUniqueId(), this);

        for (Player online : Bukkit.getOnlinePlayers()) {
            if (!online.equals(p1) && !online.equals(p2)) {
                p1.hidePlayer(online);
                p2.hidePlayer(online);
                online.hidePlayer(p1);
                online.hidePlayer(p2);
            }
        }
        p1.showPlayer(p2);
        p2.showPlayer(p1);

        setupGameScoreboard();

        Bukkit.getPluginManager().registerEvents(this, Main.Main.getInstance());

        if (setupWorldCopy()) {
            startCountdown();
        } else {
            p1.sendMessage("§cErro crítico ao criar a sala. Partida cancelada.");
            p2.sendMessage("§cErro crítico ao criar a sala. Partida cancelada.");
            resetGame();
        }
    }
    
    @EventHandler
    public void onMobSpawn(org.bukkit.event.entity.CreatureSpawnEvent e) {
        if (e.getEntity().getWorld() == gameWorld) {
            e.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    private void setupGameScoreboard() {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        this.gameScoreboard = sm.getNewScoreboard();

        Team redTeam = gameScoreboard.registerNewTeam("br_red");
        redTeam.setPrefix("§c");
        redTeam.setDisplayName("§cVermelho");
        
        Team blueTeam = gameScoreboard.registerNewTeam("br_blue");
        blueTeam.setPrefix("§9");
        blueTeam.setDisplayName("§9Azul");

        redTeam.addPlayer(redPlayer);
        blueTeam.addPlayer(bluePlayer);

        this.healthObjective = gameScoreboard.registerNewObjective("bg_health", "health");
        healthObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);
        healthObjective.setDisplayName("§c❤");

        originalScoreboards.put(redPlayer.getUniqueId(), redPlayer.getScoreboard());
        originalScoreboards.put(bluePlayer.getUniqueId(), bluePlayer.getScoreboard());

        redPlayer.setScoreboard(gameScoreboard);
        bluePlayer.setScoreboard(gameScoreboard);
        
        healthObjective.getScore(redPlayer.getName()).setScore((int) redPlayer.getHealth());
        healthObjective.getScore(bluePlayer.getName()).setScore((int) bluePlayer.getHealth());
    }

    private boolean setupWorldCopy() {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        String worldModeloNome = config.getString("mapas." + mapaNome + ".world-modelo", "nethermap");

        File serverDir = Bukkit.getWorldContainer();
        File sourceFolder = new File(serverDir, worldModeloNome);
        File targetFolder = new File(serverDir, worldInstanceName);

        if (!sourceFolder.exists()) {
            Bukkit.getLogger().severe("A pasta modelo '" + worldModeloNome + "' nao existe!");
            return false;
        }

        try {
            copyFolder(sourceFolder, targetFolder);
            File uidFile = new File(targetFolder, "uid.dat");
            if (uidFile.exists()) uidFile.delete();

            this.gameWorld = Bukkit.createWorld(new WorldCreator(worldInstanceName));
            this.gameWorld.setAutoSave(false);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void copyFolder(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            if (!dest.exists()) dest.mkdir();
            String[] files = src.list();
            if (files == null) return;
            for (String file : files) {
                copyFolder(new File(src, file), new File(dest, file));
            }
        } else {
            if (src.getName().equals("session.lock")) return;
            try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dest)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void deleteWorldInstance() {
        if (gameWorld == null) return;
        final File folder = gameWorld.getWorldFolder();

        World lobbyWorld = Bukkit.getWorld("world");
        Location lobbyLoc = (lobbyWorld != null) ? new Location(lobbyWorld, -5.5, 69.0, 2.5) : Bukkit.getWorlds().get(0).getSpawnLocation();
        
        for (Player p : new ArrayList<>(gameWorld.getPlayers())) {
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);
            p.updateInventory();

            if (p.getGameMode() == GameMode.SPECTATOR) {
                p.setGameMode(GameMode.SURVIVAL);
            }
            
            p.teleport(lobbyLoc);
            
            if (p.equals(redPlayer) || p.equals(bluePlayer)) {
                JoinItems.darItensLobby(p);
            } else {
                JoinItems.darItensLobby(p);
            }
        }

        Bukkit.unloadWorld(gameWorld, false);
        this.gameWorld = null;

        Bukkit.getScheduler().runTaskLater(Main.Main.getInstance(), () -> {
            System.gc();
            deleteFolder(folder);
        }, 20L);
    }

    private void deleteFolder(File path) {
        if (path.exists()) {
            File[] files = path.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) deleteFolder(file);
                    else file.delete();
                }
            }
            path.delete();
        }
    }

    public Location getRedSpawn() { 
        return getLocationFromConfig("red-spawn", new Location(gameWorld, 1567.5, 76.0, 1261.5, -90F, 0F));
    }
    
    public Location getBlueSpawn() { 
        return getLocationFromConfig("blue-spawn", new Location(gameWorld, 1649.5, 76.0, 1261.5, 90F, 0F));
    }

    private Location getLocationFromConfig(String key, Location fallback) {
        FileConfiguration config = Main.Main.getInstance().getConfig();
        String path = "mapas." + mapaNome + "." + key;
        if (config.contains(path)) {
            String[] split = config.getString(path).split(",");
            double x = Double.parseDouble(split[0].trim());
            double y = Double.parseDouble(split[1].trim());
            double z = Double.parseDouble(split[2].trim());
            float yaw = Float.parseFloat(split[3].trim());
            float pitch = Float.parseFloat(split[4].trim());
            return new Location(gameWorld, x, y, z, yaw, pitch);
        }
        fallback.setWorld(gameWorld);
        return fallback;
    }

    private void startCountdown() {
        new BukkitRunnable() {
            int seconds = 3;
            @Override
            public void run() {
                if (redPlayer == null || !redPlayer.isOnline() || bluePlayer == null || !bluePlayer.isOnline()) {
                    cancel();
                    resetGame();
                    return;
                }
                if (seconds > 0) {
                    sendTitleToPlayers("§e" + seconds, "");
                    sendSoundToPlayers(Sound.NOTE_STICKS, 1.0F, 1.0F);
                    seconds--;
                } else {
                    cancel();
                    setupRound();
                }
            }
        }.runTaskTimer(Main.Main.getInstance(), 0L, 20L);
    }

    private void setupRound() {
        cagesActive = true;
        lastDamageBy.clear();

        refreshPlayerState(redPlayer, true);
        refreshPlayerState(bluePlayer, false);

        redPlayer.teleport(getRedSpawn());
        bluePlayer.teleport(getBlueSpawn());

        buildCage(getRedSpawn(), (short) 14);
        buildCage(getBlueSpawn(), (short) 11);

        if (!gameStarted) {
            gameStarted = true;
            startTimer();
        }

        new BukkitRunnable() {
            int seconds = 5;
            @Override
            public void run() {
                if (!gameStarted || redPlayer == null || bluePlayer == null) { cancel(); return; }

                if (seconds > 0) {
                    String subtitle = "§7As cabines se abrirão em ... §c" + seconds;
                    if (playermarcou != null) {
                        String color = playermarcou.equals(redPlayer.getName()) ? "§c" : "§9";
                        sendTitleToPlayers(color + playermarcou + " marcou!", subtitle);
                    } else {
                        sendTitleToPlayers("", subtitle);
                    }
                    sendSoundToPlayers(Sound.NOTE_PLING, 1.0F, 1.0F);
                    seconds--;
                } else {
                    cancel();
                    removeCage(getRedSpawn());
                    removeCage(getBlueSpawn());
                    cagesActive = false;

                    sendTitleToPlayers("§a§lVALENDO!", "§fBoa sorte!");
                    sendSoundToPlayers(Sound.ENDERDRAGON_GROWL, 1.0F, 1.5F);
                }
            }
        }.runTaskTimer(Main.Main.getInstance(), 0L, 20L);
    }

    private void startTimer() {
        timerTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!gameStarted) { cancel(); return; }
                partidaSegundos--;
                partidaTempoFormatado = String.format("%02d:%02d", partidaSegundos / 60, partidaSegundos % 60);

                if (partidaSegundos <= 0) {
                    cancel();
                    endGameByTimeout();
                }
            }
        }.runTaskTimer(Main.Main.getInstance(), 20L, 20L);
    }

    private void endGameByTimeout() {
        gameStarted = false;
        if (timerTask != null) timerTask.cancel();

        if (redPoints > bluePoints) {
            broadcastMessage("§c" + redPlayer.getName() + " §7venceu por pontos!");
            if (redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 10, 80, 10, "§a§lVITÓRIA", "§7Você pontuou mais!");
            if (bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 10, 80, 10, "§c§lDERROTA", "§7O oponente pontuou mais.");
        } else if (bluePoints > redPoints) {
            broadcastMessage("§9" + bluePlayer.getName() + " §7venceu por pontos!");
            if (bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 10, 80, 10, "§a§lVITÓRIA", "§7Você pontuou mais!");
            if (redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 10, 80, 10, "§c§lDERROTA", "§7O oponente pontuou mais.");
        } else {
            broadcastMessage("§7Partida empatada!");
            if (redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 10, 80, 10, "§e§lEMPATE", "§7O tempo acabou!");
            if (bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 10, 80, 10, "§e§lEMPATE", "§7O tempo acabou!");
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                resetGame();
            }
        }.runTaskLater(Main.Main.getInstance(), 100L);
    }

    public boolean checkVictory() {
        if (redPoints >= 5) {
            gameStarted = false;
            if (timerTask != null) timerTask.cancel();
            broadcastMessage("§c§l" + redPlayer.getName() + " §avenceu a partida!");
            
            if (redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 10, 80, 10, "§a§lVITÓRIA", "§7Você venceu o jogo!");
            if (bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 10, 80, 10, "§c§lDERROTA", "§7O adversário venceu.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    resetGame();
                }
            }.runTaskLater(Main.Main.getInstance(), 100L);
            return true;
        } else if (bluePoints >= 5) {
            gameStarted = false;
            if (timerTask != null) timerTask.cancel();
            broadcastMessage("§9§l" + bluePlayer.getName() + " §avenceu a partida!");

            if (bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 10, 80, 10, "§a§lVITÓRIA", "§7Você venceu o jogo!");
            if (redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 10, 80, 10, "§c§lDERROTA", "§7O adversário venceu.");

            new BukkitRunnable() {
                @Override
                public void run() {
                    resetGame();
                }
            }.runTaskLater(Main.Main.getInstance(), 100L);
            return true;
        }
        return false;
    }

    @SuppressWarnings("deprecation")
    public void resetGame() {
        gameStarted = false;
        if (timerTask != null) timerTask.cancel();

        if (redPlayer != null && redPlayer.isOnline()) {
            Scoreboard sb = originalScoreboards.get(redPlayer.getUniqueId());
            redPlayer.setScoreboard(sb != null ? sb : Bukkit.getScoreboardManager().getMainScoreboard());
        }
        if (bluePlayer != null && bluePlayer.isOnline()) {
            Scoreboard sb = originalScoreboards.get(bluePlayer.getUniqueId());
            bluePlayer.setScoreboard(sb != null ? sb : Bukkit.getScoreboardManager().getMainScoreboard());
        }
        originalScoreboards.clear();

        if (gameScoreboard != null) {
            for (Team team : gameScoreboard.getTeams()) { team.unregister(); }
            for (Objective obj : gameScoreboard.getObjectives()) { obj.unregister(); }
        }

        for (Player p1 : Bukkit.getOnlinePlayers()) {
            for (Player p2 : Bukkit.getOnlinePlayers()) {
                if (p1.isOnline() && p2.isOnline()) {
                    p1.showPlayer(p2);
                }
            }
        }

        if (redPlayer != null) gamesByPlayer.remove(redPlayer.getUniqueId());
        if (bluePlayer != null) gamesByPlayer.remove(bluePlayer.getUniqueId());

        for (BukkitTask task : spectatorTasks.values()) {
            if (task != null) task.cancel();
        }
        spectatorTasks.clear();
        spectatorTargets.clear();
        spectatingGames.entrySet().removeIf(entry -> entry.getValue() == this);

        HandlerList.unregisterAll(this);
        deleteWorldInstance();
    }

    public void handlePlayerDeath(Player victim) {
        if (victim == null) return;
        
        boolean isRedVictim = redPlayer != null && victim.getUniqueId().equals(redPlayer.getUniqueId());
        Player survivor = isRedVictim ? bluePlayer : redPlayer;
        
        UUID killerId = lastDamageBy.get(victim.getUniqueId());
        Player killer = killerId != null ? Bukkit.getPlayer(killerId) : null;

        victim.playSound(victim.getLocation(), Sound.WITHER_HURT, 1.0F, 1.0F);

        if (survivor != null && survivor.isOnline()) {
            survivor.playSound(survivor.getLocation(), Sound.ORB_PICKUP, 1.0F, 1.0F);
        }

        if (killer != null && !killer.getUniqueId().equals(victim.getUniqueId())) {
            if (isRedVictim) {
                kills_blue++;
            } else {
                kills_red++;
            }
            
            broadcastMessage((isRedVictim ? "§c" : "§9") + victim.getName() + " §7foi morto por " + (isRedVictim ? "§9" : "§c") + killer.getName());
        } else {
            broadcastMessage((isRedVictim ? "§c" : "§9") + victim.getName() + " §7morreu sozinho.");
        }

        refreshPlayerState(victim, isRedVictim);
        victim.teleport(isRedVictim ? getRedSpawn() : getBlueSpawn());
        lastDamageBy.remove(victim.getUniqueId());
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player sender = e.getPlayer();
        
        if (spectatingGames.containsKey(sender.getUniqueId())) {
            e.setFormat("§7[SPEC] %1$s: %2$s");
        }

        if (sender.getWorld() == gameWorld) {
            e.getRecipients().removeIf(recipient -> recipient.getWorld() != gameWorld);
        } else {
            e.getRecipients().removeIf(recipient -> recipient.getWorld() == gameWorld);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        
        if (p.getWorld() != gameWorld || !gameStarted || p.getGameMode() == GameMode.SPECTATOR) return;

        if (cagesActive && (e.getFrom().getBlockX() != e.getTo().getBlockX() || e.getFrom().getBlockZ() != e.getTo().getBlockZ())) {
            Location loc = e.getFrom(); loc.setPitch(e.getTo().getPitch()); loc.setYaw(e.getTo().getYaw());
            p.teleport(loc); return;
        }

        FileConfiguration config = Main.Main.getInstance().getConfig();
        String base = "mapas." + mapaNome + ".";
        
        double y = p.getLocation().getY();
        double minYDeath = config.getDouble(base + "min-y-death");
        if (y <= minYDeath) { handlePlayerDeath(p); return; }

        double x = p.getLocation().getX(); 
        double z = p.getLocation().getZ();
        long now = System.currentTimeMillis();

        double pRedMinX = config.getDouble(base + "portal-red.min-x");
        double pRedMaxX = config.getDouble(base + "portal-red.max-x");
        double pRedMinY = config.getDouble(base + "portal-red.min-y");
        double pRedMaxY = config.getDouble(base + "portal-red.max-y");
        double pRedMinZ = config.getDouble(base + "portal-red.min-z");
        double pRedMaxZ = config.getDouble(base + "portal-red.max-z");

        double pBlueMinX = config.getDouble(base + "portal-blue.min-x");
        double pBlueMaxX = config.getDouble(base + "portal-blue.max-x");
        double pBlueMinY = config.getDouble(base + "portal-blue.min-y");
        double pBlueMaxY = config.getDouble(base + "portal-blue.max-y");
        double pBlueMinZ = config.getDouble(base + "portal-blue.min-z");
        double pBlueMaxZ = config.getDouble(base + "portal-blue.max-z");

        if (p.equals(redPlayer) && (y >= pBlueMinY && y <= pBlueMaxY) && (x >= pBlueMinX && x <= pBlueMaxX) && (z >= pBlueMinZ && z <= pBlueMaxZ)) {
            if (now - lastScoreTime < 2000L) return; lastScoreTime = now;
            redPoints++; playermarcou = redPlayer.getName();
            broadcastMessage("§c§lPONTO! §c" + redPlayer.getName()+"§7(§c"+(int)redPlayer.getHealth()+"❤§7)"  + " §7marcou! §7(" + redPoints + " - " + bluePoints + ")");
            if (!checkVictory()) setupRound();
        }
        else if (p.equals(bluePlayer) && (y >= pRedMinY && y <= pRedMaxY) && (x >= pRedMinX && x <= pRedMaxX) && (z >= pRedMinZ && z <= pRedMaxZ)) {
            if (now - lastScoreTime < 2000L) return; lastScoreTime = now;
            bluePoints++; playermarcou = bluePlayer.getName();
            broadcastMessage("§9§lPONTO! §9" + bluePlayer.getName() +"§7(§c"+(int)bluePlayer.getHealth()+"❤§7)"  + " §7marcou! §7(" + redPoints + " - " + bluePoints + ")");
            if (!checkVictory()) setupRound();
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (p.getWorld() != gameWorld) return;

        if (e.getCause() == EntityDamageEvent.DamageCause.FALL || cagesActive) { e.setCancelled(true); return; }
        if (e.getFinalDamage() >= p.getHealth()) {
            e.setCancelled(true); handlePlayerDeath(p);
            return;
        }

        Bukkit.getScheduler().runTaskLater(Main.Main.getInstance(), () -> {
            if (p.isOnline() && healthObjective != null) {
                healthObjective.getScore(p.getName()).setScore((int) p.getHealth());
            }
        }, 1L);
    }

    @EventHandler
    public void onPvp(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player victim = (Player) e.getEntity();
        if (victim.getWorld() != gameWorld) return;

        Player attacker = null;
        boolean isArrow = false;

        if (e.getDamager() instanceof Player) {
            attacker = (Player) e.getDamager();
        } else if (e.getDamager() instanceof Arrow && ((Arrow) e.getDamager()).getShooter() instanceof Player) {
            attacker = (Player) ((Arrow) e.getDamager()).getShooter();
            isArrow = true;
        }

        if (attacker != null) {
            if (attacker.getGameMode() == GameMode.SPECTATOR || attacker.equals(victim)) {
                e.setCancelled(true); return;
            }

            lastDamageBy.put(victim.getUniqueId(), attacker.getUniqueId());
            
            if (isArrow) {
                int vidaRestante = (int) Math.ceil(victim.getHealth() - e.getFinalDamage());
                if (vidaRestante < 0) vidaRestante = 0;
                String corVitima = victim.equals(redPlayer) ? "§c" : "§9";
                attacker.sendMessage(corVitima + victim.getName() + " §7está com §c" + (int)vidaRestante + "❤ §7de vida");
            }

            ItemStack itemInHand = attacker.getItemInHand();
            if (itemInHand != null && itemInHand.getType() != Material.AIR) {
                if (itemInHand.getType().getMaxDurability() > 0) itemInHand.setDurability((short) 0);
            }
        }
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;
        e.setCancelled(true);
        if (gameStarted && !cagesActive && e.getBlock().getType() == Material.STAINED_CLAY) {
            e.getBlock().breakNaturally();
        }
    }

    @EventHandler(ignoreCancelled = false)
    public void onPlace(BlockPlaceEvent e) {
        if (e.getPlayer().getWorld() != gameWorld) return;

        if (e.isCancelled()) {
            e.setCancelled(false);
        }
        
        Block b = e.getBlock();
        int x = b.getX();
        int y = b.getY();
        int z = b.getZ();

        FileConfiguration config = Main.Main.getInstance().getConfig();
        String base = "mapas." + mapaNome + ".build-limits.";

        int maxY = config.getInt(base + "max-y");
        int minZ = config.getInt(base + "min-z");
        int maxZ = config.getInt(base + "max-z");
        int minX = config.getInt(base + "min-x");
        int maxX = config.getInt(base + "max-x");

        if (!gameStarted || cagesActive || y > maxY || z < minZ || z > maxZ || x <= minX || x >= maxX) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (spectatingGames.containsKey(e.getPlayer().getUniqueId())) {
            removeSpectator(e.getPlayer());
        }

        if (e.getPlayer().equals(redPlayer) || e.getPlayer().equals(bluePlayer)) {
            broadcastMessage("§cUm jogador desconectou. Partida encerrada.");
            resetGame();
            return;
        }
    }

    @EventHandler 
    public void onEat(PlayerItemConsumeEvent e) { 
        if(e.getPlayer().getWorld() == gameWorld && e.getItem().getType() == Material.GOLDEN_APPLE) {
            e.getPlayer().setHealth(e.getPlayer().getMaxHealth());
            Player p = e.getPlayer();
            if (healthObjective != null) {
                healthObjective.getScore(p.getName()).setScore((int) p.getMaxHealth());
            }
        } 
    }
    
    @EventHandler public void onDurability(PlayerItemDamageEvent e) { if(e.getPlayer().getWorld() == gameWorld) e.setCancelled(true); }
    
    @EventHandler 
    public void onArrowHit(ProjectileHitEvent e) { 
        if (e.getEntity() instanceof Arrow && e.getEntity().getWorld() == gameWorld) {
            e.getEntity().remove(); 
        }
    }
    
    public static boolean isPlaying(Player p) { return gamesByPlayer.containsKey(p.getUniqueId()); }
    
    @EventHandler 
    public void onBow(EntityShootBowEvent e) { 
        if (e.getEntity() instanceof Player && e.getEntity().getWorld() == gameWorld) {
            final Player p = (Player) e.getEntity();
            if (p.getItemInHand().getType() == Material.BOW) p.getItemInHand().setDurability((short) 0);
            
            new BukkitRunnable() {
                int ticksLeft = 60; 
                @Override
                public void run() {
                    if (!gameStarted || !p.isOnline() || p.getWorld() != gameWorld) { cancel(); return; }
                    
                    ticksLeft -= 2; 
                    if (ticksLeft <= 0) {
                        p.setLevel(0); p.setExp(0.0F);
                        int targetSlot = 5; 
                        FileConfiguration config = Main.Main.getInstance().getConfig();
                        String path = "jogadores." + p.getName();
                        
                        if (config.contains(path)) {
                            for (int slot = 0; slot < 9; slot++) {
                                if ("ARROW".equals(config.getString(path + "." + slot))) {
                                    targetSlot = slot; break;
                                }
                            }
                        }
                        p.getInventory().setItem(targetSlot, new ItemStack(Material.ARROW, 1));
                        p.playSound(p.getLocation(), Sound.CHICKEN_EGG_POP, 0.8F, 1.2F);
                        cancel();
                    } else {
                        p.setLevel((int) Math.ceil(ticksLeft / 20.0));
                        p.setExp((float) ticksLeft / 60.0F);
                    }
                }
            }.runTaskTimer(Main.Main.getInstance(), 0L, 2L);
        }
    }

    public static BridgeGame getGameByPlayer(UUID uuid) { return gamesByPlayer.get(uuid); }

    public static BridgeGame getGameBySpectator(UUID uuid) { return spectatingGames.get(uuid); }

    public void addSpectator(Player spectator, Player target) {
        UUID specId = spectator.getUniqueId();

        BridgeGame previousGame = spectatingGames.get(specId);
        if (previousGame != null) previousGame.removeSpectator(spectator);

        spectatorTargets.put(specId, target.getUniqueId());
        spectatingGames.put(specId, this);

        spectator.teleport(target.getLocation());

        broadcastMessage("§7" + spectator.getName() + " está espectando.");
    }

    public void removeSpectator(Player spectator) {
        UUID specId = spectator.getUniqueId();
        BukkitTask task = spectatorTasks.remove(specId);
        if (task != null) task.cancel();
        spectatorTargets.remove(specId);
        spectatingGames.remove(specId);
    }

    private void broadcastMessage(String m) {
        if(redPlayer != null && redPlayer.isOnline()) redPlayer.sendMessage(m);
        if(bluePlayer != null && bluePlayer.isOnline()) bluePlayer.sendMessage(m);
    }
    private void sendTitleToPlayers(String t, String s) {
        if(redPlayer != null && redPlayer.isOnline()) TitleAPI.sendTitle(redPlayer, 5, 15, 5, t, s);
        if(bluePlayer != null && bluePlayer.isOnline()) TitleAPI.sendTitle(bluePlayer, 5, 15, 5, t, s);
    }
    private void sendSoundToPlayers(Sound sd, float v, float pi) {
        if(redPlayer != null && redPlayer.isOnline()) redPlayer.playSound(redPlayer.getLocation(), sd, v, pi);
        if(bluePlayer != null && bluePlayer.isOnline()) bluePlayer.playSound(bluePlayer.getLocation(), sd, v, pi);
    }

    private void refreshPlayerState(Player p, boolean isRed) {
        if (p == null) return;
        p.setHealth(p.getMaxHealth()); p.setFoodLevel(20); p.setFireTicks(0); p.setFallDistance(0); p.setLevel(0); p.setExp(0.0F);
        
        if (healthObjective != null) {
            healthObjective.getScore(p.getName()).setScore((int) p.getMaxHealth());
        }

        if (isRed) KitManager.giveRedKit(p); else KitManager.giveBlueKit(p);

        Color corTime = isRed ? Color.fromRGB(255, 0, 0) : Color.fromRGB(0, 0, 255);
        
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta metaHel = (LeatherArmorMeta) helmet.getItemMeta();
        metaHel.setColor(corTime); helmet.setItemMeta(metaHel);
        
        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta metaChe = (LeatherArmorMeta) chestplate.getItemMeta();
        metaChe.setColor(corTime); chestplate.setItemMeta(metaChe);
        
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta metaLeg = (LeatherArmorMeta) leggings.getItemMeta();
        metaLeg.setColor(corTime); leggings.setItemMeta(metaLeg);
        
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        LeatherArmorMeta metaBoo = (LeatherArmorMeta) boots.getItemMeta();
        metaBoo.setColor(corTime); boots.setItemMeta(metaBoo);

        p.getInventory().setHelmet(helmet);
        p.getInventory().setChestplate(chestplate);
        p.getInventory().setLeggings(leggings);
        p.getInventory().setBoots(boots);
    }

    @SuppressWarnings("deprecation")
    private void buildCage(Location l, short d) {
        World w = l.getWorld(); int sx = l.getBlockX(); int sy = l.getBlockY(); int sz = l.getBlockZ();
        for (int y = sy - 1; y <= sy + 3; y++) {
            for (int x = sx - 1; x <= sx + 1; x++) {
                for (int z = sz - 1; z <= sz + 1; z++) {
                    if (y == sy - 1) { Block b = w.getBlockAt(x, y, z); b.setType(Material.WOOL); b.setData((byte)d); }
                    else if (y == sy + 3 || (x == sx - 1 || x == sx + 1 || z == sz - 1 || z == sz + 1)) w.getBlockAt(x, y, z).setType(Material.BARRIER);
                }
            }
        }
    }

    private void removeCage(Location l) {
        World w = l.getWorld(); int sx = l.getBlockX(); int sy = l.getBlockY(); int sz = l.getBlockZ();
        for (int y = sy - 1; y <= sy + 3; y++) {
            for (int x = sx - 1; x <= sx + 1; x++) {
                for (int z = sz - 1; z <= sz + 1; z++) w.getBlockAt(x, y, z).setType(Material.AIR);
            }
        }
    }
}