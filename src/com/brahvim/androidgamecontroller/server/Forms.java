package com.brahvim.androidgamecontroller.server;

import com.brahvim.androidgamecontroller.server.AgcServerSocket.AgcClient;

import uibooster.UiBooster;
import uibooster.components.WaitingDialog;
import uibooster.components.WindowSetting;
import uibooster.model.Form;
import uibooster.model.FormBuilder;
import uibooster.model.FormElement;

public class Forms {
    // #region Fields.
    public static UiBooster ui;

    public static Form deviceSelectionForm, settingsForm, newFindingConnectionsForm, bansForm, unbanForm;

    public static FormBuilder settingsFormBuild, newFindingConnectionsFormBuild, bansFormBuild, unbanFormBuild;

    public static WaitingDialog findingDevicesDialog;

    public final static int SETTINGS_WIDTH = 360, SETTINGS_HEIGHT = 230;

    // #endregion

    public static void init(UiBooster p_ui) {
        Forms.ui = p_ui;
    }

    public static String getString(String p_key) {
        String ret = StringTable.get(p_key);
        if (ret == null)
            System.err.printf("Key `%s` not found!\n", p_key);
        return ret;
    }

    public static WaitingDialog showFindingConnectionDialog() {
        WaitingDialog ret = ui.showWaitingDialog(
                Forms.getString("FindConnectionWaitBox.text"),
                Forms.getString("FindConnectionWaitBox.title"));
        findingDevicesDialog = ret;
        return ret;
    }

    public static FormBuilder createFindingConnectionsDialogNew() {
        FormBuilder ret = ui.createForm("ProgressBar95!");
        ret.addProgress("Finding devices...", 0, 0, 0).setID("progress_find");

        WindowSetting win = ret.andWindow();
        win.setSize(200, 100);
        // win.setUndecorated();

        new Thread() {
            Form form = Forms.newFindingConnectionsForm;
            FormElement elt;
            // StringBuffer dots = new StringBuffer("...");
            // ^^^ Yep, we need thread safety here!
            // int dotsLen;

            public void run() {
                while (form == null)
                    ;

                System.out.println("Form is no longer `null`!");

                elt = form.getById("progress_find");

                while (true) {
                    // form.getWindow().setLocation(0, 0);
                    // System.out.println("THREADING!!1!1!");
                    // if (System.currentTimeMillis() % 15 == 0) {
                    // dotsLen = dots.length();
                    // if (dotsLen == 3)
                    // dots.delete(0, dotsLen);
                    // else
                    // dots.append('.');
                    // }
                    System.out.println("THREADING!");
                    elt.setValue(System.currentTimeMillis() % 15 == 0 ? 100 : 0);
                    // elt.setValue("Finding devices".concat(dots.toString()));
                }
            };
        };

        return Forms.newFindingConnectionsFormBuild = ret;
    }

    public static FormBuilder createSettingsForm() {
        FormBuilder ret = ui.createForm(Forms.getString("SettingsForm.title"));
        WindowSetting win = ret.andWindow();
        win.setSize(Forms.SETTINGS_WIDTH, Forms.SETTINGS_HEIGHT);

        ret.addButton(Forms.getString("SettingsForm.exitButton"), new Runnable() {
            @Override
            public void run() {
                Sketch.SKETCH.agcExit();
            }
        });

        ret.addButton(Forms.getString("SettingsForm.bansMenuButton"), new Runnable() {
            @Override
            public void run() {
                Forms.bansForm = Sketch.showForm(Forms.bansForm, Forms.createBansForm());
            }
        });

        ret.addSlider(Forms.getString("SettingsForm.timeoutSlider"), 2, 20, 2, 2, 0);

        return Forms.settingsFormBuild = ret;
    }

    public static FormBuilder createBansForm() {
        FormBuilder ret = Forms.ui.createForm(Forms.getString("BansForm.title"));
        ret.addLabel(Sketch.SKETCH.socket.clients.size() == 0 ? Forms.getString("BansForm.noBans")
                : Forms.getString("BansForm.label"));

        for (AgcClient c : Sketch.SKETCH.socket.clients) {
            String clientName = c.getName();
            ret.addButton(clientName, new Runnable() {
                @Override
                public void run() {
                    Forms.unbanForm = Sketch.showForm(
                            Forms.unbanForm, Forms.createUnbanForm(clientName, c.getIp()));
                }
            });
        }

        return ret;
    }

    public static FormBuilder createUnbanForm(String p_clientName, String p_clientIp) {
        FormBuilder ret = Forms.ui.createForm("UnbansForm.title".concat(p_clientName).concat("?"));
        ret.addLabel(p_clientName.concat(" (IP: `").concat(p_clientIp).concat("`)"));

        ret.addButton(Forms.getString("UnbansForm.unbanButton"), new Runnable() {
            @Override
            public void run() {
                Sketch.SKETCH.socket.unbanIp(p_clientName);
            }
        });

        ret.addButton(Forms.getString("UnbansForm.permBanButton"), new Runnable() {
            @Override
            public void run() {
                // IP should be written to `AgcBannedClients.csv`
            }
        });

        return ret;
    }

}
