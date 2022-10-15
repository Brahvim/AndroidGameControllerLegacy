import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JPanel;

import hypermedia.net.UDP;
import processing.awt.PSurfaceAWT;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import uibooster.UiBooster;
import uibooster.model.Form;
import uibooster.model.FormBuilder;
import uibooster.model.UiBoosterOptions;

public class App extends PApplet {
    // #region Fields.
    final static AppWithScenes SKETCH = new AppWithScenes();

    // #region Stuff that makes AGC *go!*
    // final static String NEWLINE = System.lineSeparator();
    UDP server;
    UdpSocket socket;
    Robot robot;
    UiBooster ui;
    HashMap<String, String> strTable = parseStringTableFromFile("AGC_StringTable.ini");
    ArrayList<String> possibleClients, connectedClients;
    PGraphics gr;
    boolean hasOneConnection;
    float frameStartTime, deltaTime, pframeTime, frameTime;
    // #endregion

    // #region Exit fade animation variables.
    int bgColor = color(0, 150);
    SineWave exitFadeWave;
    // #endregion

    // #region Window coordinates and states.
    PImage surfaceIcon;
    PVector minExtent, maxExtent;

    float cx, cy, qx, qy, q3x, q3y;
    int pwidth, pheight;

    int pwinMouseX, pwinMouseY;
    int winMouseX, winMouseY;
    int sfX, sfY; // Used to constrain the position of the overlay.
    int pPsdX, pPsdY; // Where was the mouse when it was last clicked?
    boolean mouseInWin;
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

    // #region Processing's windowing callbacks.
    public void dispose() {
        if (!(connectedClients == null || server == null))
            for (String s : connectedClients)
                server.send(requestCodeToBytes("SERVER_CLOSE"), s);

        // Exit the sketch:
        super.dispose();
    }

    public void setup() {
        // Window setup:
        surface.setTitle("AndroidGameController Server");
        surface.setIcon(surfaceIcon = loadImage("data/icon-192.png"));
        minExtent = new PVector();
        maxExtent = new PVector(displayWidth - width, displayHeight - height);

        // Forms:
        ui = new UiBooster(UiBoosterOptions.Theme.DARK_THEME);
        // strTable = parseIniFile("AGC_StringTable.ini");

        Forms.init(ui, strTable);
        Forms.createSettingsForm();

        // First scene! AFTER THE S-T-R TABLE GHSBSAJDHHSDVHJVASHj:
        App.SKETCH.initFirstScene();

        // Stuff with processing!:
        updateRatios();
        frameRate(240);
        surface.setVisible(false); // Visible after it has connected.
        prepareJPanel();

        Forms.showFindingConnectionDialog();

        // Networking:
        possibleClients = getNetworks();
        System.out.printf("Possible clients:\n%s\n", possibleClients.toString());
        server = new UDP(this);

        for (String s : possibleClients)
            server.send(requestCodeToBytes("FINDING_DEVICES"), s);

        // while (!hasOneConnection)
        // ;
        surface.setVisible(true);
        Forms.findingDevicesDialog.close();
        surface.setVisible(true);
    }

    public void pre() {
        Scene.currentScene.pre();
    }

