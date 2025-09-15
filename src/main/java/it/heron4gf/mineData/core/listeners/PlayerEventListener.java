package it.heron4gf.mineData.core.listeners;

import it.heron4gf.mineData.api.MineDataAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerEventListener implements Listener {
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Initialize player data if needed
        // This could be used to set initial episode_id or other metadata
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        
        // Mark the frame as done if needed
        // This could be used to finalize an episode
        Map<String, Object> quitEvent = new HashMap<>();
        quitEvent.put("reason", "quit");
        MineDataAPI.handlePlayerEvent(playerId, "player_quit", quitEvent);
    }
}
