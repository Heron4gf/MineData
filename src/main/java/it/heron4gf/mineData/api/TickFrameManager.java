package it.heron4gf.mineData.api;

import java.util.Map;
import java.util.UUID;

public interface TickFrameManager {
    // Current frame access
    TickFrame getCurrentFrame(UUID playerId);
    
    // Event handling
    void handleEvent(UUID playerId, String eventType, Map<String, Object> eventData);
    
    // Frame lifecycle
    void startNewTick();
    void endCurrentTick();
    
    // Registration
    void registerComposer(TickFrameComposer composer);
    void registerEventHandler(String eventType, TickEventHandler handler);
}
