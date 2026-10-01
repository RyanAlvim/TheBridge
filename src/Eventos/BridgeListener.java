package Eventos;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BridgeListener implements Listener {
	
	@EventHandler
	public void onMove(EntityTargetEvent e) {

	    if (e.getEntity() instanceof Blaze) {

	        Blaze blaze = (Blaze) e.getEntity();

	        if ("§b§lTHE BRIDGE".equals(blaze.getCustomName())) {
	            e.setCancelled(true);
	        }
	    }
	}

    @EventHandler
    public void onTarget(EntityTargetEvent e) {

        if (e.getEntity() instanceof Blaze) {

            Blaze blaze = (Blaze) e.getEntity();

            if (blaze.getCustomName() != null &&
                    blaze.getCustomName().equals("§b§lTHE BRIDGE")) {

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent e) {

        Entity entity = e.getRightClicked();

        if (!(entity instanceof Blaze))
            return;

        if (entity.getCustomName() == null)
            return;

        if (!entity.getCustomName().equals("§b§lTHE BRIDGE"))
            return;

        Inventory inv = Bukkit.createInventory(
                null,
                27,
                "§b§lTHE BRIDGE"
        );

        ItemStack item = new ItemStack(Material.DIAMOND_SWORD);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§b§lTHE BRIDGE");
        item.setItemMeta(meta);

        inv.setItem(13, item);

        e.getPlayer().openInventory(inv);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {

        if (e.getInventory() == null)
            return;

        if (!e.getInventory().getName()
                .equals("§b§lTHE BRIDGE"))
            return;

        e.setCancelled(true);

        if (e.getCurrentItem() == null)
            return;

        if (e.getCurrentItem().getType() != Material.DIAMOND_SWORD)
            return;

        Player p = (Player) e.getWhoClicked();

        p.chat("/entrarbridge");
        p.closeInventory();
    }
}