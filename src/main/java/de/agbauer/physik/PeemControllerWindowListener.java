package de.agbauer.physik;

import de.agbauer.physik.PeemCommunicator.SerialConnectionHandler;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PeemControllerWindowListener implements WindowListener {
    private SerialConnectionHandler serialConnectionHandler;

    PeemControllerWindowListener(SerialConnectionHandler serialConnectionHandler) {
        this.serialConnectionHandler = serialConnectionHandler;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        serialConnectionHandler.closePort();
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {

    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
