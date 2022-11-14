package com.brahvim.androidgamecontroller.server;

import java.util.ArrayList;

import com.brahvim.androidgamecontroller.serial.config.AgcConfigurationPacket;
import com.brahvim.androidgamecontroller.serial.state.ButtonState;
import com.brahvim.androidgamecontroller.serial.state.DpadButtonState;
import com.brahvim.androidgamecontroller.serial.state.KeyboardState;
import com.brahvim.androidgamecontroller.serial.state.ThumbstickState;
import com.brahvim.androidgamecontroller.serial.state.TouchpadState;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

import processing.core.PApplet;

public class AgcClientWindow extends PApplet {
    public AgcClient parentClient;
    public AgcClientWindow SKETCH = this;

    public ArrayList<ButtonState> buttonStates;
    public ArrayList<DpadButtonState> dpadButtonStates;
    public ArrayList<ThumbstickState> thumbstickStates;
    public ArrayList<TouchpadState> touchpadStates;
    public KeyboardState keyboardState;

    private AgcClientWindow() {
    }

    AgcClientWindow(AgcClient p_client) {
        this.parentClient = p_client;

        this.parentClient.config = new AgcConfigurationPacket();
        // this.config.buttons

        PApplet.runSketch(new String[] { this.getClass().getName() }, this);
    }

    public static AgcClientWindow buildForMainWindow() {
        return new AgcClientWindow();
    }

    @Override
    public void settings() {
        size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT, JAVA2D);
    }

    @Override
    public void setup() {
    }

    @Override
    public void draw() {
    }

    public void onReceive(byte[] p_data) {

    }

    /**
     * @return the parentClient
     */
    public AgcClient getParentClient() {
        return parentClient;
    }

    /**
     * @param parentClient the parentClient to set
     */
    public void setParentClient(AgcClient parentClient) {
        this.parentClient = parentClient;
    }

    /**
     * @return the sKETCH
     */
    public AgcClientWindow getSKETCH() {
        return SKETCH;
    }

    /**
     * @param sKETCH the sKETCH to set
     */
    public void setSKETCH(AgcClientWindow sKETCH) {
        SKETCH = sKETCH;
    }
}
