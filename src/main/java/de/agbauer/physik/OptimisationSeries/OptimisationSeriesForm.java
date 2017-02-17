package de.agbauer.physik.OptimisationSeries;

import de.agbauer.physik.Generic.ActivatableForm;

import javax.swing.*;


/**
 * Created by dennis on 29/01/2017.
 */
public class OptimisationSeriesForm implements ActivatableForm {
    JPanel optimisationSeriesPanel;
    JRadioButton focusRadioButton;
    JRadioButton stigmatorXRadioButton;
    JRadioButton stigmatorYRadioButton;
    JRadioButton extraktorRadioButton;
    JTextField startingValueTextField;
    JTextField endingValueTextField;
    JTextField stepSizeTextField;
    JTextField exposureTextField;
    JButton startSeriesButton;
    private JLabel startingValueLabel;
    private JLabel endingValueLabel;
    private JLabel stepSizeValueLabel;
    private JLabel exposureLabel;
    JCheckBox saveSeriesCheckBox;
    JCheckBox sendNotificationCheckBox;

    void setGUIToMeasuringState() {
        this.enableRadioButtons(false);
        this.enableTextFields(false);
        this.enableCheckboxes(false);

        this.startSeriesButton.setEnabled(true);
        this.startSeriesButton.setText("Stop series");
    }

    void setGUIToInputState() {
        this.enableRadioButtons(true);
        this.enableTextFields(true);
        this.enableCheckboxes(true);

        this.startSeriesButton.setEnabled(true);
        this.startSeriesButton.setText("Start series");
    }

    void setGUIToCancelledSeriesState() {
        this.enableRadioButtons(false);
        this.enableTextFields(false);
        this.enableCheckboxes(false);

        this.startSeriesButton.setText("Stop series");
        this.startSeriesButton.setEnabled(false);
    }

    private void enableCheckboxes(boolean enabled) {
        saveSeriesCheckBox.setEnabled(enabled);
        sendNotificationCheckBox.setEnabled(enabled);
    }

    private void enableRadioButtons(boolean enabled) {
        this.focusRadioButton.setEnabled(enabled);
        this.stigmatorXRadioButton.setEnabled(enabled);
        this.stigmatorYRadioButton.setEnabled(enabled);
        this.extraktorRadioButton.setEnabled(enabled);
    }

    private void enableTextFields(boolean enabled) {
        this.startingValueTextField.setEnabled(enabled);
        this.endingValueTextField.setEnabled(enabled);
        this.stepSizeTextField.setEnabled(enabled);
        this.exposureTextField.setEnabled(enabled);

        this.startingValueLabel.setEnabled(enabled);
        this.endingValueLabel.setEnabled(enabled);
        this.stepSizeValueLabel.setEnabled(enabled);
        this.exposureLabel.setEnabled(enabled);
    }

    public void setEnabledState(boolean enabled) {
        enableRadioButtons(enabled);
        enableTextFields(enabled);
        this.startSeriesButton.setEnabled(enabled);
        saveSeriesCheckBox.setEnabled(enabled);
        sendNotificationCheckBox.setEnabled(enabled);
    }
}
