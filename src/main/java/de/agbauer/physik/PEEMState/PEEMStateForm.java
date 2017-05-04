package de.agbauer.physik.PEEMState;

import javax.swing.*;

/**
 * Created by dennis on 09/02/2017.
 */
public class PEEMStateForm {
    JTextField extTextField;
    JButton setExtButton;
    JTextField focusTextField;
    JButton setFocusButton;
    JTextField colTextField;
    JButton setColButton;
    JTextField p1TextField;
    JButton setP1Button;
    JTextField p2TextField;
    JButton setP2Button;
    JTextField mcpTextfield;
    JButton setMcpButton;
    JTextField scrTextField;
    JButton setScrButton;
    JPanel peemStatePanel;
    JButton readAllButton;
    JTextField vxTextField;
    JTextField vyTextField;
    JTextField sxTextField;
    JTextField syTextField;
    JButton setVxButton;
    JButton setVyButton;
    JButton setSxButton;
    JButton setSyButton;
    JButton readSaveButton;
    JButton loadButton;

    void setEnabledState(boolean enabled) {
        readSaveButton.setEnabled(enabled);
        loadButton.setEnabled(enabled);
    }
}
