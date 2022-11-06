package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.DpadDirection;

import processing.core.PVector;

public class DpadButtonConfig extends ControlConfigBase {
    public final static long serialVersionUID = -4244298640535047093L;

    // PS I WILL have to use composition over inheritance here
    // STRICTLY because - if I don't, `instanceof` checks, our
    // only hope, would fail!

    public DpadDirection dir; // Enumerations are safer! Don't use `transform`'s `z`!

    public DpadButtonConfig() {
    }

    public DpadButtonConfig(PVector p_transform, DpadDirection p_dir) {
        super.transform = p_transform; // The `z` is NOT used at all here...
        // (Unless I do some stupid `java.lang.Enum.ordinal()` thing, which would be unnecessary!)
        this.dir = p_dir;
    }
}
