package Permissao;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PermissionManager {

    private final Main.Main plugin;
    private final HashMap<UUID, PermissionAttachment> attachments;

    public PermissionManager(Main.Main plugin) {
        this.plugin = plugin;
        this.attachments = new HashMap<>();
    }

   
    public String getGrupo(String playerName) {
        String caminhoJogador = "jogadores." + playerName.toLowerCase();
        return plugin.getConfig().getString(caminhoJogador, "membro").toLowerCase();
    }

  
    public String getGrupo(Player player) {
        return getGrupo(player.getName());
    }

    public void setPlayerGroup(String playerName, String group) {
        String grupoFormatado = group.toLowerCase();
        
        plugin.getConfig().set("jogadores." + playerName.toLowerCase(), grupoFormatado);
        plugin.saveConfig();

        Player target = Bukkit.getPlayer(playerName);
        if (target != null) {
            atualizarPermissoes(target);
        }
    }

    public void atualizarPermissoes(Player player) {
        UUID uuid = player.getUniqueId();
        
        removerPermissoes(player);

        PermissionAttachment attachment = player.addAttachment(plugin);
        attachments.put(uuid, attachment);

        String playerNameLower = player.getName().toLowerCase();
        String caminhoJogador = "jogadores." + playerNameLower;

        if (!plugin.getConfig().contains(caminhoJogador)) {
            plugin.getConfig().set(caminhoJogador, "membro");
            plugin.saveConfig();
            plugin.getLogger().info("Jogador novo detectado! " + player.getName() + " foi registrado como membro.");
        }

        String grupo = getGrupo(player);

        List<String> permissoes = plugin.getConfig().getStringList("grupos." + grupo);

        if (permissoes != null && !permissoes.isEmpty()) {
            for (String perm : permissoes) {
                String permLower = perm.toLowerCase();
                attachment.setPermission(permLower, true); // Injeta a permissão
            }
        } else {
            plugin.getLogger().warning("O grupo '" + grupo + "' nao foi encontrado no config.yml ou nao possui permissoes!");
        }
        
        if (player.isOp()) {
            attachment.setPermission("meuplugin.admin", true);
        }
        
        player.recalculatePermissions();
    }

    public void removerPermissoes(Player player) {
        PermissionAttachment attachment = attachments.remove(player.getUniqueId());
        if (attachment != null) {
            attachment.remove();
        }
    }

    public void limparTudo() {
        for (PermissionAttachment attachment : attachments.values()) {
            attachment.remove();
        }
        attachments.clear();
    }
}