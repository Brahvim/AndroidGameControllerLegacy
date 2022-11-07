package com.brahvim.androidgamecontroller.render;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.serial.config.TouchpadConfig;
import com.brahvim.androidgamecontroller.serial.state.TouchpadState;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PVector;

public class TouchpadRendererBase {
    protected TouchpadConfig config;
    public TouchpadState state;

    public TouchpadRendererBase(@NotNull TouchpadConfig p_config) {
        this.config = p_config;
        this.state = new TouchpadState();
    }

    public void draw(@NotNull PGraphics p_graphics) {
        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.pushMatrix();
        // region Drawing the touchpad.
        p_graphics.translate(this.config.transform.x,
                this.config.transform.y);
        // Touchpads ain't got any rotation! (Yet!...)
        p_graphics.scale(this.config.scale.x, this.config.scale.x);

        p_graphics.rectMode(PConstants.CENTER);
        p_graphics.rect(0, 0, 1.2f, 0.55f,
                0.1f, 0.1f, 0.1f, 0.1f);
        // endregion
        p_graphics.popMatrix();

        for (PVector v : this.state.touches) {
            p_graphics.scale(v.z);
            p_graphics.ellipse(v.x, v.y, 1, 1);
        }

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

}
