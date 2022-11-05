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

    ConfigurationPacket() {
        // Please set `this.appStartMilliSinceEpoch` in this manner!:
        // this.appStartMilliSinceEpoch = System.currentTimeMillis() -
        // MainActivity.sketch.millis();
    }

}
