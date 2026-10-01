package Eventos;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import Main.Main;
import MapasMenu.MapasMenu;
import TheBridge.BridgeGame;

public class JoinItems implements Listener {

    private final String MENU_JOGOS = "§8Escolha seu Jogo";
    private final String MENU_CONFIG = "§8Configurações";

    public static void darItensLobby(Player p) {
        p.getInventory().clear();
        p.getInventory().setItem(0, createItem(Material.NETHER_STAR, "§6§lRANKS"));
        p.getInventory().setItem(2, createItem(Material.STORAGE_MINECART, "§a§lMENU"));
        p.getInventory().setItem(4, createItem(Material.COMPASS, "§b§lJOGOS"));
        p.getInventory().setItem(6, createItem(Material.STRING, "§e§lCONFIGURAÇÕES"));
        p.getInventory().setItem(8, createItem(Material.JUKEBOX, "§d§lEFEITOS"));
        p.updateInventory();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
    	TabManager.updateAll();
        darItensLobby(e.getPlayer());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        ItemStack item = e.getItem();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK ||
            e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            
            if (item == null || !item.hasItemMeta()) return;

            String displayName = item.getItemMeta().getDisplayName();

            if (item.getType() == Material.COMPASS && displayName.equals("§b§lJOGOS")) {
                e.setCancelled(true);
                openGamesMenu(p);
                return;
            }

            if (item.getType() == Material.STRING && displayName.equals("§e§lCONFIGURAÇÕES")) {
                e.setCancelled(true);
                openConfigMenu(p);
                return;
            }
        }
    }

    @EventHandler
    public void onMenuClick(InventoryClickEvent e) {
        String title = e.getView().getTitle();
        
        if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
        Player p = (Player) e.getWhoClicked();

        if (title.equals(MENU_JOGOS)) {
            e.setCancelled(true); 
            Material clickedType = e.getCurrentItem().getType();

            if (clickedType == Material.EMERALD) {
                p.closeInventory();
                p.performCommand("entrarbridge"); 
            } else if (clickedType == Material.CAKE) {
                p.closeInventory(); 
                MapasMenu.open(p);  
            }
            return;
        }

        if (title.equals(MENU_CONFIG)) {
            e.setCancelled(true);
            ItemMeta meta = e.getCurrentItem().getItemMeta();
            if (meta == null || !meta.hasDisplayName()) return;
            
            String displayName = meta.getDisplayName();

            if (displayName.equals("§aTELL") ) {
                p.closeInventory();
                p.performCommand("tell toggle");
            } else if (displayName.equals("§aINVENTÁRIO")) {
                p.closeInventory();
                p.performCommand("editarinventario");
            }else if(displayName.equals("§aSPEC")) {
            	p.closeInventory();
            	p.performCommand("spec");
            	
            }
        }
    }

    private void openGamesMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, MENU_JOGOS);
        inv.setItem(11, createItem(Material.EMERALD, "§aJogar Aleatório"));
        inv.setItem(15, createItem(Material.CAKE, "§aEscolher Mapa"));
        p.openInventory(inv);
    }

    private void openConfigMenu(Player p) {
        Inventory inv = Bukkit.createInventory(null, 9 * 3, MENU_CONFIG);
        inv.setItem(11, createItem(Material.FEATHER, "§aTELL"));
        inv.setItem(13, createItem(Material.SULPHUR, "§aSPEC")); // Mude para Material.GUNPOWDER se usar 1.13+
        inv.setItem(15, createItem(Material.SLIME_BALL, "§aINVENTÁRIO"));
        p.openInventory(inv);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }
    
    @EventHandler
    public void sair(PlayerQuitEvent e) {
    	Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> TabManager.updateAll(), 1L);        e.setQuitMessage(null);
    }
    
    @EventHandler
    public void quebrar(BlockBreakEvent e) {
        Player p = e.getPlayer();
        if (BridgeGame.getGameByPlayer(p.getUniqueId()) == null) {
            e.setCancelled(true);
        }
    }
    
    @EventHandler
    public void colocar(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        if (BridgeGame.getGameByPlayer(p.getUniqueId()) == null) {
            e.setCancelled(true);
        }
    }

    public static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        item.setItemMeta(meta);
        return item;
    }
}