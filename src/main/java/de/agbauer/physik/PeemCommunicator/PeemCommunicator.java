package de.agbauer.physik.PeemCommunicator;

import java.io.IOException;

public interface PeemCommunicator {
    void setProperty(PeemProperty property, Double value) throws IOException;
    String getProperty(PeemProperty property, PeemQuantity quantity) throws IOException;
}
