package com.brahvim.androidgamecontroller.serial.config;

import processing.core.PVector;

public class TouchpadConfig extends ControlConfigBase {
    public final static long serialVersionUID = 7084394767346371323L;

    public TouchpadConfig() {
    }

    TouchpadConfig(PVector p_scale, PVector p_transform) {
        super(p_scale, p_transform); // No use for the `z`, again...
    }
}
