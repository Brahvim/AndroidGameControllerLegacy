package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

import processing.core.PVector;

public class TouchpadState implements Serializable {
    // NO idea why I would need to use abstraction like this here...
    // Better do it anyway! This is software development!

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
