package com.brahvim.androidgamecontroller;

// Do not use this `Scene` class in other projects!
// It's now focused on this one!

public class Scene {
    // region Not used in AGC anymore!:
    // public final static ArrayList<Scene> SCENES = new ArrayList<Scene>(3);
    // public static Scene currentScene;

    // public static void setScene(Scene p_scene) {
    // Scene.currentScene = p_scene;
    // p_scene.setup();
    // }

    // public static void addScene(Scene p_scene) {
    // Scene.SCENES.add(p_scene);
    // }
    // endregion

    public Scene() {
        // Not in AGC!...:
        // Scene.SCENES.add(this);
    }

    // region Application callback structure.
    public void setup() {
    }

    public void draw() {
    }

    public void pre() {
    }

    public void post() {
    }

    // @SuppressWarnings("unused")
    public void onReceive(byte[] p_data, String p_ip, int p_port) {
    }
    // endregion

    // region Mouse events.
    public void mousePressed() {
    }

    public void mouseMoved() {
    }

    public void mouseWheel(processing.event.MouseEvent p_mouseEvent) {
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
    // endregion

    // region Keyboard events.
    public void keyPressed() {
    }

    public void keyTyped() {
    }

    public void keyReleased() {
    }
    // endregion
}
