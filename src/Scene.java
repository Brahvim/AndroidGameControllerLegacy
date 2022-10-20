import java.util.ArrayList;

import processing.event.MouseEvent;

class Scene {
    public final static ArrayList<Scene> SCENES = new ArrayList<Scene>(3);
    public static Scene currentScene;

    public static void setScene(Scene p_scene) {
        Scene.currentScene = p_scene;
        p_scene.setup();
    }

    public Scene() {
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
    public void mousePressed() {
    }

    public void mouseMoved() {
    }

    public void mouseWheel(MouseEvent p_event) {
    }

    public void mouseClicked() {
    }

    public void mouseDragged() {
    }

    public void mouseReleased() {
    }

    public void mouseExited() {
    }

    public void mouseEntered() {
    }
    // #endregion

    // #region Keyboard callbacks.
    public void keyPressed() {
    }

    public void keyTyped() {
    }

    public void keyReleased() {
    }
    // #endregion

}
