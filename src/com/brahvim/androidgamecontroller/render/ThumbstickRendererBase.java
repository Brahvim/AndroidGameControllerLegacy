package com.brahvim.androidgamecontroller.render;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.serial.config.ThumbstickConfig;
import com.brahvim.androidgamecontroller.serial.state.ThumbstickState;

import processing.core.PGraphics;

public class ThumbstickRendererBase {
    protected ThumbstickConfig config;
    public ThumbstickState state;

    public ThumbstickRendererBase(@NotNull ThumbstickConfig p_config) {
        this.config = p_config;
        this.state = new ThumbstickState();
    }

    public void draw(@NotNull PGraphics p_graphics) {
    }

}
