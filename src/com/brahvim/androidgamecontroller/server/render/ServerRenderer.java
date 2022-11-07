package com.brahvim.androidgamecontroller.server.render;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import processing.core.PGraphics;

public interface ServerRenderer {
    ArrayList<ServerRenderer> all = new ArrayList<>(5);

    void draw(@NotNull PGraphics p_graphics);
}
