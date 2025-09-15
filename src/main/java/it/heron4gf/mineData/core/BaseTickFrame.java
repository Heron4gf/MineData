package it.heron4gf.mineData.core;

import it.heron4gf.mineData.api.TickFrame;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Getter
public class BaseTickFrame implements TickFrame {

    private final String playerId;
    private final long globalTick;
    private final long timestampMs;

    @Setter
    @NonNull // marker to include this into the constructor
    private int timeStep;

    private final Map<String, Object> data = new HashMap<>();
    private final Map<String, List<Map<String, Object>>> events = new HashMap<>();

    @Setter
    private String episodeId;

    @Setter
    private boolean done = false;

    @Setter
    private boolean timeout = false;

    @Setter
    private double reward = 0.0;

    @Override
    public Object getData(String key) {
        return data.get(key);
    }

    @Override
    public void setData(String key, Object value) {
        data.put(key, value);
    }

    @Override
    public <T> T getData(String key, Class<T> type) {
        Object value = data.get(key);
        return type.isInstance(value) ? type.cast(value) : null;
    }

    @Override
    public void addEvent(String eventType, Map<String, Object> eventData) {
        events.computeIfAbsent(eventType, k -> new ArrayList<>()).add(eventData);
    }

    @Override
    public Map<String, Object> getEvents(String eventType) {
        List<Map<String, Object>> eventList = events.get(eventType);
        return eventList != null && !eventList.isEmpty() ? eventList.get(eventList.size() - 1) : null;
    }

    @Override
    public List<Map<String, Object>> getAllEvents() {
        List<Map<String, Object>> allEvents = new ArrayList<>();
        events.values().forEach(allEvents::addAll);
        return allEvents;
    }
}