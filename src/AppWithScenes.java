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
            gr.text(shownText, cx, cy);
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

    Scene exitingScene = new Scene() {
        // #region "Exiting" scene.
        @Override
        public void draw() {
            gr.textAlign(CENTER);
            gr.textSize(28);
            gr.fill(255, alpha(bgColor));
            gr.text("Thank you for\nusing\nAndroidGameController!", cx, qy);

            float wave = exitFadeWave.get();
            if (wave == 0) {
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
