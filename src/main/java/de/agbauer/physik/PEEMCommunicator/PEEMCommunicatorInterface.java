package de.agbauer.physik.PEEMCommunicator;

import java.io.IOException;

public interface PEEMCommunicatorInterface {
    void setProperty(PEEMProperty property, Float value) throws IOException;
    String getProperty(PEEMProperty property, PEEMQuantity quantity) throws IOException;
}
