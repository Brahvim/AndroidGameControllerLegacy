package com.brahvim.androidgamecontroller.serial;

public enum ControlType {
    //A(), B(), X(), Y(),
    //UP(), LEFT(), DOWN(), RIGHT(),
    //TOUCHPAD(), THUMBSTICK();

    // ...Use of the system keyboard should be allowed whenever wished by the user.
    // Let's make buttons for it!

    // Oh! And also a settings button.


    // ...here's an AGC layout with *everything:*

    /*
    ___________________________________
    | (S)      (I)  (II) (III)        |
    |                          [KB]   |
    |  [LEFT]               [RIGHT]   |
    |       _______  _____            |
    |   +   |     | /     \   (X)     |
    | +   + |T-PAD| |TSTCK| (A) (Y)   |
    |   +   |_____| \_____/   (B)     |
    |_________________________________|
     */

    // (ROUND brackets denote round buttons, RECTANGULAR ones denote rectangular ones!)
    // (The stuff inside pipes is stuff that is rectangular unless it has slashes on the edges,
    // in which case, it is round.)


    // Here are the 'button codes' explained:
    /*
    // Shortcut buttons! ...and a button, "S", for settings.
    // "KB" is the "Keyboard Button".
    // "+" denotes D-PAD buttons.
    // "T-PAD" being the "TouchPad",
    // "T-STCK" being the "ThumbSTiCK".
     */
}
