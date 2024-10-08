package com.brahvim.androidgamecontroller;

import java.util.ArrayList;

public class Scene {
    public final static ArrayList<Scene> SCENES = new ArrayList<Scene>(3);
    public static Scene currentScene; // Used *only* by the AGC client.

    // region "`static`s".
    public static void setScene(Scene p_scene) {
        Scene.currentScene = p_scene;
        p_scene.setup();
    }

    public static void addScene(Scene p_scene) {
        Scene.SCENES.add(p_scene);
    }

    public Scene() {
        Scene.SCENES.add(this);
    }
    // endregion

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
