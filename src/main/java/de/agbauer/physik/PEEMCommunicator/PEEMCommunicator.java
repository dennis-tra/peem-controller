package de.agbauer.physik.PEEMCommunicator;

import de.agbauer.physik.Generic.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by dennis on 02/02/2017.
 */
public class PEEMCommunicator {
    InputStream inputStream;
    OutputStream outputStream;
    LogManager logManager;

    public PEEMCommunicator(InputStream inputStream, OutputStream outputStream, LogManager logManager) {
        this.logManager = logManager;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void setProperty(PEEMProperty property, Float value) throws IOException {
        String valueStr = String.format(Locale.ROOT, "%.2f", value);
        String commandStr = "set " + property.cmdString() + " " + valueStr + "\r";

        this.sendCommand(commandStr);
    }

    public String getProperty(PEEMProperty property, PEEMQuantity quantity) throws IOException {
        this.flushInputStream();

        String commandStr = "get " + property.cmdString() + " " + quantity.toString()+"\r";
        this.sendCommand(commandStr);
        return this.readPeemBlockingUntilMessageReceived();
    }

    private void sendCommand(String commandStr) throws IOException {
        logManager.inform("Sending command: '" + commandStr + "'", true, true);

        byte[] command = commandStr.getBytes();
        this.outputStream.write(command);
    }

    private void flushInputStream() {
        byte[] buffer = new byte[1024];
        try {
            while ( this.inputStream.read(buffer) > 0 ) { }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private String readPeemBlockingUntilMessageReceived() throws IOException {
        byte[] buffer = new byte[1024];
        int len;

        String inputString = "";

        while ( ( len = this.inputStream.read(buffer)) > -1 ) {
            inputString += new String(buffer,0, len);

            int returnValueCompletionIndex = inputString.indexOf("\r\n");
            if (returnValueCompletionIndex != -1) {
                inputString = inputString.substring(0, returnValueCompletionIndex).replaceAll("\\p{Cntrl}", "");
                break;
            }
        }

        logManager.inform("Received message: '" + inputString + "'", true, true);
        return inputString;
    }
}
