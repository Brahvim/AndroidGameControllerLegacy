package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

public class ThumbstickConfig extends ControlConfigBase implements Serializable {
    // Of course, thumbsticks don't have rotation.
    // ...unless I put a fancy texture with shadows on them.

    ThumbstickConfig(PVector p_transform, PVector p_scale) {
        super(p_scale, p_transform);
    }
}
