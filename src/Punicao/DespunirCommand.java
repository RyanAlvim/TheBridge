package Punicao;

import Punicao.BanManager;
import Punicao.MuteManager;
import Punicao.TempBanManager;
import Punicao.TempMuteManager;
import org.bukkit.BanList;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DespunirCommand implements CommandExecutor, Listener {

    private static final Map<UUID, String> alvoPorStaff = new HashMap<>();

    private static class MenuDespunirHolder implements InventoryHolder {
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

        Player admin = (Player) sender;

        if (!admin.hasPermission("tag.admin")) {
            admin.sendMessage("§c§lERRO§7: Você não tem permissão para acessar o menu de despunição.");
            return true;
        }

        if (args.length < 1) {
            admin.sendMessage("§cUso correto: /despunir <jogador>");
            return true;
        }

        String nomeAlvo = args[0];
        alvoPorStaff.put(admin.getUniqueId(), nomeAlvo);

        abrirMenuDespunicao(admin, nomeAlvo);
        return true;
    }

    private void abrirMenuDespunicao(Player admin, String alvo) {
        Inventory gui = Bukkit.createInventory(new MenuDespunirHolder(), 27, "§8Despunir: §a" + alvo);

        

        gui.setItem(10, criarItem(Material.REDSTONE_BLOCK, "§a§lDESBANIR PERMANENTE", 
                "§7Remove o banimento permanente", "§7do jogador §f" + alvo + "§7."));

        gui.setItem(12, criarItem(Material.WATCH, "§a§lDESBANIR TEMPORÁRIO", 
                "§7Remove o banimento temporário", "§7do jogador §f" + alvo + "§7."));

        gui.setItem(14, criarItem(Material.DEAD_BUSH, "§a§lDESMUTAR PERMANENTE", 
                "§7Remove o mute permanente", "§7do jogador §f" + alvo + "§7 no chat."));

        gui.setItem(16, criarItem(Material.REDSTONE_TORCH_ON, "§a§lDESMUTAR TEMPORÁRIO", 
                "§7Remove o mute temporário", "§7do jogador §f" + alvo + "§7 no chat."));

        admin.openInventory(gui);
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
        if (e.getInventory() == null || !(e.getInventory().getHolder() instanceof MenuDespunirHolder)) {
            return;
        }

        e.setCancelled(true);

        if (!(e.getWhoClicked() instanceof Player)) return;
        Player admin = (Player) e.getWhoClicked();

        ItemStack clicado = e.getCurrentItem();
        if (clicado == null || clicado.getType() == Material.AIR) return;

        String alvo = alvoPorStaff.get(admin.getUniqueId());
        if (alvo == null) {
            admin.closeInventory();
            admin.sendMessage("§cNenhum jogador alvo foi encontrado.");
            return;
        }

        admin.closeInventory();
        FileConfiguration config = Main.Main.getInstance().getConfig();

        switch (clicado.getType()) {
            case REDSTONE_BLOCK:
                if (Bukkit.getBanList(BanList.Type.NAME).isBanned(alvo)) {
                    Bukkit.getBanList(BanList.Type.NAME).pardon(alvo);
                    admin.sendMessage("§aVocê removeu o §c§lBAN PERMANENTE §ade §f" + alvo + "§a!");
                } else {
                    admin.sendMessage("§cO jogador §f" + alvo + " §cnão está banido permanentemente.");
                }
                break;

            case WATCH:
                String tempBanPath = "tempbans." + alvo.toLowerCase();
                if (config.contains(tempBanPath)) {
                    config.set(tempBanPath, null);
                    Main.Main.getInstance().saveConfig();
                    admin.sendMessage("§aVocê removeu o §e§lBAN TEMPORÁRIO §ade §f" + alvo + "§a!");
                } else {
                    admin.sendMessage("§cO jogador §f" + alvo + " §cnão possui um ban temporário ativo.");
                }
                break;

            case DEAD_BUSH:
                String mutePermPath = "mutes.permanentes." + alvo.toLowerCase();
                if (config.contains(mutePermPath)) {
                    config.set(mutePermPath, null);
                    Main.Main.getInstance().saveConfig();
                    admin.sendMessage("§aVocê removeu o §c§lMUTE PERMANENTE §ade §f" + alvo + "§a!");
                } else {
                    admin.sendMessage("§cO jogador §f" + alvo + " §cnão está mutado permanentemente.");
                }
                break;

            case REDSTONE_TORCH_ON:
                String tempMutePath = "tempmutes." + alvo.toLowerCase();
                if (config.contains(tempMutePath)) {
                    config.set(tempMutePath, null);
                    Main.Main.getInstance().saveConfig();
                    admin.sendMessage("§aVocê removeu o §6§lMUTE TEMPORÁRIO §ade §f" + alvo + "§a!");
                } else {
                    admin.sendMessage("§cO jogador §f" + alvo + " §cnão possui um mute temporário ativo.");
                }
                break;

            default:
                break;
        }

        alvoPorStaff.remove(admin.getUniqueId());
    }
}