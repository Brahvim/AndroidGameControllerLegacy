package com.brahvim.androidgamecontroller;

import java.awt.event.MouseEvent;

public class AppWithScenes extends App {
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
        void onReceive(byte[] p_data, String p_ip, int p_port) {
            System.out.printf("Received `%d` bytes saying \"%s\" from IP: `%s`, port: `%d`.\n",
                    p_data.length, new String(p_data), p_ip, p_port);
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
        void setup() {
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
