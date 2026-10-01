package Comandos;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Tag implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();

        if (player.getScoreboard() == Bukkit.getScoreboardManager().getMainScoreboard()) {
            player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }

        updateTag(player);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        String prefix = getPrefix(e.getPlayer());

        e.setFormat(
                prefix +
                e.getPlayer().getName() +
                " §7» §f" +
                e.getMessage()
        );
    }

    public static void updateTag(Player player) {

        String teamName;
        String prefix;
         if (player.hasPermission("tag.admin")) {
            teamName = "02ADMIN";
            prefix = "§4§lADMIN §4"; 
        } 
        else if (player.hasPermission("tag.mod")) {
            teamName = "03MOD";
            prefix = "§5§lMOD §5"; 
        } 
        else if (player.hasPermission("tag.helper")) {
            teamName = "04HELPER";
            prefix = "§9§lHELPER §9";
        } 
        else if (player.hasPermission("tag.partner")) {
            teamName = "05PARTNER";
            prefix = "§b§lPARTNER §b";
        } 
        else if (player.hasPermission("tag.beta")) {
            teamName = "06BETA";
            prefix = "§1§lBETA §1";
        } 
        else if (player.hasPermission("tag.2026")) {
            teamName = "07ANO2026";
            prefix = "§b§l2026 §b";
        } 
        else if (player.hasPermission("tag.natal")) {
            teamName = "08NATAL";
            prefix = "§c§lNATAL §c";
        } 
        else if (player.hasPermission("tag.summer")) {
            teamName = "09SUMMER";
            prefix = "§6§lSUMMER §6";
        } 
        else if (player.hasPermission("tag.bloom")) {
            teamName = "10BLOOM";
            prefix = "§5§lBLOOM §5";
        } 
        else if (player.hasPermission("tag.frost")) {
            teamName = "11FROST";
            prefix = "§b§lFROST §b";
        } 
        else if (player.hasPermission("tag.vip")) {
            teamName = "12VIP";
            prefix = "§a§lVIP §a";
        } 
        else {
            teamName = "99MEMBRO";
            prefix = "§7";
        }

        setTag(player, teamName, prefix);
    }

    public static void setTag(Player player, String teamName, String prefix) {

        for (Player viewer : Bukkit.getOnlinePlayers()) {

            Scoreboard scoreboard = viewer.getScoreboard();

            if (scoreboard == Bukkit.getScoreboardManager().getMainScoreboard()) {
                scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                viewer.setScoreboard(scoreboard);
            }

            for (Team team : scoreboard.getTeams()) {
                team.removeEntry(player.getName());
            }

            Team team = scoreboard.getTeam(teamName);

            if (team == null) {
                team = scoreboard.registerNewTeam(teamName);
            }

            team.setPrefix(prefix);
            team.addEntry(player.getName());
        }

        player.setPlayerListName(prefix + player.getName());
    }

    public static String getPrefix(Player player) {

        Scoreboard scoreboard = player.getScoreboard();

        for (Team team : scoreboard.getTeams()) {

            if (team.hasEntry(player.getName())) {
                return team.getPrefix();
            }
        }

        return "";
    }
}