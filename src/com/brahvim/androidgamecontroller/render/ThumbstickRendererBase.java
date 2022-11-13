package com.brahvim.androidgamecontroller.render;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.serial.config.ThumbstickConfig;
import com.brahvim.androidgamecontroller.serial.state.ThumbstickState;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PVector;

public class ThumbstickRendererBase {
  public ThumbstickConfig config;
  public ThumbstickState state;

  protected volatile PVector draggingTouch;
  protected PVector innerCircleScale;

  public ThumbstickRendererBase(@NotNull ThumbstickConfig p_config) {
    this.config = p_config;
    this.state = new ThumbstickState();

    this.draggingTouch = new PVector();
    this.innerCircleScale = PVector.mult(this.config.scale, 0.25f);
  }

  public void draw(@NotNull PGraphics p_graphics) {
    p_graphics.pushMatrix();
    p_graphics.pushStyle();

    p_graphics.translate(this.config.transform.x, this.config.transform.y);

    // region The thumbstick's border ring.
    p_graphics.pushMatrix();

    // p_graphics.scale(this.config.scale.x, this.config.scale.y);
    // p_graphics.noFill();
    // p_graphics.strokeWeight(8);
    // p_graphics.stroke(100);
    // p_graphics.ellipse(0, 0, 1, 1);

    p_graphics.popMatrix();
    // endregion

    // region The inner circle that moves.
    // p_graphics.pushMatrix();

    p_graphics.translate(
        // HA-HA! You can't make these elliptical now... yippee! ^^:
        PApplet.constrain(draggingTouch.x - this.config.transform.x, 0, this.config.scale.x),
        PApplet.constrain(draggingTouch.y - this.config.transform.y, 0, this.config.scale.y));

    // ...of course, that was just a joke about *my* laziness -_-
    // *We* should add these rich, rich API features :D
    // Want me to do it? Sure! Tell me! Open an issue!
    // Tell me you read the source code! :sparkling_heart:

    // Should there be a... `configChanged(p_oldConfig, p_newConfig)`
    // event so this can track the magnitude?:
    p_graphics.scale(this.innerCircleScale.x, this.innerCircleScale.y);

    p_graphics.noStroke();
    p_graphics.fill(220, 255, 220, this.state.pressed ? 120 : 60);
    p_graphics.ellipse(0, 0, 1, 1);

    // p_graphics.popMatrix();
    // endregion

    p_graphics.popMatrix();
    p_graphics.popStyle();
  }
}
