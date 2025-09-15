# MineData - Minecraft Player Data Collection Library

MineData is a flexible Minecraft plugin library designed for collecting player data in a structured format suitable for machine learning applications. It provides an event-driven architecture that allows other plugins to easily contribute data to each tick frame.

## Features

- **Event-Driven Data Collection**: Collect data immediately when events occur
- **Flexible Data Types**: Support for continuous, categorical, and discrete data
- **JSONL Output**: Data is written in JSON Lines format for easy processing
- **Extensible Architecture**: Easy to add new data sources and event handlers
- **No Session Management**: Focuses on tick-level data collection without complex session tracking

## Quick Start

### 1. Basic Setup

In your main plugin class:

```java
public class MyPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Initialize MineData if not already initialized
        if (!MineDataAPI.isInitialized()) {
            Path dataPath = Paths.get(getDataFolder().getPath(), "my_plugin_data.jsonl");
            JsonDataWriter dataWriter = new JsonDataWriter(dataPath);
            TickFrameManager manager = new TickFrameManagerImpl(dataWriter);
            MineDataAPI.initialize(manager);
            
            // Register event listeners
            getServer().getPluginManager().registerEvents(new ServerTickListener(manager), this);
        }
        
        // Register your event handlers
        MineDataAPI.registerEventHandler("player_move", new MovementEventHandler());
    }
}
```

### 2. Collecting Event Data

In your event handlers:

```java
@EventHandler
public void onPlayerMove(PlayerMoveEvent event) {
    Player player = event.getPlayer();
    UUID playerId = player.getUniqueId();
    
    // Create event data
    Map<String, Object> eventData = new HashMap<>();
    eventData.put("from", event.getFrom());
    eventData.put("to", event.getTo());
    eventData.put("distance", event.getTo().distance(event.getFrom()));
    
    // Handle the event through the API
    MineDataAPI.handlePlayerEvent(playerId, "player_move", eventData);
}
```

### 3. Processing Events in Handlers

Create event handlers to process and store data:

```java
public class MovementEventHandler implements TickEventHandler {
    
    @Override
    public void handleEvent(TickFrame frame, Player player, String eventType, Map<String, Object> eventData) {
        if ("player_move".equals(eventType)) {
            Location from = (Location) eventData.get("from");
            Location to = (Location) eventData.get("to");
            
            // Calculate movement data
            double deltaX = to.getX() - from.getX();
            double deltaY = to.getY() - from.getY();
            double deltaZ = to.getZ() - from.getZ();
            
            // Store in frame's action data
            Map<String, Object> actionData = frame.getData("action", Map.class);
            if (actionData == null) {
                actionData = new HashMap<>();
                frame.setData("action", actionData);
            }
            
            Map<String, Object> movement = new HashMap<>();
            movement.put("delta_x", deltaX);
            movement.put("delta_y", deltaY);
            movement.put("delta_z", deltaZ);
            movement.put("speed", Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ));
            
            actionData.put("movement", movement);
        }
    }
}
```

### 4. Using Composers for Periodic Data

For data that needs to be collected periodically (not just on events):

```java
public class PlayerStateComposer implements TickFrameComposer {
    
    @Override
    public void compose(TickFrame frame, Player player, long globalTick) {
        // Collect player state data
        Map<String, Object> state = new HashMap<>();
        state.put("hp", player.getHealth());
        state.put("food", player.getFoodLevel());
        state.put("location", player.getLocation());
        state.put("inventory", player.getInventory().getContents());
        
        frame.setData("state", state);
    }
}
```

Register the composer:

```java
MineDataAPI.registerComposer(new PlayerStateComposer());
```

## Data Format

Each tick frame is written as a JSON object with the following structure:

