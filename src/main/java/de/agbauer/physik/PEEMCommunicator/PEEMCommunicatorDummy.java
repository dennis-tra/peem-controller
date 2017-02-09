package de.agbauer.physik.PEEMCommunicator;

import de.agbauer.physik.Generic.LogManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

/**
 * Created by dennis on 09/02/2017.
 */
public class PEEMCommunicatorDummy extends PEEMCommunicator {

    public PEEMCommunicatorDummy(InputStream inputStream, OutputStream outputStream, LogManager logManager) {
        super(inputStream, outputStream, logManager);
    }


    public void setProperty(PEEMProperty property, Float value) throws IOException {
        String valueStr = String.format(Locale.ROOT, "%.2f", value);
        String commandStr = "set " + property.cmdString() + " " + valueStr + "\r";
    }

    public String getProperty(PEEMProperty property, PEEMQuantity quantity) throws IOException {
        return "0.0";
    }
}
