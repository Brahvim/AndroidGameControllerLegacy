package com.brahvim.androidgamecontroller.serial.config;

import processing.core.PVector;

public class ThumbstickConfig extends ControlConfigBase {
    // Of course, thumbsticks don't have rotation.
    // ...unless I put a fancy texture with shadows on them.

    ThumbstickConfig(PVector p_transform, PVector p_scale) {
        super(p_scale, p_transform);
    }
}
