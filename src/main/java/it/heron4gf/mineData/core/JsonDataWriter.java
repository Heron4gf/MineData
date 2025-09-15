package it.heron4gf.mineData.core;

import it.heron4gf.mineData.api.TickFrame;
import com.google.gson.Gson;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonDataWriter implements DataWriter {
    private final Path dataDir;
    private final Map<String, BufferedWriter> episodeWriters = new HashMap<>();
    private final Gson gson = new Gson();
    
    public JsonDataWriter(Path dataDir) throws IOException {
        this.dataDir = dataDir;
        Files.createDirectories(dataDir);
    }
    
    @Override
    public void write(TickFrame frame) {
        String episodeId = frame.getEpisodeId();
        if (episodeId == null || episodeId.isEmpty()) {
            System.err.println("Cannot write frame - missing episodeId");
            return;
        }

        try {
            BufferedWriter writer = episodeWriters.computeIfAbsent(episodeId, id -> {
                try {
                    Path filePath = dataDir.resolve("episode_" + id + ".jsonl");
                    return Files.newBufferedWriter(filePath);
                } catch (IOException e) {
                    System.err.println("Error creating writer for episode " + id + ": " + e.getMessage());
                    return null;
                }
            });

            if (writer != null) {
                String json = convertToJson(frame);
                writer.write(json);
                writer.newLine();
                writer.flush();
            }
        } catch (IOException e) {
            System.err.println("Error writing tick frame: " + e.getMessage());
        }
    }
    
    private String convertToJson(TickFrame frame) {
        Map<String, Object> jsonMap = new HashMap<>();
        
        // Basic metadata
        jsonMap.put("episode_id", frame.getEpisodeId());
        jsonMap.put("t", frame.getTimeStep());
        jsonMap.put("global_tick", frame.getGlobalTick());
        jsonMap.put("timestamp_ms", frame.getTimestampMs());
        
        // State and action data
        jsonMap.put("state", frame.getData("state"));
        jsonMap.put("action", frame.getData("action"));
        
        // Events
        jsonMap.put("events", frame.getAllEvents());
        
        // Completion status
        jsonMap.put("reward", frame.getReward());
        jsonMap.put("done", frame.isDone());
        jsonMap.put("timeout", frame.isTimeout());
        
        return gson.toJson(jsonMap);
    }
    
    @Override
    public void flush() {
        episodeWriters.values().forEach(writer -> {
            try {
                if (writer != null) {
                    writer.flush();
                }
            } catch (IOException e) {
                System.err.println("Error flushing writer: " + e.getMessage());
            }
        });
    }
    
    @Override
    public void close() {
        episodeWriters.values().forEach(writer -> {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing writer: " + e.getMessage());
            }
        });
        episodeWriters.clear();
    }
}
