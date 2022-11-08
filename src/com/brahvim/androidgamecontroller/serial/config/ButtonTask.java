package com.brahvim.androidgamecontroller.serial.config;

public enum ButtonTask /*implements Serializable*/ {
    AGC_TASK(), // It's another ordinary button that presses a key on the keyboard.

    // QUICK1(), QUICK2(), QUICK3(),
    // ^^^ These could be used to do something inside the cli- NO.

    SETTINGS_MENU(), // This button opens the settings menu!
    KEYBOARD(); // This button opens the keyboard!
}
