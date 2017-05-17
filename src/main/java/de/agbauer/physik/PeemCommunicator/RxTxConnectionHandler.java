package de.agbauer.physik.PeemCommunicator;

import gnu.io.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class RxTxConnectionHandler implements SerialConnectionHandler {

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private SerialPort openedPort;

    public void closePort() {
        if (openedPort != null) {
            logger.info("Closing serial connection...");

            openedPort.close();
            openedPort = null;

            logger.info("Closed serial connection.");
        } else {
            logger.warning("Attempted to close serial connection, but was already closed.");
        }
    }

    public boolean isConnected() {
        return openedPort != null;
    }

    public void connectTo(String portName) throws IOException {
        try {
            logger.info("Opening port " + portName + "...");

            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(portName);

            if (portIdentifier.isCurrentlyOwned()) {
                throw new IOException("Port is currently in use");
            }

            CommPort commPort = portIdentifier.open(this.getClass().getName(),2000);

            if (!isSerialPort(commPort)) {
                throw new IOException("Only serial ports are handled");
            }

            openedPort = (SerialPort) commPort;
            openedPort.setSerialPortParams(9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);

            logger.info("Port '" + portName +  "' opened!");
        } catch (NoSuchPortException | PortInUseException | UnsupportedCommOperationException exception) {
            throw new IOException(exception);
        }
    }

    private boolean isSerialPort(CommPort commPort) {
        return  commPort instanceof SerialPort;
    }

    public InputStream getInputStream() throws IOException {
        return this.openedPort.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return this.openedPort.getOutputStream();
    }

}
