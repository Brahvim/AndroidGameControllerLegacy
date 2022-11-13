package com.brahvim.androidgamecontroller.server;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.Scene;
import com.brahvim.androidgamecontroller.render.ButtonRendererBase;
import com.brahvim.androidgamecontroller.render.DpadButtonRendererBase;
import com.brahvim.androidgamecontroller.render.TouchpadRendererBase;
import com.brahvim.androidgamecontroller.serial.ByteSerial;
import com.brahvim.androidgamecontroller.serial.config.ButtonConfig;
import com.brahvim.androidgamecontroller.serial.config.ConfigurationPacket;
import com.brahvim.androidgamecontroller.serial.config.DpadButtonConfig;
import com.brahvim.androidgamecontroller.serial.config.TouchpadConfig;
import com.brahvim.androidgamecontroller.serial.state.ButtonState;
import com.brahvim.androidgamecontroller.serial.state.DpadButtonState;
import com.brahvim.androidgamecontroller.serial.state.TouchpadState;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;
import com.brahvim.androidgamecontroller.server.render.ButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.render.DpadButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.render.ServerRenderer;
import com.brahvim.androidgamecontroller.server.render.TouchpadRendererForServer;

public class SketchWithScenes extends Sketch {
    void initFirstScene() {
        // #region "Are Wii gunna have a problem?""
        Scene firstScene = awaitingConnectionScene;
        firstScene.setup();
        Scene.currentScene = firstScene;
        // #endregion
    }

    public void settingsMenuCheck() {
        if (mouseButton == MouseEvent.BUTTON3 && !Forms.isFormOpen(Forms.settingsForm)) {
            Forms.showSettingsForm(Sketch.myWindow);
        }
    }

    public void settingsMenuKbCheck() {
        // `525` is the context menu key / "right-click key" *at least* on my keyboard.
        if (keyCode == KeyEvent.VK_SPACE || keyCode == 525)
            Forms.showSettingsForm(Sketch.myWindow);
    }

    public void noClientsCheck() {
        if (socket.clients.size() == 0)
            Scene.setScene(awaitingConnectionScene);
    }

    public void registerClientConfig(byte[] p_data, AgcClient p_client) {
        System.out.println("Received the configuration from the client.");

        byte[] extraData = RequestCode.getPacketExtras(p_data);

        // If it is the primary client, the main window's controller should be changed!:
        if (p_client.equals(socket.clients.get(0)))
            Sketch.myConfig = (ConfigurationPacket) ByteSerial.decode(extraData);
        else
            // Okay, okay, which client?
            for (AgcClient c : socket.clients) {
                if (c.equals(p_client))
                    c.config = (ConfigurationPacket) ByteSerial.decode(extraData);
            }

        System.out.println("Config hashes:");
        for (ButtonConfig c : Sketch.myConfig.buttons) {
            System.out.println(c.hashCode());
        }

        socket.sendCode(RequestCode.SERVER_GOT_CONFIG, p_client);

        System.out.println("Told client that we got the config.!");
    }

    // "Please never make any `Scene` instances `static`."
    Scene awaitingConnectionScene, workScene, exitScene;

