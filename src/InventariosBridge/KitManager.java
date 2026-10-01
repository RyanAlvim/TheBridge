package InventariosBridge;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import Main.Main;

import java.util.List;

public class KitManager {

    public static void giveRedKit(Player p) {
        giveKit(p, "RED", (short) 14);
    }

    public static void giveBlueKit(Player p) {
        giveKit(p, "BLUE", (short) 11);
    }

    private static void giveKit(Player p, String teamName, short clayData) {
        p.getInventory().clear();
        FileConfiguration config = Main.getInstance().getConfig();
        String path = "jogadores." + p.getName();

        if (config.contains(path)) {
            for (int i = 0; i < 9; i++) {
                String itemType = config.getString(path + "." + i);
                if (itemType == null) continue;

                switch (itemType) {
                    case "SWORD":
                        p.getInventory().setItem(i, new ItemStack(Material.IRON_SWORD));
                        break;
                    case "PICKAXE":
                        ItemStack pi = new ItemStack(Material.DIAMOND_PICKAXE);
                        pi.addEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED, 1);
                        p.getInventory().setItem(i, pi);
                        break;
                    case "CLAY_1":
                        p.getInventory().setItem(i, new ItemStack(Material.STAINED_CLAY, 64, clayData));
                        break;
                    case "CLAY_2":
                        p.getInventory().setItem(i, new ItemStack(Material.STAINED_CLAY, 64, clayData));
                        break;
                    case "BOW":
                        p.getInventory().setItem(i, new ItemStack(Material.BOW));
                        break;
                    case "ARROW":
                        p.getInventory().setItem(i, new ItemStack(Material.ARROW, 1));
                        break;
                    case "APPLE":
                        p.getInventory().setItem(i, new ItemStack(Material.GOLDEN_APPLE, 8));
                        break;
                }
            }
        } else {
            p.getInventory().setItem(0, new ItemStack(Material.IRON_SWORD));
            ItemStack pi = new ItemStack(Material.DIAMOND_PICKAXE);
            pi.addEnchantment(org.bukkit.enchantments.Enchantment.DIG_SPEED, 1);
            p.getInventory().setItem(1, pi);
            p.getInventory().setItem(2, new ItemStack(Material.STAINED_CLAY, 64, clayData));
            p.getInventory().setItem(3, new ItemStack(Material.STAINED_CLAY, 64, clayData));
            p.getInventory().setItem(4, new ItemStack(Material.BOW));
            p.getInventory().setItem(5, new ItemStack(Material.ARROW, 1));
            p.getInventory().setItem(6, new ItemStack(Material.GOLDEN_APPLE, 8));
        }


        p.updateInventory();
    }

    
}