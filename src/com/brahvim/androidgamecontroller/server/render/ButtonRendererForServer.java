package com.brahvim.androidgamecontroller.server.render;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.ButtonRendererBase;
import com.brahvim.androidgamecontroller.serial.config.ButtonConfig;
import com.brahvim.androidgamecontroller.server.Sketch;

import processing.core.PConstants;
import processing.core.PGraphics;

public class ButtonRendererForServer extends ButtonRendererBase {
    public static float textOffX, textOffY;

    public ButtonRendererForServer(@NotNull ButtonConfig p_config) {
        super(p_config);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        // super.state.ppressed = super.state.pressed; // Nope! The impl. handles super!

        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        p_graphics.translate(super.config.transform.x,
                super.config.transform.y);
        p_graphics.scale(super.config.scale.x, super.config.scale.x);
        p_graphics.rotate(super.config.transform.z);

        p_graphics.fill(230, super.state.pressed ? 100 : 50);
        p_graphics.noStroke();

        switch (super.config.shape) {
            case ROUND:
                p_graphics.ellipse(0, 0, 1, 1);
                break;

            case RECTANGLE:
                p_graphics.rectMode(PConstants.CENTER);
                p_graphics.rect(0, 0, 1.2f, 0.55f,
                        0.1f, 0.1f, 0.1f, 0.1f);
                break;

            default:
                break;
        }

        p_graphics.textSize(0.35f);
        p_graphics.translate(-0.5f * p_graphics.textWidth(super.config.text), 0.1f);
        p_graphics.textAlign(PConstants.CENTER, PConstants.BASELINE);
        p_graphics.text(super.config.text, 0, -1f);

        // Pop back!:
        p_graphics.popMatrix();
        p_graphics.popStyle();
    }

}
