package com.brahvim.androidgamecontroller.server;

import uibooster.model.Form;

public class BlockingConfirmDialog {
    private boolean dialogEnded;
    private Form form;

    private BlockingConfirmDialog(String p_label, String p_windowTitle,
            Runnable p_yes, Runnable p_no) {
        this.form = Forms.ui.createForm(p_windowTitle)
                .addLabel(p_label)
                .addButton("Yes", new Runnable() {
                    public void run() {
                        p_yes.run();
                        dialogEnded = true;
                    };
                })
                .addButton("No", new Runnable() {
                    public void run() {
                        p_yes.run();
                        dialogEnded = true;
                    };
                }).show();
    }

    public static boolean create(String p_label, String p_windowTitle,
            Runnable p_yes, Runnable p_no) {
        BlockingConfirmDialog dialog = new BlockingConfirmDialog(
                p_label, p_windowTitle, p_yes, p_no);

        return dialog.form.isClosedByUser();

        /*
         * Forms.ui.showConfirmDialog(p_label, p_windowTitle,
         * new Runnable() {
         * 
         * @Override
         * public void run() {
         * p_yes.run();
         * dialogEnded = true;
         * }
         * },
         * new Runnable() {
         * 
         * @Override
         * public void run() {
         * p_no.run();
         * dialogEnded = true;
         * }
         * });
         * 
         * while (!dialogEnded)
         * ;
         */

    }
}
