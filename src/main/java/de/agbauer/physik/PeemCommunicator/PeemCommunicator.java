package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;

public interface PeemCommunicator {
    void setProperty(PeemProperty property, Float value) throws IOException;
    String getProperty(PeemProperty property, PeemQuantity quantity) throws IOException;
}
