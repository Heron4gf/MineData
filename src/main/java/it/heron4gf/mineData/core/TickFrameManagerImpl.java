package it.heron4gf.mineData.core;

import it.heron4gf.mineData.api.*;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.nio.file.Path;
import java.util.*;

public class TickFrameManagerImpl implements TickFrameManager {
    private final Map<UUID, BaseTickFrame> activeFrames = new HashMap<>();
    private final List<TickFrameComposer> composers = new ArrayList<>();
    private final Map<String, List<TickEventHandler>> eventHandlers = new HashMap<>();
    private final DataWriter dataWriter;
    private int globalTickCounter = 0;
    private final Map<UUID, EpisodeInfo> playerEpisodes = new HashMap<>();
    
    public TickFrameManagerImpl(DataWriter dataWriter) {
        this.dataWriter = dataWriter;
    }
    
    @Override
    public void startNewTick() {
        globalTickCounter++;
        
        // Create new frames for all online players
        for (Player player : Bukkit.getOnlinePlayers()) {
            EpisodeInfo episode = playerEpisodes.computeIfAbsent(player.getUniqueId(),
                k -> new EpisodeInfo(player.getUniqueId()));
            
            BaseTickFrame frame = new BaseTickFrame(
                player.getUniqueId().toString(),
                globalTickCounter,
                episode.getStartTime(),
                0 // Time step will be updated when writing
            );
            
            // Set episode info if available
            frame.setEpisodeId(getEpisodeId(player));
            
            activeFrames.put(player.getUniqueId(), frame);
        }
    }
    
    @Override
    public void endCurrentTick() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            BaseTickFrame frame = activeFrames.get(player.getUniqueId());
            if (frame != null) {
                // Compose data from all registered composers
                for (TickFrameComposer composer : composers) {
                    composer.compose(frame, player, globalTickCounter);
                }
                
                // Update time step
                frame.setTimeStep(calculateTimeStep(frame));
                
                // Write the frame
                dataWriter.write(frame);
            }
        }
    }
    
    @Override
    public TickFrame getCurrentFrame(UUID playerId) {
        return activeFrames.get(playerId);
    }
    
    @Override
    public void handleEvent(UUID playerId, String eventType, Map<String, Object> eventData) {
        BaseTickFrame frame = activeFrames.get(playerId);
        if (frame != null) {
            // Add event to frame
            frame.addEvent(eventType, eventData);
            
            // Call registered event handlers
            List<TickEventHandler> handlers = eventHandlers.get(eventType);
            if (handlers != null) {
                Player player = Bukkit.getPlayer(playerId);
                if (player != null) {
                    for (TickEventHandler handler : handlers) {
                        handler.handleEvent(frame, player, eventType, eventData);
                    }
                }
            }
        }
    }
    
    @Override
    public void registerComposer(TickFrameComposer composer) {
        composers.add(composer);
    }
    
    @Override
    public void registerEventHandler(String eventType, TickEventHandler handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(handler);
    }
    
    // Helper methods
    private String getEpisodeId(Player player) {
        EpisodeInfo episode = playerEpisodes.computeIfAbsent(player.getUniqueId(),
            k -> new EpisodeInfo(player.getUniqueId()));
        return episode.getEpisodeId();
    }
    
    private static class EpisodeInfo {
        private final UUID playerId;
        @Getter
        private final String episodeId;
        @Getter
        private final long startTime;
        
        public EpisodeInfo(UUID playerId) {
            this.playerId = playerId;
            this.episodeId = playerId.toString() + "_" + UUID.randomUUID().toString();
            this.startTime = System.currentTimeMillis();
        }
    }
    
    private int calculateTimeStep(BaseTickFrame frame) {
        // Simple implementation - could be more sophisticated
        return (int) (frame.getTimestampMs() / 50); // Assuming 20 ticks per second
    }
}
