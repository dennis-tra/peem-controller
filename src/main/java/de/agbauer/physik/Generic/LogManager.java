package de.agbauer.physik.Generic;

import javax.swing.*;

public class LogManager {
    private org.micromanager.LogManager logManager;
    private JLabel parent;

    public LogManager(org.micromanager.LogManager logManager, JLabel parent) {
        this.logManager = logManager;
        this.parent = parent;
    }

    public void inform(String message, boolean writeToUi, boolean writeToConsole) {
        if (writeToUi) {
            parent.setText(message);
        }

        logManager.logDebugMessage(message);
    }

    public void error(String message, Exception exception, boolean writeToUi) {
        if (writeToUi) {
            parent.setText(message);
        }

        logManager.logDebugMessage(message + exception.getMessage());
        exception.printStackTrace();

    }

    public void showDialog(String message) {
        logManager.showMessage(message);
    }

}
