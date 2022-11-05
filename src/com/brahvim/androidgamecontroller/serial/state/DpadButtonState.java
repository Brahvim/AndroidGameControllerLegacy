package com.brahvim.androidgamecontroller.serial.state;

import java.io.Serializable;

import com.brahvim.androidgamecontroller.serial.DpadDirection;

public class DpadButtonState extends StateBase implements Serializable {
    // No OOP here either!~ ^^
    public DpadDirection dir;
}
