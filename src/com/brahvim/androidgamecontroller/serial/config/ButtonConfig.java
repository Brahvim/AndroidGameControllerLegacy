package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.ButtonShape;

import processing.core.PVector;

public class ButtonConfig extends ControlConfigBase {
    public final static long serialVersionUID = 3448431857528425173L;

    public String text;
    public ButtonShape shape;

    public ButtonConfig() {
        super();
    }

    /**
     * Create a button at the desired position with text.
     */
    public ButtonConfig(float p_x, float p_y, String p_text) {
        super(
          new PVector(400, 400),
          new PVector(p_x, p_y, 0)); // The `z` is rotation.

        this.text = p_text;
        this.shape = ButtonShape.RECTANGLE;
    }

    /**
     * Create a button with the desired position and size with text.
     */
    public ButtonConfig(PVector p_transform, PVector p_scale, String p_text) {
        super(p_scale, p_transform); // The `z` is rotation.

        this.text = p_text;
        this.shape = ButtonShape.RECTANGLE;
    }

    /**
     * Create a button at the desired position, with the desired shape and size, with text.
     */
    public ButtonConfig(PVector p_transform, PVector p_scale, String p_text, ButtonShape p_shape) {
        super(p_scale, p_transform); // The `z` is rotation.

        this.text = p_text;
        this.shape = p_shape;
    }


    /**
     * Create a circular button at the desired position with the desired radius and text.
     */
    public ButtonConfig(PVector p_transform, float p_radius, String p_text) {
        super(new PVector(p_radius, p_radius), p_transform);
        this.shape = ButtonShape.ROUND;
        this.text = p_text;
    }

    @Override // `Objects.hash()` provides inconsistent results across compilers...
    public int hashCode() {
        final int prime = 31;
        int result = 1;

        // result = prime * result + ((text == null) ? 0 : text.hashCode());
        int textHash = 1;
        {
            for (int i = 0; i < text.length(); i++)
                textHash = prime * textHash + text.charAt(i);
        }

        // result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        result = prime * result + textHash;
        result = prime * result + (shape == null? 0 : shape.ordinal() + 1);

        return result;
    }

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
