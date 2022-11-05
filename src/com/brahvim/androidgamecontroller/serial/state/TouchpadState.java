package com.brahvim.androidgamecontroller.serial.state;

import processing.core.PVector;

public class TouchpadState {
    // NO idea why I would need to use abstraction like this here...
    // Better do it anyway! This is software development!

    public boolean pressed, ppressed;
    public PVector[] touches;

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
