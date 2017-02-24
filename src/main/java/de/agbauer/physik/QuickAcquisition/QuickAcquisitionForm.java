package de.agbauer.physik.QuickAcquisition;

import javax.swing.*;

public class QuickAcquisitionForm {
    private JPanel quickAcquisitionPanel;
    JButton liveButton;
    JTextField liveTextField;
    JComboBox liveComboBox;
    JTextField snapTextField;
    JComboBox snapComboBox;
    JButton snapButton;
    JButton stopButton;

    public QuickAcquisitionForm() {
        addBinningValues(liveComboBox);
        addBinningValues(snapComboBox);

        liveComboBox.setSelectedItem("4");
        snapComboBox.setSelectedItem("1");

        liveTextField.setText("333");
        snapTextField.setText("10000");
    }

    void addBinningValues(JComboBox comboBox) {
        comboBox.addItem("1");
        comboBox.addItem("2");
        comboBox.addItem("4");
        comboBox.addItem("8");
    }

    void setGeneralInformationGiven(boolean enabled) {
        snapButton.setEnabled(enabled);
        snapTextField.setEnabled(enabled);
        snapComboBox.setEnabled(enabled);
    }


}
