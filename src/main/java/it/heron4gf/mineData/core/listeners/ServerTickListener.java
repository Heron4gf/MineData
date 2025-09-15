package it.heron4gf.mineData.core.listeners;

import it.heron4gf.mineData.api.TickFrameManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.plugin.Plugin;

public class ServerTickListener implements Listener {
    private final TickFrameManager manager;
    private final Plugin plugin;
    
    public ServerTickListener(TickFrameManager manager, Plugin plugin) {
        this.manager = manager;
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onTickStart(ServerTickStartEvent event) {
        manager.startNewTick();
    }
    
    @EventHandler
    public void onTickEnd(ServerTickEndEvent event) {
        manager.endCurrentTick();
    }
}
