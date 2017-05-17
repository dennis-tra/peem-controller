package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.logging.Logger;

public class FocusPeemCommunicator implements PeemCommunicator {
    private InputStream inputStream;
    private OutputStream outputStream;
    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public FocusPeemCommunicator(InputStream inputStream, OutputStream outputStream) {
        this.inputStream = inputStream;
        this.outputStream = outputStream;
    }

    public synchronized void setProperty(PeemProperty property, Float value) throws IOException {
        String valueStr = String.format(Locale.ROOT, "%.2f", value);
        String commandStr = "set " + property.setCmdString() + " " + valueStr + "\r";

        this.sendCommand(commandStr);
    }

    public synchronized String getProperty(PeemProperty property, PeemQuantity quantity) throws IOException {
        this.flushInputStream();

        String commandStr = "get " + property.cmdString() + " " + quantity.toString()+"\r";
        this.sendCommand(commandStr);
        return this.readPeemBlockingUntilMessageReceived();
    }

    private void sendCommand(String commandStr) throws IOException {
        logger.info("Sending command: '" + commandStr + "'");
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

        logger.info("Received message: '" + inputString + "'");
        return inputString;
    }
}
