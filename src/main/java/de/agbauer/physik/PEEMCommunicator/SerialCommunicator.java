package de.agbauer.physik.PEEMCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by dennis on 09/02/2017.
 */
public interface SerialCommunicator {
    boolean isConnected();
    void connectTo(String portName) throws IOException;
    void closePort();
    InputStream getInputStream() throws IOException;
    OutputStream getOutputStream() throws IOException;
}
