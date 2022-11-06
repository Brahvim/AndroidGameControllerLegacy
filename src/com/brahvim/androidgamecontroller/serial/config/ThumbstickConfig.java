package com.brahvim.androidgamecontroller.serial.config;

import processing.core.PVector;

public class ThumbstickConfig extends ControlConfigBase {
    public final static long serialVersionUID = -6618151523553691071L;

    // Of course, thumbsticks don't have rotation.
    // ...unless I put a fancy texture with shadows on them.

    ThumbstickConfig(PVector p_transform, PVector p_scale) {
        super(p_scale, p_transform);
    }
}
