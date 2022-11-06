package com.brahvim.androidgamecontroller.serial.config;

import com.brahvim.androidgamecontroller.serial.ButtonShape;
import com.brahvim.androidgamecontroller.serial.DpadDirection;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;

import processing.core.PVector;

public class ConfigurationPacket implements Serializable {
    public String AGC_VERSION;
    public PVector screenDimensions;
    public long appStartMilliSinceEpoch;

    // Control configurations:
    public ArrayList<ButtonConfig> buttons;
    public ArrayList<DpadButtonConfig> dpadButtons;
    public ArrayList<ThumbstickConfig> thumbsticks;
    public ArrayList<TouchpadConfig> touchpads;

    public ConfigurationPacket() {
        // Please set `this.appStartMilliSinceEpoch` in this manner!:
        // this.appStartMilliSinceEpoch = System.currentTimeMillis() -
        // MainActivity.sketch.millis();
    }

    public <T> T addObject(Object p_object) {
        if (p_object instanceof ButtonConfig)
            this.buttons.add((ButtonConfig)p_object);

        else if (p_object instanceof DpadButtonConfig)
            this.dpadButtons.add((DpadButtonConfig)p_object);

        else if (p_object instanceof ThumbstickConfig)
            this.thumbsticks.add((ThumbstickConfig)p_object);

        else if (p_object instanceof TouchpadConfig)
            this.touchpads.add((TouchpadConfig)p_object);

        else throw new IllegalArgumentException();
        return (T)p_object;
    }

    // We return a new object instead so you can *revert if needed or something I dunno:*
    public static ConfigurationPacket parse(InputStream p_fileStream) {
        //int lineNumber = 0;

        ConfigurationPacket ret = new ConfigurationPacket();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p_fileStream))) {
            String section = "", property = "", value = "";
            int eqPos, lineLen;

            for (String line; (line = reader.readLine()) != null; /*lineNumber++*/) {
                switch (line.charAt(0)) {
                    case '#':
                    case ';':
                        continue;
                    case '[':
                        section = line.substring(1, line.indexOf(']'));
                        break;
                    default:
                        break;
                }

                lineLen = line.length();

                // For parsing "`property= value`":
                eqPos = line.indexOf('=');
                property = line.substring(0, eqPos);
                value = line.substring(eqPos + 1, lineLen);

                switch (section) {
                    case "Button" -> {
                        ButtonConfig record = new ButtonConfig();

                        switch (property) {
                            case "x":
                                record.transform.x = Integer.parseInt(value);
                                break;
                            case "y":
                                record.transform.y = Integer.parseInt(value);
                                break;
                            case "w":
                                record.scale.x = Integer.parseInt(value);
                                break;
                            case "h":
                                record.scale.y = Integer.parseInt(value);
                                break;
                            case "text":
                                record.text = value;
                                break;
                            case "shape":
                                record.shape = ButtonShape.valueOf(value);
                                break;
                        }

                        ret.buttons.add(record);
                    }

                    case "DpadButton" -> {
                        DpadButtonConfig record = new DpadButtonConfig();

                        switch (property) {
                            case "x":
                                record.transform.x = Integer.parseInt(value);
                                break;
                            case "y":
                                record.transform.y = Integer.parseInt(value);
                                break;
                            case "w":
                                record.scale.x = Integer.parseInt(value);
                                break;
                            case "h":
                                record.scale.y = Integer.parseInt(value);
                                break;
                            case "shape":
                                record.dir = DpadDirection.valueOf(value);
                                break;
                        }

                        ret.dpadButtons.add(record);
                    }

                    case "Thumbstick" -> {
                    }

                    case "Touchpad" -> {
                    }

                    case "Settings" -> {
                    }

                    // region Quick-settings buttons.
                    case "QuickSettings1" -> {
                    }

                    case "QuickSettings2" -> {
                    }

                    case "QuickSettings3" -> {
                    }
                    // endregion

                    default -> {
                    }
                }
            }
        } catch (
          FileNotFoundException e) {
            e.printStackTrace();
        } catch (
          IOException p_e) {
            p_e.printStackTrace();
            return null;
        }

        return ret;
    }
}
