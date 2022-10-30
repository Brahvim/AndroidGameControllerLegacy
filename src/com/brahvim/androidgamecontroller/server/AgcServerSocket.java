package com.brahvim.androidgamecontroller.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import org.jetbrains.annotations.NotNull;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.Scene;
import com.brahvim.androidgamecontroller.UdpSocket;

public class AgcServerSocket extends UdpSocket {
    class AgcClient {
        private String ip;
        private int port;
        private AgcServerSocket parent;
        // ^^^ Didn't know whether or not it was necessary to have it, but I did anyway!

        // The manufacturer-assigned name and the user-assigned bluetooth name for the
        // connected client Android device:
        private String manName;
        private String btName;

        // AgcClient(AgcServerSocket p_parent, String p_ip, int p_port) {
        // this.ip = p_ip;
        // this.port = p_port;
        // this.parent = p_parent;
        // }

        public AgcClient(AgcServerSocket p_parent, String p_ip, int p_port, String p_manName, String p_btName) {
            this.ip = p_ip;
            this.port = p_port;
            this.parent = p_parent;

            // The new stuff:

            this.btName = p_btName;
            this.manName = p_manName;
        }

        // #region Getters and Setters
        /**
         * @return The user-assigned "bluetooth name" of the client device.
         */
        public String getBtName() {
            return this.btName;
        }

        /**
         * @return The name of the client device given to it by its manufacturer. This
         *         is the most used way to identify devices.
         */
        public String getManName() {
            return this.manName;
        }

        public int getPort() {
            return this.port;
        }

        public String getIp() {
            return this.ip;
        }

        /**
         * @return The IP Address as a {@code java.net.InetAddress}. May the conversion
         *         from a {@code String} to an {@code InetAddress} fail (which is rare
         *         if not impossible), the method returns {@code null}.
         */
        public InetAddress getIpAddr() {
            InetAddress ret = null;
            try {
                ret = InetAddress.getByName(this.ip);
            } catch (UnknownHostException e) {
            }
            return ret;
        }
        // #endregion

        public void send(byte[] p_data) {
            this.parent.send(p_data, this.ip, this.port);
        }

        public void sendCode(RequestCode p_code) {
            this.parent.send(p_code.toBytes(), this.ip, this.port);
        }

        @Override
        public boolean equals(Object p_obj) {
            if (!(p_obj instanceof AgcClient))
                return false;
            else
                return this.equals((AgcClient) p_obj);
        }

        public boolean equals(AgcClient p_client) {
            // No `AgcClient` is equal to `null`!:
            if (p_client == null)
                return false;

            // If the ports are not the same, they're still unequal:
            if (p_client.getPort() != this.port)
                return false;

            // If the IP strings are not the same, they're also unequal:
            if (!p_client.getIp().equals(this.ip))
                return false;

            // The parent socket need not be the same.

            // Check passed! Object is indeed equal :)
            return true;
        }
    }

    ArrayList<AgcClient> clients;

    AgcServerSocket() {
        super(RequestCode.SERVER_PORT);
        this.clients = new ArrayList<>();
    }

    // #region Client management methods.
    void addClientIfAbsent(AgcClient p_client) {
        boolean absent = true;
        for (AgcClient c : this.clients)
            if (c.equals(p_client))
                absent = false;

        if (absent)
            this.clients.add(p_client);
    }

    void removeClient(String p_ip) {
        // Section `3.1` on: [https://www.baeldung.com/java-list-iterate-backwards].

        for (int i = this.clients.size(); i-- > 0;) {
            AgcClient c = this.clients.get(i);
            if (c.getIp().equals(p_ip))
                this.clients.remove(c);
        }
    }

    // #endregion

    // #region Custom methods.
    // Using `AgcServerSocket.AgcClient`s:
    public void sendCode(RequestCode p_code, AgcClient p_client) {
        this.sendCode(p_code, p_client.ip, p_client.port);
    }

    public void sendCode(RequestCode p_code, String p_extraData, AgcClient p_client) {
        this.sendCode(p_code, p_extraData, p_client.ip, p_client.port);
    }

    public void tellAllClients(RequestCode p_code) {
        for (AgcClient c : this.clients)
            this.sendCode(p_code, c);
    }

    public void tellAllClients(RequestCode p_code, String p_extraData) {
        for (AgcClient c : this.clients)
            this.sendCode(p_code, p_extraData, c);
    }

