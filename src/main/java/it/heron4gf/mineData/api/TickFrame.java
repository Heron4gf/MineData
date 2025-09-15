package it.heron4gf.mineData.api;

import java.util.List;
import java.util.Map;

public interface TickFrame {
    // Data access
    Object getData(String key);
    void setData(String key, Object value);
    <T> T getData(String key, Class<T> type);
    
    // Metadata
    String getPlayerId();
    long getGlobalTick();
    long getTimestampMs();
    int getTimeStep();
    void setTimeStep(int timeStep);
    
    // Episode tracking (optional, managed by external systems)
    String getEpisodeId();
    void setEpisodeId(String episodeId);
    
    // Completion status
    boolean isDone();
    void setDone(boolean done);
    boolean isTimeout();
    void setTimeout(boolean timeout);
    double getReward();
    void setReward(double reward);
    
    // Event data collection
    void addEvent(String eventType, Map<String, Object> eventData);
    Map<String, Object> getEvents(String eventType);
    List<Map<String, Object>> getAllEvents();
}
