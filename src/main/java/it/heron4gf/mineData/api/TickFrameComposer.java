package it.heron4gf.mineData.api;

import org.bukkit.entity.Player;

public interface TickFrameComposer {
    void compose(TickFrame frame, Player player, long globalTick);
}
