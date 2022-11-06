package com.brahvim.androidgamecontroller.serial.state;

public class ThumbstickState extends StateBase {
    public final static long serialVersionUID = -8801139154962962076L;
    public float mag, dir;

    public ThumbstickState() {
        super();
    }

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
