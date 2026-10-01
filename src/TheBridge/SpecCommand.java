package TheBridge;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.List;
public class SpecCommand implements CommandExecutor {
    private final JavaPlugin plugin; 
    private final String CONFIG_PATH = "spec-desativado";
    public SpecCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    @Override
    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cApenas jogadores podem usar este comando.");
            return true;
        }
        Player spectator = (Player) sender;
        String specUUID = spectator.getUniqueId().toString();
        List<String> listaDesativados = plugin.getConfig().getStringList(CONFIG_PATH);
        if (args.length == 0 || (args.length == 1 && args[0].equalsIgnoreCase("toggle"))) {
            if (listaDesativados.contains(specUUID)) {
                listaDesativados.remove(specUUID);
                spectator.sendMessage("§a[Espectador] Agora outros jogadores PODEM assistir suas partidas!");
            } else {
                listaDesativados.add(specUUID);
                spectator.sendMessage("§c[Espectador] Agora outros jogadores NÃO PODEM mais assistir suas partidas!");
            }
            
            plugin.getConfig().set(CONFIG_PATH, listaDesativados);
            plugin.saveConfig();
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null || !target.isOnline()) {
            spectator.sendMessage("§cJogador não encontrado ou está offline.");
            return true;
        }
        String targetUUID = target.getUniqueId().toString();
        if (listaDesativados.contains(targetUUID)) {
            spectator.sendMessage("§cEste jogador desativou a opção de ser assistido.");
            return true;
        }
        BridgeGame game = BridgeGame.getGameByPlayer(target.getUniqueId());
        if (game == null || !game.gameStarted) {
            spectator.sendMessage("§cEste jogador não está em uma partida ativa do The Bridge.");
            return true;
        }
        if (spectator.equals(game.redPlayer) || spectator.equals(game.bluePlayer)) {
            spectator.sendMessage("§cVocê está jogando esta partida e não pode entrar em modo espectador!");
            return true;
        }
        spectator.setGameMode(GameMode.SPECTATOR);

      
        game.addSpectator(spectator, target);

        spectator.showPlayer(game.redPlayer);
        spectator.showPlayer(game.bluePlayer);
        game.redPlayer.hidePlayer(spectator);
        game.bluePlayer.hidePlayer(spectator);
        spectator.sendMessage("§aVocê agora está assistindo a partida de §e" + game.redPlayer.getName() + " §avs §e" + game.bluePlayer.getName() + "§a.");
        return true;
    }
}