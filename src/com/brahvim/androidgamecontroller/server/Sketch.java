package com.brahvim.androidgamecontroller.server;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.brahvim.androidgamecontroller.Scene;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import uibooster.UiBooster;
import uibooster.model.UiBoosterOptions;

public class Sketch extends PApplet {
    // #region Fields.
    final static SketchWithScenes SKETCH = new SketchWithScenes();
    final static String VERSION = "v1.0";
    final static int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getScreenDevices()[0].getDisplayMode().getRefreshRate();

    // #region Stuff that makes AGC *go!*
    PGraphics gr;
    static AgcServerSocket socket;
    int bgColor = color(0, 150); // Exit fade animation, et cetera.
    float frameStartTime, pframeTime, frameTime;
    // #endregion

    // #region Window coordinates and states.
    PImage surfaceIcon;
    PVector minExtent, maxExtent;

    // Ma' boilerplate :D
    float cx, cy, qx, qy, q3x, q3y;
    int pwidth, pheight;

    boolean mouseInWin;
    int pwinMouseX, pwinMouseY;
    int winMouseX, winMouseY;
    int surfaceX, surfaceY; // Used to constrain the position of the overlay.
    int pMousePsdX, pMousePsdY; // Where was the mouse when it was last clicked?
    // #endregion
    // #endregion Fields.

    // #region `main()` + `settings()`.
    public static void main(String[] p_args) {
        String[] args = new String[] { SKETCH.getClass().getName() };

        if (p_args == null)
            PApplet.runSketch(args, SKETCH);
        else
            PApplet.runSketch(PApplet.concat(p_args, args), SKETCH);
    }

    public void settings() {
        size(400, 200, JAVA2D);
    }
    // #endregion

    // #region Processing's `setup()`, `draw()` and other callbacks.
    public void dispose() {
        agcExit(); // This used to be a part of the comment, LOL.
        super.dispose();
    }

