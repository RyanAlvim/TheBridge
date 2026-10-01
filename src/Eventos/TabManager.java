package Eventos;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

public class TabManager {


    public static void setTab(Player player) {
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        String headerText = colorize(
            "\n" +
            "&b&lBAPPEP MC\n" +
            "&7jogar.bappepmc.com\n"
        );

        String footerText = colorize(
            "\n" +
            "&fOnline: &a" + online + "&7/&c" + max + "\n" +
            "&fLoja: &ebappepmc.com.br\n" +
            "&fDiscord: &bdiscord.gg/bappepmc\n"
        );

        sendHeaderFooter(player, headerText, footerText);
    }


    public static void updateAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            setTab(player);
        }
    }

    private static void sendHeaderFooter(Player player, String headerJson, String footerJson) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

            Class<?> chatSerializer = Class.forName("net.minecraft.server." + version + ".IChatBaseComponent$ChatSerializer");
            Class<?> packetClass = Class.forName("net.minecraft.server." + version + ".PacketPlayOutPlayerListHeaderFooter");

            Object headerComponent = chatSerializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + headerJson + "\"}");
            Object footerComponent = chatSerializer.getMethod("a", String.class).invoke(null, "{\"text\":\"" + footerJson + "\"}");

            Constructor<?> constructor = packetClass.getConstructor();
            Object packet = constructor.newInstance();

            Field headerField = packetClass.getDeclaredField("a");
            headerField.setAccessible(true);
            headerField.set(packet, headerComponent);

            Field footerField = packetClass.getDeclaredField("b");
            footerField.setAccessible(true);
            footerField.set(packet, footerComponent);

            Object craftPlayer = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer").cast(player);
            Object handle = craftPlayer.getClass().getMethod("getHandle").invoke(craftPlayer);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);

            Class<?> packetInterface = Class.forName("net.minecraft.server." + version + ".Packet");
            playerConnection.getClass().getMethod("sendPacket", packetInterface).invoke(playerConnection, packet);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}