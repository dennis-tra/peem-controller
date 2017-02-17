package de.agbauer.physik.GeneralInformation;

import javax.swing.*;

/**
 * Created by dennis on 13/02/2017.
 */
public class GeneralInformationForm {
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

}
