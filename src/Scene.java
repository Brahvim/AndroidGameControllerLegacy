import java.util.ArrayList;

import processing.event.MouseEvent;

class Scene {
    final static ArrayList<Scene> SCENES = new ArrayList<Scene>(3);
    static Scene currentScene;

    static void setScene(Scene p_scene) {
        Scene.currentScene = p_scene;
        p_scene.setup();
    }

    Scene() {
        SCENES.add(this);
    }

    // #region Application callback structure:
    void setup() {
    }

    void draw() {
    }

    void pre() {
    }

    void post() {
    }

    void receive(byte[] p_data, String p_ip, int p_port) {
    }
    // #endregion

    // #region Mouse callbacks.
    void mousePressed() {
    }

    void mouseMoved() {
    }

    void mouseWheel(MouseEvent p_event) {
    }

    void mouseClicked() {
    }

    void mouseDragged() {
    }

    void mouseReleased() {
    }
    // #endregion

    // #region Keyboard callbacks.
    void keyPressed() {
    }

    void keyTyped() {
    }

    void keyReleased() {
    }
    // #endregion
}
