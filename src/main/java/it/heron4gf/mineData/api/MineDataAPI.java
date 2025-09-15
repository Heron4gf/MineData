package it.heron4gf.mineData.api;

import lombok.NonNull;

import java.util.Map;
import java.util.UUID;

public class MineDataAPI {

    private static TickFrameManager manager;
    
    public static void initialize(@NonNull TickFrameManager manager) {
        MineDataAPI.manager = manager;
    }
    
    public static boolean isInitialized() {
        return manager != null;
    }

    private static void exceptionIfUnitialized() {
        if(!isInitialized()) throw new NullPointerException("TickFrameManager not initialized");
    }

    public static TickFrame getCurrentPlayerFrame(UUID playerId) {
        exceptionIfUnitialized();
        return manager.getCurrentFrame(playerId);
    }
    
    public static void handlePlayerEvent(UUID playerId, String eventType, Map<String, Object> eventData) {
        exceptionIfUnitialized();
        manager.handleEvent(playerId, eventType, eventData);
    }
    
    public static void registerComposer(TickFrameComposer composer) {
        exceptionIfUnitialized();
        manager.registerComposer(composer);
    }
    
    public static void registerEventHandler(String eventType, TickEventHandler handler) {
        exceptionIfUnitialized();
        manager.registerEventHandler(eventType, handler);
    }
}
