package de.agbauer.physik.Generic;

import javax.swing.*;

public class LogManager {
    private org.micromanager.LogManager logManager;
    private JLabel parent;

    public LogManager(org.micromanager.LogManager logManager, JLabel parent) {
        this.logManager = logManager;
        this.parent = parent;
    }

    public void inform(String message) {
        this.inform(message, true, true);
    }

    public void inform(String message, boolean writeToUi) {
        this.inform(message, writeToUi, false);
    }

    public void inform(String message, boolean writeToUi, boolean writeToConsole) {
        if (writeToUi && parent != null) {
            parent.setText(message);
        }

        if (writeToConsole && logManager != null) {
            logManager.showMessage(message);
        }
    }

}
