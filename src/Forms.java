import java.util.HashMap;

import uibooster.UiBooster;
import uibooster.components.WaitingDialog;
import uibooster.components.WindowSetting;
import uibooster.model.Form;
import uibooster.model.FormBuilder;
import uibooster.model.FormElement;

public class Forms {
    // #region Fields.
    // From `App`:
    static UiBooster ui; // = App.ui;
    static HashMap<String, String> strTable;

    static Form deviceSelectionForm, settingsForm, newFindingConnectionsForm;

    static FormBuilder settingsFormBuild, newFindingConnectionsFormBuild;

    static WaitingDialog findingDevicesDialog;
    // #endregion

    static void init(UiBooster p_ui, HashMap<String, String> p_strTable) {
        Forms.ui = p_ui;
        Forms.strTable = p_strTable;
    }

    static String getString(String p_key) {
        String ret = strTable.get(p_key);
        if (ret == null)
            System.err.printf("Key `%s` not found!\n", p_key);
        return ret;
    }

    public static WaitingDialog showFindingConnectionDialog() {
        WaitingDialog ret = ui.showWaitingDialog(
                getString("FindConnectionWaitBox.text"),
                getString("FindConnectionWaitBox.title"));
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
        FormBuilder ret = ui.createForm(getString("SettingsForm.title"));
        WindowSetting win = ret.andWindow();
        win.setSize(360, 180);

        ret.addButton(getString("SettingsForm.exitButton"), new Runnable() {
            @Override
            public void run() {
                App.SKETCH.agcExit();
            }
        });

        ret.addSlider(getString("SettingsForm.timeoutSlider"), 2, 20, 2, 2, 0);

        return Forms.settingsFormBuild = ret;
    }

}
