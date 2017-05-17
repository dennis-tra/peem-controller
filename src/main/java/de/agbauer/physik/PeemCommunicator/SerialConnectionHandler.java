package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerialConnectionHandler {
    boolean isConnected();
    void connectTo(String portName) throws IOException;
    void closePort();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
}