    { // Scene definitions.

        awaitingConnectionScene = new Scene() {
            boolean noMorePings = false;
            String shownText;

            @Override
            public void setup() {
                shownText = Forms.getString("AwaitingConnectionsScene.text");

                /*
                 * // "Can you serialize enums?" YES!
                 *
                 * File test = new File("enum.ser");
                 * 
                 * if (test.exists()) {
                 * try (FileInputStream fStr = new FileInputStream(test)) {
                 * try (ObjectInputStream oStr = new ObjectInputStream(fStr)) {
                 * DpadButtonConfig loaded = (DpadButtonConfig) oStr.readObject();
                 * System.out.printf("""
                 * Loaded up a configuration with
                 * transform `%s` and
                 * direction `%s`! Congrats?\n""",
                 * loaded.transform.toString(),
                 * loaded.dir.toString());
                 * }catch(****
                 * 
                 * Exception e)**
                 * {
                 * e.printStackTrace();
                 * }*}catch(**
                 * Exception e)**
                 * {
                 * e.printStackTrace();
                 * }*}else{*try(*
                 * FileOutputStream fStr = new FileOutputStream(test))**
                 * {
                 * try (ObjectOutputStream oStr = new ObjectOutputStream(fStr)) {
                 * oStr.writeObject(new DpadButtonConfig(
                 * new PVector(1, 2, 3), DpadButtonConfig.Direction.RIGHT));
                 * } catch (Exception e) {
                 * e.printStackTrace();
                 * }
                 * }catch(*
                 * Exception e)**
                 * {
                 * e.printStackTrace();
                 * }***
                 * }
                 */
            }

            @Override
            public void draw() {
                // if (shownText != null) {
                gr.textAlign(CENTER);
                gr.textSize(28);
                gr.text(shownText, cx + sin(millis() * 0.001f) * 25, cy);
                // }
            }

            @Override
            public void mousePressed() {
                settingsMenuCheck();
            }

            @Override
            public void keyPressed() {
                settingsMenuKbCheck();
            }

            public void confirmConnection(AgcClient p_client) {
                // BlockingConfirmDialog.create(

                Forms.ui.showConfirmDialog(
                        Forms.getString("ConfirmConnection.message")
                                .replace("<name>", p_client.getName())
                                .replace("<address>", p_client.getIp()),
                        // Window title:
                        Forms.getString("ConfirmConnection.windowTitle"),
                        // If yes,
                        new Runnable() {
                            @Override
                            public void run() {
                                socket.addClientIfAbsent(p_client);
                                socket.sendCode(RequestCode.CLIENT_WAS_REGISTERED, p_client);
                            }
                        },
                        // If no,
                        new Runnable() {
                            @Override
                            public void run() {
                                confirmRejection(p_client);
                            }
                        });
            }

            public void confirmRejection(AgcClient p_client) {
                // BlockingConfirmDialog.create(
                Forms.ui.showConfirmDialog(
                        // Older stuff that used `.concat()`:
                        /*
                         * // Forms.ui.showConfirmDialog(
                         * // Forms.getString("RejectConnection.begin")
                         * // .concat(" \"")
                         * // .concat(p_client.getName())
                         * // .concat("\" (IP: `")
                         * // .concat(p_client.getIp())
                         * // .concat("`)? "),
                         * // Forms.getString("RejectConnection.windowTitle"),
                         */

                        Forms.getString("RejectConnection.message")
                                .replace("<name>", p_client.getName()),

                        Forms.getString("RejectConnection.windowTitle"),

                        new Runnable() {
                            @Override
                            public void run() {
                                socket.banClient(p_client);
                                socket.sendCode(RequestCode.CLIENT_WAS_BANNED, p_client);
                                System.out.printf(
                                        "Client from IP `%s` was rejected and banned.\n",
                                        p_client.getIp());
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {
                                confirmConnection(p_client);
                            }
                        });
            }

            @Override
            public void onReceive(byte[] p_data, String p_ip, int p_port) {
                if (socket.isIpBanned(p_ip))
                    return;

                if (RequestCode.packetHasCode(p_data)) {
                    switch (RequestCode.fromPacket(p_data)) {
                        case ADD_ME: { // Limits the stack so
                                       // that `client` is
                                       // namespaced here :D

                            System.out.printf("Client wished to join! IP: `%s`, port: `%d`.\n",
                                    p_ip, p_port);

                            byte[] nameBytes = new byte[p_data.length - RequestCode.EXTRA_DATA_START];
                            System.arraycopy(p_data, RequestCode.EXTRA_DATA_START,
                                    nameBytes, 0, nameBytes.length);

                            AgcServerSocket.AgcClient client = socket.new AgcClient(socket, p_ip, p_port,
                                    new String(nameBytes));

                            System.out.printf(
                                    "The client of IP `%s` reported the name: \"%s\".\n",
                                    p_ip, client.getName());

                            if (!(socket.isClientBanned(client) && socket.clients.contains(client)))
                                if (!noMorePings) {
                                    noMorePings = true;
                                    new Thread() {
                                        public void run() {
                                            confirmConnection(client);
                                            noMorePings = false;
                                        };
                                    }.start();
                                }
                        }
                            break;

                        case CLIENT_SENDS_CONFIG:
                            if (socket.clients.size() != 0) {
                                socket.clients.get(0).window = Sketch.myWindow;
                            }

                            registerClientConfig(p_data, socket.getClientFromIp(p_ip));
                            Scene.setScene(workScene);
                            break;

                        default:
                            noClientsCheck();

                            System.out.printf("""
                                    Received a `%d`-byte long unrecognizzed sequence of bytes saying
                                        \"%s\" from IP: `%s`, port: `%d`.\n""",
                                    p_data.length, new String(p_data), p_ip, p_port);
                            break;
                    }
                }
            };
        };

        workScene = new Scene() {
            ArrayList<Robot> robots = new ArrayList<>(); // Holds a new instance of `Robot` for each type of control.
            ArrayList<ButtonRendererForServer> buttonRenderers = new ArrayList<>();
            ArrayList<DpadButtonRendererForServer> dpadButtonRenderers = new ArrayList<>();
            ArrayList<TouchpadRendererForServer> touchpadRenderers = new ArrayList<>();

            @Override
            public void setup() {
                Robot robot = null; // Used for iteration, I guess.

                // Coordinate mapping and addition to the 'buttonRenderers'! :joy:...
                for (ButtonConfig c : Sketch.myConfig.buttons) {
                    // System.out.println("Old scale and transform:");
                    // System.out.println(c.scale);
                    // System.out.println(c.transform);

                    c.scale.set(
                            map(c.scale.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.scale.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    c.transform.set(
                            map(c.transform.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.transform.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    // Construct a robot:
                    try {
                        robot = new Robot();
                        robots.add(robot);
                    } catch (AWTException e) {
                        // It just... won't happen!
                    }

                    // Add the button:
                    ButtonRendererForServer button = new ButtonRendererForServer(c, robot);
                    buttonRenderers.add(button);

                    // System.out.println("New, mapped scale and transform:");
                    // System.out.println(c.scale);
                    // System.out.println(c.transform);
                }

                for (DpadButtonConfig c : Sketch.myConfig.dpadButtons) {
                    c.scale.set(
                            map(c.scale.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.scale.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    c.transform.set(
                            map(c.transform.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.transform.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    // Construct a robot:
                    try {
                        robot = new Robot();
                        robots.add(robot);
                    } catch (AWTException e) {
                        // It just... won't happen!
                    }

                    // Add the DPAD button:
                    DpadButtonRendererForServer button = new DpadButtonRendererForServer(c, robot);
                    dpadButtonRenderers.add(button);
                }

                for (TouchpadConfig c : Sketch.myConfig.touchpads) {
                    c.scale.set(
                            map(c.scale.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.scale.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    c.transform.set(
                            map(c.transform.x, 0, Sketch.myConfig.screenDimensions.x, 0, Sketch.AGC_WIDTH),
                            map(c.transform.y, 0, Sketch.myConfig.screenDimensions.y, 0, Sketch.AGC_HEIGHT));

                    // Construct a robot:
                    try {
                        robot = new Robot();
                        robots.add(robot);
                    } catch (AWTException e) {
                        // It just... won't happen!
                    }

                    // Add the DPAD button:
                    TouchpadRendererForServer button = new TouchpadRendererForServer(c, robot);
                    touchpadRenderers.add(button);
                }

                // More button types are initialized here...
            }

            @Override
            public void draw() {
                gr.textAlign(CENTER);
                gr.textSize(28);
                // gr.text("AndroidGameController!", cx, cy);

                if (ServerRenderer.all != null)
                    // Iterating in this manner helps avoid concurrent modification:
                    for (int i = 0; i < ServerRenderer.all.size(); i++)
                        ServerRenderer.all.get(i).draw(gr);

            }

            @Override
            public void mousePressed() {
                settingsMenuCheck();
            }

            @Override
            public void keyPressed() {
                settingsMenuKbCheck();
            }

            public void onReceive(byte[] p_data, String p_ip, int p_port) {
                // System.out.printf("Received *some* bytes from IP: `%s`, on port:`%d`.\n",
                // p_ip, p_port);

                if (RequestCode.packetHasCode(p_data)) {
                    RequestCode code = RequestCode.fromPacket(p_data);
                    System.out.printf("Received a code, `%s`!\n", code.toString());
                    switch (code) {
                        case CLIENT_CLOSE:
                        case CLIENT_LOW_BATTERY:
                            socket.removeClient(p_ip);
                            noClientsCheck();
                            break;

                        case CLIENT_SENDS_CONFIG:
                            registerClientConfig(p_data, socket.getClientFromIp(p_ip));

                        default:
                            break;
                    } // End of `packetHasCode()` check,
                } // End of `onReceive()`.
                else {
                    System.out.println("Received a packet of button data.");
                    Object receivedObject = ByteSerial.decode(p_data);

                    if (receivedObject == null || Sketch.myConfig == null)
                        return;

                    AgcClient client = Sketch.socket.getClientFromIp(p_ip);

                    if (client == null)
                        return;

                    // System.out.println("Config hash info:");

                    if (receivedObject instanceof ButtonState buttonState) {
                        System.out.println("The packet contained data for a button!");
                        for (int i = 0; i < buttonRenderers.size(); i++) {
                            ButtonRendererBase r = buttonRenderers.get(i);
                            if (r.config.controlNumber == buttonState.controlNumber)
                                r.state = buttonState;
                        }
                    } else if (receivedObject instanceof DpadButtonState dpadButtonState) {
                        System.out.println("The packet contained data for a DPAD button!");
                        for (int i = 0; i < dpadButtonRenderers.size(); i++) {
                            DpadButtonRendererBase r = dpadButtonRenderers.get(i);
                            if (r.config.controlNumber == dpadButtonState.controlNumber)
                                r.state = dpadButtonState;
                        }
                    } else if (receivedObject instanceof TouchpadState touchpadState) {
                        System.out.println("The packet contained data for a touchpad!");
                        for (int i = 0; i < touchpadRenderers.size(); i++) {
                            TouchpadRendererBase r = touchpadRenderers.get(i);
                            if (r.config.controlNumber == touchpadState.controlNumber)
                                r.state = touchpadState;
                        }
                    } else {
                        System.out.println("RECEIVED A PACKET FOR AN UNKNOWN CONTROL.");
                    }

                }

                // End of packet data check,
            } // End of `onReceive()`,
        }; // End of `workScene`'s definition'.

        exitScene = new Scene() {
            String thankYouText;
            SineWave windowFadeWave;

            @Override
            public void setup() {
                windowFadeWave = new SineWave(0.0008f);
                windowFadeWave.zeroWhenInactive = true;
                windowFadeWave.endWhenAngleIs(90);
                windowFadeWave.start();

                thankYouText = Forms.getString("ExitScene.text");
                socket.tellAllClients(RequestCode.SERVER_CLOSE);
                socket.close();
            }

            @Override
            public void draw() {
                if (thankYouText != null) { // Need this for some reason...
                    gr.textAlign(CENTER);
                    gr.textSize(28);
                    gr.fill(255, alpha(bgColor));
                    gr.text(thankYouText, cx, qy);
                }

                if (windowFadeWave != null) {
                    float wave = windowFadeWave.get();
                    if (wave == 0) {
                        windowFadeWave.end();

                        while (!Forms.isFormClosed(Forms.settingsForm))
                            ;
                        delay(100);
                        exit();
                    } else {
                        bgColor = color(0, abs(1 - wave) * 150);
                    }
                }
            }
        };

    } // End of scene definitions.

}
