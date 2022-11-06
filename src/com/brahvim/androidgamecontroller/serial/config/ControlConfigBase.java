package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

/**
 * Base class for all controller elements' configuration objects.
 */
public class ControlConfigBase implements Serializable {
    public final static long serialVersionUID = 8587363312969447326L;

    public PVector scale;
    public PVector transform;

    // This constructor helps NOT force extending classes from always giving the
    // data this class needs. *"Might"* not be a good idea.
    public ControlConfigBase() {
    }

    ControlConfigBase(PVector p_scale, PVector p_transform) {
        this.scale = p_scale;
        this.transform = p_transform;
    }
}
