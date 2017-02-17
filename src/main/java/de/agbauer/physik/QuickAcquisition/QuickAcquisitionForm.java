package de.agbauer.physik.QuickAcquisition;

import javax.swing.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class QuickAcquisitionForm {
    private JPanel quickAcquisitionPanel;
    JButton liveButton;
    JTextField liveTextField;
    JComboBox liveComboBox;
    JTextField snapTextField;
    JComboBox snapComboBox;
    JButton snapButton;
    JButton snapPlusButton;
    JComboBox snapPlusComboBox;
    JTextField snapPlusTextField;
    JButton stopButton;

    public QuickAcquisitionForm() {
        addBinningValues(liveComboBox);
        addBinningValues(snapComboBox);
        addBinningValues(snapPlusComboBox);

        liveComboBox.setSelectedItem("4");
        snapComboBox.setSelectedItem("1");
        snapPlusComboBox.setSelectedItem("1");

        liveTextField.setText("333");
        snapTextField.setText("10000");
        snapPlusTextField.setText("10000");
    }

    void addBinningValues(JComboBox comboBox) {
        comboBox.addItem("1");
        comboBox.addItem("2");
        comboBox.addItem("4");
        comboBox.addItem("8");
    }

    public void setGeneralInformationGiven(boolean enabled) {
        snapPlusButton.setEnabled(enabled);
        snapPlusTextField.setEnabled(enabled);
        snapPlusComboBox.setEnabled(enabled);
    }


}
