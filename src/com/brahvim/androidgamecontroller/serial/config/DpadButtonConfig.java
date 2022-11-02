package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

public class DpadButtonConfig implements Serializable {
    // PS I WILL have to use composition over inheritance here
    // STRICTLY because - if I don't, `instanceof` checks, our
    // only hope, would fail!

    public PVector transform; // The `z` is NOT used here...
    public Direction dir; // Enumerations are safer!

    public DpadButtonConfig(PVector p_transform, Direction p_dir) {
        this.transform = p_transform;
        this.dir = p_dir;
    }

    public enum Direction {
        UP, LEFT, DOWN, RIGHT // `W`-`A`-`S`-D` order.
    }
}