    /*
     * Older versions:
     * public void sendCode(RequestCode p_code, String p_ip, int p_port) {
     * byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES];
     * 
     * // Copy over the suffix,
     * for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * // Put the code in!:
     * for (int i = RequestCode.CODE_SUFFIX.length; i < toSend.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * super.send(toSend, p_ip, p_port);
     * }
     * 
     * public void sendCode(RequestCode p_code, String p_extraData, String p_ip, int
     * p_port) {
     * byte[] extraBytes = p_extraData.getBytes(StandardCharsets.UTF_8);
     * 
     * byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES +
     * extraBytes.length];
     * 
     * // Copy over the suffix,
     * for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++)
     * toSend[i] = RequestCode.CODE_SUFFIX[i];
     * 
     * // Put the code in!:
     * byte[] codeBytes = p_code.toBytes();
     * for (int i = RequestCode.CODE_SUFFIX.length; i < toSend.length; i++)
     * toSend[i] = codeBytes[i];
     * 
     * super.send(toSend, p_ip, p_port);
     * }
     */

    public void sendCode(RequestCode p_code, String p_ip, int p_port) {
        byte[] codeBytes = p_code.toBytes();
        byte[] toSend = new byte[codeBytes.length + RequestCode.CODE_SUFFIX.length];

        // System.out.printf("Copying the suffix, which takes `%d` out of `%d`
        // bytes.\n",
        /// RequestCode.CODE_SUFFIX.length, toSend.length);

        int i = 0;

        // Copy over the suffix,
        for (; i < RequestCode.CODE_SUFFIX.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n", i);
            toSend[i] = RequestCode.CODE_SUFFIX[i];
        }

        // System.out.printf("Copying the CODE, which takes `%d` out of `%d` bytes.\n",
        // codeBytes.length, toSend.length);

        // Put the code in!:
        for (i = 0; i < Integer.BYTES; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[RequestCode.CODE_SUFFIX.length + i] = codeBytes[i];
        }

        System.out.printf("Sent `%s` to IP: `%s`, port: `%d`.\n", new String(toSend), p_ip, p_port);
        super.send(toSend, p_ip, p_port);
    }

    public void sendCode(RequestCode p_code, String p_extraData, String p_ip, int p_port) {
        byte[] extraBytes = p_extraData.getBytes(StandardCharsets.UTF_8);

        byte[] toSend = new byte[RequestCode.CODE_SUFFIX.length + Integer.BYTES + extraBytes.length];
        byte[] codeBytes = p_code.toBytes();

        // System.out.printf("Copying the suffix, which takes `%d` out of `%d`
        // bytes.\n",
        /// RequestCode.CODE_SUFFIX.length, toSend.length);

        int i = 0;

        // Copy over the suffix,
        for (; i < RequestCode.CODE_SUFFIX.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n", i);
            toSend[i] = RequestCode.CODE_SUFFIX[i];
        }

        // System.out.printf("Copying the CODE, which takes `%d` out of `%d` bytes.\n",
        // codeBytes.length, toSend.length);

        // Put the code in!:
        for (i = 0; i < Integer.BYTES; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[RequestCode.CODE_SUFFIX.length + i] = codeBytes[i];
        }

        // System.out.printf("Copying EXTRA DATA, which takes `%d` out of `%d`
        // bytes.\n",
        // extraBytes.length, toSend.length);

        // Copy over extra bytes!:
        int startIdExtDataCopy = RequestCode.CODE_SUFFIX.length + codeBytes.length;
        for (i = 0; i < extraBytes.length; i++) {
            // System.out.printf("Value of iterator: `%d`.\n",
            // RequestCode.CODE_SUFFIX.length + i);
            toSend[startIdExtDataCopy + i] = extraBytes[i];
        }

        System.out.printf("Sent `%s` to IP: `%s`, port: `%d`.\n",
                new String(toSend).replace('\n', '\0'), p_ip, p_port);
        super.send(toSend, p_ip, p_port);
    }
    // #endregion

    @Override
    public void onReceive(@NotNull byte[] p_data, String p_ip, int p_port) {
        System.out.println("Received some data!");
        if (Scene.currentScene != null)
            Scene.currentScene.onReceive(p_data, p_ip, p_port);
    }

    // #region Non-so-important Overrides.
    @Override
    protected void onStart() {
        // this.setPort(RequestCodes.get("SERVER_PORT"));
        System.out.println("The socket has begun, boiiii!");
        System.out.printf("Socket-Stats!:\n\t- IP: `%s`\n\t- Port: `%d`\n", super.getIp(), super.getPort());
    }

    @Override
    protected void onClose() {
        System.out.println("The socket's been disposed off, thanks for taking the service :)");
    }
    // #endregion

}
