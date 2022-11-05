package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.client.MainActivity;

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
        this.appStartMilliSinceEpoch =
          System.currentTimeMillis() - MainActivity.sketch.millis();
    }

}
