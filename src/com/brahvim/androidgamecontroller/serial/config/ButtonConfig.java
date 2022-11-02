package com.brahvim.androidgamecontroller.serial.config;

import java.io.Serializable;

import processing.core.PVector;

public class ButtonConfig implements Serializable {
    public PVector transform;
    public String text;

    /*
     * // Hopefully, we can also have STYLE information in the future! :D
     * // AndroidGameController themes! 😍
     * int fillColor, strokeColor;
     * int strokeWeight, strokeCap, strokeJoin;
     */

}
