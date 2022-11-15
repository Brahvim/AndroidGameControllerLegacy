package com.brahvim.androidgamecontroller.server;

import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.UdpSocket;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

// Singleton:
public class AgcReceptionThread extends Thread {

    private AgcReceptionThread instance;
    private UdpSocket socket;
    private ArrayList<AgcClient> clients;

    private AgcReceptionThread() {
    }

    public void init() {
        this.instance = new AgcReceptionThread();
        this.clients = new ArrayList<>();
        this.initSocket(); // Not using a functional pattern because... performance?
    }

    @Override
    public void run() {
    }

    private void initSocket() {
        AgcReceptionThread receptor = this;
        this.socket = new UdpSocket() {
            @Override
            public void onReceive(@NotNull byte[] p_data, String p_ip, int p_port) {
                if (RequestCode.packetHasCode(p_data)) {
                } else {
                    receptor.giveToClient(p_data, p_ip, p_port);
                }
            }
        };
    }

    private void giveToClient(@NotNull byte[] p_data, String p_ip, int p_port) {
        for (AgcClient c : this.clients) {
            if (c.getIp().equals(p_ip)) {
                // c.window.receptData();
            }
        }
    }

}
