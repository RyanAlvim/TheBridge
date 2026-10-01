package Main;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import Anticheat.ReachCheck;
import Clima.Clima;
import Comandos.BuildCommand;
import Comandos.EntrarBridgeCommand;
import Comandos.LobbyCommand;
import Comandos.SetSpawnCommand;
import Comandos.SummonBridgeCommand;
import Comandos.Tag;
import Comandos.TagCommand;
import Comandos.TellCommand;
import Eventos.BridgeListener;
import Eventos.BuildListener;
import Eventos.Eventos;
import Eventos.JoinItems;
import Eventos.SpawnListener;
import Eventos.TabManager;
import InventariosBridge.InventoryEditorCommand;
import MapasMenu.MapasMenu;
import Permissao.GrupoComando;
import Permissao.PermissionManager;
import Permissao.PlayerListener;
import Punicao.BanManager;
import Punicao.DespunirCommand;
import Punicao.MuteManager;
import Punicao.PunirCommand;
import Punicao.TempBanManager;
import Punicao.TempMuteManager;
import Scoreboard.ScoreboardAPI;
import TheBridge.BridgeDeathListener;
import TheBridge.BridgeGame;
import TheBridge.SpecCommand;

public class Main extends JavaPlugin {
    
    private static Main instance;
    private PermissionManager permissionManager;
    
    @Override
    public void onEnable() {
        instance = this;
        PluginManager pm = Bukkit.getPluginManager();

        Bukkit.getConsoleSender().sendMessage("§a[TheBridge] Código novo carregado com sucesso!");
        Bukkit.getConsoleSender().sendMessage("§a[TheBridge] Plugin LIGADO com sucesso!");

        saveDefaultConfig();

        getCommand("tag").setExecutor(new TagCommand());
        getCommand("summonbridge").setExecutor(new SummonBridgeCommand());
        getCommand("setspawn").setExecutor(new SetSpawnCommand(this));
        getCommand("entrarbridge").setExecutor(new EntrarBridgeCommand());
        getCommand("spec").setExecutor(new SpecCommand(instance));
        getCommand("despunir").setExecutor(new DespunirCommand());

        pm.registerEvents(new Tag(), this);
        pm.registerEvents(new BridgeListener(), this); 
        pm.registerEvents(new SpawnListener(this), this);
        pm.registerEvents(new BridgeDeathListener(), this);
        
        pm.registerEvents(new JoinItems(), this);
        pm.registerEvents(new Eventos(), this);
        pm.registerEvents(new BanManager(), this);
        pm.registerEvents(new TempBanManager(), this);
        pm.registerEvents(new MuteManager(), this);
        pm.registerEvents(new TempMuteManager(), this);
        pm.registerEvents(new DespunirCommand(), this);
        pm.registerEvents(new ReachCheck(), this);
        pm.registerEvents(new MapasMenu(), this);
        this.permissionManager = new PermissionManager(this);
        
        
        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                ReachCheck.recordLocation(player);
            }
        }, 0L, 1L);

        Bukkit.getPluginManager().registerEvents(new PlayerListener(permissionManager), this);

        getCommand("grupo").setExecutor(new GrupoComando(permissionManager));
        Bukkit.getPluginManager().registerEvents(new Clima(), this);
        Bukkit.getPluginManager().registerEvents(new EntrarBridgeCommand(), this);
        InventoryEditorCommand editor = new InventoryEditorCommand(this);
        getCommand("editarinventario").setExecutor(editor);
        getServer().getPluginManager().registerEvents(editor, this);
       getCommand("lobby").setExecutor(new LobbyCommand());
       getCommand("tell").setExecutor(new TellCommand(instance));
       getCommand("l").setExecutor(new LobbyCommand());
      
       PunirCommand punirCmd = new PunirCommand(this.permissionManager);

       getCommand("punir").setExecutor(punirCmd);
       Bukkit.getPluginManager().registerEvents(punirCmd, this);
      
        ScoreboardAPI.start();
        
        Bukkit.getScheduler().runTaskTimer(this, TabManager::updateAll, 20L, 100L);
        
        for (World world : Bukkit.getWorlds()) {
            world.setTime(6000L); 
            world.setStorm(false);
            world.setThundering(false);
            world.setGameRuleValue("doDaylightCycle", "false");
            world.setGameRuleValue("doWeatherCycle", "false");
        }

        Bukkit.getScheduler().runTaskTimer(this, () -> {
            for (World world : Bukkit.getWorlds()) {
                if (world.getTime() != 6000L) {
                    world.setTime(6000L);
                }
                if (world.hasStorm()) {
                    world.setStorm(false);
                }
                if (world.isThundering()) {
                    world.setThundering(false);
                }
            }
        }, 0L, 20L); 
    }
    @Override
    public void onDisable() {
    	if (permissionManager != null) {
            permissionManager.limparTudo();
        }
        Bukkit.getConsoleSender().sendMessage("§c[TheBridge] Plugin DESLIGADO com sucesso!");
        instance = null;
    }

    public static Main getInstance() {
        return instance;
    }
}