package InventariosBridge;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import Main.Main;

public class InventoryEditorCommand implements CommandExecutor, Listener {

    private final Main plugin;
    private final String menuTitle = "§bEdite sua Hotbar:";

    public InventoryEditorCommand(Main plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Apenas jogadores podem usar este comando.");
            return true;
        }

        Player p = (Player) sender;
        
        Inventory inv = Bukkit.createInventory(null, 9, menuTitle);

        FileConfiguration config = plugin.getConfig();
        String path = "jogadores." + p.getName();

        if (config.contains(path)) {
            for (int slot = 0; slot < 9; slot++) {
                String identifier = config.getString(path + "." + slot);
                if (identifier == null) continue;

                switch (identifier) {
                    case "SWORD":
                        inv.setItem(slot, new ItemStack(Material.IRON_SWORD));
                        break;
                    case "PICKAXE":
                        ItemStack pi = new ItemStack(Material.DIAMOND_PICKAXE);
                        pi.addEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED, 1);
                        inv.setItem(slot, pi);
                        break;
                    case "CLAY_1":
                    case "CLAY_2":
                        inv.setItem(slot, new ItemStack(Material.STAINED_CLAY, 64, (short)0)); 
                        break;
                    case "BOW":
                        inv.setItem(slot, new ItemStack(Material.BOW));
                        break;
                    case "ARROW":
                        inv.setItem(slot, new ItemStack(Material.ARROW, 1));
                        break;
                    case "APPLE":
                        inv.setItem(slot, new ItemStack(Material.GOLDEN_APPLE, 8));
                        break;
                }
            }
        } else {
            inv.setItem(0, new ItemStack(Material.IRON_SWORD));
            ItemStack pi = new ItemStack(Material.DIAMOND_PICKAXE);
            pi.addEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED, 1);
            inv.setItem(1, pi);
            inv.setItem(2, new ItemStack(Material.STAINED_CLAY, 64, (short)0)); 
            inv.setItem(3, new ItemStack(Material.STAINED_CLAY, 64, (short)0)); 
            inv.setItem(4, new ItemStack(Material.BOW));
            inv.setItem(5, new ItemStack(Material.ARROW, 1));
            inv.setItem(6, new ItemStack(Material.GOLDEN_APPLE, 8));
        }

        p.openInventory(inv);
        p.sendMessage("§aOrganize os itens na sua Hotbar e feche o inventário para salvar!");
        return true;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (!e.getInventory().getTitle().equals(menuTitle)) return;
        
        Player p = (Player) e.getPlayer();
        Inventory inv = e.getInventory();
        String path = "jogadores." + p.getName();

        plugin.getConfig().set(path, null);

        int clayCount = 1;
        for (int slot = 0; slot < 9; slot++) {
            ItemStack item = inv.getItem(slot);
            if (item == null || item.getType() == Material.AIR) continue;

            String identifier = "";
            if (item.getType() == Material.IRON_SWORD) identifier = "SWORD";
            else if (item.getType() == Material.DIAMOND_PICKAXE) identifier = "PICKAXE";
            else if (item.getType() == Material.BOW) identifier = "BOW";
            else if (item.getType() == Material.ARROW) identifier = "ARROW";
            else if (item.getType() == Material.GOLDEN_APPLE) identifier = "APPLE";
            else if (item.getType() == Material.STAINED_CLAY) {
                identifier = "CLAY_" + clayCount;
                clayCount++;
            }

            if (!identifier.isEmpty()) {
                plugin.getConfig().set(path + "." + slot, identifier);
            }
        }

        plugin.saveConfig();
        p.sendMessage("§aConfiguração de inventário salva com sucesso!");
    }
}