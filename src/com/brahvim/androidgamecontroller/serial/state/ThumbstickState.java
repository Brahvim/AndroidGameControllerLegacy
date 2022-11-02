package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

public class ThumbstickState implements Serializable {
    public boolean pressed, ppressed;
    public float mag, dir;

    /*
     * // in-TENSE Object-oriented programming!:
     * 
     * private boolean pressed, ppressed;
     * private float mag, dir;
     * 
     * public float getDirection() {
     * return this.dir;
     * }
     * 
     * public float getMagnitude() {
     * return this.mag;
     * }
     * 
     * public boolean wasPressed() {
     * return this.ppressed;
     * }
     * 
     * public boolean isPressed() {
     * return this.pressed;
     * }
     * 
     */
}
