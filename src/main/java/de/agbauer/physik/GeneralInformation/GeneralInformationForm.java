package de.agbauer.physik.GeneralInformation;

import de.agbauer.physik.Generic.ActivatableForm;

import javax.swing.*;

/**
 * Created by dennis on 13/02/2017.
 */
public class GeneralInformationForm implements ActivatableForm {
    private JPanel generalInformationPanel;
    JComboBox apertureComboBox;
    JTextField excitationTextField;
    JTextField probeNameTextField;
    private JLabel probeNameLabel;
    private JLabel excitationLabel;
    private JLabel apertureLabel;

    public GeneralInformationForm() {
        apertureComboBox.addItem("50");
        apertureComboBox.addItem("75");
        apertureComboBox.addItem("500");
        apertureComboBox.addItem("1500");
        apertureComboBox.setSelectedItem("500");
    }

    @Override
    public void setEnabledState(boolean enabled) {
        apertureComboBox.setEnabled(enabled);
        apertureLabel.setEnabled(enabled);
        excitationTextField.setEnabled(enabled);
        excitationLabel.setEnabled(enabled);
        probeNameTextField.setEnabled(enabled);
        probeNameLabel.setEnabled(enabled);
    }

}