```json
{
  "episode_id": "player_uuid_timestamp",
  "life_id": 0,
  "t": 123,
  "global_tick": 123456789,
  "timestamp_ms": 1694710000000,
  "state": {
    "hp": 20.0,
    "food": 20,
    "location": {
      "x": 100.5,
      "y": 64.0,
      "z": -200.5,
      "yaw": 90.0,
      "pitch": 0.0
    }
  },
  "action": {
    "movement": {
      "delta_x": 0.0,
      "delta_y": 0.0,
      "delta_z": 1.0,
      "speed": 1.0
    }
  },
  "events": [
    {
      "type": "player_move",
      "data": {
        "from": {...},
        "to": {...},
        "distance": 1.0
      },
      "timestamp": 1694710000000
    }
  ],
  "reward": 0.0,
  "done": false,
  "timeout": false
}
```

## API Reference

### MineDataAPI

The main entry point for the library.

- `initialize(TickFrameManager manager)`: Initialize the API with a tick frame manager
- `getCurrentPlayerFrame(UUID playerId)`: Get the current tick frame for a player
- `handlePlayerEvent(UUID playerId, String eventType, Map<String, Object> eventData)`: Handle a player event
- `registerComposer(TickFrameComposer composer)`: Register a data composer
- `registerEventHandler(String eventType, TickEventHandler handler)`: Register an event handler

### TickFrame

Represents a single tick of player data.

- `getData(String key)`: Get data by key
- `setData(String key, Object value)`: Set data by key
- `addEvent(String eventType, Map<String, Object> eventData)`: Add an event to the frame
- `getEvents(String eventType)`: Get events of a specific type
- `getAllEvents()`: Get all events in the frame

### TickFrameManager

Manages the lifecycle of tick frames.

- `startNewTick()`: Start a new tick for all players
- `endCurrentTick()`: End the current tick and write frames
- `handleEvent(UUID playerId, String eventType, Map<String, Object> eventData)`: Handle an event
- `registerComposer(TickFrameComposer composer)`: Register a composer
- `registerEventHandler(String eventType, TickEventHandler handler)`: Register an event handler

## Best Practices

1. **Event Types**: Use descriptive event types like "player_move", "player_interact", "player_damage"
2. **Data Structure**: Keep event data structures consistent and well-documented
3. **Performance**: Be mindful of performance when collecting large amounts of data
4. **Error Handling**: Add proper error handling in your event handlers
5. **Data Filtering**: Consider filtering or sampling data if you collect too much

## Example: Complete Movement Collection

Here's a complete example of collecting movement data:

```java
public class MovementPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        // Register movement event handler
        MineDataAPI.registerEventHandler("player_move", new MovementEventHandler());
    }
}

public class MovementEventHandler implements TickEventHandler {
    
    private final Map<UUID, Location> previousLocations = new HashMap<>();
    
    @Override
    public void handleEvent(TickFrame frame, Player player, String eventType, Map<String, Object> eventData) {
        if ("player_move".equals(eventType)) {
            UUID playerId = player.getUniqueId();
            Location current = player.getLocation();
            Location previous = previousLocations.get(playerId);
            
            if (previous != null) {
                // Calculate movement deltas
                double deltaX = current.getX() - previous.getX();
                double deltaY = current.getY() - previous.getY();
                double deltaZ = current.getZ() - previous.getZ();
                
                // Calculate yaw and pitch deltas
                float deltaYaw = current.getYaw() - previous.getYaw();
                float deltaPitch = current.getPitch() - previous.getPitch();
                
                // Calculate speed
                double distance = Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
                
                // Store movement data
                Map<String, Object> movement = new HashMap<>();
                movement.put("delta_x", deltaX);
                movement.put("delta_y", deltaY);
                movement.put("delta_z", deltaZ);
                movement.put("delta_yaw", deltaYaw);
                movement.put("delta_pitch", deltaPitch);
                movement.put("speed", distance);
                
                // Add to frame's action data
                Map<String, Object> actionData = frame.getData("action", Map.class);
                if (actionData == null) {
                    actionData = new HashMap<>();
                    frame.setData("action", actionData);
                }
                actionData.put("movement", movement);
            }
            
            // Update previous location
            previousLocations.put(playerId, current);
        }
    }
}
```

## Contributing

This library is designed to be extensible. If you have suggestions for improvements or new features, please open an issue or submit a pull request.

## License

This project is licensed under the MIT License.
