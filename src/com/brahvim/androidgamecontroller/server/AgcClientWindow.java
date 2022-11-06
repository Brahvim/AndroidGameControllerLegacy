package com.brahvim.androidgamecontroller.server;

import com.brahvim.androidgamecontroller.serial.config.ConfigurationPacket;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

import processing.core.PApplet;

public class AgcClientWindow extends PApplet {
    public AgcClient parentClient;
    public ConfigurationPacket config;
    public AgcClientWindow SKETCH = this;

    AgcClientWindow(AgcClient p_client) {
        this.parentClient = p_client;

        this.config = new ConfigurationPacket();
        // this.config.buttons

        PApplet.runSketch(new String[] { this.getClass().getName() }, this);
    }

    @Override
    public void settings() {
        size(400, 400, JAVA2D);
    }

    @Override
    public void setup() {
    }

    @Override
    public void draw() {
    }

    public void onReceive(byte[] p_data) {

    }
}
