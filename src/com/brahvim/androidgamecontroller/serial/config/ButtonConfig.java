package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.ButtonShape;

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

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */

    @Override // `Objects.hash()` provides inconsistent results across compilers...
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((text == null) ? 0 : text.hashCode());
        result = prime * result + ((shape == null) ? 0 : shape.hashCode());
        return result;
    }

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
