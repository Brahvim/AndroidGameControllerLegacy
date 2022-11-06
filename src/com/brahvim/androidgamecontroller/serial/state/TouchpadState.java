package com.brahvim.androidgamecontroller.serial.state;

import processing.core.PVector;

public class TouchpadState extends StateBase {
    // NO idea why I would need to use abstraction like this here...
    // Better do it anyway! This is software development!
    public PVector[] touches;

    public TouchpadState() {
        super();
    }

    /*
     * public int count() {
     * return this.touches.length;
     * }
     *
     * public PVector[] allTouches() {
     * return this.touches;
     * }
     */
}
