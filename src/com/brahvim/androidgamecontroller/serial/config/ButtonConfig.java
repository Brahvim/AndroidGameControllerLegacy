package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import com.brahvim.androidgamecontroller.serial.ButtonShape;

import processing.core.PVector;

public class ButtonConfig implements Serializable {
    public PVector transform; // The `z` is rotation.
    public ButtonShape shape;
    public PVector scale;
    public String text;

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