    public void draw() {
        frameStartTime = millis(); // Timestamp.
        frameTime = frameStartTime - pframeTime;
        pframeTime = frameStartTime;
        deltaTime = frameTime * 0.01f;

        // #region Window dragging logic:
        pwinMouseX = winMouseX;
        pwinMouseY = winMouseY;

        winMouseX = MouseInfo.getPointerInfo().getLocation().x;
        winMouseY = MouseInfo.getPointerInfo().getLocation().y;

        if (mousePressed) {
            sfX = winMouseX - pPsdX;
            sfY = winMouseY - pPsdY;

            if (sfX < minExtent.x)
                sfX = (int) minExtent.x;

            if (sfY < minExtent.y)
                sfY = (int) minExtent.y;

            if (sfX > maxExtent.x)
                sfX = (int) maxExtent.x;

            if (sfY > maxExtent.y)
                sfY = (int) maxExtent.y;

            surface.setLocation(sfX, sfY);
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

    // #region Processing and other libraries' input callbacks.
    public void receive(byte[] p_data, String p_ip, int p_port) {
        String request = new String(p_data);
        switch (request) {
            case "FINDING_DEVICES":
                break;

            case "ADD_ME":
                connectedClients.add(p_ip);

                if (connectedClients.size() > 0)
                    hasOneConnection = true;
                break;

            case "CLIENT_CLOSE":
                connectedClients.remove(p_ip);
                break;
        }

    }

    public void mousePressed() {
        pPsdX = mouseX;
        pPsdY = mouseY;

        if (mouseButton == MouseEvent.BUTTON3) {
            if (isOpen(Forms.settingsForm)) {
            } else if ((Forms.settingsForm = showForm(
                    Forms.settingsForm, Forms.settingsFormBuild)) != null) {
                Forms.settingsForm.getWindow().setLocation(frame.getX(), frame.getY());
                Forms.settingsForm.getWindow().setResizable(false);
            }
        }
    }
    // #endregion

    // #region Custom methods.
    public byte[] requestCodeToBytes(String p_req) {
        return ByteBuffer.allocate(Integer.BYTES)
                .putInt(RequestCode.values.get(p_req)).array();
    }

    public void updateRatios() {
        cx = width * 0.5f;
        cy = height * 0.5f;
        qx = cx * 0.5f;
        qy = cy * 0.5f;
        q3x = cx + qx;
        q3y = cy + qy;
    }

    public void agcExit() {
        Scene.setScene(App.SKETCH.exitingScene);

        exitFadeWave = new SineWave(0.0008f);
        exitFadeWave.zeroWhenInactive = true;
        exitFadeWave.endWhenAngleIs(90);
        exitFadeWave.start();

        if (Forms.settingsForm != null)
            if (!Forms.settingsForm.isClosedByUser())
                Forms.settingsForm.close();
    }

    public void prepareJPanel() {
        frame = (JFrame) ((PSurfaceAWT.SmoothCanvas) getSurface().getNative()).getFrame();
        frame.removeNotify();
        frame.setUndecorated(true);
        frame.setLayout(null);
        frame.addNotify();

        // The `JPanel`:
        JPanel panel = new JPanel() {

            @Override
            protected void paintComponent(Graphics p_graphics) {
                if (p_graphics instanceof Graphics2D) {
                    Graphics2D g2d = (Graphics2D) p_graphics;
                    g2d.drawImage(gr.image, 0, 0, null);
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
        MouseAdapter mA = new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                mousePressed = true;
                mouseButton = me.getButton();
                SKETCH.mousePressed();
            }

            public void mouseReleased(MouseEvent me) {
                mousePressed = false;
                SKETCH.mouseReleased();
            }
        };
        panel.addMouseListener(mA);

        // We only utilize this variable again. Java makes copies before using it.
        mA = new MouseAdapter() {
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
        };
        panel.addMouseMotionListener(mA);

        gr = createGraphics(width, height);
    }

    // Using `netsh interface ip show address`:
    public HashMap<String, String> getNetworksOld() {
        Process wlan = null;
        try {
            wlan = new ProcessBuilder("netsh", "interface", "ip", "show", "address").start();
        } catch (IOException e) {
            e.printStackTrace();
            agcExit();
        }

        HashMap<String, String> ret = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(wlan.getInputStream()))) {
            boolean findingIp = false;
            // I do not cache `line.length()` because it is not needed in *every* case.

            for (String line, currentInterface = ""; (line = reader.readLine()) != null;) {
                // If you are looking at the configuration of an interface:
                if (findingIp && line.contains("Default Gateway")) {
                    int colonPos = line.indexOf(':'), ipStart = -1, lineLen = line.length();

                    for (int i = colonPos + 1; i < lineLen; i++)
                        if (line.charAt(i) != ' ') {
                            ipStart = i; // The first number of the IP address is at this position!
                            break;
                        }
                    String ip = line.substring(ipStart, lineLen);
                    // System.out.printf("Putting `%s` for %s.\n", ip, currentInterface);
                    ret.put(currentInterface, ip);
                    findingIp = false;
                } else if (line.contains("Configuration for interface")) {
                    currentInterface = line.substring(line.indexOf('\"') + 1, line.length() - 1);
                    // System.out.println(currentInterface);
                    findingIp = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    // Using `arp -a`:
    public ArrayList<String> getNetworks() {
        Process arp = null;

        try {
            arp = new ProcessBuilder("arp", "-a").start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> ret = new ArrayList<String>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arp.getInputStream()))) {
            for (String line; (line = reader.readLine()) != null;) {
                if (line.contains("dynamic")) {
                    ret.add(line.substring(2, line.indexOf(' ', 2)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ret;
    }

    public boolean isOpen(Form p_form) {
        // if (p_form == null)
        // return false;
        // else if (p_form.isClosedByUser())
        // return false;
        // else
        // return true;
        return p_form == null ? false : p_form.isClosedByUser() ? false : true;
    }

    public Form showForm(Form p_form, FormBuilder p_formBuild) {
        if (p_form != null)
            if (!p_form.isClosedByUser())
                p_form.close();

        p_form = p_formBuild.run();
        return p_form;
    }

    public static HashMap<String, String> parseRequestCodesFromFile(String p_fileName) {
        HashMap<String, String> parsedMap = new HashMap<>();
        File tableFile = new File("data", p_fileName);

        if (tableFile.exists()) {
            System.out.println("Found string table file!");
            try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
                int eqPos;
                for (String line; (line = reader.readLine()) != null;) {

                    if (line.isBlank() || line.charAt(0) == '#')
                        continue;

                    eqPos = line.indexOf('=');

                    if (eqPos == -1)
                        continue;

                    parsedMap.put(
                            line.substring(0, eqPos),
                            line.substring(eqPos + 1, line.length()));
                }
            } catch (IOException e) {
                System.out.println("Failed to read string table file!");
                e.printStackTrace();
            }
        }
        return parsedMap;
    }

    public static HashMap<String, String> parseStringTableFromFile(String p_fileName) {
        HashMap<String, String> parsedMap = new HashMap<>();
        File tableFile = new File("data", p_fileName);

        if (tableFile.exists()) {
            System.out.println("Found string table file!");
            try (BufferedReader reader = new BufferedReader(new FileReader(tableFile))) {
                String section = null, content = null;
                StringBuilder parsedContent;
                int eqPos = 0, lineLen = 0, newLineCharPos = 0;

                // Remember that this loop goes through EACH LINE!
                // Not each *character!* :joy::
                for (String line; (line = reader.readLine()) != null;) {
                    lineLen = line.length();

                    // Leave empty lines alone!:
                    if (line.isBlank())
                        continue;

                    // Skipping comments and registering sections,
                    // and skipi this iteration if they exist:
                    switch (line.charAt(0)) {
                        case '#':
                            continue;
                        case '[':
                            section = line.substring(1, line.indexOf(']'));
                            continue;
                    }

                    // Find where the `=` sign is!:
                    eqPos = line.indexOf('=');
                    content = line.substring(eqPos + 1, lineLen);

                    // Parse out `\n`s!:
                    parsedContent = new StringBuilder(content);

                    while ((newLineCharPos = parsedContent.indexOf("\\n")) != -1) {
                        // Causes an infinite loop, and I won't be writing `\n` anywhere, anyway:
                        // if (parsedContent.charAt(newLineCharPos - 1) == '\\')
                        // continue;

                        for (int i = 0; i < 2; i++)
                            parsedContent.deleteCharAt(newLineCharPos);
                        parsedContent.insert(newLineCharPos, '\n');
                    }

                    // if (content.contains("<br>"))
                    // content = content.replace("\\\\n", App.NEWLINE);

                    parsedMap.put(
                            // Format: `SectionName.propertyName`:
                            section.concat(".")
                                    .concat(line.substring(0, eqPos)),
                            parsedContent.toString());
                }
            } catch (IOException e) {
                System.out.println("Failed to read string table file!");
                e.printStackTrace();
            }
        }
        return parsedMap;
    }
    // #endregion
}
