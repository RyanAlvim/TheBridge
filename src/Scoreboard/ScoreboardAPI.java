package Scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import Comandos.EntrarBridgeCommand;
import Comandos.Tag;
import Main.Main;
import TheBridge.BridgeGame;

public class ScoreboardAPI {
    private static final String[] TITLES = {"§b§lBAPPEP MC", "§f§lBAPPEP MC"};
    private static final String[] BRIDGE_TITLES = {"§b§lTHE BRIDGE", "§f§lTHE BRIDGE"};
    private static int anim = 0;

    public static void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                anim = (anim + 1) % 2;
                for (Player p : Bukkit.getOnlinePlayers()) {
                    update(p);
                }
            }
        }.runTaskTimer(Main.getInstance(), 0L, 10L);
    }

    public static void update(Player player) {
        Scoreboard sb = player.getScoreboard();

        BridgeGame game = BridgeGame.getGameByPlayer(player.getUniqueId());
        boolean isSpectating = false;
        if (game == null) {
            game = BridgeGame.getGameBySpectator(player.getUniqueId());
            isSpectating = (game != null);
        }

        if (game == null && sb.getObjective("bg_health") != null) {
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        if (isSpectating && sb.getObjective("bg_health") != null) {
            sb = Bukkit.getScoreboardManager().getNewScoreboard();
        }

        Objective obj = sb.getObjective("sidebar");
        if (obj == null) {
            obj = sb.registerNewObjective("sidebar", "dummy");
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        }
        
        for (String entry : sb.getEntries()) {
            sb.resetScores(entry);
        }

        boolean inQueue = EntrarBridgeCommand.isInQueue(player);

        if (inQueue && game == null) {
            obj.setDisplayName(BRIDGE_TITLES[anim]);
            obj.getScore("§1").setScore(10);
            obj.getScore("§fJogadores: §b" + EntrarBridgeCommand.getQueueSize() + "/2").setScore(9);
            obj.getScore("§f ").setScore(8);
            obj.getScore("§fAguardando ...").setScore(7);
            obj.getScore("§f  ").setScore(6);
            obj.getScore("§fModo: §b1v1").setScore(5);
            obj.getScore("§fWinStreak: §b0").setScore(4);
            obj.getScore("§3").setScore(3);
            obj.getScore("§ewww.garkey.com").setScore(0);
        } 
        else if (game == null) {
            obj.setDisplayName(TITLES[anim]);
            obj.getScore("§1").setScore(10);
            obj.getScore("§7Rank: " + Tag.getPrefix(player)).setScore(9);
            obj.getScore("§2").setScore(8);
            obj.getScore("§7Winstreak: §b0").setScore(7);
            obj.getScore("§7Vitórias: §b0").setScore(6);
            obj.getScore("§7Derrotas: §b0").setScore(5);
            obj.getScore("§7Online: §b" + Bukkit.getOnlinePlayers().size()).setScore(4);
            obj.getScore("§3 ").setScore(3);
            obj.getScore("§ewww.garkey.com").setScore(0);
        } 
        else {
            obj.setDisplayName(BRIDGE_TITLES[anim]);
            obj.getScore("§1").setScore(10);
            obj.getScore("§fTempo Restante: §b" + game.partidaTempoFormatado).setScore(9);
            obj.getScore("§1 ").setScore(8);
            
            String rBar = "§c➜" + new String(new char[game.redPoints]).replace("\0", "⬛") + new String(new char[5 - game.redPoints]).replace("\0", "⬜");
            obj.getScore(rBar).setScore(7);
            
            String bBar = "§9➜" + new String(new char[game.bluePoints]).replace("\0", "⬛") + new String(new char[5 - game.bluePoints]).replace("\0", "⬜");
            obj.getScore(bBar).setScore(6);
            obj.getScore("§2").setScore(5);

            int kills = 0;
            if (game.redPlayer != null && player.getUniqueId().equals(game.redPlayer.getUniqueId())) {
                kills = game.kills_red;
            } else if (game.bluePlayer != null && player.getUniqueId().equals(game.bluePlayer.getUniqueId())) {
                kills = game.kills_blue;
            }

            int winStreak = 0; 

            obj.getScore("§fKills: §b" + kills).setScore(3);
            obj.getScore("§fWinstreak: §b" + winStreak).setScore(2);
            obj.getScore("§3 ").setScore(1);
            obj.getScore("§ewww.garkey.com").setScore(0);
        }
        
        player.setScoreboard(sb);
    }
}