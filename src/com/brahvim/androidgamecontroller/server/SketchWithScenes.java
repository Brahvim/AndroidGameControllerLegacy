package com.brahvim.androidgamecontroller.server;

import java.awt.event.MouseEvent;

import com.brahvim.androidgamecontroller.RequestCode;
import com.brahvim.androidgamecontroller.Scene;

import processing.core.PApplet;

public class SketchWithScenes extends Sketch {
    void initFirstScene() {
        Scene firstScene = awaitingConnectionScene;

        // #region "Are Wii gunna have a problem?""
        firstScene.setup();
        Scene.currentScene = firstScene;
        // #endregion
    }

    public void settingsMenuCheck() {
        if (mouseButton == MouseEvent.BUTTON3) {
            if (isFormOpen(Forms.settingsForm)) {
            } else if ((Forms.settingsForm = showForm(
                    Forms.settingsForm, Forms.settingsFormBuild)) != null) {
                Forms.settingsForm.getWindow().setLocation(frame.getX(), frame.getY());
                Forms.settingsForm.getWindow().setResizable(false);
            }
        }
    }

    Scene awaitingConnectionScene = new Scene() {
        // #region "Awaiting Connections" scene.
        String shownText;

        @Override
        public void setup() {
            shownText = Forms.getString("AwaitingConnectionsScene.text");
        }

        @Override
        public void draw() {
            gr.textAlign(CENTER);
            gr.textSize(28);
            gr.text(shownText, cx + sin(millis() * 0.001f) * 25, cy);
        }

        @Override
        public void mousePressed() {
            settingsMenuCheck();
        }

        @Override
        public void onReceive(byte[] p_data, String p_ip, int p_port) {
            System.out.printf("Received `%d` bytes saying \"%s\" from IP: `%s`, port:`%d`.\n",
                    p_data.length, new String(p_data), p_ip, p_port);

            boolean isCode = true;
            for (int i = 0; i < RequestCode.CODE_SUFFIX.length; i++) {
                if (p_data[i] != RequestCode.CODE_SUFFIX[i]) {
                    isCode = false;
                    break;
                }
            }

            if (isCode) {
                switch (RequestCode.fromPacket(p_data)) {
                    case ADD_ME: { // Limits the stack so that `client` is namespaced here :D
                        System.out.printf("Client joined! IP: `%s`, port: `%d`.\n",
                                p_ip, p_port);

                        AgcServerSocket.AgcClient client;
                        String[] names; // The Android client device's names.
                        try {
                            int strEnd = Integer.BYTES;
                            // ^^^ Since array indices are whole numbers, this starts AFTER the code part.

                            boolean second = false;

                            for (; strEnd < p_data.length; strEnd++)
                                if (p_data[strEnd] == '\0') {
                                    if (second)
                                        break;
                                    second = true;
                                }

                            String namesStr = new String(p_data, Integer.BYTES, strEnd);
                            System.out.println(namesStr);

                            // Usually, this constructor would make a string on its own, but in this case,
                            // it would also include the code's bytes, which isn't what we want, so we use
                            // the value of the iterator of the loop from before:
                            names = PApplet.split(namesStr,
                                    // p_data.length * Character.BYTES),
                                    '\n');
                            client = socket.new AgcClient(socket, p_ip, p_port, names[0], names[1]);

                            printArray(names);
                            System.out.printf("The client of IP `%s` reported the names: \"%s\" and \"%s\".\n", p_ip,
                                    names[0], names[1]);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            String name = new String(p_data, Integer.BYTES + (Character.BYTES * 2),
                                    p_data.length / Character.BYTES);
                            // The other string is re-allocated to make sure that no changes occur in the
                            // first one when editing the second string (the client's "bluetooth name").
                            client = socket.new AgcClient(socket, p_ip, p_port, name, new String(name));

                            System.out.printf("The client of IP `%s`, reported only one name, \"%s\".\n", p_ip, name);
                        }

                        socket.addClientIfAbsent(client);
                        // ^^^ Should be included in the `AgcClient` constructor now,
                        // ...just like the initial plan!

                        socket.sendCode(RequestCode.CLIENT_WAS_REGISTERED, client);
                    }
                        break;
                    default:
                        break;
                }
            } else {
                // The packet contains data! Get your `java.awt.Robot`s now!
            }
        };
        // #endregion
    };

    Scene workingScene = new Scene() {
        // #region "Working Connections" scene.
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
        // #endregion
    };

    Scene exitScene = new Scene() {
        // #region "Exiting" scene.
        String thankYouText;

        @Override
        public void setup() {
            thankYouText = Forms.getString("ExitScene.text");
        }

        @Override
        public void draw() {
            gr.textAlign(CENTER);
            gr.textSize(28);
            gr.fill(255, alpha(bgColor));
            gr.text(thankYouText, cx, qy);

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
        // #endregion
    };

}
