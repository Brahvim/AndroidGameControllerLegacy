package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;
import java.util.ArrayList;

import processing.core.PVector;

public class ConfigurationPacket implements Serializable {
    public String AGC_VERSION;
    public PVector screenDimensions;
    public long appStartMilliSinceEpoch;

    // Control configurations:
    public ArrayList<ButtonConfig> buttons;
    public ArrayList<DpadButtonConfig> dpadButtons;
    public ArrayList<ThumbstickConfig> thumbsticks;
    public ArrayList<TouchpadConfig> touchpads;

    public ConfigurationPacket() {
        // Please set `this.appStartMilliSinceEpoch` in this manner!:
        // this.appStartMilliSinceEpoch = System.currentTimeMillis() -
        // MainActivity.sketch.millis();
    }

    public <T> T addObject(Object p_object) {
        if (p_object instanceof ButtonConfig)
            this.buttons.add((ButtonConfig)p_object);

        else if (p_object instanceof DpadButtonConfig)
            this.dpadButtons.add((DpadButtonConfig)p_object);

        else if (p_object instanceof ThumbstickConfig)
            this.thumbsticks.add((ThumbstickConfig)p_object);

        else if (p_object instanceof TouchpadConfig)
            this.touchpads.add((TouchpadConfig)p_object);

        else throw new IllegalArgumentException();
        return (T)p_object;
    }

}
