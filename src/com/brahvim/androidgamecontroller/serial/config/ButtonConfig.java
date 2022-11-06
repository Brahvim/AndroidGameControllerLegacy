package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.ButtonShape;

import java.util.Objects;

import processing.core.PVector;

public class ButtonConfig extends ControlConfigBase {
    public final static long serialVersionUID = 3448431857528425173L;

    public String text;
    public ButtonShape shape;


    public ButtonConfig() {
    }

    public ButtonConfig(float p_x, float p_y, String p_text) {
        super(
          new PVector(400, 400),
          new PVector(p_x, p_y, 0)); // The `z` is rotation.

        this.text = p_text;
        this.shape = ButtonShape.RECTANGLE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, shape);
    }

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
