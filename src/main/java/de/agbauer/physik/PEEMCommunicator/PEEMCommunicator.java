package de.agbauer.physik.PEEMCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by dennis on 02/02/2017.
 */
public class PEEMCommunicator {
    private InputStream inputStream;
    private OutputStream outputStream;

    public PEEMCommunicator(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public void setProperty(PEEMProperty property, Float value) throws IOException {
        this.sendCommand("set " + property.cmdString() + " " + String.format(Locale.ROOT, "%.2f", value) + "\r");
    }

    public String getProperty(PEEMProperty property, PEEMQuantity quantity) throws IOException {
        this.flushInputStream();
        this.sendCommand("get " + property.cmdString() + " " + quantity.toString()+"\r");
        return this.readPeemBlockingUntilMessageReceived();
    }

    private void sendCommand(String commandStr) throws IOException {
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

        return inputString;
    }
}
