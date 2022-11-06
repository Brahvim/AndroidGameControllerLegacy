package com.brahvim.androidgamecontroller.serial.state;

public class KeyboardState extends StateBase {
    public final static long serialVersionUID = -3605879719869452598L;

    public char keyPressed;
    public int keyCode;
    public int lastKeyMillis;
}
