package com.brahvim.androidgamecontroller.server.render;

import java.awt.Robot;
import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.DpadButtonRendererBase;
import com.brahvim.androidgamecontroller.serial.config.DpadButtonConfig;
import com.brahvim.androidgamecontroller.server.Sketch;

import processing.core.PConstants;
import processing.core.PGraphics;

public class DpadButtonRendererForServer extends DpadButtonRendererBase implements ServerRenderer {
    private Robot robot;

    public DpadButtonRendererForServer(@NotNull DpadButtonConfig p_config) {
        super(p_config);
        ServerRenderer.all.add(this);
    }

    public DpadButtonRendererForServer(@NotNull DpadButtonConfig p_config, Robot p_robot) {
        super(p_config);
        this.robot = p_robot;
        ServerRenderer.all.add(this);
    }

    @Override
    public void draw(@NotNull PGraphics p_graphics) {
        // super.state.ppressed = super.state.pressed; // Nope! The impl. handles super!

        p_graphics.pushMatrix();
        p_graphics.pushStyle();

        // p_graphics.translate(super.config.transform.x,
        // super.config.transform.y);

        p_graphics.translate(super.config.transform.x, super.config.transform.y);

        p_graphics.scale(super.config.scale.x, super.config.scale.x);

        switch (super.config.dir) {
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

        p_graphics.fill(230, super.state.pressed ? 100 : 50);
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

}
