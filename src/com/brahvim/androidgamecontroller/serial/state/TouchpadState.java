package com.brahvim.androidgamecontroller.serial.state;

import java.util.ArrayList;

import processing.core.PVector;

public class TouchpadState extends StateBase {
    // NO idea why I would need to use abstraction like this here...
    // Better do it anyway! This is software development!

    public ArrayList<PVector> touches; // The `z` holds pressure or area. Probably area.
    public boolean doubleTapped, pdoubleTapped;

    public TouchpadState() {
    }

    public int count() {
        return this.touches.size();
    }

}
