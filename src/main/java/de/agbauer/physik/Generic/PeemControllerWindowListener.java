package de.agbauer.physik.Generic;

import de.agbauer.physik.PEEMCommunicator.RxTxConnectionHandler;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class PeemControllerWindowListener implements WindowListener {
    private RxTxConnectionHandler rxTxConnectionHandler;

    public PeemControllerWindowListener(RxTxConnectionHandler rxTxConnectionHandler) {
        this.rxTxConnectionHandler = rxTxConnectionHandler;
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        rxTxConnectionHandler.closePort();
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
