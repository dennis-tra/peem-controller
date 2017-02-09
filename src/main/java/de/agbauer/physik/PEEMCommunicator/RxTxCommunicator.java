package de.agbauer.physik.PEEMCommunicator;

import de.agbauer.physik.Generic.LogManager;
import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

public class RxTxCommunicator implements SerialCommunicator {

    private LogManager logManager;
    private SerialPort openedPort;

    RxTxCommunicator(LogManager logManager) {
        this.logManager = logManager;
    }

    public void closePort() {
        if (openedPort != null) {
            logManager.inform("Closing serial connection...");

            openedPort.close();
            openedPort = null;

            logManager.inform("Closed serial connection.");
        } else {
            logManager.inform("Warning: Attempted to close serial connection, but was already closed.", false, true);
        }
    }

    public boolean isConnected() {
        return openedPort != null;
    }

    public void connectTo(String portName) throws IOException {
        try {
            logManager.inform("Opening port " + portName + "...");

            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if ( portIdentifier.isCurrentlyOwned() ) {
                throw new IOException("Port is currently in use");
            }

            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if (!( commPort instanceof SerialPort)) {
                throw new IOException("Only serial ports are handled");
            }

            openedPort = (SerialPort) commPort;
            openedPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            logManager.inform("Port '" + portName +  "' opened!");
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException exception) {
            throw new IOException(exception);
        }
    }

    List<CommPortIdentifier> getAvailablePorts() {
        Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
        return Collections.list(portEnum);
    }

    public InputStream getInputStream() throws IOException {
        return this.openedPort.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.openedPort.getOutputStream();
    }

}
