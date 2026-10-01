package Permissao;


import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    private final PermissionManager permissionManager;

    public PlayerListener(PermissionManager permissionManager) {
        this.permissionManager = permissionManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        permissionManager.atualizarPermissoes(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        permissionManager.removerPermissoes(event.getPlayer());
    }
}