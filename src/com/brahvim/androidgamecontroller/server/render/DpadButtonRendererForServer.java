package com.brahvim.androidgamecontroller.server.render;

import java.awt.Robot;
import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.render.DpadButtonRendererBase;
import com.brahvim.androidgamecontroller.serial.config.DpadButtonConfig;

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

}
