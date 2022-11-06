package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

/**
 * Every controller element's state object extends this class.
 */
public class StateBase implements Serializable {
    public final static long serialVersionUID = -8801139154962962076L;

    public long millis;
    public int configHash;
    public boolean pressed, ppressed; // May have different meanings. May be refreshed
    // either in the loop, or during events. Depends on the implementation.

    public StateBase() {
    }
}
