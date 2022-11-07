package com.brahvim.androidgamecontroller.render;

import com.brahvim.androidgamecontroller.serial.config.ButtonConfig;
import com.brahvim.androidgamecontroller.serial.config.DpadButtonConfig;
import com.brahvim.androidgamecontroller.serial.state.ButtonState;
import com.brahvim.androidgamecontroller.serial.state.DpadButtonState;

import org.jetbrains.annotations.NotNull;

import processing.core.PConstants;
import processing.core.PGraphics;

public class DpadButtonRendererBase {
    protected DpadButtonConfig config;
    public DpadButtonState state;

    public DpadButtonRendererBase(@NotNull DpadButtonConfig p_config) {
        this.config = p_config;
        this.state = new DpadButtonState();
    }

    public void draw(@NotNull PGraphics p_graphics) {
        // this.state.ppressed = this.state.pressed; // Nope! The impl. handles this!

        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.translate(this.config.transform.x,
                this.config.transform.y);
        p_graphics.scale(this.config.scale.x, this.config.scale.x);

        switch (this.config.dir) {
            case DOWN:
                p_graphics.rotate(PConstants.TAU);
                break;

            case LEFT:
                p_graphics.rotate(-PConstants.HALF_PI);
                break;

            case RIGHT:
                p_graphics.rotate(PConstants.HALF_PI);
                break;

            // No rotation. It's already pointing up, ":D!
            // case UP:
            // break;

            default:
                break;
        }

        p_graphics.fill(230, this.state.pressed ? 100 : 50);
        p_graphics.noStroke();

        p_graphics.beginShape(PConstants.POLYGON);
        p_graphics.vertex(-0.5f, 0.35f);
        p_graphics.vertex(0.5f, 0.35f);
        p_graphics.vertex(0.5f, -0.35f);
        // p_graphics.edge(true);
        p_graphics.vertex(0, -0.85f);
        // p_graphics.edge(false);
        p_graphics.vertex(-0.5f, -0.35f);
        p_graphics.endShape(PConstants.CLOSE);

        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

    public boolean wasPressed() {
        return this.state.ppressed;
    }

    public boolean isPressed() {
        return this.state.pressed;
    }

    public long configHash() {
        return this.config.hashCode();
    }
}
