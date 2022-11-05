package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.ControlType;

import processing.core.PVector;

/**
 * Base class for all controller elements' configuration objects.
 */
public class ControlConfigBase {
    public ControlType type;

    public PVector scale;
    public PVector transform;

    // This constructor helps NOT force extending classes from always giving the
    // data this class needs. *"Might"* not be a good idea.
    ControlConfigBase() {
    }

    ControlConfigBase(PVector p_scale, PVector p_transform) {
        this.scale = p_scale;
        this.transform = p_transform;
    }

    ControlConfigBase(ControlType p_type, PVector p_scale, PVector p_transform) {
        this.type = p_type;
        this.scale = p_scale;
        this.transform = p_transform;
    }
}
