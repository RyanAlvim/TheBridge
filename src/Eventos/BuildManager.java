package Eventos;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BuildManager {

    private static final Set<UUID> buildPlayers = new HashSet<UUID>();

    public static boolean canBuild(UUID uuid) {
        return buildPlayers.contains(uuid);
    }

    public static void enableBuild(UUID uuid) {
        buildPlayers.add(uuid);
    }

    public static void disableBuild(UUID uuid) {
        buildPlayers.remove(uuid);
    }
}