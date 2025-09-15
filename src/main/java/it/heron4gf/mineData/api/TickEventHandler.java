package it.heron4gf.mineData.api;

import org.bukkit.entity.Player;
import java.util.Map;

public interface TickEventHandler {
    void handleEvent(TickFrame frame, Player player, String eventType, Map<String, Object> eventData);
}
