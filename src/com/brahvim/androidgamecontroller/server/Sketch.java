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
    public final static SketchWithScenes SKETCH = new SketchWithScenes();
    public final static String VERSION = "v1.0.0";
    public final static int REFRESH_RATE = GraphicsEnvironment.getLocalGraphicsEnvironment()
            .getScreenDevices()[0].getDisplayMode().getRefreshRate();

    // #region Stuff that makes AGC *go!*
    public PGraphics gr;
    public static AgcServerSocket socket;
    public static AgcClientWindow primaryClientWindow;

    public int bgColor = color(0, 150); // Exit fade animation, et cetera.
    public float frameStartTime, pframeTime, frameTime;
    // #endregion

    // #region Window coordinates and states.
    public PImage surfaceIcon;
    public PVector minExtent, maxExtent;

    // Ma' boilerplate :D
    public float cx, cy, qx, qy, q3x, q3y;
    public int pwidth, pheight;

    public JFrame sketchFrame; // We do not rely on the Processing 3 'dummy' variable!

    public boolean mouseInWin;
    public int pwinMouseX, pwinMouseY;
    public int winMouseX, winMouseY;
    public int surfaceX, surfaceY; // Used to constrain the position of the overlay.
    public int pmousePressX, pmousePressY; // Where was the mouse when it was last clicked?
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

        updateRatios();

        // Window setup:
        surface.setTitle("AndroidGameController Server ".concat(Sketch.VERSION));
        surface.setIcon(surfaceIcon = loadImage("data/icon-192.png"));
        surface.setLocation(displayWidth / 2 - (int) cx, displayHeight / 2 + (int) cy);
        minExtent = new PVector();
        maxExtent = new PVector(displayWidth - width, displayHeight - height);

        // Forms:
        Forms.init(new UiBooster(UiBoosterOptions.Theme.DARK_THEME));
        Forms.createSettingsForm();

        // Stuff with processing!:
        System.out.printf("Running on a `%d`Hz display.\n", REFRESH_RATE);
        frameRate(REFRESH_RATE);

        // Make the window undecorated and all!:
        surface.setAlwaysOnTop(true);
        gr = createGraphics(width, height);
        sketchFrame = createSketchPanel(this, gr);

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
            surfaceX = winMouseX - pmousePressX;
            surfaceY = winMouseY - pmousePressY;

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

        mouseInWin = winMouseX > sketchFrame.getX() &&
                winMouseX < sketchFrame.getX() + width &&
                winMouseY > sketchFrame.getY() &&
                winMouseY < sketchFrame.getY() + height;
        // #endregion

        sketchFrame.setBackground(new Color(0, 0, 0, 0));

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

    public JFrame createSketchPanel(PApplet p_sketch, PGraphics p_sketchGraphics) {
        // This is the dummy variable from Processing.
        JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) p_sketch.getSurface().getNative()).getFrame();
        ret.removeNotify();
        ret.setUndecorated(true);
        ret.setLayout(null);
        ret.addNotify();

        ret.addWindowListener(new WindowListener() {
            @Override
            public void windowClosing(WindowEvent p_event) {
                System.out.println("Window closing...");
                Sketch.agcExit();
            }

            // #region Unused...
            // Never called!:
            @Override
            public void windowClosed(WindowEvent p_event) {
                // System.out.println("Window CLOSED.");
                // Sketch.agcExit();
            }

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

        // #region The `JPanel`:

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics p_javaGaphics) {
                if (p_javaGaphics instanceof Graphics2D) {
                    ((Graphics2D) p_javaGaphics).drawImage(p_sketchGraphics.image, 0, 0, null);
                }
            }
        };

        // Let the `JFrame` be visible and request for `OS` permissions:
        ((JFrame) ret).setContentPane(panel); // This is the dummy variable from Processing.
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
            public void mouseDragged(MouseEvent p_mouseEvent) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;

                SKETCH.mouseDragged();
            }

            public void mouseMoved(MouseEvent me) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;
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
        // It is kinda 'stupid' to use another listener for optimization, but the reason
        // why multiple listeners are allowed anyway is to let outer code access events
        // and also give you convenience :P

        // PS Notice how this uses `KeyAdapter` instead for
        panel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (Scene.currentScene == Sketch.SKETCH.exitScene)
                    return;

                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && e.getKeyCode() == KeyEvent.VK_F4) {

                    // Apparently this wasn't the cause of an error I was trying to rectify.
                    // However, it *still is a good practice!*
                    if (!Sketch.SKETCH.exitCalled()) {
                        Sketch.agcExit();
                        e.consume();
                    }
                }
            }
        });
        // #endregion
        return ret;
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
    public void mouseWheel(processing.event.MouseEvent p_mouseEvent) {
        Scene.currentScene.mouseWheel(p_mouseEvent);
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
        pmousePressX = mouseX;
        pmousePressY = mouseY;

        Scene.currentScene.mousePressed();
    }
    // #endregion
}
