package de.agbauer.physik.DelayStageServerCommunicator;

import javax.swing.*;

public class TimeResolvedForm {
    private JPanel rootPanel;
    JTextField startingValueTextField;
    JTextField endingValueTextField;
    JTextField stepSizeTextField;
    JTextField exposureTextField;
    JButton goHomeButton;
    JButton defineHomeButton;
    JButton getPositionButton;
    JButton startButton;
    JButton stepBackButton;
    JButton stepForwardButton;
    JCheckBox sendNotificationCheckBox;
    JTextField manualStepSizeTextField;

    public void setGUIToInputState() {
        this.enableDelayStageControls(true);
        this.enableTextFields(true);
        this.enableCheckboxes(true);

        this.startButton.setEnabled(true);
        this.startButton.setText("Start");
    }

    private void enableCheckboxes(boolean b) {
        this.sendNotificationCheckBox.setEnabled(b);
    }

    private void enableTextFields(boolean b) {
        this.startingValueTextField.setEnabled(b);
        this.endingValueTextField.setEnabled(b);
        this.stepSizeTextField.setEnabled(b);
        this.exposureTextField.setEnabled(b);
    }

    private void enableDelayStageControls(boolean b) {
        this.goHomeButton.setEnabled(b);
        this.defineHomeButton.setEnabled(b);
        this.getPositionButton.setEnabled(b);
        this.stepBackButton.setEnabled(b);
        this.stepBackButton.setEnabled(b);
        this.stepForwardButton.setEnabled(b);
        this.manualStepSizeTextField.setEnabled(b);
    }

    public void setGUIToMeasuringState() {
        this.enableDelayStageControls(false);
        this.enableTextFields(false);
        this.enableCheckboxes(false);

        this.startButton.setEnabled(true);
        this.startButton.setText("Stop");
    }

    public void setGUIToCancelledSeriesState() {
        this.enableDelayStageControls(false);
        this.enableTextFields(false);
        this.enableCheckboxes(false);

        this.startButton.setText("Stopping...");
        this.startButton.setEnabled(false);
    }

    public void setEnabledState(boolean enabled) {
        enableDelayStageControls(enabled);
        enableTextFields(enabled);
        startButton.setEnabled(enabled);
        enableCheckboxes(enabled);
    }
}
