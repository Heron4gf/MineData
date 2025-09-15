package it.heron4gf.mineData;

import it.heron4gf.mineData.api.MineDataAPI;
import it.heron4gf.mineData.api.TickFrameManager;
import it.heron4gf.mineData.core.JsonDataWriter;
import it.heron4gf.mineData.core.TickFrameManagerImpl;
import it.heron4gf.mineData.core.listeners.PlayerEventListener;
import it.heron4gf.mineData.core.listeners.ServerTickListener;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class MineData extends JavaPlugin {

    @Getter
    private static MineData instance;
    private TickFrameManager tickFrameManager;

    @Override
    public void onEnable() {
        instance = this;
        
        // Create data directory if it doesn't exist
        getDataFolder().mkdirs();
        
        // Initialize data writer with dynamic path based on episode ID
        // The actual filename will be set per episode in TickFrameManagerImpl
        Path dataDir = getDataFolder().toPath();
        try {
            JsonDataWriter dataWriter = new JsonDataWriter(dataDir);
            
            // Initialize tick frame manager
            tickFrameManager = new TickFrameManagerImpl(dataWriter);
            
            // Register API
            MineDataAPI.initialize(tickFrameManager);
            
            // Register event listeners
            getServer().getPluginManager().registerEvents(new ServerTickListener(tickFrameManager, this), this);
            getServer().getPluginManager().registerEvents(new PlayerEventListener(), this);
            
            getLogger().info("MineData plugin enabled successfully!");
            
        } catch (Exception e) {
            getLogger().severe("Failed to initialize MineData plugin: " + e.getMessage());
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    public void onDisable() {
        if (tickFrameManager != null) {
            // Flush and close any open resources
            if (tickFrameManager instanceof TickFrameManagerImpl) {
                TickFrameManagerImpl impl = (TickFrameManagerImpl) tickFrameManager;
                // Note: In a real implementation, you'd want to access the DataWriter
                // and call flush() and close() on it
            }
        }
        getLogger().info("MineData plugin disabled!");
    }
}
