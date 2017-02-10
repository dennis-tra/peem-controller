package de.agbauer.physik;

import javax.swing.*;

/**
 * Created by dennis on 10/02/2017.
 */
public class GeneralInformationGUIHandler implements GeneralInformation {
    private JTextField probeNameTextField;
    private JTextField excitationTextField;
    private JComboBox apertureComboBox;

    public GeneralInformationGUIHandler(JTextField probeNameTextField, JTextField excitationTextField, JComboBox apertureComboBox) {
        this.probeNameTextField = probeNameTextField;
        this.excitationTextField = excitationTextField;
        this.apertureComboBox = apertureComboBox;
    }

    public String getProbeName() {
        return probeNameTextField.getText();
    }

    public String getExcitation() {
        return excitationTextField.getText();
    }

    public String getAperture() {
        return (String) apertureComboBox.getSelectedItem();
    }
}
