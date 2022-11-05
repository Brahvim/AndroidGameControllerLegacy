package com.brahvim.androidgamecontroller.serial.config;

import processing.core.PVector;

public class TouchpadConfig extends ControlConfigBase {
    TouchpadConfig(PVector p_scale, PVector p_transform) {
        super(p_scale, p_transform); // No use for the `z`, again...
    }
}
