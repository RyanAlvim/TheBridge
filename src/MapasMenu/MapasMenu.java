package MapasMenu;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MapasMenu implements Listener {

    private static final String MENU_TITLE = "§8Selecione o Mapa";

    public static void open(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 4, MENU_TITLE);

        String[] nomesMapas = {
            "Nethermap", "Urban"
        };

        int slot = 10;
        for (String nome : nomesMapas) {
            if (slot == 17) slot = 19;
            if (slot == 26) slot = 28;

            inv.setItem(slot, createItem(Material.MAP, "§b"+ nome));
            slot++;
        }

        p.openInventory(inv);
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals(MENU_TITLE)) {
            e.setCancelled(true); 

            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;

            Player p = (Player) e.getWhoClicked();
            String displayName = e.getCurrentItem().getItemMeta().getDisplayName();

           
            p.closeInventory();
            
            if(displayName.contains("Nether")) {
            	p.performCommand("entrarbridge nethermap");
            }else if (displayName.contains("Urban")) {
            	p.performCommand("entrarbridge urban");

            }
           
        }
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}