    public void setup() {
        System.out.printf(
                "Welcome to the AndroidGameController Server application `%s`!\n\n",
                Sketch.VERSION);

        // JVM shutdown hook:
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                System.out.println("JVM EXITED.");
                agcExit();
            }
        });

        // Window setup:
        surface.setTitle("AndroidGameController Server ".concat(Sketch.VERSION));
        surface.setIcon(surfaceIcon = loadImage("data/icon-192.png"));
        minExtent = new PVector();
        maxExtent = new PVector(displayWidth - width, displayHeight - height);

        // Forms:
        Forms.init(new UiBooster(UiBoosterOptions.Theme.DARK_THEME));
        Forms.createSettingsForm();

        // Stuff with processing!:
        updateRatios();
        System.out.printf("Running on a `%d`Hz display.\n", REFRESH_RATE);
        frameRate(REFRESH_RATE);

        prepareJPanel();
        // surface.setResizable(true);
        // ^^^ The `surface` or its `JFrame`, ..or even the `JPanel`.
        // NOBODY takes this request!

        // Networking:
        socket = new AgcServerSocket();

        // The very LAST thing to do:
        Sketch.SKETCH.initFirstScene();
    }

    public void pre() {
        Scene.currentScene.pre();
    }

    public void draw() {
        frameStartTime = millis(); // Timestamp.
        frameTime = frameStartTime - pframeTime;
        pframeTime = frameStartTime;

        // #region Window dragging logic:
        pwinMouseX = winMouseX;
        pwinMouseY = winMouseY;

        winMouseX = MouseInfo.getPointerInfo().getLocation().x;
        winMouseY = MouseInfo.getPointerInfo().getLocation().y;

        if (mousePressed) {
            surfaceX = winMouseX - pMousePsdX;
            surfaceY = winMouseY - pMousePsdY;

            if (surfaceX < minExtent.x)
                surfaceX = (int) minExtent.x;

            if (surfaceY < minExtent.y)
                surfaceY = (int) minExtent.y;

            if (surfaceX > maxExtent.x)
                surfaceX = (int) maxExtent.x;

            if (surfaceY > maxExtent.y)
                surfaceY = (int) maxExtent.y;

            surface.setLocation(surfaceX, surfaceY);
        }

        mouseInWin = winMouseX > frame.getX() &&
                winMouseX < frame.getX() + width &&
                winMouseY > frame.getY() &&
                winMouseY < frame.getY() + height;
        // #endregion

        frame.setBackground(new Color(0, 0, 0, 0));

        gr.beginDraw();
        gr.background(bgColor);

        if (Scene.currentScene != null)
            Scene.currentScene.draw();

        gr.endDraw();
    }

    public void post() {
        Scene.currentScene.post();
    }
    // #endregion

    // #region Processing's keyboard event callbacks.
    @Override
    public void keyPressed() {
        Scene.currentScene.keyPressed();
    }

    @Override
    public void keyReleased() {
        Scene.currentScene.keyReleased();
    }

    @Override
    public void keyTyped() {
        Scene.currentScene.keyTyped();
    }

    // #endregion

    // #region Processing's mouse event callbacks.
    @Override
    public void mouseMoved() {
        Scene.currentScene.mouseMoved();
    }

    @Override
    public void mouseWheel(processing.event.MouseEvent p_event) {
        Scene.currentScene.mouseWheel(p_event);
    }

    @Override
    public void mouseClicked() {
        Scene.currentScene.mouseClicked();
    }

    @Override
    public void mouseDragged() {
        Scene.currentScene.mouseDragged();
    }

    @Override
    public void mouseExited() {
        Scene.currentScene.mouseExited();
    }

    @Override
    public void mouseEntered() {
        Scene.currentScene.mouseEntered();
    }

    @Override
    public void mouseReleased() {
        Scene.currentScene.mouseReleased();
    }

    @Override
    public void mousePressed() {
        pMousePsdX = mouseX;
        pMousePsdY = mouseY;

        Scene.currentScene.mousePressed();
    }
    // #endregion

    // #region Custom methods.
    public void updateRatios() {
        cx = width * 0.5f;
        cy = height * 0.5f;
        qx = cx * 0.5f;
        qy = cy * 0.5f;
        q3x = cx + qx;
        q3y = cy + qy;
    }

    public static void agcExit() {
        if (Forms.isFormOpen(Forms.settingsForm))
            Forms.settingsForm.close();

        Scene.setScene(Sketch.SKETCH.exitScene);
    }

    public void prepareJPanel() {
        frame = (JFrame) ((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
        frame.removeNotify();
        frame.setUndecorated(true);
        frame.setLayout(null);
        frame.addNotify();

        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent p_event) {
                System.out.println("Window closing...");
                Sketch.agcExit();
            }

            // Never called!:
            @Override
            public void windowClosed(WindowEvent p_event) {
                System.out.println("Window CLOSED.");
                Sketch.agcExit();
            }

            // #region Unused...
            @Override
            public void windowOpened(WindowEvent p_event) {
            }

            @Override
            public void windowIconified(WindowEvent p_event) {
            }

            @Override
            public void windowDeiconified(WindowEvent p_event) {
            }

            @Override
            public void windowActivated(WindowEvent p_event) {
            }

            @Override
            public void windowDeactivated(WindowEvent p_event) {
            }
            // #endregion
        });

        // The `JPanel`:
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics p_graphics) {
                if (p_graphics instanceof Graphics2D) {
                    // Graphics2D g2d = (Graphics2D) p_graphics;
                    // g2d.drawImage(gr.image, 0, 0, null);
                    ((Graphics2D) p_graphics).drawImage(gr.image, 0, 0, null);
                }
            }
        };

        // Let the `JFrame` be visible and request for `OS` permissions:
        ((JFrame) frame).setContentPane(panel);
        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);
        panel.requestFocus();
        panel.requestFocusInWindow();

        // Listeners for handling events :+1::
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent p_mouseEvent) {
                mousePressed = true;
                mouseButton = p_mouseEvent.getButton();
                SKETCH.mousePressed();
            }

            public void mouseReleased(MouseEvent p_mouseEvent) {
                mousePressed = false;
                SKETCH.mouseReleased();
            }
        });

        // Listeners for `mouseDragged()` and `mouseMoved()`:
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent me) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y;
                SKETCH.mouseDragged();
            }

            public void mouseMoved(MouseEvent me) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - frame.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - frame.getLocation().y;
                SKETCH.mouseMoved();
            }
        });

        // For `keyPressed()`, `keyReleased()` and `keyTyped()`:
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent p_keyEvent) {
                Sketch.SKETCH.key = p_keyEvent.getKeyChar();
                Sketch.SKETCH.keyCode = p_keyEvent.getKeyCode();

                Sketch.SKETCH.keyTyped();
            }

            @Override
            public void keyPressed(KeyEvent p_keyEvent) {
                Sketch.SKETCH.key = p_keyEvent.getKeyChar();
                Sketch.SKETCH.keyCode = p_keyEvent.getKeyCode();

                // System.out.println("Heard a keypress!");

                Sketch.SKETCH.keyPressed();
            }

            @Override
            public void keyReleased(KeyEvent p_keyEvent) {
                Sketch.SKETCH.key = p_keyEvent.getKeyChar();
                Sketch.SKETCH.keyCode = p_keyEvent.getKeyCode();

                Sketch.SKETCH.keyReleased();
            }
        });

        // Handle `Alt + F4` closes ourselves!:
        panel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && e.getKeyCode() == KeyEvent.VK_F4) {

                    if (!Sketch.SKETCH.exitCalled()) {
                        Sketch.agcExit();
                        e.consume();
                    }
                }
            }
        });

        gr = createGraphics(width, height);
    }
    // #endregion
}
