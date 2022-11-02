package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

public class DpadConfig implements Serializable {
    public enum Direction {
        UP, LEFT, DOWN, RIGHT // `W`-`A`-`S`-D` order.
    }
}
