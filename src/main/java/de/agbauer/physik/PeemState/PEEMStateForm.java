package de.agbauer.physik.PeemState;

import javax.swing.*;

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
    JButton setAllButton;

    void enableControls(boolean b) {
        extTextField.setEnabled(b);
        setExtButton.setEnabled(b);
        focusTextField.setEnabled(b);
        setFocusButton.setEnabled(b);
        colTextField.setEnabled(b);
        setColButton.setEnabled(b);
        p1TextField.setEnabled(b);
        setP1Button.setEnabled(b);
        p2TextField.setEnabled(b);
        setP2Button.setEnabled(b);
        mcpTextfield.setEnabled(b);
        setMcpButton.setEnabled(b);
        scrTextField.setEnabled(b);
        setScrButton.setEnabled(b);
        readAllButton.setEnabled(b);
        vxTextField.setEnabled(b);
        vyTextField.setEnabled(b);
        sxTextField.setEnabled(b);
        syTextField.setEnabled(b);
        setVxButton.setEnabled(b);
        setVyButton.setEnabled(b);
        setSxButton.setEnabled(b);
        setSyButton.setEnabled(b);
        setAllButton.setEnabled(b);
    }
}
