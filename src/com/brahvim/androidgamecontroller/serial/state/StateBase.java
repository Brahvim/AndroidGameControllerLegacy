package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

/**
 * Every controller element's state object extends this class.
 */
public class StateBase implements Serializable {
    public long millis, configHash;
    public boolean pressed, ppressed;
}
