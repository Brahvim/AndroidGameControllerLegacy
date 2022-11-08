package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

public class TouchpadConfig extends ControlConfigBase implements Serializable {
    public final static long serialVersionUID = 7084394767346371323L;

    public TouchpadConfig() {
        super();
    }

    public TouchpadConfig(PVector p_scale, PVector p_transform) {
        super(p_scale, p_transform); // No use for the `z`, again...
    }

    // We have no fields to take the hash-codes of, *ooF!:*
    @Override
    public int hashCode() {
        final int prime = 31;
        float result = 1;

        // Not casting these to `int`s to avoid
        // hash collisions for when two touchpads
        // are LITERALLY colliding because
        // they're on top of each other. :rofl:.

        // Could map the vector components
        // to a smaller range for avoiding
        // integer overflow, but that would
        // give more chances for hash collisions!

        // Transform:
        result = prime * result + super.transform.x;
        result = prime * result + super.transform.y;

        // Scale:
        result = prime * result + super.scale.x;
        result = prime * result + super.scale.y;

        return (int)result;
    }

}
