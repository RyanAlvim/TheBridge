package Punicao;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Permissao.PermissionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PunirCommand implements CommandExecutor, Listener {

    private static final Map<UUID, String> alvoPorStaff = new HashMap<>();
    private final PermissionManager permissao;

    public PunirCommand(PermissionManager permissao) {
        this.permissao = permissao;
    }

    private static class MenuPunirHolder implements InventoryHolder {
        @Override
        public Inventory getInventory() {
            return null;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem executar este comando.");
            return true;
        }

        Player staff = (Player) sender;

        if (!staff.hasPermission("tag.helper")) {
            staff.sendMessage("§c§lERRO§7: Você não tem permissão para acessar o menu de punições.");
            return true;
        }

        if (args.length < 1) {
            staff.sendMessage("§cUso correto: /punir <jogador>");
            return true;
        }

        String nomeAlvo = args[0];
        alvoPorStaff.put(staff.getUniqueId(), nomeAlvo);

        abrirMenuPunicao(staff, nomeAlvo);
        return true;
    }

    private void abrirMenuPunicao(Player staff, String alvo) {
        Inventory gui = Bukkit.createInventory(new MenuPunirHolder(), 27, "§8Punir: §c" + alvo);

        gui.setItem(11, criarItem(Material.REDSTONE, "§c§lBAN PERMANENTE", 
                "§7Aplica um banimento permanente", "§7no jogador §f" + alvo + "§7."));

        gui.setItem(12, criarItem(Material.WATCH, "§e§lBAN TEMPORÁRIO", 
                "§7Aplica um banimento temporário", "§7no jogador §f" + alvo + "§7."));

        gui.setItem(13, criarItem(Material.DEAD_BUSH, "§c§lMUTE PERMANENTE", 
                "§7Silencia permanentemente o", "§7jogador §f" + alvo + "§7 no chat."));

        gui.setItem(14, criarItem(Material.REDSTONE_TORCH_ON, "§6§lMUTE TEMPORÁRIO", 
                "§7Silencia temporariamente o", "§7jogador §f" + alvo + "§7 no chat."));

        gui.setItem(15, criarItem(Material.BLAZE_ROD, "§a§lKICK", 
                "§7Expulsa o jogador §f" + alvo, "§7da partida ou servidor."));

        staff.openInventory(gui);
    }

    private ItemStack criarItem(Material material, String nome, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(nome);
            meta.setLore(Arrays.asList(lore));
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (!(e.getInventory().getHolder() instanceof MenuPunirHolder)) return;

        e.setCancelled(true);

        if (e.getClickedInventory().getHolder() != e.getInventory().getHolder()) return;
        if (!(e.getWhoClicked() instanceof Player)) return;

        Player staff = (Player) e.getWhoClicked();

        ItemStack clicado = e.getCurrentItem();
        if (clicado == null || clicado.getType() == Material.AIR) return;

        String alvo = alvoPorStaff.get(staff.getUniqueId());
        if (alvo == null) {
            staff.closeInventory();
            staff.sendMessage("§cNenhum jogador alvo foi encontrado.");
            return;
        }

        staff.closeInventory();

        String grupoStaff = permissao.getGrupo(staff);

        switch (clicado.getType()) {

            case REDSTONE:
                if (grupoStaff.equalsIgnoreCase("helper")) {
                    staff.sendMessage("§4§lERRO§e: §7Helpers não podem aplicar banimento permanente!");
                } else {
                    BanManager.banirPermanente(alvo, "Uso de trapaças / Violação das regras", staff.getName());
                    staff.sendMessage("§aPunição aplicada: §cBAN PERMANENTE §aem §f" + alvo);
                }
                break;

            case WATCH:
                if (grupoStaff.equalsIgnoreCase("helper")) {
                    staff.sendMessage("§4§lERRO§e: §7Helpers não podem aplicar banimento temporário!");
                } else {
                    long seteDiasEmMs = 7L * 24L * 60L * 60L * 1000L;
                    TempBanManager.banirTemporario(alvo, seteDiasEmMs, "Uso de trapaças", staff.getName());
                    staff.sendMessage("§aPunição aplicada: §eBAN TEMPORÁRIO (7d) §aem §f" + alvo);
                }
                break;

            case DEAD_BUSH:
                MuteManager.mutarPermanente(alvo, "Comportamento Inadequado", staff.getName());
                staff.sendMessage("§aPunição aplicada: §cMUTE PERMANENTE §aem §f" + alvo);
                break;

            case REDSTONE_TORCH_ON:
                long umaHoraEmMs = 1L * 60L * 60L * 1000L; // 1 hora
                TempMuteManager.mutarTemporario(alvo, umaHoraEmMs, "Comportamento Inadequado", staff.getName());
                staff.sendMessage("§aPunição aplicada: §6MUTE TEMPORÁRIO (1h) §aem §f" + alvo);
                break;

            case BLAZE_ROD:
                KickManager.expulsarJogador(alvo, "Expulso por comportamento inadequado", staff.getName());
                staff.sendMessage("§aPunição aplicada: §aKICK §aem §f" + alvo);
                break;

            default:
                break;
        }

        alvoPorStaff.remove(staff.getUniqueId());
    }
}