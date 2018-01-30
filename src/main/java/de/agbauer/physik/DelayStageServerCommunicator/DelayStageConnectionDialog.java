package de.agbauer.physik.DelayStageServerCommunicator;

import javax.swing.*;

public class DelayStageConnectionDialog extends JDialog {

    JPanel contentPane;
    JTextField hostTextField;
    JTextField portTextField;

    DelayStageConnectionDialog(String host, int port) {
        setContentPane(contentPane);
        setModal(true);

        hostTextField.setText(host);
        portTextField.setText(String.valueOf(port));
    }
}
