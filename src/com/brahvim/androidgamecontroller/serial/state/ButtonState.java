package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

public class ButtonState implements Serializable {
    // Nope! No OOP here :)
    public boolean pressed, ppressed;

    /*
     * // NO idea why I would need to use abstraction like this here...
     * // Better do it anyway! This is software development!
     *
     * private boolean pressed, ppressed;
     *
     * public boolean wasPressed() {
     * return this.ppressed;
     * }
     *
     * public boolean isPressed() {
     * return this.pressed;
     * }
     */
}
