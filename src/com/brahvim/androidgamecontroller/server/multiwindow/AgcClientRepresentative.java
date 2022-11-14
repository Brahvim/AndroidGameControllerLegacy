package com.brahvim.androidgamecontroller.server.multiwindow;

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

// "Do I make a factory or something? Hmmm..."
// - Brahvim, 2022.

public class AgcClientRepresentative extends PApplet {
    AgcClient guyImRepresenting;
    Scene currentScene;
    UdpSocket socket;

    // #region Scene definitions!
    Scene workScene, exitScene;
    {
        workScene = new Scene() {
        };

        exitScene = new Scene() {
        };
    }
    // #endregion

    // #region Renderer lists
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
        size(Sketch.AGC_WIDTH, Sketch.AGC_HEIGHT, JAVA2D);
    }

    @Override
    public void setup() {

    }

    @Override
    public void draw() {
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
