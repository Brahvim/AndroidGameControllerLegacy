package com.brahvim.androidgamecontroller.server;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import com.brahvim.androidgamecontroller.Scene;
import com.brahvim.androidgamecontroller.serial.config.ConfigurationPacket;
import com.brahvim.androidgamecontroller.serial.state.ButtonState;
import com.brahvim.androidgamecontroller.serial.state.DpadButtonState;
import com.brahvim.androidgamecontroller.serial.state.KeyboardState;
import com.brahvim.androidgamecontroller.serial.state.ThumbstickState;
import com.brahvim.androidgamecontroller.serial.state.TouchpadState;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

public class AgcClientWindow extends PApplet {
    // #region Fields.
    public AgcClient parentClient;
    public AgcClientWindow SKETCH = this;
    public static ArrayList<AgcClientWindow> all = new ArrayList<>();
    public Scene currentScene;

    // #region Stuff that makes AGC *go!*
    public PGraphics gr;
    public static AgcServerSocket socket;
    public static AgcClientWindow myWindow; // The primary client's window AKA the main window! :D
    public static ConfigurationPacket myConfig; // The primary client's configuration.

    public int bgColor = color(0, 150); // Exit fade animation, et cetera.
    public static float frameStartTime, pframeTime, frameTime;

    public ArrayList<ButtonState> buttonStates;
    public ArrayList<DpadButtonState> dpadButtonStates;
    public ArrayList<ThumbstickState> thumbstickStates;
    public ArrayList<TouchpadState> touchpadStates;
    public KeyboardState keyboardState;
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
    // #endregion

    AgcClientWindow(AgcClient p_client) {
        AgcClientWindow.all.add(this);

        this.parentClient = p_client;
        this.parentClient.config = new ConfigurationPacket();

        this.buttonStates = new ArrayList<>();
        this.dpadButtonStates = new ArrayList<>();
        this.thumbstickStates = new ArrayList<>();
        this.touchpadStates = new ArrayList<>();
        this.keyboardState = new KeyboardState();

        PApplet.runSketch(new String[] { this.getClass().getName() }, this);
    }

    @Override
    public void settings() {
        super.size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT, JAVA2D);
    }

    public void agcExit() {
        this.currentScene = Sketch.SKETCH.exitScene;
        Sketch.agcExit();
    }

    public void dispose() {
        this.agcExit();
        super.dispose();
    }

    @Override
    public void setup() {
        this.sketchFrame = AgcClientWindow.createSketchPanel(this, this.g);
    }

    @Override
    public void draw() {
    }

    public void onReceive(byte[] p_data, String p_ip, int p_port) {
    }

    /**
     * @param p_sketch         The `PApplet` instance you are working with.
     * @param p_sketchGraphics A graphics buffer into which you will draw.
     * @return The new {@code JFrame} assigned to your sketch's surface / "window".
     *         Better store it somewhere!
     */
    public static JFrame createSketchPanel(AgcClientWindow p_sketch, PGraphics p_sketchGraphics) {
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
                if (p_sketch.currentScene == Sketch.SKETCH.exitScene)
                    return;

                if (KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, InputEvent.ALT_DOWN_MASK) != null
                        && p_keyEvent.getKeyCode() == KeyEvent.VK_F4) {

                    // Apparently this wasn't the cause of an error I was trying to rectify.
                    // However, it *still is a good practice!*
                    if (!p_sketch.exitCalled()) {
                        p_sketch.agcExit();
                        p_keyEvent.consume();
                    }
                }
            }
        });
        // #endregion
        // #endregion

        return ret;
    }

}
