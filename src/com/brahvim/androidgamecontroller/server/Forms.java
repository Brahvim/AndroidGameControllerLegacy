package com.brahvim.androidgamecontroller.server;

import uibooster.UiBooster;
import uibooster.components.WaitingDialog;
import uibooster.components.WindowSetting;
import uibooster.model.Form;
import uibooster.model.FormBuilder;
import uibooster.model.FormElement;
import uibooster.model.FormElementChangeListener;

public class Forms {
    // #region Fields.
    public static UiBooster ui;

    public static Form deviceSelectionForm, settingsForm, newFindingConnectionsForm, bansForm, unbanForm;

    public static FormBuilder settingsFormBuild, newFindingConnectionsFormBuild, bansFormBuild, unbanFormBuild;

    public static WaitingDialog findingDevicesDialog;

    public final static int SETTINGS_WIDTH = 360, SETTINGS_HEIGHT = 230;
    // #endregion

    // #region General methods...
    public static void init(UiBooster p_ui) {
        Forms.ui = p_ui;
    }

    public static boolean isFormOpen(Form p_form) {
        // if (p_form == null)
        // return false;
        // else if (p_form.isClosedByUser())
        // return false;
        // else
        // return true;
        return p_form == null ? false : p_form.isClosedByUser() ? false : true;
    }

    public static boolean isFormClosed(Form p_form) {
        return p_form == null ? true : p_form.isClosedByUser();
    }

    public static Form showForm(Form p_form, FormBuilder p_formBuild) {
        if (p_form != null)
            if (!p_form.isClosedByUser())
                p_form.close();

        p_form = p_formBuild.run();
        return p_form;
    }

    public static Form showBlockingForm(Form p_form, FormBuilder p_formBuild) {
        if (p_form != null)
            if (!p_form.isClosedByUser())
                p_form.close();

        p_form = p_formBuild.show();
        return p_form;
    }

    public static String getString(String p_key) {
        String ret = StringTable.get(p_key);
        if (ret == null)
            System.err.printf("Key `%s` not found!\n", p_key);
        return ret;
    }
    // #endregion

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
                Sketch.agcExit();
            }
        });

        ret.addButton(Forms.getString("SettingsForm.bansMenuButton"), new Runnable() {
            @Override
            public void run() {
                Forms.bansForm = Forms.showForm(Forms.bansForm, Forms.createBansForm());
            }
        });

        ret.addSlider(Forms.getString("SettingsForm.timeoutSlider"), 2, 20, 2, 2, 0);

        return Forms.settingsFormBuild = ret;
    }

    public static FormBuilder createBansForm() {
        FormBuilder ret = Forms.ui.createForm(Forms.getString("BansForm.title"));
        ret.addLabel(Sketch.socket.bannedIpStrings.size() == 0
                ? Forms.getString("BansForm.noBans")
                : Forms.getString("BansForm.label"));

        for (int i = 0, max = Sketch.socket.bannedIpStrings.size(); i < max; i++) {
            String clientName = Sketch.socket.bannedClientNames.get(i),
                    clientIp = Sketch.socket.bannedIpStrings.get(i);
            // Can't use this variable inside an inner class
            // without declaring it `final`. Impossible!
            ret.addButton(clientName, new Runnable() {
                @Override
                public void run() {
                    Forms.unbanForm = Forms.showForm(
                            Forms.unbanForm, Forms.createUnbanForm(
                                    clientName, clientIp));
                }
            });
        }

        ret.andWindow().setSize(Forms.SETTINGS_WIDTH, Forms.SETTINGS_HEIGHT);
        return ret;
    }

    public static FormBuilder createUnbanForm(String p_clientName, String p_clientIp) {
        FormBuilder ret = Forms.ui.createForm("UnbansForm.title".concat(p_clientName).concat("?"));
        ret.addLabel(p_clientName.concat(" (IP: `").concat(p_clientIp).concat("`)"));

        ret.addButton(Forms.getString("UnbansForm.unbanButton"), new Runnable() {
            @Override
            public void run() {
                Sketch.socket.unbanIp(p_clientName);
            }
        }).setID("btn_unban");

        ret.addButton(Forms.getString("UnbansForm.permBanButton"), new Runnable() {
            @Override
            public void run() {
                // IP should be written to `AgcBannedClients.csv`
            }
        }).setID("btn_perm_ban");

        ret.setChangeListener(new FormElementChangeListener() {
            @Override
            public void onChange(FormElement p_elt, Object p_value, Form p_parentForm) {
                switch (p_elt.getId()) {
                    case "btn_unban" -> {
                        p_parentForm.close();
                    }
                    case "btn_perm_ban" -> {
                        p_parentForm.close();
                    }
                    default -> {
                    }
                }
            }

        });

        ret.andWindow().setSize(Forms.SETTINGS_WIDTH, Forms.SETTINGS_HEIGHT);
        return ret;
    }

}
