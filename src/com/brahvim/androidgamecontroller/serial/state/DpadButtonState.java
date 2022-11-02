package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

import com.brahvim.androidgamecontroller.serial.DpadDirection;

public class DpadButtonState implements Serializable {
    // No OOP here either!~ ^^
    public boolean pressed, ppressed;
    public DpadDirection dir;
}
