package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;
import java.util.Locale;


public class DummyPeemCommunicator implements PeemCommunicator {

    public void setProperty(PeemProperty property, Float value) throws IOException {
        String valueStr = String.format(Locale.ROOT, "%.2f", value);
        String commandStr = "set " + property.cmdString() + " " + valueStr + "\r";
    }

    public String getProperty(PeemProperty property, PeemQuantity quantity) throws IOException {
        return "0.0";
    }
}
