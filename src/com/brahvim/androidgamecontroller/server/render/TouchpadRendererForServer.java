package com.brahvim.androidgamecontroller.server.render;

import java.awt.Robot;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.TouchpadRendererBase;
import com.brahvim.androidgamecontroller.serial.config.TouchpadConfig;

public class TouchpadRendererForServer extends TouchpadRendererBase implements ServerRenderer {
    @SuppressWarnings("unused")
    private Robot robot;

    public TouchpadRendererForServer(@NotNull TouchpadConfig p_config) {
        super(p_config);
        ServerRenderer.all.add(this);
    }

    public TouchpadRendererForServer(@NotNull TouchpadConfig p_config, Robot p_robot) {
        super(p_config);
        this.robot = p_robot;
        ServerRenderer.all.add(this);
    }
}
