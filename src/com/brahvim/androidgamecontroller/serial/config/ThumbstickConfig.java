package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

public class ThumbstickConfig implements Serializable {
    public PVector scale;
    public PVector transform; // Of course, thumbsticks don't have rotation.
    // ...unless I put a fancy texture with shadows on them.
}
