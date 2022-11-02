package com.brahvim.androidgamecontroller.server;

import java.awt.event.MouseEvent;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.Scene;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

public class SketchWithScenes extends Sketch {
    void initFirstScene() {
        // #region "Are Wii gunna have a problem?""
        Scene firstScene = awaitingConnectionScene;
        firstScene.setup();
        Scene.currentScene = firstScene;
        // #endregion
    }

    public void settingsMenuCheck() {
        if (mouseButton == MouseEvent.BUTTON3 && !isFormOpen(Forms.settingsForm)) {
            Forms.settingsForm = showForm(Forms.settingsForm, Forms.settingsFormBuild);
            Forms.settingsForm.getWindow().setLocation(frame.getX(), frame.getY());
            Forms.settingsForm.getWindow().setResizable(false);
        }
    }

    public void noClientsCheck() {
        if (socket.clients.size() == 0) {
            Scene.setScene(awaitingConnectionScene);
        }
    }

    public void confirmConnection(AgcClient p_client) {
        Forms.ui.showConfirmDialog(
                "Ping-pong!\nA device named \""
                        .concat(p_client.getName())
                        .concat("\" (IP: `")
                        .concat(p_client.getIp())
                        .concat(")`")
                        .concat(" would like to connect!\n\nDo you allow this?"),

                // Window title:
                "New connection!",
                new Runnable() {
                    @Override
                    public void run() {
                        socket.addClientIfAbsent(p_client);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        confirmRejection(p_client);
                    }
                });
    }

    public void confirmRejection(AgcClient p_client) {
        Forms.ui.showConfirmDialog(
                """
                        For the rest of this session
                            (AKA till AndroidGameController restarts),
                            the device \"
                            """
                        .concat(p_client.getName())
                        .concat("\" (IP: `(")
                        .concat("`). ")
                        .concat("won't be allowed to connect.\n")
                        .concat("Is this OK?"),
                "Are you sure..?", new Runnable() {
                    @Override
                    public void run() {
                        socket.banIp(p_client.getIp());
                        System.out.println("Client from IP `%s` was rejected and banned.\n");
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        confirmConnection(p_client);
                    }
                });
    }

    // "Please never make any `Scene` instances `static`."
    Scene awaitingConnectionScene, workingScene, exitScene;

    { // Scene definitions.

        awaitingConnectionScene = new Scene() {
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
                 */}

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
            public void onReceive(byte[] p_data, String p_ip, int p_port) {
                System.out.printf("Received `%d` bytes saying \"%s\" from IP: `%s`, port: `%d`.\n",
                        p_data.length, new String(p_data), p_ip, p_port);

                if (RequestCode.packetHasCode(p_data)) {
                    switch (RequestCode.fromPacket(p_data)) {
                        case ADD_ME: { // Limits the stack so
                                       // that `client` is
                                       // namespaced here :D
                            System.out.printf("Client joined! IP: `%s`, port: `%d`.\n", p_ip, p_port);

                            byte[] nameBytes = new byte[p_data.length - RequestCode.EXTRA_DATA_START];
                            System.arraycopy(p_data, RequestCode.EXTRA_DATA_START, nameBytes, 0,
                                    nameBytes.length);

                            AgcServerSocket.AgcClient client = socket.new AgcClient(socket, p_ip, p_port,
                                    new String(nameBytes));

                            System.out.printf(
                                    "The client of IP `%s` reported the name: \"%s\".\n",
                                    p_ip, client.getName());

                            if (!socket.isClientBanned(client))
                                confirmConnection(client);
                            // socket.addClientIfAbsent(client);
                            // ^^^ Should be included in the `AgcClient` constructor now,
                            // ...just like the initial plan!

                            socket.sendCode(RequestCode.CLIENT_WAS_REGISTERED, client);

                            // ...so we finally have a client!:
                            if (socket.clients.size() != 0)
                                Scene.setScene(workingScene);
                        }
                            break;

                        default:
                            noClientsCheck();
                            break;
                    }
                } else {
                    // The packet contains data! Get your `java.awt.Robot`s now!
                }
            };
        };

        workingScene = new Scene() {
            @Override
            public void draw() {
                gr.textAlign(CENTER);
                gr.textSize(28);
                gr.text("AndroidGameController!", cx, cy);
            }

            @Override
            public void mousePressed() {
                settingsMenuCheck();
            }

            public void onReceive(byte[] p_data, String p_ip, int p_port) {
                System.out.printf("Received *some* bytes from IP: `%s`, on port:`%d`.\n", p_ip, p_port);

                if (RequestCode.packetHasCode(p_data)) {
                    RequestCode code = RequestCode.fromPacket(p_data);
                    System.out.printf("It was a code, `%s`!\n", code.toString());
                    switch (code) {
                        case CLIENT_CLOSE:
                        case CLIENT_LOW_BATTERY:
                            socket.removeClient(p_ip);
                            noClientsCheck();
                            break;

                        default:
                            break;
                    } // End of `packetHasCode()` check,
                } // End of `onReceive()`.
                else
                    System.out.println("It was a packet of button data.");
            };
        };

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
                        while (!Forms.settingsForm.isClosedByUser())
                            ;
                        delay(100);
                        exit();
                    } else {
                        bgColor = color(0, abs(1 - wave) * 150);
                    }
                }
            }
        };

    }

}
