package com.brahvim.androidgamecontroller.server.multiwindow;

// Again, `Ctrl + Shift + [` to fold, just as in Visual Studio!
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import com.brahvim.androidgamecontroller.UdpSocket;
import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;
import com.brahvim.androidgamecontroller.server.Sketch;
import com.brahvim.androidgamecontroller.server.render.ButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.render.DpadButtonRendererForServer;
import com.brahvim.androidgamecontroller.server.render.TouchpadRendererForServer;

import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;

// "Do I make a factory or something? Hmmm..."
// - Brahvim, 2022.

public class AgcClientRepresentative extends PApplet {
    // AGC stuff!:
    public AgcClient guyImRepresenting;
    public Scene currentScene;
    public UdpSocket socket;

    // #region Window coordinates and states.
    public static float cx, cy, qx, qy, q3x, q3y;
    public static int pwidth, pheight;

    public PGraphics gr;

    public JFrame sketchFrame; // We do not rely on the Processing 3 'dummy' variable!

    public boolean mouseInWin;
    public int pwinMouseX, pwinMouseY;
    public int winMouseX, winMouseY;
    public int surfaceX, surfaceY; // Used to constrain the position of the overlay.
    public int pmousePressX, pmousePressY; // Where was the mouse when it was last clicked?

    public int bgColor = color(0, 150); // Exit fade animation, et cetera.
    public float frameStartTime, pframeTime, frameTime;
    // #endregion

    // #region Scene definitions!
    Scene workScene, exitScene;
    {
        // Oooh! JavaScript!:
        this.workScene = new Scene() {
            // Rendering goes here...
        };

        exitScene = new Scene() {
            // "Exeunt!"
        };
    }
    // #endregion

    // #region Renderer lists.
    ArrayList<ButtonRendererForServer> buttonRenderers;
    ArrayList<DpadButtonRendererForServer> dpadButtonRenderers;
    ArrayList<TouchpadRendererForServer> touchpadRenderers;
    // ArrayList<ThumbstickRendererForServer> thumbstickRenderers;
    // (It doesn't exist yet!)
    // #endregion

    AgcClientRepresentative(AgcClient p_guyToRepresent) {
        this.guyImRepresenting = p_guyToRepresent; // :rofl:
    }

    @Override
    public void settings() {
        super.size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT, JAVA2D);
    }

    @Override
    public void setup() {
        super.surface.setTitle("AndroidGameController Server ".concat(Sketch.VERSION));
        super.surface.setIcon(Sketch.surfaceIcon);
        super.surface.setLocation(displayWidth / 2 - (int) cx, displayHeight / 2 + (int) cy);
    }

    public void pre() {
        this.currentScene.pre();
    }

    public void draw() {
        this.frameStartTime = millis(); // Timestamp.
        this.frameTime = this.frameStartTime - this.pframeTime;
        this.pframeTime = this.frameStartTime;

        // #region Window dragging logic:
        this.pwinMouseX = this.winMouseX;
        this.pwinMouseY = this.winMouseY;

        this.winMouseX = MouseInfo.getPointerInfo().getLocation().x;
        this.winMouseY = MouseInfo.getPointerInfo().getLocation().y;

        if (this.mousePressed) {
            this.surfaceX = this.winMouseX - this.pmousePressX;
            this.surfaceY = this.winMouseY - this.pmousePressY;

            if (this.surfaceX < Sketch.minExtent.x)
                this.surfaceX = (int) Sketch.minExtent.x;

            if (this.surfaceY < Sketch.minExtent.y)
                this.surfaceY = (int) Sketch.minExtent.y;

            if (this.surfaceX > Sketch.maxExtent.x)
                this.surfaceX = (int) Sketch.maxExtent.x;

            if (this.surfaceY > Sketch.maxExtent.y)
                this.surfaceY = (int) Sketch.maxExtent.y;

            super.surface.setLocation(surfaceX, surfaceY);
        }

        mouseInWin = winMouseX > sketchFrame.getX() &&
                winMouseX < sketchFrame.getX() + width &&
                winMouseY > sketchFrame.getY() &&
                winMouseY < sketchFrame.getY() + height;
        // #endregion

        this.sketchFrame.setBackground(new Color(0, 0, 0, 0));

        this.gr.beginDraw();
        this.gr.background(bgColor);

        // This! This! This! Not the scene from the 'global' current scene!
        // That's for the client app!
        if (this.currentScene != null)
            this.currentScene.draw();

        this.gr.endDraw();
    }

    public void post() {
        this.currentScene.post();
    }

    public JFrame createSketchPanel(AgcClientRepresentative p_clientWindow) {
        JFrame ret = (JFrame) ((PSurfaceAWT.SmoothCanvas) p_clientWindow.getSurface().getNative()).getFrame();
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
            Image clientImage = p_clientWindow.getGraphics().image;

            @Override
            protected void paintComponent(Graphics p_javaGaphics) {
                if (p_javaGaphics instanceof Graphics2D) {
                    ((Graphics2D) p_javaGaphics).drawImage(clientImage, 0, 0, null);
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
                p_clientWindow.mousePressed();
            }

            public void mouseReleased(MouseEvent p_mouseEvent) {
                mousePressed = false;
                p_clientWindow.mouseReleased();
            }
        });

        // Listeners for `mouseDragged()` and `mouseMoved()`:
        panel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent p_mouseEvent) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;

                p_clientWindow.mouseDragged();
            }

            public void mouseMoved(MouseEvent me) {
                mouseX = MouseInfo.getPointerInfo().getLocation().x - ret.getLocation().x;
                mouseY = MouseInfo.getPointerInfo().getLocation().y - ret.getLocation().y;
                p_clientWindow.mouseMoved();
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
                if (p_clientWindow.currentScene == p_clientWindow.exitScene)
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

}
