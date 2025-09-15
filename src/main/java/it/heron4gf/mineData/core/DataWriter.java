package it.heron4gf.mineData.core;

import it.heron4gf.mineData.api.TickFrame;

public interface DataWriter {
    void write(TickFrame frame);
    void flush();
    void close();
}
