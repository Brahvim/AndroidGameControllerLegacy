package com.brahvim.androidgamecontroller.serial.state;

import com.brahvim.androidgamecontroller.serial.DpadDirection;

public class DpadButtonState extends StateBase {
    public final static long serialVersionUID = 6831770768002521244L;

    public DpadButtonState() {
    }

    // No OOP here either!~ ^^
    public DpadDirection dir;
}
