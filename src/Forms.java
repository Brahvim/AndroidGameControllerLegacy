import java.util.HashMap;
import java.util.Map;

import uibooster.UiBooster;
import uibooster.components.WaitingDialog;
import uibooster.components.WindowSetting;
import uibooster.model.Form;
import uibooster.model.FormBuilder;

public class Forms {
    // #region Fields.
    // From `App`:
    static UiBooster ui; // = App.ui;
    static HashMap<String, String> strTable;

    static Form deviceSelectionForm, settingsForm;

    static FormBuilder deviceSelectionFormBuild, settingsFormBuild;

    static WaitingDialog findingDevicesDialog;
    // #endregion

    static void init(UiBooster p_ui, HashMap<String, String> p_strTable) {
        Forms.ui = p_ui;
        Forms.strTable = p_strTable;
    }

    static String getString(String p_key) {
        return strTable.get(p_key);
    }

    static FormBuilder createDeviceSelectionForm(HashMap<String, String> p_clients) {
        FormBuilder build = ui.createForm(getString("DeviceSelectionForm_Title"));
        build.addLabel(getString("DeviceSelectionForm_Guide"));

        for (Map.Entry<String, String> m : p_clients.entrySet()) {
            build.addButton(m.getKey(), new Runnable() {
                public void run() {
                }
            });
        }

        return deviceSelectionFormBuild = build;
    }

    public static WaitingDialog showFindingConnectionDialog() {
        WaitingDialog ret = ui.showWaitingDialog(
                getString("FindConnectionWaitBox.text"),
                getString("FindConnectionWaitBox.title"));
        findingDevicesDialog = ret;
        return ret;
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

        return settingsFormBuild = ret;
    }
}
