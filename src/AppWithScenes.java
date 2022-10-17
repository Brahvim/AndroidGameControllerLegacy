public class AppWithScenes extends App {
    void initFirstScene() {
        Scene firstScene = awaitingConnectionScene;

        // #region "Are Wii gunna have a problem?""
        firstScene.setup();
        Scene.currentScene = firstScene;
        // #endregion
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
        // #endregion
    };

    Scene exitScene = new Scene() {
        String thankYouText;

        @Override
        void setup() {
            thankYouText = Forms.getString("ExitScene.text");
        }

        // #region "Exiting" scene.
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
