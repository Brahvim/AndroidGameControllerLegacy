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
import com.brahvim.androidgamecontroller.serial.config.ConfigurationPacket;

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
    public final static int AGC_WIDTH = 400, AGC_HEIGHT = 200;
    public static Scene currentScene;

    // #region Stuff that makes AGC *go!*
    public PGraphics gr;
    public static AgcServerSocket socket;
    public static AgcClientWindow myWindow; // The primary client's window AKA the main window! :D
    public static ConfigurationPacket myConfig; // The primary client's configuration.

    public int bgColor = color(0, 150); // Exit fade animation, et cetera.
    public static float frameStartTime, pframeTime, frameTime;
    // #endregion

    // #region Window coordinates and states.
    public PImage surfaceIcon;
    public PVector minExtent, maxExtent;

    // #region Ma'h boilerplate :D
    public static float cx, cy, qx, qy, q3x, q3y;
    public static int pwidth, pheight;
    // #endregion

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
        size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT, JAVA2D);
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

        // Networking, plus AGC stuff!:
        socket = new AgcServerSocket();

        // The very LAST thing to do:
        initFirstScene();
        // Sketch.SKETCH.initFirstScene();
    }

    public void pre() {
        Sketch.currentScene.pre();
    }

    public void initFirstScene() {
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

        if (Sketch.currentScene != null)
            Sketch.currentScene.draw();

        gr.endDraw();
    }

    public void post() {
        Sketch.currentScene.post();
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

        Sketch.currentScene = Sketch.SKETCH.exitScene;
    }

    /**
     * @param p_sketch         The `PApplet` instance you are working with.
     * @param p_sketchGraphics A graphics buffer into which you will draw.
     * @return The new {@code JFrame} assigned to your sketch's surface / "window".
     *         Better store it somewhere!
     */
    public static JFrame createSketchPanel(PApplet p_sketch, PGraphics p_sketchGraphics) {
        // This is NOT the dummy variable from Processing.
        JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) p_sketch.getSurface().getNative())
                .getFrame();
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
        ret.setContentPane(panel); // This is NOT the dummy variable from Processing.

        panel.setFocusable(true);
        panel.setFocusTraversalKeysEnabled(false);
        panel.requestFocus();
        panel.requestFocusInWindow();

        // #region Listeners for handling events :+1::
        // Listener for `mousePressed()` and `mouseReleased()`:
        panel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = true;
                p_sketch.mouseButton = p_mouseEvent.getButton();
                p_sketch.mousePressed();
            }

            public void mouseReleased(MouseEvent p_mouseEvent) {
                p_sketch.mousePressed = false;
                p_sketch.mouseReleased();
            }
        });

        // Listener for `mouseDragged()` and `mouseMoved()`:
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent p_mouseEvent) {
                p_sketch.mouseX = MouseInfo.getPointerInfo()
                        .getLocation().x - ret.getLocation().x;

                p_sketch.mouseY = MouseInfo.getPointerInfo()
                        .getLocation().y - ret.getLocation().y;

                p_sketch.mouseDragged();
            }

            public void mouseMoved(MouseEvent p_mouseEvent) {
                p_sketch.mouseX = MouseInfo.getPointerInfo()
                        .getLocation().x - ret.getLocation().x;

                p_sketch.mouseY = MouseInfo.getPointerInfo()
                        .getLocation().y - ret.getLocation().y;

                p_sketch.mouseMoved();
            }
        });

        // For `keyPressed()`, `keyReleased()` and `keyTyped()`:
        panel.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                p_sketch.keyTyped();
            }

            @Override
            public void keyPressed(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                // System.out.println("Heard a keypress!");
                p_sketch.keyPressed();
            }

            @Override
            public void keyReleased(KeyEvent p_keyEvent) {
                p_sketch.key = p_keyEvent.getKeyChar();
                p_sketch.keyCode = p_keyEvent.getKeyCode();
                p_sketch.keyReleased();
            }
        });

        // Handle `Alt + F4` closes ourselves!:
        // It is kinda 'stupid' to use another listener for optimization, but the reason
        // why multiple listeners are allowed anyway is to let outer code access events
        // and also give you convenience :P

        // PS Notice how this uses `KeyAdapter` instead for
        panel.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent p_keyEvent) {
                if (Sketch.currentScene == Sketch.SKETCH.exitScene)
                    return;

                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && p_keyEvent.getKeyCode() == KeyEvent.VK_F4) {

                    // Apparently this wasn't the cause of an error I was trying to rectify.
                    // However, it *still is a good practice!*
                    if (!p_sketch.exitCalled()) {
                        Sketch.agcExit();
                        p_keyEvent.consume();
                    }
                }
            }
        });
        // #endregion
        // #endregion

        return ret;
    }
    // #endregion

    // #region Processing's keyboard event callbacks.
    @Override
    public void keyPressed() {
        Sketch.currentScene.keyPressed();
    }

    @Override
    public void keyReleased() {
        Sketch.currentScene.keyReleased();
    }

    @Override
    public void keyTyped() {
        Sketch.currentScene.keyTyped();
    }
    // #endregion

    // #region Processing's mouse event callbacks.
    @Override
    public void mouseMoved() {
        Sketch.currentScene.mouseMoved();
    }

    @Override
    public void mouseWheel(processing.event.MouseEvent p_mouseEvent) {
        Sketch.currentScene.mouseWheel(p_mouseEvent);
    }

    @Override
    public void mouseClicked() {
        Sketch.currentScene.mouseClicked();
    }

    @Override
    public void mouseDragged() {
        Sketch.currentScene.mouseDragged();
    }

    @Override
    public void mouseExited() {
        Sketch.currentScene.mouseExited();
    }

    @Override
    public void mouseEntered() {
        Sketch.currentScene.mouseEntered();
    }

    @Override
    public void mouseReleased() {
        Sketch.currentScene.mouseReleased();
    }

    @Override
    public void mousePressed() {
        pmousePressX = mouseX;
        pmousePressY = mouseY;

        Sketch.currentScene.mousePressed();
    }
    // #endregion

}
