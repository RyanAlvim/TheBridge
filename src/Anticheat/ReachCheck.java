package Anticheat;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.util.Vector;

import Punicao.KickManager;

import java.util.*;

public class ReachCheck implements Listener {

    private static final double BASE_REACH_LIMIT = 3.2;

    private static final Map<UUID, Double> violationBuffer = new HashMap<>();
    
    private static final Map<UUID, LinkedList<Location>> locationHistory = new HashMap<>();

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();

        if (attacker.getGameMode() == GameMode.CREATIVE || attacker.getGameMode() == GameMode.SPECTATOR) {
            return;
        }

        int pingAttacker = getPing(attacker);
        int pingVictim = getPing(victim);

        int totalPing = pingAttacker + pingVictim;
        double pingBuffer = Math.min((totalPing / 100.0) * 0.15, 0.35); // Máximo 0.35m extra para lag extremo

        double victimSpeed = victim.getVelocity().length();
        double speedBuffer = Math.min(victimSpeed * 0.25, 0.30);

        double dynamicLimit = BASE_REACH_LIMIT + pingBuffer + speedBuffer;

        List<Location> possibleLocations = getCandidateLocations(victim);
        Location attackerEye = attacker.getEyeLocation();

        double lowestReachFound = Double.MAX_VALUE;

        for (Location loc : possibleLocations) {
            double reach = calculateBoxDistance(attackerEye, loc);
            if (reach < lowestReachFound) {
                lowestReachFound = reach;
            }
        }

        if (lowestReachFound > dynamicLimit) {
            event.setCancelled(true);

            UUID uuid = attacker.getUniqueId();
            double excess = lowestReachFound - dynamicLimit;
            
            double currentBuffer = violationBuffer.getOrDefault(uuid, 0.0) + excess;
            violationBuffer.put(uuid, currentBuffer);

            Bukkit.getLogger().warning("[Anticheat] Hit de " + attacker.getName() + " bloqueado! Distância: " 
                + String.format("%.2f", lowestReachFound) + "m (Limite dinâmico: " 
                + String.format("%.2f", dynamicLimit) + "m) | Buffer: " + String.format("%.2f", currentBuffer));

       
            if (lowestReachFound > 3.2 || currentBuffer >= 3.0) {
                violationBuffer.remove(uuid);

                KickManager.expulsarJogador(
                    attacker.getName(), 
                    "Uso de trapaças / Reach (" + String.format("%.2f", lowestReachFound) + "m > 3.2m)", 
                    "CONSOLE"
                );
            }
        } else {
            UUID uuid = attacker.getUniqueId();
            if (violationBuffer.containsKey(uuid)) {
                double newBuf = Math.max(0.0, violationBuffer.get(uuid) - 0.5);
                if (newBuf <= 0) {
                    violationBuffer.remove(uuid);
                } else {
                    violationBuffer.put(uuid, newBuf);
                }
            }
        }
    }


    private double calculateBoxDistance(Location eye, Location victimLoc) {
        double minX = victimLoc.getX() - 0.35;
        double maxX = victimLoc.getX() + 0.35;
        double minY = victimLoc.getY() - 0.1;
        double maxY = victimLoc.getY() + 1.9;
        double minZ = victimLoc.getZ() - 0.35;
        double maxZ = victimLoc.getZ() + 0.35;

        double closestX = clamp(eye.getX(), minX, maxX);
        double closestY = clamp(eye.getY(), minY, maxY);
        double closestZ = clamp(eye.getZ(), minZ, maxZ);

        Vector eyeVec = eye.toVector();
        Vector closestVec = new Vector(closestX, closestY, closestZ);

        return eyeVec.distance(closestVec);
    }

    private double clamp(double val, double min, double max) {
        return Math.max(min, Math.min(max, val));
    }

    private List<Location> getCandidateLocations(Player victim) {
        List<Location> candidates = new ArrayList<>();
        candidates.add(victim.getLocation());

        LinkedList<Location> history = locationHistory.get(victim.getUniqueId());
        if (history != null && !history.isEmpty()) {
            candidates.addAll(history);
        }
        return candidates;
    }

    public static void recordLocation(Player player) {
        UUID id = player.getUniqueId();
        locationHistory.putIfAbsent(id, new LinkedList<>());
        LinkedList<Location> list = locationHistory.get(id);

        list.addFirst(player.getLocation().clone());
        if (list.size() > 10) {
            list.removeLast();
        }
    }

    private int getPing(Player player) {
        try {
            Object entityPlayer = player.getClass().getMethod("getHandle").invoke(player);
            return (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
        } catch (Exception e) {
            return 50;
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        locationHistory.remove(e.getPlayer().getUniqueId());
        violationBuffer.remove(e.getPlayer().getUniqueId());
    }
}