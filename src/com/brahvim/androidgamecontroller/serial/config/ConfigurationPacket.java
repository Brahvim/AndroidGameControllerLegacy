package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

public class ConfigurationPacket implements Serializable {
    public String AGC_VERSION;

    public ButtonConfig[] buttons;
    public DpadButtonConfig[] dpadButtons;
    public ThumbstickConfig[] thumbsticks;
    public TouchpadConfig[] touchpads;
}
